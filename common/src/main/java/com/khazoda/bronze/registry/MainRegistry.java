package com.khazoda.bronze.registry;

import com.khazoda.bronze.Constants;
import com.khazoda.bronze.block.BronzeDoor;
import com.khazoda.bronze.block.BronzeTrapdoor;
import com.khazoda.bronze.block.CutTinSlab;
import com.khazoda.bronze.block.CutTinStairs;
import com.khazoda.bronze.block.TinFramedGlass;
import com.khazoda.bronze.item.FarmersDelightKnife;
import com.khazoda.bronze.item.Sickle;
import com.khazoda.bronze.material.BronzeMaterial;
import com.khazoda.bronze.material.TinMaterial;
import com.khazoda.bronze.platform.Services;
import com.khazoda.bronze.registry.helper.KhazReg;
import com.khazoda.bronze.registry.helper.KhazReg.BlockEntry;
import com.khazoda.bronze.registry.helper.KhazReg.Entry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Consumer;

public final class MainRegistry {
  private static boolean initialized;
  public static final KhazReg reg = new KhazReg(Constants.MOD_ID);

  /**
   * ==========[ Items ]==========
   */
  public static final Entry<Item> RAW_TIN = reg.item("raw_tin");
  public static final Entry<Item> TIN_NUGGET = reg.item("tin_nugget");
  public static final Entry<Item> TIN_INGOT = reg.item("tin_ingot");
  public static final Entry<Item> TIN_HORSE_ARMOR = reg.item("tin_horse_armor", (key, props) -> new Item(props.horseArmor(TinMaterial.ARMOR)));
  public static final Entry<Item> TIN_NAUTILUS_ARMOR = reg.item("tin_nautilus_armor", (key, props) -> new Item(props.nautilusArmor(TinMaterial.ARMOR)));
  public static final Entry<Item> TIN_SWORD = reg.item("tin_sword", (key, props) -> new Item(props.sword(TinMaterial.TOOL, 3.0F, -2.4F)));
  public static final Entry<Item> TIN_SPEAR = reg.item("tin_spear", (key, props) -> new Item(props.spear(TinMaterial.TOOL, 0.85F, 0.82F, 0.65F, 4.0F, 9.0F, 8.25F, 5.1F, 12.5F, 4.6F)));
  public static final Entry<AxeItem> TIN_AXE = reg.item("tin_axe", (key, props) -> new AxeItem(TinMaterial.TOOL, 7F, -3.1F, props));
  public static final Entry<Item> TIN_PICKAXE = reg.item("tin_pickaxe", (key, props) -> new Item(props.pickaxe(TinMaterial.TOOL, 1.0F, -2.8F)));
  public static final Entry<ShovelItem> TIN_SHOVEL = reg.item("tin_shovel", (key, props) -> new ShovelItem(TinMaterial.TOOL, 1.5F, -3.0F, props));
  public static final Entry<HoeItem> TIN_HOE = reg.item("tin_hoe", (key, props) -> new HoeItem(TinMaterial.TOOL, -2.0F, 0.0F, props));
  public static final Entry<Item> TIN_HELMET = reg.item("tin_helmet", (key, props) -> new Item(props.humanoidArmor(TinMaterial.ARMOR, ArmorType.HELMET)));
  public static final Entry<Item> TIN_CHESTPLATE = reg.item("tin_chestplate", (key, props) -> new Item(props.humanoidArmor(TinMaterial.ARMOR, ArmorType.CHESTPLATE)));
  public static final Entry<Item> TIN_LEGGINGS = reg.item("tin_leggings", (key, props) -> new Item(props.humanoidArmor(TinMaterial.ARMOR, ArmorType.LEGGINGS)));
  public static final Entry<Item> TIN_BOOTS = reg.item("tin_boots", (key, props) -> new Item(props.humanoidArmor(TinMaterial.ARMOR, ArmorType.BOOTS)));

