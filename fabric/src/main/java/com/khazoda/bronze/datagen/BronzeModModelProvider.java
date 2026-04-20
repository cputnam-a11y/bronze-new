package com.khazoda.bronze.datagen;

import com.khazoda.bronze.material.BronzeMaterial;
import com.khazoda.bronze.material.TinMaterial;
import com.khazoda.bronze.registry.MainRegistry;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;

public class BronzeModModelProvider extends FabricModelProvider {
  public BronzeModModelProvider(FabricPackOutput output) {
    super(output);
  }

  @Override
  public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
    blockModelGenerators.createTrivialCube(MainRegistry.TIN_BLOCK.get());
    blockModelGenerators.createTrivialCube(MainRegistry.BRONZE_BLOCK.get());
    blockModelGenerators.createTrivialCube(MainRegistry.TIN_ORE.get());
    blockModelGenerators.createTrivialCube(MainRegistry.DEEPSLATE_TIN_ORE.get());
    blockModelGenerators.createTrivialCube(MainRegistry.TIN_TILES.get());
    blockModelGenerators.createTrivialCube(MainRegistry.BRONZE_BLEND_BLOCK.get());
    blockModelGenerators.createTrivialCube(MainRegistry.CHISELED_TIN.get());
    blockModelGenerators.createTrivialCube(MainRegistry.RAW_TIN_BLOCK.get());

    blockModelGenerators.createTrapdoor(MainRegistry.BRONZE_TRAPDOOR.get());
    blockModelGenerators.createDoor(MainRegistry.BRONZE_DOOR.get());
    blockModelGenerators.createTrivialCube(MainRegistry.TIN_FRAMED_GLASS.get());

    var cutTin = blockModelGenerators.family(MainRegistry.CUT_TIN.get());
    cutTin.stairs(MainRegistry.CUT_TIN_STAIRS.get());
    cutTin.slab(MainRegistry.CUT_TIN_SLAB.get());
  }

  @Override
  public void generateItemModels(ItemModelGenerators itemModelGenerators) {
    itemModelGenerators.generateFlatItem(MainRegistry.RAW_TIN.get(), ModelTemplates.FLAT_ITEM);
    itemModelGenerators.generateFlatItem(MainRegistry.TIN_NUGGET.get(), ModelTemplates.FLAT_ITEM);
    itemModelGenerators.generateFlatItem(MainRegistry.TIN_INGOT.get(), ModelTemplates.FLAT_ITEM);
    itemModelGenerators.generateFlatItem(MainRegistry.TIN_HORSE_ARMOR.get(), ModelTemplates.FLAT_ITEM);
    itemModelGenerators.generateFlatItem(MainRegistry.TIN_NAUTILUS_ARMOR.get(), ModelTemplates.FLAT_ITEM);
    itemModelGenerators.generateFlatItem(MainRegistry.TIN_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerators.generateSpear(MainRegistry.TIN_SPEAR.get());
    itemModelGenerators.generateFlatItem(MainRegistry.TIN_AXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerators.generateFlatItem(MainRegistry.TIN_PICKAXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerators.generateFlatItem(MainRegistry.TIN_SHOVEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerators.generateFlatItem(MainRegistry.TIN_HOE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerators.generateFlatItem(MainRegistry.TIN_KNIFE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerators.generateTrimmableItem(MainRegistry.TIN_HELMET.get(), TinMaterial.TIN_ARMOR_MATERIAL_KEY, ItemModelGenerators.TRIM_PREFIX_HELMET, false);
    itemModelGenerators.generateTrimmableItem(MainRegistry.TIN_CHESTPLATE.get(), TinMaterial.TIN_ARMOR_MATERIAL_KEY, ItemModelGenerators.TRIM_PREFIX_CHESTPLATE, false);
    itemModelGenerators.generateTrimmableItem(MainRegistry.TIN_LEGGINGS.get(), TinMaterial.TIN_ARMOR_MATERIAL_KEY, ItemModelGenerators.TRIM_PREFIX_LEGGINGS, false);
    itemModelGenerators.generateTrimmableItem(MainRegistry.TIN_BOOTS.get(), TinMaterial.TIN_ARMOR_MATERIAL_KEY, ItemModelGenerators.TRIM_PREFIX_BOOTS, false);

    itemModelGenerators.generateFlatItem(MainRegistry.BRONZE_BLEND.get(), ModelTemplates.FLAT_ITEM);
    itemModelGenerators.generateFlatItem(MainRegistry.BRONZE_NUGGET.get(), ModelTemplates.FLAT_ITEM);
    itemModelGenerators.generateFlatItem(MainRegistry.BRONZE_INGOT.get(), ModelTemplates.FLAT_ITEM);
    itemModelGenerators.generateFlatItem(MainRegistry.BRONZE_HORSE_ARMOR.get(), ModelTemplates.FLAT_ITEM);
    itemModelGenerators.generateFlatItem(MainRegistry.BRONZE_NAUTILUS_ARMOR.get(), ModelTemplates.FLAT_ITEM);
    itemModelGenerators.generateFlatItem(MainRegistry.BRONZE_SWORD.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerators.generateSpear(MainRegistry.BRONZE_SPEAR.get());
    itemModelGenerators.generateFlatItem(MainRegistry.BRONZE_AXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerators.generateFlatItem(MainRegistry.BRONZE_PICKAXE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerators.generateFlatItem(MainRegistry.BRONZE_SHOVEL.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerators.generateFlatItem(MainRegistry.BRONZE_HOE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerators.generateFlatItem(MainRegistry.SICKLE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerators.generateFlatItem(MainRegistry.BRONZE_KNIFE.get(), ModelTemplates.FLAT_HANDHELD_ITEM);
    itemModelGenerators.generateFlatItem(MainRegistry.BRONZE_COIN.get(), ModelTemplates.FLAT_ITEM);
    itemModelGenerators.generateTrimmableItem(MainRegistry.BRONZE_HELMET.get(), BronzeMaterial.BRONZE_ARMOR_MATERIAL_KEY, ItemModelGenerators.TRIM_PREFIX_HELMET, false);
    itemModelGenerators.generateTrimmableItem(MainRegistry.BRONZE_CHESTPLATE.get(), BronzeMaterial.BRONZE_ARMOR_MATERIAL_KEY, ItemModelGenerators.TRIM_PREFIX_CHESTPLATE, false);
    itemModelGenerators.generateTrimmableItem(MainRegistry.BRONZE_LEGGINGS.get(), BronzeMaterial.BRONZE_ARMOR_MATERIAL_KEY, ItemModelGenerators.TRIM_PREFIX_LEGGINGS, false);
    itemModelGenerators.generateTrimmableItem(MainRegistry.BRONZE_BOOTS.get(), BronzeMaterial.BRONZE_ARMOR_MATERIAL_KEY, ItemModelGenerators.TRIM_PREFIX_BOOTS, false);
  }
}
