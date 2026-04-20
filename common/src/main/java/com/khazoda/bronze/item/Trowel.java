package com.khazoda.bronze.item;

import com.khazoda.bronze.registry.MainRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.UseEffects;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Trowel extends Item {
  private static final Map<UUID, BlockHitResult> ACTIVE_TARGETS = new HashMap<>();

  public static Trowel create(Properties properties) {
    return new Trowel(properties.durability(238).component(DataComponents.TOOL, new Tool(List.of(), 1.0F, 1, true)).component(DataComponents.USE_EFFECTS, new UseEffects(true, true, 1.0F)));
  }

  public Trowel(Properties properties) {
    super(properties);
  }

  @Override
  public InteractionResult useOn(UseOnContext context) {
    Player player = context.getPlayer();
    if (player == null) return InteractionResult.PASS;

    if (!(context.getLevel() instanceof ServerLevel)) {
      return InteractionResult.SUCCESS_SERVER;
    }

    if (player.isUsingItem() && player.getUsedItemHand() == context.getHand() && ItemStack.isSameItemSameComponents(player.getUseItem(), context.getItemInHand())) {
      return InteractionResult.CONSUME;
    }

    if (TrowelConversions.get(context.getLevel().getBlockState(context.getClickedPos())) == null) {
      return InteractionResult.PASS;
    }

    ACTIVE_TARGETS.put(player.getUUID(), new BlockHitResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside()));
    player.startUsingItem(context.getHand());
    return InteractionResult.CONSUME;
  }

  @Override
  public int getUseDuration(ItemStack itemStack, LivingEntity user) {
    return 200;
  }

  @Override
  public void onUseTick(Level level, LivingEntity livingEntity, ItemStack itemStack, int ticksRemaining) {
    if (!(livingEntity instanceof Player player)) {
      livingEntity.releaseUsingItem();
      return;
    }

    int elapsedTicks = getUseDuration(itemStack, livingEntity) - ticksRemaining + 1;
    if (elapsedTicks >= 10) {
      livingEntity.releaseUsingItem();
      return;
    }

    if (elapsedTicks != 4) return;

    if (!(level instanceof ServerLevel serverLevel)) return;

    BlockHitResult blockHitResult = ACTIVE_TARGETS.remove(player.getUUID());
    if (blockHitResult == null) {
      livingEntity.releaseUsingItem();
      return;
    }

    BlockPos pos = blockHitResult.getBlockPos();
    BlockState state = level.getBlockState(pos);
    TrowelConversion conversion = TrowelConversions.get(state);
    if (conversion == null) {
      livingEntity.releaseUsingItem();
      return;
    }

    if (!level.setBlock(pos, conversion.to().defaultBlockState(), Block.UPDATE_ALL)) {
      livingEntity.releaseUsingItem();
      return;
    }

    serverLevel.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(player, state));
    level.playSound(null, pos, MainRegistry.TROWEL_DIG.get(), SoundSource.PLAYERS, 1.4F, level.getRandom().nextBoolean() ? 1.0F : 1.1F);
    spawnDigParticles(serverLevel, blockHitResult, state);
    spawnDigLoot(serverLevel, player, itemStack, blockHitResult, state, conversion);
    itemStack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(itemStack));
  }

  @Override
  public boolean releaseUsing(ItemStack itemStack, Level level, LivingEntity entity, int remainingTime) {
    if (level instanceof ServerLevel && entity instanceof Player player) {
      ACTIVE_TARGETS.remove(player.getUUID());
    }
    return false;
  }

  private static void spawnDigLoot(ServerLevel level, Player player, ItemStack tool, BlockHitResult hitResult, BlockState state, TrowelConversion conversion) {
    ResourceKey<LootTable> lootKey = conversion.loot();
    if (lootKey == null) return;

    LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(lootKey);
    LootParams lootParams = new LootParams.Builder(level).withParameter(LootContextParams.BLOCK_STATE, state).withParameter(LootContextParams.ORIGIN, hitResult.getLocation()).withParameter(LootContextParams.TOOL, tool).withOptionalParameter(LootContextParams.THIS_ENTITY, player).withLuck(player.getLuck()).create(LootContextParamSets.BLOCK);

    Direction direction = hitResult.getDirection();
    BlockPos blockPos = hitResult.getBlockPos();
    Vec3 hitLocation = hitResult.getLocation();
    Vec3 spawnPos = switch (direction) {
      case UP -> new Vec3(hitLocation.x, blockPos.getY() + 1.0D, hitLocation.z);
      case DOWN -> new Vec3(hitLocation.x, blockPos.getY() - 1.0D, hitLocation.z);
      case EAST, WEST -> new Vec3(hitLocation.x + direction.getStepX() * 0.32D, hitLocation.y, hitLocation.z);
      case NORTH, SOUTH -> new Vec3(hitLocation.x, hitLocation.y, hitLocation.z + direction.getStepZ() * 0.32D);
    };
    Vec3 velocity = switch (direction) {
      case UP -> new Vec3(0.0D, 0.18D, 0.0D);
      case DOWN -> Vec3.ZERO;
      default -> {
        double outwardVelocity = 0.1D + level.getRandom().nextDouble() * 0.2D;
        yield new Vec3(direction.getStepX() * outwardVelocity, 0.03D, direction.getStepZ() * outwardVelocity);
      }
    };
    boolean[] spawnedLoot = {false};
    lootTable.getRandomItems(lootParams, stack -> {
      if (spawnItem(level, spawnPos, velocity, stack)) {
        spawnedLoot[0] = true;
      }
    });

    if (spawnedLoot[0]) {
      level.playSound(null, BlockPos.containing(spawnPos), SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 0.5F, level.getRandom().nextBoolean() ? 1.25F : 1.45F);
    }
  }

  private static boolean spawnItem(ServerLevel level, Vec3 pos, Vec3 velocity, ItemStack stack) {
    if (stack.isEmpty()) return false;

    ItemEntity itemEntity = new ItemEntity(level, pos.x, pos.y, pos.z, stack, velocity.x, velocity.y, velocity.z);
    level.addFreshEntity(itemEntity);
    return true;
  }

  private static void spawnDigParticles(ServerLevel level, BlockHitResult hitResult, BlockState state) {
    BlockParticleOption particle = new BlockParticleOption(ParticleTypes.BLOCK, state);
    Direction direction = hitResult.getDirection();
    Direction.Axis axis = direction.getAxis();
    Vec3 hitLocation = hitResult.getLocation();
    double x = hitLocation.x + direction.getStepX() * 0.07D;
    double y = hitLocation.y + direction.getStepY() * 0.07D;
    double z = hitLocation.z + direction.getStepZ() * 0.07D;

    level.levelEvent(2001, hitResult.getBlockPos(), Block.getId(state));
    level.sendParticles(particle, x, y, z, 16, axis == Direction.Axis.X ? 0.02D : 0.08D, axis == Direction.Axis.Y ? 0.02D : 0.08D, axis == Direction.Axis.Z ? 0.02D : 0.08D, 0.18D);
  }
}