  public static final Entry<Item> BRONZE_BLEND = reg.item("bronze_blend");
  public static final Entry<Item> BRONZE_NUGGET = reg.item("bronze_nugget");
  public static final Entry<Item> BRONZE_INGOT = reg.item("bronze_ingot");
  public static final Entry<Item> BRONZE_HORSE_ARMOR = reg.item("bronze_horse_armor", (key, props) -> new Item(props.horseArmor(BronzeMaterial.ARMOR)));
  public static final Entry<Item> BRONZE_NAUTILUS_ARMOR = reg.item("bronze_nautilus_armor", (key, props) -> new Item(props.nautilusArmor(BronzeMaterial.ARMOR)));
  public static final Entry<Item> BRONZE_SWORD = reg.item("bronze_sword", (key, props) -> new Item(props.sword(BronzeMaterial.TOOL, 3.0F, -2.4F)));
  public static final Entry<Item> BRONZE_SPEAR = reg.item("bronze_spear", (key, props) -> new Item(props.spear(BronzeMaterial.TOOL, 0.95F, 0.95F, 0.6F, 2.5F, 8.0F, 6.75F, 5.1F, 11.25F, 4.6F)));
  public static final Entry<AxeItem> BRONZE_AXE = reg.item("bronze_axe", (key, props) -> new AxeItem(BronzeMaterial.TOOL, 6F, -3.1F, props));
  public static final Entry<Item> BRONZE_PICKAXE = reg.item("bronze_pickaxe", (key, props) -> new Item(props.pickaxe(BronzeMaterial.TOOL, 1.0F, -2.8F)));
  public static final Entry<ShovelItem> BRONZE_SHOVEL = reg.item("bronze_shovel", (key, props) -> new ShovelItem(BronzeMaterial.TOOL, 1.5F, -3.0F, props));
  public static final Entry<HoeItem> BRONZE_HOE = reg.item("bronze_hoe", (key, props) -> new HoeItem(BronzeMaterial.TOOL, -2.0F, 0.0F, props));
  public static final Entry<Sickle> SICKLE = reg.item("bronze_sickle", (key, props) -> new Sickle(props.durability(238).component(DataComponents.TOOL, Sickle.createToolProperties())));
  public static final Entry<Item> BRONZE_HELMET = reg.item("bronze_helmet", (key, props) -> new Item(props.humanoidArmor(BronzeMaterial.ARMOR, ArmorType.HELMET)));
  public static final Entry<Item> BRONZE_CHESTPLATE = reg.item("bronze_chestplate", (key, props) -> new Item(props.humanoidArmor(BronzeMaterial.ARMOR, ArmorType.CHESTPLATE)));
  public static final Entry<Item> BRONZE_LEGGINGS = reg.item("bronze_leggings", (key, props) -> new Item(props.humanoidArmor(BronzeMaterial.ARMOR, ArmorType.LEGGINGS)));
  public static final Entry<Item> BRONZE_BOOTS = reg.item("bronze_boots", (key, props) -> new Item(props.humanoidArmor(BronzeMaterial.ARMOR, ArmorType.BOOTS)));
  public static final Entry<FarmersDelightKnife> BRONZE_KNIFE = reg.item("bronze_knife", (key, props) -> new FarmersDelightKnife(FarmersDelightKnife.createProperties(key, BronzeMaterial.TOOL)));
  public static final Entry<FarmersDelightKnife> TIN_KNIFE = reg.item("tin_knife", (key, props) -> new FarmersDelightKnife(FarmersDelightKnife.createProperties(key, TinMaterial.TOOL)));

