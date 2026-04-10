package com.khazoda.bronze.item;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.item.component.Weapon;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public class FarmersDelightKnife extends Item {
  public static final TagKey<Item> KNIVES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("farmersdelight", "tools/knives"));
  public static final TagKey<Block> MINEABLE_WITH_KNIFE = TagKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("farmersdelight", "mineable/knife"));

  public static Properties createProperties(ResourceKey<Item> id, ToolMaterial material) {
    HolderGetter<Block> holderGetter = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.BLOCK);

    return new Properties().durability(material.durability()).repairable(material.repairItems()).enchantable(material.enchantmentValue())
        .attributes(createAttributes(material, 0.5F, -2.0F))
        .component(DataComponents.TOOL, new Tool(
            List.of(
                Tool.Rule.deniesDrops(holderGetter.getOrThrow(material.incorrectBlocksForDrops())),
                Tool.Rule.minesAndDrops(holderGetter.getOrThrow(MINEABLE_WITH_KNIFE), material.speed())
            ), 1.0F, 1, false))
        .component(DataComponents.WEAPON, new Weapon(2))
        .setId(id);
  }

  public FarmersDelightKnife(Properties properties) {
    super(properties);
  }

  public static ItemAttributeModifiers createAttributes(ToolMaterial material, float attackDamage, float attackSpeed) {
    return ItemAttributeModifiers.builder()
        .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, attackDamage + material.attackDamageBonus(), AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
        .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, attackSpeed, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
        .build();
  }


  @Override
  public InteractionResult useOn(UseOnContext context) {
    Level level = context.getLevel();
    ItemStack toolStack = context.getItemInHand();
    BlockPos pos = context.getClickedPos();
    BlockState state = level.getBlockState(pos);
    Direction facing = context.getClickedFace();

    System.out.println(toolStack.is(KNIVES));
    if (state.getBlock() == Blocks.PUMPKIN && toolStack.is(KNIVES)) {
      Player player = context.getPlayer();
      if (player != null && !level.isClientSide()) {
        Direction direction = facing.getAxis() == Direction.Axis.Y ? player.getDirection().getOpposite() : facing;
        level.playSound(null, pos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
        level.setBlock(pos, Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, direction), 11);
        ItemEntity itemEntity = new ItemEntity(level, (double) pos.getX() + 0.5D + (double) direction.getStepX() * 0.65D, (double) pos.getY() + 0.1D, (double) pos.getZ() + 0.5D + (double) direction.getStepZ() * 0.65D, new ItemStack(Items.PUMPKIN_SEEDS, 4));
        itemEntity.setDeltaMovement(0.05D * (double) direction.getStepX() + level.getRandom().nextDouble() * 0.02D, 0.05D, 0.05D * (double) direction.getStepZ() + level.getRandom().nextDouble() * 0.02D);
        level.addFreshEntity(itemEntity);
        toolStack.hurtAndBreak(1, player, context.getHand());
      }
      return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    } else {
      return InteractionResult.PASS;
    }
  }
}
