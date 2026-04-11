package com.khazoda.bronze.item;

import com.khazoda.bronze.BronzeCommon;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.khazoda.bronze.Constants.ID;
import static com.khazoda.bronze.Constants.LOG;

/**
 *
 * <b>A sickle has two main functions, mowing and harvesting</b>
 * <br>Mowing: removing grass, leaf litter, ferns (or anything else defined in the {@link #SICKLE_MOW_BLOCKS} block tag) in an area
 * <br>Harvesting: breaking and replanting crops, with a low chance of extra loot drops
 * <br>
 * <br><b>Definitions</b>
 * <br> <i>Grasslike</i>: a block that is defined in {@link #SICKLE_MOW_BLOCKS}
 * <br> <i>SHR</i>: stands for sickle_harvest_range, an integer defined in config which denotes how large the mowing/harvesting area is
 * <br>
 * <br><b>Left and Right click do different things based on what is being interacted with</b>
 * <br>Left Click Crops: cancel interaction, crop remains intact ({@link #canDestroyBlock})
 * <br>Left Click Grasslikes: break all grasslikes in SHR ({@link #aoeMow})
 * <br>Right Click Crops: break and replant all crop blocks in SHR ({@link #aoeHarvest})
 * <br>Right Click Grasslikes/Flowers: break all flowers in SHR, leave grasslikes intact
 */
public class Sickle extends Item {
  public static final TagKey<Block> SICKLE_MOW_BLOCKS = TagKey.create(Registries.BLOCK, ID("sickle_mow"));
  public static final TagKey<Block> SICKLE_PLUCK_BLOCKS = TagKey.create(Registries.BLOCK, ID("sickle_pluck"));

  public Sickle(Properties properties) {
    super(properties);
  }

  public static Tool createToolProperties() {
    return new Tool(List.of(), 1.0F, 1, true);
  }

  @Override
  public boolean canDestroyBlock(ItemStack stack, BlockState state, Level level, BlockPos pos, LivingEntity entity) {
    /* Prevent crop blocks from being destroyed */
    return super.canDestroyBlock(stack, state, level, pos, entity) && !(state.getBlock() instanceof CropBlock) && !(state.getBlock() instanceof NetherWartBlock);
  }