  /**
   * ==========[ Blocks + BlockItems ]==========
   */
  public static final BlockEntry<Block, BlockItem> TIN_ORE = blockWithItem("tin_ore_block", 2.5F, 0.0F, MapColor.STONE, NoteBlockInstrument.BASEDRUM, SoundType.STONE);
  public static final BlockEntry<Block, BlockItem> DEEPSLATE_TIN_ORE = blockWithItem("deepslate_tin_ore_block", 3.5F, 0.0F, MapColor.DEEPSLATE, NoteBlockInstrument.BASEDRUM, SoundType.DEEPSLATE);
  public static final BlockEntry<Block, BlockItem> RAW_TIN_BLOCK = blockWithItem("raw_tin_block", 2.5F, 6.0F, MapColor.TERRACOTTA_WHITE, NoteBlockInstrument.BASEDRUM, SoundType.COPPER);
  public static final BlockEntry<Block, BlockItem> TIN_BLOCK = blockWithItem("tin_block", 2.5F, 6.0F, MapColor.TERRACOTTA_WHITE, NoteBlockInstrument.IRON_XYLOPHONE, SoundType.COPPER);
  public static final BlockEntry<Block, BlockItem> CHISELED_TIN = blockWithItem("chiseled_tin", 2.5F, 6.0F, MapColor.TERRACOTTA_WHITE, NoteBlockInstrument.IRON_XYLOPHONE, SoundType.COPPER);
  public static final BlockEntry<Block, BlockItem> CUT_TIN = blockWithItem("cut_tin", 2.5F, 6.0F, MapColor.TERRACOTTA_WHITE, NoteBlockInstrument.IRON_XYLOPHONE, SoundType.COPPER);
  public static final BlockEntry<Block, BlockItem> TIN_TILES = blockWithItem("tin_tiles", 2.5F, 6.0F, MapColor.TERRACOTTA_WHITE, NoteBlockInstrument.IRON_XYLOPHONE, SoundType.COPPER);
  public static final BlockEntry<Block, BlockItem> BRONZE_BLEND_BLOCK = blockWithItem("bronze_blend_block", 2.5F, 0.0F, MapColor.DIRT, NoteBlockInstrument.BASEDRUM, SoundType.STONE);
  public static final BlockEntry<Block, BlockItem> BRONZE_BLOCK = blockWithItem("bronze_block", 3.5F, 0.0F, MapColor.GOLD, NoteBlockInstrument.IRON_XYLOPHONE, SoundType.METAL);
  public static final BlockEntry<TinFramedGlass, BlockItem> TIN_FRAMED_GLASS = reg.blockWithItem("tin_framed_glass", (key, props) -> new TinFramedGlass(key), BlockItem::new);
  public static final BlockEntry<CutTinSlab, BlockItem> CUT_TIN_SLAB = reg.blockWithItem("cut_tin_slab", (key, props) -> new CutTinSlab(BlockBehaviour.Properties.ofFullCopy(CUT_TIN.get()).setId(key)), BlockItem::new);
  public static final BlockEntry<CutTinStairs, BlockItem> CUT_TIN_STAIRS = reg.blockWithItem("cut_tin_stairs", (key, props) -> new CutTinStairs(CUT_TIN.get().defaultBlockState(), BlockBehaviour.Properties.ofFullCopy(CUT_TIN.get()).setId(key)), BlockItem::new);
  public static final BlockEntry<BronzeTrapdoor, BlockItem> BRONZE_TRAPDOOR = reg.blockWithItem("bronze_trapdoor_block", (key, props) -> new BronzeTrapdoor(key), BlockItem::new);
  public static final BlockEntry<BronzeDoor, BlockItem> BRONZE_DOOR = reg.blockWithItem("bronze_door_block", (key, props) -> new BronzeDoor(key), BlockItem::new);

  /**
   * ==========[ Worldgen ]==========
   */
  public static final ResourceKey<PlacedFeature> TIN_ORE_PLACED_KEY = ResourceKey.create(Registries.PLACED_FEATURE, Constants.ID("ore_tin"));
  public static final ResourceKey<PlacedFeature> TIN_ORE_SMALL_PLACED_KEY = ResourceKey.create(Registries.PLACED_FEATURE, Constants.ID("ore_tin_small"));

