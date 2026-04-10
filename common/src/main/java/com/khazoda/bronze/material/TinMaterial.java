package com.khazoda.bronze.material;

import com.khazoda.bronze.Constants;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Util;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.equipment.ArmorMaterial;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.item.equipment.EquipmentAsset;

import java.util.EnumMap;

public class TinMaterial {

  public static final ResourceKey<? extends Registry<EquipmentAsset>> ROOT_ID = ResourceKey.createRegistryKey(Identifier.withDefaultNamespace("equipment_asset"));
  public static final ResourceKey<EquipmentAsset> TIN_ARMOR_MATERIAL_KEY = ResourceKey.create(ROOT_ID, Constants.ID("tin"));

  public static final TagKey<Item> TIN_INGOT_TAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "ingots/tin"));
  
  public static final ArmorMaterial ARMOR = new ArmorMaterial(7,
    Util.make(new EnumMap<>(ArmorType.class), map -> {
      map.put(ArmorType.BOOTS, 1);
      map.put(ArmorType.LEGGINGS, 3);
      map.put(ArmorType.CHESTPLATE, 5);
      map.put(ArmorType.HELMET, 2);
      map.put(ArmorType.BODY, 4);
    }), 9, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, 0.0F, TIN_INGOT_TAG, TIN_ARMOR_MATERIAL_KEY);
  
  public static final ToolMaterial TOOL = new ToolMaterial(BlockTags.INCORRECT_FOR_COPPER_TOOL, 151, 6.0F, 1F, 13, TIN_INGOT_TAG);
}