  @Override
  public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity miningEntity) {
    Tool tool = stack.get(DataComponents.TOOL);
    if (tool == null) return false;

    if (state.is(SICKLE_MOW_BLOCKS)) {
      // Sickle Mowing
      aoeMow(level, miningEntity, state, state, pos, SICKLE_MOW_BLOCKS, 0);
      stack.hurtAndBreak(1, miningEntity, EquipmentSlot.MAINHAND);
    } else if (!level.isClientSide() && !state.is(BlockTags.FIRE) && state.getDestroySpeed(level, pos) != 0.0f && tool.damagePerBlock() > 0) {
      // Normal Tool Damage
      stack.hurtAndBreak(tool.damagePerBlock(), miningEntity, EquipmentSlot.MAINHAND);
    }
    return true;
  }


  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();
    Player player = context.getPlayer();
    if (level.isClientSide() || player == null) return InteractionResult.PASS;
    ServerLevel serverLevel = (ServerLevel) level;
    ItemStack stack = context.getItemInHand();
    BlockPos pos = context.getClickedPos();
    BlockState state = level.getBlockState(pos);

    if ((state.getBlock() instanceof CropBlock || state.getBlock() instanceof NetherWartBlock) && isMature(state)) {
      BlockPos basePos = findCropBase(level, pos);
      BlockState baseState = level.getBlockState(basePos);

      playSweepFeedback(serverLevel, player, context.getHand(), basePos, baseState, 1.0F, 1.0F);
      stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
      Set<BlockPos> visited = new HashSet<>();
      aoeHarvest(level, player, basePos, 0, visited);
      return InteractionResult.SUCCESS;
    } else if ((state.is(SICKLE_PLUCK_BLOCKS))) {
      playSweepFeedback(serverLevel, player, context.getHand(), pos, state, 0.8F, 1.0F);
      level.playSound(null, pos, SoundEvents.BUBBLE_POP, SoundSource.BLOCKS, 1.0F, 1.0F);
      stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(stack));
      aoeMow(level, player, state, state, pos, SICKLE_PLUCK_BLOCKS, 0, new HashSet<>());
      return InteractionResult.SUCCESS;
    }
    return InteractionResult.PASS;
  }

  @Override
  public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
    stack.hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
  }

  private static void aoeMow(Level level, LivingEntity entity, BlockState initialBlockState, BlockState currentBlockState, BlockPos pos, TagKey<Block> blocksToMow, int iteration) {
    aoeMow(level, entity, initialBlockState, currentBlockState, pos, blocksToMow, iteration, new HashSet<>());
  }

  private static void aoeMow(Level level, LivingEntity entity, BlockState initialBlockState, BlockState currentBlockState, BlockPos pos, TagKey<Block> blocksToMow, int iteration, Set<BlockPos> visited) {
    if (level.isClientSide()) return;
    if (!visited.add(pos)) return;
    /* Cut Grass */
    if (initialBlockState.is(blocksToMow)) {
      Block currentBlock = currentBlockState.getBlock();
      if (currentBlockState.is(blocksToMow)) {
        if (entity instanceof Player player) {
          if (level.getBlockState(pos).getBlock() == currentBlock) {
            currentBlock.playerDestroy(level, player, pos, currentBlockState, null, player.getMainHandItem());
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

            // Add block breaking particles
            ((ServerLevel) level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, currentBlockState), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5, 0.2, 0.2, 0.2, 0.1);
          }
        } else {
          Block.dropResources(currentBlockState, level, pos, null, entity, ItemStack.EMPTY);
          level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
      }

      int sickleMowRange = BronzeCommon.CONFIG.get(BronzeCommon.SICKLE_MOW_RANGE);
      if (iteration < sickleMowRange - 1) {
        aoeMow(level, entity, initialBlockState, level.getBlockState(pos.east()), pos.east(), blocksToMow, iteration + 1, visited);
        aoeMow(level, entity, initialBlockState, level.getBlockState(pos.north()), pos.north(), blocksToMow, iteration + 1, visited);
        aoeMow(level, entity, initialBlockState, level.getBlockState(pos.west()), pos.west(), blocksToMow, iteration + 1, visited);
        aoeMow(level, entity, initialBlockState, level.getBlockState(pos.south()), pos.south(), blocksToMow, iteration + 1, visited);
      }
    }
  }

  private static void aoeHarvest(Level level, LivingEntity entity, BlockPos pos, int iteration, Set<BlockPos> visited) {
    if (level.isClientSide()) return;
    BlockState blockState = level.getBlockState(pos);
    /* Harvest Crops */
    if ((blockState.getBlock() instanceof CropBlock || blockState.getBlock() instanceof NetherWartBlock)) {
      BlockPos basePos = findCropBase(level, pos);
      if (!visited.add(basePos)) return;

      BlockState baseState = level.getBlockState(basePos);

      /* Ascertain the age IntegerProperty of the block */
      IntegerProperty ageProperty = null;
      boolean isMature = switch (baseState.getBlock()) {
        case CropBlock cropBlock -> {
          ageProperty = baseState.getProperties().stream()
              .filter(IntegerProperty.class::isInstance)
              .filter(property -> property.getName().equals("age"))
              .map(IntegerProperty.class::cast)
              .findFirst()
              .orElse(null);
          if (ageProperty == null) yield false;

          int currentAge = baseState.getValue(ageProperty);
          yield currentAge >= cropBlock.getMaxAge();
        }
        case NetherWartBlock netherWart -> {
          int currentAge = baseState.getValue(NetherWartBlock.AGE);
          ageProperty = NetherWartBlock.AGE;
          yield currentAge >= 3;
        }
        default -> false;
      };
      if (!isMature) return;

      /* Break the entire crop column from top to bottom, then replant at base */
      harvestCropColumn(level, entity, basePos);
      tryExtraHappyLootChance(level, basePos);

      /* Replant at base position */
      replantCrop(level, baseState, basePos, ageProperty);
      int sickleHarvestRange = BronzeCommon.CONFIG.get(BronzeCommon.SICKLE_HARVEST_RANGE);
      if (iteration < sickleHarvestRange - 1) {
        aoeHarvest(level, entity, pos.east(), iteration + 1, visited);
        aoeHarvest(level, entity, pos.north(), iteration + 1, visited);
        aoeHarvest(level, entity, pos.west(), iteration + 1, visited);
        aoeHarvest(level, entity, pos.south(), iteration + 1, visited);
      }
    }
  }

  private static void playSweepFeedback(ServerLevel level, Player player, InteractionHand hand, BlockPos pos, BlockState state, float volume, float pitch) {
    level.playSound(null, pos, state.getSoundType().getBreakSound(), SoundSource.BLOCKS, volume, pitch);
    player.swing(hand, true);
    level.sendParticles(ParticleTypes.SWEEP_ATTACK, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1, 0, 0, 0, 0);
  }

  private static void tryExtraHappyLootChance(Level level, BlockPos pos) {
    if (!level.isClientSide()) {
      /* 1/50 chance to drop bonemeal when a crop tile is harvested */
      if (level.getRandom().nextInt(50) == 0) {
        int count = level.getRandom().nextInt(4) + 2; // Random number between 2-5
        ItemStack bonemeal = new ItemStack(Items.BONE_MEAL, count);
        Block.popResource(level, pos, bonemeal);

        // Play a happy congratulatory note block sound with random pitch :)
        float randomPitch = 1.0F + level.getRandom().nextFloat() * 0.5F; // Random pitch between 1.0 and 1.5
        level.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.NOTE_BLOCK_CHIME, SoundSource.BLOCKS, 1.0F, randomPitch);
      }
      /* 1/150 chance to drop mob loot */
      if (level.getRandom().nextInt(150) == 0) {
        ItemStack drop;
        // Randomly choose one of the mob drops
        switch (level.getRandom().nextInt(4)) {
          case 0:
            drop = new ItemStack(Items.SPIDER_EYE);
            break;
          case 1:
            drop = new ItemStack(Items.ROTTEN_FLESH);
            break;
          case 2:
            drop = new ItemStack(Items.BONE);
            break;
          default:
            drop = new ItemStack(Items.GUNPOWDER);
            break;
        }
        Block.popResource(level, pos, drop);
        // Play a lower bell sound with random pitch
        float spookyPitch = 0.8F + level.getRandom().nextFloat() * 0.3F; // Random pitch between 0.8 and 1.1
        level.playSound(null, (double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D, SoundEvents.NOTE_BLOCK_BELL, SoundSource.BLOCKS, 1.0F, spookyPitch);
      }
    }
  }

  private static boolean isMature(BlockState state) {
    if (state.getBlock() instanceof CropBlock cropBlock) {
      return cropBlock.isMaxAge(state);
    } else if (state.getBlock() instanceof NetherWartBlock) {
      return state.getValue(NetherWartBlock.AGE) >= NetherWartBlock.MAX_AGE;
    }
    return false;
  }

  private static BlockPos findCropBase(Level level, BlockPos pos) {
    Block cropBlock = level.getBlockState(pos).getBlock();
    BlockPos basePos = pos;
    while (level.getBlockState(basePos.below()).getBlock() == cropBlock) {
      basePos = basePos.below();
    }
    return basePos;
  }

  private static void harvestCropColumn(Level level, LivingEntity entity, BlockPos basePos) {
    Block cropBlock = level.getBlockState(basePos).getBlock();

    /* Find the top of the column */
    BlockPos topPos = basePos;
    while (level.getBlockState(topPos.above()).getBlock() == cropBlock) {
      topPos = topPos.above();
    }

    /* Break from top down to avoid cascading block updates causing drops to vanish */
    BlockPos current = topPos;
    while (current.getY() >= basePos.getY()) {
      BlockState currentState = level.getBlockState(current);
      if (currentState.getBlock() != cropBlock) break;

      boolean selfHarvested = false;
      try {
        if (entity instanceof Player player) {
          /* Try the block's own harvest interaction first (compat for e.g. Farmer's Delight tomatoes) */
          BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(current), Direction.UP, current, false);
          InteractionResult result = currentState.useWithoutItem(level, player, hit);
          if (result.consumesAction()) {
            selfHarvested = true;
          } else {
            currentState.getBlock().playerDestroy(level, player, current, currentState, null, player.getMainHandItem());
          }
        } else {
          Block.dropResources(currentState, level, current, null, entity, ItemStack.EMPTY);
        }
      } catch (Exception e) {
        LOG.warn("[Sickle] Failed to harvest {} at {}: {}", cropBlock.getClass().getSimpleName(), current, e.getMessage());
      }

      /* Only clear the block if it wasn't self-harvested and playerDestroy didn't replace it */
      if (!selfHarvested && level.getBlockState(current).getBlock() == cropBlock) {
        level.setBlock(current, Blocks.AIR.defaultBlockState(), 3);
      }

      if (!selfHarvested) {
        ((ServerLevel) level).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, currentState), current.getX() + 0.5, current.getY() + 0.5, current.getZ() + 0.5, 5, 0.2, 0.2, 0.2, 0.1);
      }

      current = current.below();
    }
  }

  private static void replantCrop(Level level, BlockState state, BlockPos pos, IntegerProperty ageProperty) {
    try {
      level.setBlock(pos, state.setValue(ageProperty, 0), 3);
    } catch (IllegalArgumentException e) {
      LOG.warn("[Sickle] Failed to replant {} at {}: {}", state.getBlock().getClass().getSimpleName(), pos, e.getMessage());
    }
  }
}