  /**
   * ==========[ Tabs ]==========
   */
  public static final Entry<CreativeModeTab> BRONZE_TAB = reg.tab("main", () -> new ItemStack(BRONZE_BLOCK.item().get()));

  private MainRegistry() {
  }

  public static void init() {
    if (initialized) return;
    initialized = true;
    reg.freeze();
  }

  public static void addMainTabItems(Consumer<ItemLike> output) {
    output.accept(BRONZE_SWORD.get());
    output.accept(BRONZE_SPEAR.get());
    output.accept(BRONZE_AXE.get());
    output.accept(BRONZE_PICKAXE.get());
    output.accept(BRONZE_SHOVEL.get());
    output.accept(BRONZE_HOE.get());
    output.accept(BRONZE_HELMET.get());
    output.accept(BRONZE_CHESTPLATE.get());
    output.accept(BRONZE_LEGGINGS.get());
    output.accept(BRONZE_BOOTS.get());
    output.accept(TIN_SWORD.get());
    output.accept(TIN_SPEAR.get());
    output.accept(TIN_AXE.get());
    output.accept(TIN_PICKAXE.get());
    output.accept(TIN_SHOVEL.get());
    output.accept(TIN_HOE.get());
    output.accept(TIN_HELMET.get());
    output.accept(TIN_CHESTPLATE.get());
    output.accept(TIN_LEGGINGS.get());
    output.accept(TIN_BOOTS.get());
    output.accept(SICKLE.get());
    if (Services.PLATFORM.isModLoaded("farmersdelight")) {
      output.accept(BRONZE_KNIFE.get());
      output.accept(TIN_KNIFE.get());
    }

    output.accept(RAW_TIN.get());
    output.accept(TIN_NUGGET.get());
    output.accept(TIN_INGOT.get());
    output.accept(TIN_HORSE_ARMOR.get());
    output.accept(TIN_NAUTILUS_ARMOR.get());

    output.accept(BRONZE_BLEND.get());
    output.accept(BRONZE_NUGGET.get());
    output.accept(BRONZE_INGOT.get());
    output.accept(BRONZE_HORSE_ARMOR.get());
    output.accept(BRONZE_NAUTILUS_ARMOR.get());

    output.accept(TIN_BLOCK.item().get());
    output.accept(BRONZE_BLOCK.item().get());
    output.accept(RAW_TIN_BLOCK.item().get());
    output.accept(BRONZE_BLEND_BLOCK.item().get());
    output.accept(TIN_ORE.item().get());
    output.accept(DEEPSLATE_TIN_ORE.item().get());

    output.accept(BRONZE_DOOR.item().get());
    output.accept(BRONZE_TRAPDOOR.item().get());
    output.accept(TIN_FRAMED_GLASS.item().get());
    output.accept(CHISELED_TIN.item().get());
    output.accept(TIN_TILES.item().get());
    output.accept(CUT_TIN.item().get());
    output.accept(CUT_TIN_STAIRS.item().get());
    output.accept(CUT_TIN_SLAB.item().get());
  }

  private static BlockEntry<Block, BlockItem> blockWithItem(String name, float destroyTime, float explosionResistance, MapColor mapColor, NoteBlockInstrument instrument, SoundType soundType) {
    return reg.blockWithItem(name, (key, props) -> new Block(baseBlockProperties(key, destroyTime, explosionResistance, mapColor, instrument, soundType)), BlockItem::new);
  }

  private static BlockBehaviour.Properties baseBlockProperties(net.minecraft.resources.ResourceKey<Block> key, float destroyTime, float explosionResistance, MapColor mapColor, NoteBlockInstrument instrument, SoundType soundType) {
    return BlockBehaviour.Properties.of()
        .strength(destroyTime, explosionResistance)
        .mapColor(mapColor)
        .instrument(instrument)
        .sound(soundType)
        .requiresCorrectToolForDrops()
        .setId(key);
  }
}
