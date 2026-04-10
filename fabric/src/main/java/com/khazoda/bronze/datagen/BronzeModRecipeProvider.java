package com.khazoda.bronze.datagen;

import com.khazoda.bronze.registry.MainRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalItemTags;
import net.fabricmc.fabric.api.tag.convention.v2.TagUtil;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.concurrent.CompletableFuture;

import static com.khazoda.bronze.Constants.recipeKey;

public class BronzeModRecipeProvider extends FabricRecipeProvider {
  public BronzeModRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
    super(output, registryLookup);
  }

  @Override
  protected RecipeProvider createRecipeProvider(HolderLookup.Provider registryLookup, RecipeOutput output) {
    return new RecipeProvider(registryLookup, output) {
      private static final TagKey<Item> BRONZE_INGOT_TAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(TagUtil.C_TAG_NAMESPACE, "ingots/bronze"));
      private static final TagKey<Item> RAW_TIN_TAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(TagUtil.C_TAG_NAMESPACE, "raw_materials/tin"));
      private static final TagKey<Item> TIN_INGOT_TAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(TagUtil.C_TAG_NAMESPACE, "ingots/tin"));
      private static final TagKey<Item> TIN_BLOCK_TAG = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(TagUtil.C_TAG_NAMESPACE, "storage_blocks/tin"));

      @Override
      public void buildRecipes() {
        HolderGetter<Item> registryEntryLookup = registryLookup.lookupOrThrow(Registries.ITEM);

        nineBlockStorageRecipes(RecipeCategory.MISC, MainRegistry.BRONZE_BLEND.get(), RecipeCategory.BUILDING_BLOCKS, MainRegistry.BRONZE_BLEND_BLOCK.get(), "crafting/bronze_blend_block", null, "crafting/bronze_blend", "bronze_blend");
        nineBlockStorageRecipes(RecipeCategory.MISC, MainRegistry.BRONZE_INGOT.get(), RecipeCategory.BUILDING_BLOCKS, MainRegistry.BRONZE_BLOCK.get(), "crafting/bronze_block", null, "crafting/bronze_ingot", "bronze_ingot");
        nineBlockStorageRecipes(RecipeCategory.MISC, MainRegistry.BRONZE_NUGGET.get(), RecipeCategory.MISC, MainRegistry.BRONZE_INGOT.get(), "crafting/bronze_ingot_from_nuggets", "bronze_ingot", "crafting/bronze_nugget", "bronze_nugget");
        nineBlockStorageRecipes(RecipeCategory.MISC, MainRegistry.RAW_TIN.get(), RecipeCategory.BUILDING_BLOCKS, MainRegistry.RAW_TIN_BLOCK.get(), "crafting/raw_tin_block", null, "crafting/raw_tin", "raw_tin");
        nineBlockStorageRecipes(RecipeCategory.MISC, MainRegistry.TIN_INGOT.get(), RecipeCategory.BUILDING_BLOCKS, MainRegistry.TIN_BLOCK.get(), "crafting/tin_block", null, "crafting/tin_ingot", "tin_ingot");
        nineBlockStorageRecipes(RecipeCategory.MISC, MainRegistry.TIN_NUGGET.get(), RecipeCategory.MISC, MainRegistry.TIN_INGOT.get(), "crafting/tin_ingot_from_nuggets", "tin_ingot", "crafting/tin_nugget", "tin_nugget");

        ShapelessRecipeBuilder.shapeless(registryEntryLookup, RecipeCategory.MISC, MainRegistry.BRONZE_BLEND.get())
            .group("bronze_blend")
            .requires(Ingredient.of(tagHolder(ConventionalItemTags.COPPER_RAW_MATERIALS)), 3)
            .requires(Ingredient.of(tagHolder(RAW_TIN_TAG)))
            .unlockedBy(getHasName(MainRegistry.RAW_TIN.get()), has(RAW_TIN_TAG))
            .save(output, recipeKey("crafting/bronze_blend_from_copper_and_tin"));
        ShapelessRecipeBuilder.shapeless(registryEntryLookup, RecipeCategory.MISC, MainRegistry.BRONZE_INGOT.get())
            .requires(Ingredient.of(tagHolder(ConventionalItemTags.COPPER_INGOTS)), 4)
            .requires(Ingredient.of(tagHolder(TIN_INGOT_TAG)))
            .unlockedBy(getHasName(MainRegistry.TIN_INGOT.get()), has(TIN_INGOT_TAG))
            .save(output, recipeKey("crafting/bronze_ingot_from_copper_and_tin_ingots"));

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(MainRegistry.BRONZE_BLEND.get()), RecipeCategory.MISC, CookingBookCategory.MISC, MainRegistry.BRONZE_INGOT.get(), 2.8F, 200)
            .unlockedBy(getHasName(MainRegistry.BRONZE_BLEND.get()), has(MainRegistry.BRONZE_BLEND.get()))
            .save(output, recipeKey("smelting/bronze_ingot_from_smelting_bronze_blend"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(MainRegistry.BRONZE_BLEND.get()), RecipeCategory.MISC, CookingBookCategory.MISC, MainRegistry.BRONZE_INGOT.get(), 2.8F, 100)
            .unlockedBy(getHasName(MainRegistry.BRONZE_BLEND.get()), has(MainRegistry.BRONZE_BLEND.get()))
            .save(output, recipeKey("smelting/bronze_ingot_from_blasting_bronze_blend"));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(MainRegistry.BRONZE_PICKAXE.get(), MainRegistry.BRONZE_SHOVEL.get(), MainRegistry.BRONZE_AXE.get(), MainRegistry.BRONZE_HOE.get(), MainRegistry.BRONZE_SWORD.get(), MainRegistry.BRONZE_SPEAR.get(), MainRegistry.BRONZE_HELMET.get(), MainRegistry.BRONZE_CHESTPLATE.get(), MainRegistry.BRONZE_LEGGINGS.get(), MainRegistry.BRONZE_BOOTS.get(), MainRegistry.BRONZE_HORSE_ARMOR.get(), MainRegistry.BRONZE_NAUTILUS_ARMOR.get()), RecipeCategory.MISC, CookingBookCategory.MISC, MainRegistry.BRONZE_NUGGET.get(), 0.1F, 200)
            .unlockedBy(getHasName(MainRegistry.BRONZE_INGOT.get()), has(BRONZE_INGOT_TAG))
            .save(output, recipeKey("smelting/bronze_nugget_from_smelting"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(MainRegistry.BRONZE_PICKAXE.get(), MainRegistry.BRONZE_SHOVEL.get(), MainRegistry.BRONZE_AXE.get(), MainRegistry.BRONZE_HOE.get(), MainRegistry.BRONZE_SWORD.get(), MainRegistry.BRONZE_SPEAR.get(), MainRegistry.BRONZE_HELMET.get(), MainRegistry.BRONZE_CHESTPLATE.get(), MainRegistry.BRONZE_LEGGINGS.get(), MainRegistry.BRONZE_BOOTS.get(), MainRegistry.BRONZE_HORSE_ARMOR.get(), MainRegistry.BRONZE_NAUTILUS_ARMOR.get()), RecipeCategory.MISC, CookingBookCategory.MISC, MainRegistry.BRONZE_NUGGET.get(), 0.1F, 100)
            .unlockedBy(getHasName(MainRegistry.BRONZE_INGOT.get()), has(BRONZE_INGOT_TAG))
            .save(output, recipeKey("smelting/bronze_nugget_from_blasting"));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(MainRegistry.RAW_TIN.get()), RecipeCategory.MISC, CookingBookCategory.MISC, MainRegistry.TIN_INGOT.get(), 0.7F, 200)
            .unlockedBy(getHasName(MainRegistry.RAW_TIN.get()), has(MainRegistry.RAW_TIN.get()))
            .save(output, recipeKey("smelting/tin_ingot_from_smelting_raw_tin"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(MainRegistry.RAW_TIN.get()), RecipeCategory.MISC, CookingBookCategory.MISC, MainRegistry.TIN_INGOT.get(), 0.7F, 100)
            .unlockedBy(getHasName(MainRegistry.RAW_TIN.get()), has(MainRegistry.RAW_TIN.get()))
            .save(output, recipeKey("smelting/tin_ingot_from_blasting_raw_tin"));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(MainRegistry.TIN_ORE.get()), RecipeCategory.MISC, CookingBookCategory.MISC, MainRegistry.TIN_INGOT.get(), 0.7F, 200)
            .unlockedBy(getHasName(MainRegistry.TIN_ORE.get()), has(MainRegistry.TIN_ORE.get()))
            .save(output, recipeKey("smelting/tin_ingot_from_smelting_tin_ore"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(MainRegistry.TIN_ORE.get()), RecipeCategory.MISC, CookingBookCategory.MISC, MainRegistry.TIN_INGOT.get(), 0.7F, 100)
            .unlockedBy(getHasName(MainRegistry.TIN_ORE.get()), has(MainRegistry.TIN_ORE.get()))
            .save(output, recipeKey("smelting/tin_ingot_from_blasting_tin_ore"));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(MainRegistry.DEEPSLATE_TIN_ORE.get()), RecipeCategory.MISC, CookingBookCategory.MISC, MainRegistry.TIN_INGOT.get(), 0.7F, 200)
            .unlockedBy(getHasName(MainRegistry.DEEPSLATE_TIN_ORE.get()), has(MainRegistry.DEEPSLATE_TIN_ORE.get()))
            .save(output, recipeKey("smelting/tin_ingot_from_smelting_deepslate_tin_ore"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(MainRegistry.DEEPSLATE_TIN_ORE.get()), RecipeCategory.MISC, CookingBookCategory.MISC, MainRegistry.TIN_INGOT.get(), 0.7F, 100)
            .unlockedBy(getHasName(MainRegistry.DEEPSLATE_TIN_ORE.get()), has(MainRegistry.DEEPSLATE_TIN_ORE.get()))
            .save(output, recipeKey("smelting/tin_ingot_from_blasting_deepslate_tin_ore"));
        SimpleCookingRecipeBuilder.smelting(Ingredient.of(MainRegistry.TIN_PICKAXE.get(), MainRegistry.TIN_SHOVEL.get(), MainRegistry.TIN_AXE.get(), MainRegistry.TIN_HOE.get(), MainRegistry.TIN_SWORD.get(), MainRegistry.TIN_SPEAR.get(), MainRegistry.TIN_HELMET.get(), MainRegistry.TIN_CHESTPLATE.get(), MainRegistry.TIN_LEGGINGS.get(), MainRegistry.TIN_BOOTS.get(), MainRegistry.TIN_HORSE_ARMOR.get(), MainRegistry.TIN_NAUTILUS_ARMOR.get()), RecipeCategory.MISC, CookingBookCategory.MISC, MainRegistry.TIN_NUGGET.get(), 0.1F, 200)
            .unlockedBy(getHasName(MainRegistry.BRONZE_INGOT.get()), has(BRONZE_INGOT_TAG))
            .save(output, recipeKey("smelting/tin_nugget_from_smelting"));
        SimpleCookingRecipeBuilder.blasting(Ingredient.of(MainRegistry.TIN_PICKAXE.get(), MainRegistry.TIN_SHOVEL.get(), MainRegistry.TIN_AXE.get(), MainRegistry.TIN_HOE.get(), MainRegistry.TIN_SWORD.get(), MainRegistry.TIN_SPEAR.get(), MainRegistry.TIN_HELMET.get(), MainRegistry.TIN_CHESTPLATE.get(), MainRegistry.TIN_LEGGINGS.get(), MainRegistry.TIN_BOOTS.get(), MainRegistry.TIN_HORSE_ARMOR.get(), MainRegistry.TIN_NAUTILUS_ARMOR.get()), RecipeCategory.MISC, CookingBookCategory.MISC, MainRegistry.TIN_NUGGET.get(), 0.1F, 100)
            .unlockedBy(getHasName(MainRegistry.BRONZE_INGOT.get()), has(BRONZE_INGOT_TAG))
            .save(output, recipeKey("smelting/tin_nugget_from_blasting"));

        SingleItemRecipeBuilder.stonecutting(Ingredient.of(MainRegistry.CUT_TIN.get()), RecipeCategory.BUILDING_BLOCKS, MainRegistry.CHISELED_TIN.get(), 1).group("chiseled_tin").unlockedBy(getHasName(MainRegistry.CUT_TIN.get()), has(MainRegistry.CUT_TIN.get())).save(output, recipeKey("stonecutting/chiseled_tin_from_cut_tin"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(tagHolder(TIN_BLOCK_TAG)), RecipeCategory.BUILDING_BLOCKS, MainRegistry.CHISELED_TIN.get(), 9).group("chiseled_tin").unlockedBy(getHasName(MainRegistry.TIN_BLOCK.get()), has(TIN_BLOCK_TAG)).save(output, recipeKey("stonecutting/chiseled_tin_from_tin_block"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(tagHolder(TIN_BLOCK_TAG)), RecipeCategory.BUILDING_BLOCKS, MainRegistry.CUT_TIN.get(), 9).group("cut_tin").unlockedBy(getHasName(MainRegistry.TIN_BLOCK.get()), has(TIN_BLOCK_TAG)).save(output, recipeKey("stonecutting/cut_tin_from_tin_block"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(tagHolder(TIN_BLOCK_TAG)), RecipeCategory.BUILDING_BLOCKS, MainRegistry.CUT_TIN_SLAB.get(), 18).group("cut_tin_slab").unlockedBy(getHasName(MainRegistry.TIN_BLOCK.get()), has(TIN_BLOCK_TAG)).save(output, recipeKey("stonecutting/cut_tin_slab_from_tin_block"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(MainRegistry.CUT_TIN.get()), RecipeCategory.BUILDING_BLOCKS, MainRegistry.CUT_TIN_SLAB.get(), 2).group("cut_tin_slab").unlockedBy(getHasName(MainRegistry.CUT_TIN.get()), has(MainRegistry.CUT_TIN.get())).save(output, recipeKey("stonecutting/cut_tin_slab_from_cut_tin"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(tagHolder(TIN_BLOCK_TAG)), RecipeCategory.BUILDING_BLOCKS, MainRegistry.CUT_TIN_STAIRS.get(), 9).group("cut_tin_stairs").unlockedBy(getHasName(MainRegistry.TIN_BLOCK.get()), has(TIN_BLOCK_TAG)).save(output, recipeKey("stonecutting/cut_tin_stairs_from_tin_block"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(MainRegistry.CUT_TIN.get()), RecipeCategory.BUILDING_BLOCKS, MainRegistry.CUT_TIN_STAIRS.get(), 1).group("cut_tin_stairs").unlockedBy(getHasName(MainRegistry.CUT_TIN.get()), has(MainRegistry.CUT_TIN.get())).save(output, recipeKey("stonecutting/cut_tin_stairs_from_cut_tin"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(tagHolder(TIN_BLOCK_TAG)), RecipeCategory.BUILDING_BLOCKS, MainRegistry.TIN_TILES.get(), 9).group("tin_tiles").unlockedBy(getHasName(MainRegistry.TIN_BLOCK.get()), has(TIN_BLOCK_TAG)).save(output, recipeKey("stonecutting/tin_tiles_from_tin_block"));
        SingleItemRecipeBuilder.stonecutting(Ingredient.of(MainRegistry.CUT_TIN.get()), RecipeCategory.BUILDING_BLOCKS, MainRegistry.TIN_TILES.get(), 1).group("tin_tiles").unlockedBy(getHasName(MainRegistry.CUT_TIN.get()), has(MainRegistry.CUT_TIN.get())).save(output, recipeKey("stonecutting/tin_tiles_from_cut_tin"));

        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.TOOLS, MainRegistry.TIN_AXE.get()).pattern("TT").pattern("T#").pattern(" #").define('T', Ingredient.of(tagHolder(TIN_INGOT_TAG))).define('#', Ingredient.of(tagHolder(ConventionalItemTags.WOODEN_RODS))).unlockedBy(getHasName(MainRegistry.TIN_INGOT.get()), has(TIN_INGOT_TAG)).save(output, recipeKey("crafting/tin_axe"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.COMBAT, MainRegistry.TIN_BOOTS.get()).pattern("T T").pattern("T T").define('T', Ingredient.of(tagHolder(TIN_INGOT_TAG))).unlockedBy(getHasName(MainRegistry.TIN_INGOT.get()), has(TIN_INGOT_TAG)).save(output, recipeKey("crafting/tin_boots"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.COMBAT, MainRegistry.TIN_CHESTPLATE.get()).pattern("T T").pattern("TTT").pattern("TTT").define('T', Ingredient.of(tagHolder(TIN_INGOT_TAG))).unlockedBy(getHasName(MainRegistry.TIN_INGOT.get()), has(TIN_INGOT_TAG)).save(output, recipeKey("crafting/tin_chestplate"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.COMBAT, MainRegistry.TIN_HELMET.get()).pattern("TTT").pattern("T T").define('T', Ingredient.of(tagHolder(TIN_INGOT_TAG))).unlockedBy(getHasName(MainRegistry.TIN_INGOT.get()), has(TIN_INGOT_TAG)).save(output, recipeKey("crafting/tin_helmet"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.TOOLS, MainRegistry.TIN_HOE.get()).pattern("TT").pattern(" #").pattern(" #").define('T', Ingredient.of(tagHolder(TIN_INGOT_TAG))).define('#', Ingredient.of(tagHolder(ConventionalItemTags.WOODEN_RODS))).unlockedBy(getHasName(MainRegistry.TIN_INGOT.get()), has(TIN_INGOT_TAG)).save(output, recipeKey("crafting/tin_hoe"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.COMBAT, MainRegistry.TIN_LEGGINGS.get()).pattern("TTT").pattern("T T").pattern("T T").define('T', Ingredient.of(tagHolder(TIN_INGOT_TAG))).unlockedBy(getHasName(MainRegistry.TIN_INGOT.get()), has(TIN_INGOT_TAG)).save(output, recipeKey("crafting/tin_leggings"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.TOOLS, MainRegistry.TIN_PICKAXE.get()).pattern("TTT").pattern(" # ").pattern(" # ").define('T', Ingredient.of(tagHolder(TIN_INGOT_TAG))).define('#', Ingredient.of(tagHolder(ConventionalItemTags.WOODEN_RODS))).unlockedBy(getHasName(MainRegistry.TIN_INGOT.get()), has(TIN_INGOT_TAG)).save(output, recipeKey("crafting/tin_pickaxe"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.TOOLS, MainRegistry.TIN_SHOVEL.get()).pattern("T").pattern("#").pattern("#").define('T', Ingredient.of(tagHolder(TIN_INGOT_TAG))).define('#', Ingredient.of(tagHolder(ConventionalItemTags.WOODEN_RODS))).unlockedBy(getHasName(MainRegistry.TIN_INGOT.get()), has(TIN_INGOT_TAG)).save(output, recipeKey("crafting/tin_shovel"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.COMBAT, MainRegistry.TIN_SWORD.get()).pattern("T").pattern("T").pattern("#").define('T', Ingredient.of(tagHolder(TIN_INGOT_TAG))).define('#', Ingredient.of(tagHolder(ConventionalItemTags.WOODEN_RODS))).unlockedBy(getHasName(MainRegistry.TIN_INGOT.get()), has(TIN_INGOT_TAG)).save(output, recipeKey("crafting/tin_sword"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.COMBAT, MainRegistry.TIN_SPEAR.get()).pattern("  T").pattern(" # ").pattern("#  ").define('T', Ingredient.of(tagHolder(TIN_INGOT_TAG))).define('#', Ingredient.of(tagHolder(ConventionalItemTags.WOODEN_RODS))).unlockedBy(getHasName(MainRegistry.TIN_INGOT.get()), has(TIN_INGOT_TAG)).save(output, recipeKey("crafting/tin_spear"));

        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.TOOLS, MainRegistry.BRONZE_AXE.get()).pattern("BB").pattern("B#").pattern(" #").define('B', Ingredient.of(tagHolder(BRONZE_INGOT_TAG))).define('#', Ingredient.of(tagHolder(ConventionalItemTags.WOODEN_RODS))).unlockedBy(getHasName(MainRegistry.BRONZE_INGOT.get()), has(BRONZE_INGOT_TAG)).save(output, recipeKey("crafting/bronze_axe"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.COMBAT, MainRegistry.BRONZE_BOOTS.get()).pattern("B B").pattern("B B").define('B', Ingredient.of(tagHolder(BRONZE_INGOT_TAG))).unlockedBy(getHasName(MainRegistry.BRONZE_INGOT.get()), has(BRONZE_INGOT_TAG)).save(output, recipeKey("crafting/bronze_boots"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.COMBAT, MainRegistry.BRONZE_CHESTPLATE.get()).pattern("B B").pattern("BBB").pattern("BBB").define('B', Ingredient.of(tagHolder(BRONZE_INGOT_TAG))).unlockedBy(getHasName(MainRegistry.BRONZE_INGOT.get()), has(BRONZE_INGOT_TAG)).save(output, recipeKey("crafting/bronze_chestplate"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.COMBAT, MainRegistry.BRONZE_HELMET.get()).pattern("BBB").pattern("B B").define('B', Ingredient.of(tagHolder(BRONZE_INGOT_TAG))).unlockedBy(getHasName(MainRegistry.BRONZE_INGOT.get()), has(BRONZE_INGOT_TAG)).save(output, recipeKey("crafting/bronze_helmet"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.TOOLS, MainRegistry.BRONZE_HOE.get()).pattern("BB").pattern(" #").pattern(" #").define('B', Ingredient.of(tagHolder(BRONZE_INGOT_TAG))).define('#', Ingredient.of(tagHolder(ConventionalItemTags.WOODEN_RODS))).unlockedBy(getHasName(MainRegistry.BRONZE_INGOT.get()), has(BRONZE_INGOT_TAG)).save(output, recipeKey("crafting/bronze_hoe"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.COMBAT, MainRegistry.BRONZE_LEGGINGS.get()).pattern("BBB").pattern("B B").pattern("B B").define('B', Ingredient.of(tagHolder(BRONZE_INGOT_TAG))).unlockedBy(getHasName(MainRegistry.BRONZE_INGOT.get()), has(BRONZE_INGOT_TAG)).save(output, recipeKey("crafting/bronze_leggings"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.TOOLS, MainRegistry.BRONZE_PICKAXE.get()).pattern("BBB").pattern(" # ").pattern(" # ").define('B', Ingredient.of(tagHolder(BRONZE_INGOT_TAG))).define('#', Ingredient.of(tagHolder(ConventionalItemTags.WOODEN_RODS))).unlockedBy(getHasName(MainRegistry.BRONZE_INGOT.get()), has(BRONZE_INGOT_TAG)).save(output, recipeKey("crafting/bronze_pickaxe"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.TOOLS, MainRegistry.BRONZE_SHOVEL.get()).pattern("B").pattern("#").pattern("#").define('B', Ingredient.of(tagHolder(BRONZE_INGOT_TAG))).define('#', Ingredient.of(tagHolder(ConventionalItemTags.WOODEN_RODS))).unlockedBy(getHasName(MainRegistry.BRONZE_INGOT.get()), has(BRONZE_INGOT_TAG)).save(output, recipeKey("crafting/bronze_shovel"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.TOOLS, MainRegistry.SICKLE.get()).pattern(" B ").pattern("  B").pattern("#B ").define('B', Ingredient.of(tagHolder(BRONZE_INGOT_TAG))).define('#', Ingredient.of(tagHolder(ConventionalItemTags.WOODEN_RODS))).unlockedBy(getHasName(MainRegistry.BRONZE_INGOT.get()), has(BRONZE_INGOT_TAG)).save(output, recipeKey("crafting/bronze_sickle"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.COMBAT, MainRegistry.BRONZE_SWORD.get()).pattern("B").pattern("B").pattern("#").define('B', Ingredient.of(tagHolder(BRONZE_INGOT_TAG))).define('#', Ingredient.of(tagHolder(ConventionalItemTags.WOODEN_RODS))).unlockedBy(getHasName(MainRegistry.BRONZE_INGOT.get()), has(BRONZE_INGOT_TAG)).save(output, recipeKey("crafting/bronze_sword"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.COMBAT, MainRegistry.BRONZE_SPEAR.get()).pattern("  B").pattern(" # ").pattern("#  ").define('B', Ingredient.of(tagHolder(BRONZE_INGOT_TAG))).define('#', Ingredient.of(tagHolder(ConventionalItemTags.WOODEN_RODS))).unlockedBy(getHasName(MainRegistry.BRONZE_INGOT.get()), has(BRONZE_INGOT_TAG)).save(output, recipeKey("crafting/bronze_spear"));

        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.BUILDING_BLOCKS, MainRegistry.BRONZE_DOOR.get(), 3).pattern("BB").pattern("BB").pattern("BB").define('B', Ingredient.of(tagHolder(BRONZE_INGOT_TAG))).unlockedBy(getHasName(MainRegistry.BRONZE_INGOT.get()), has(BRONZE_INGOT_TAG)).save(output, recipeKey("crafting/bronze_door"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.BUILDING_BLOCKS, MainRegistry.BRONZE_TRAPDOOR.get(), 2).pattern("BB").pattern("BB").define('B', Ingredient.of(tagHolder(BRONZE_INGOT_TAG))).unlockedBy(getHasName(MainRegistry.BRONZE_INGOT.get()), has(BRONZE_INGOT_TAG)).save(output, recipeKey("crafting/bronze_trapdoor"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.BUILDING_BLOCKS, MainRegistry.CHISELED_TIN.get()).group("chiseled_tin").pattern("S").pattern("S").define('S', Ingredient.of(MainRegistry.CUT_TIN_SLAB.get())).unlockedBy(getHasName(MainRegistry.TIN_INGOT.get()), has(TIN_INGOT_TAG)).save(output, recipeKey("crafting/chiseled_tin_from_slabs"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.BUILDING_BLOCKS, MainRegistry.CUT_TIN_SLAB.get(), 6).group("cut_tin_slab").pattern("TTT").define('T', Ingredient.of(MainRegistry.CUT_TIN.get())).unlockedBy(getHasName(MainRegistry.CUT_TIN.get()), has(TIN_INGOT_TAG)).save(output, recipeKey("crafting/cut_tin_slab"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.BUILDING_BLOCKS, MainRegistry.CUT_TIN_STAIRS.get(), 4).group("cut_tin_stairs").pattern("T  ").pattern("TT ").pattern("TTT").define('T', Ingredient.of(MainRegistry.CUT_TIN.get())).unlockedBy(getHasName(MainRegistry.CUT_TIN.get()), has(TIN_INGOT_TAG)).save(output, recipeKey("crafting/cut_tin_stairs"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.BUILDING_BLOCKS, MainRegistry.CUT_TIN.get(), 4).group("cut_tin").pattern("TT").pattern("TT").define('T', Ingredient.of(tagHolder(TIN_INGOT_TAG))).unlockedBy(getHasName(MainRegistry.TIN_INGOT.get()), has(TIN_INGOT_TAG)).save(output, recipeKey("crafting/cut_tin"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.BUILDING_BLOCKS, MainRegistry.TIN_FRAMED_GLASS.get(), 8).pattern("###").pattern("#T#").pattern("###").define('T', Ingredient.of(tagHolder(TIN_INGOT_TAG))).define('#', Ingredient.of(tagHolder(ConventionalItemTags.GLASS_BLOCKS_CHEAP))).unlockedBy(getHasName(MainRegistry.TIN_INGOT.get()), has(TIN_INGOT_TAG)).save(output, recipeKey("crafting/tin_framed_glass"));
        ShapedRecipeBuilder.shaped(registryEntryLookup, RecipeCategory.BUILDING_BLOCKS, MainRegistry.TIN_TILES.get(), 4).pattern(" T ").pattern("T T").pattern(" T ").define('T', Ingredient.of(MainRegistry.CUT_TIN.get())).unlockedBy(getHasName(MainRegistry.TIN_INGOT.get()), has(TIN_INGOT_TAG)).save(output, recipeKey("crafting/tin_tiles"));
      }

      private HolderSet.Named<Item> tagHolder(TagKey<Item> tag) {
        return registryLookup.lookupOrThrow(Registries.ITEM).getOrThrow(tag);
      }
    };
  }

  @Override
  public String getName() {
    return "BronzeModRecipeProvider";
  }
}
