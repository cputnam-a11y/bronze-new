package com.khazoda.bronze.datagen.advancements;

import com.khazoda.bronze.registry.MainRegistry;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.criterion.InventoryChangeTrigger;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.function.Consumer;

import static com.khazoda.bronze.Constants.ID;
import static com.khazoda.bronze.Constants.recipeKey;

@SuppressWarnings("removal")
public final class BronzeAdvancements {
  private BronzeAdvancements() {
  }

  public static void generate(Consumer<AdvancementHolder> advancementConsumer) {
    AdvancementHolder gotTinIngotAdvancement = Advancement.Builder.advancement()
        .parent(Identifier.withDefaultNamespace("story/upgrade_tools"))
        .display(MainRegistry.TIN_INGOT.get(), Component.translatable("advancement.bronze.got_tin_ingot.title"), Component.translatable("advancement.bronze.got_tin_ingot.description"), null, AdvancementType.TASK, true, true, false)
        .addCriterion("got_tin_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(MainRegistry.TIN_INGOT.get()))
        .rewards(AdvancementRewards.Builder.recipe(recipeKey("crafting/tin_block"))
            .addRecipe(recipeKey("crafting/tin_nugget"))
            .addRecipe(recipeKey("smelting/tin_nugget_from_smelting"))
            .addRecipe(recipeKey("smelting/tin_nugget_from_blasting"))
            .addRecipe(recipeKey("crafting/tin_helmet"))
            .addRecipe(recipeKey("crafting/tin_chestplate"))
            .addRecipe(recipeKey("crafting/tin_leggings"))
            .addRecipe(recipeKey("crafting/tin_boots"))
            .addRecipe(recipeKey("crafting/tin_sword"))
            .addRecipe(recipeKey("crafting/tin_spear"))
            .addRecipe(recipeKey("crafting/tin_pickaxe"))
            .addRecipe(recipeKey("crafting/tin_shovel"))
            .addRecipe(recipeKey("crafting/tin_axe"))
            .addRecipe(recipeKey("crafting/tin_hoe"))
            .addRecipe(recipeKey("stonecutting/chiseled_tin_from_tin_block"))
            .addRecipe(recipeKey("stonecutting/cut_tin_from_tin_block"))
            .addRecipe(recipeKey("stonecutting/cut_tin_slab_from_tin_block"))
            .addRecipe(recipeKey("stonecutting/cut_tin_stairs_from_tin_block"))
            .addRecipe(recipeKey("stonecutting/tin_tiles_from_tin_block"))
            .addRecipe(recipeKey("crafting/tin_framed_glass")))
        .build(ID("bronze/got_tin_ingot"));
    advancementConsumer.accept(gotTinIngotAdvancement);

    AdvancementHolder gotCutTinAdvancement = Advancement.Builder.advancement()
        .parent(gotTinIngotAdvancement)
        .display(MainRegistry.CUT_TIN.get(), Component.translatable("advancement.bronze.got_cut_tin.title"), Component.translatable("advancement.bronze.got_cut_tin.description"), null, AdvancementType.TASK, true, true, false)
        .addCriterion("got_cut_tin", InventoryChangeTrigger.TriggerInstance.hasItems(MainRegistry.CUT_TIN.get()))
        .rewards(AdvancementRewards.Builder.recipe(recipeKey("crafting/tin_tiles"))
            .addRecipe(recipeKey("stonecutting/chiseled_tin_from_cut_tin"))
            .addRecipe(recipeKey("stonecutting/cut_tin_slab_from_cut_tin"))
            .addRecipe(recipeKey("stonecutting/cut_tin_stairs_from_cut_tin"))
            .addRecipe(recipeKey("stonecutting/tin_tiles_from_cut_tin"))
            .addRecipe(recipeKey("crafting/cut_tin_slab"))
            .addRecipe(recipeKey("crafting/cut_tin_stairs"))
            .addRecipe(recipeKey("crafting/chiseled_tin_from_slabs")))
        .build(ID("bronze/got_cut_tin"));
    advancementConsumer.accept(gotCutTinAdvancement);

    AdvancementHolder gotBronzeBlendAdvancement = Advancement.Builder.advancement()
        .parent(Identifier.withDefaultNamespace("story/upgrade_tools"))
        .display(MainRegistry.BRONZE_BLEND.get(), Component.translatable("advancement.bronze.got_bronze_blend.title"), Component.translatable("advancement.bronze.got_bronze_blend.description"), null, AdvancementType.TASK, true, false, false)
        .addCriterion("got_bronze_blend", InventoryChangeTrigger.TriggerInstance.hasItems(MainRegistry.BRONZE_BLEND.get()))
        .rewards(AdvancementRewards.Builder.recipe(recipeKey("smelting/bronze_ingot_from_smelting_bronze_blend"))
            .addRecipe(recipeKey("smelting/bronze_ingot_from_blasting_bronze_blend"))
            .addRecipe(recipeKey("crafting/bronze_blend_block")))
        .build(ID("bronze/got_bronze_blend"));
    advancementConsumer.accept(gotBronzeBlendAdvancement);

    AdvancementHolder gotBronzeIngotAdvancement = Advancement.Builder.advancement()
        .parent(gotBronzeBlendAdvancement)
        .display(MainRegistry.BRONZE_INGOT.get(), Component.translatable("advancement.bronze.got_bronze_ingot.title"), Component.translatable("advancement.bronze.got_bronze_ingot.description"), null, AdvancementType.TASK, true, true, false)
        .addCriterion("got_bronze_ingot", InventoryChangeTrigger.TriggerInstance.hasItems(MainRegistry.BRONZE_INGOT.get()))
        .rewards(AdvancementRewards.Builder.recipe(recipeKey("crafting/bronze_helmet"))
            .addRecipe(recipeKey("crafting/bronze_chestplate"))
            .addRecipe(recipeKey("crafting/bronze_leggings"))
            .addRecipe(recipeKey("crafting/bronze_boots"))
            .addRecipe(recipeKey("crafting/bronze_sword"))
            .addRecipe(recipeKey("crafting/bronze_spear"))
            .addRecipe(recipeKey("crafting/bronze_pickaxe"))
            .addRecipe(recipeKey("crafting/bronze_shovel"))
            .addRecipe(recipeKey("crafting/bronze_axe"))
            .addRecipe(recipeKey("crafting/bronze_hoe"))
            .addRecipe(recipeKey("crafting/bronze_nugget"))
            .addRecipe(recipeKey("smelting/bronze_nugget_from_smelting"))
            .addRecipe(recipeKey("smelting/bronze_nugget_from_blasting"))
            .addRecipe(recipeKey("crafting/bronze_sickle"))
            .addRecipe(recipeKey("crafting/trowel"))
            .addRecipe(recipeKey("crafting/bronze_door"))
            .addRecipe(recipeKey("crafting/bronze_trapdoor"))
            .addRecipe(recipeKey("crafting/bronze_block")))
        .build(ID("bronze/got_bronze_ingot"));
    advancementConsumer.accept(gotBronzeIngotAdvancement);

    advancementConsumer.accept(Advancement.Builder.advancement()
        .parent(gotBronzeIngotAdvancement)
        .display(MainRegistry.BRONZE_CHESTPLATE.get(), Component.translatable("advancement.bronze.got_bronze_chestplate.title"), Component.translatable("advancement.bronze.got_bronze_chestplate.description"), null, AdvancementType.TASK, true, true, false)
        .addCriterion("got_bronze_helmet", InventoryChangeTrigger.TriggerInstance.hasItems(MainRegistry.BRONZE_HELMET.get()))
        .addCriterion("got_bronze_chestplate", InventoryChangeTrigger.TriggerInstance.hasItems(MainRegistry.BRONZE_CHESTPLATE.get()))
        .addCriterion("got_bronze_leggings", InventoryChangeTrigger.TriggerInstance.hasItems(MainRegistry.BRONZE_LEGGINGS.get()))
        .addCriterion("got_bronze_boots", InventoryChangeTrigger.TriggerInstance.hasItems(MainRegistry.BRONZE_BOOTS.get()))
        .build(ID("bronze/got_bronze_chestplate")));

    advancementConsumer.accept(Advancement.Builder.advancement()
        .parent(gotBronzeIngotAdvancement)
        .display(MainRegistry.SICKLE.get(), Component.translatable("advancement.bronze.got_sickle.title"), Component.translatable("advancement.bronze.got_sickle.description"), null, AdvancementType.TASK, true, true, false)
        .addCriterion("got_sickle", InventoryChangeTrigger.TriggerInstance.hasItems(MainRegistry.SICKLE.get()))
        .build(ID("bronze/got_sickle")));

    advancementConsumer.accept(Advancement.Builder.advancement()
        .parent(gotBronzeIngotAdvancement)
        .display(MainRegistry.TROWEL.get(), Component.translatable("advancement.bronze.got_trowel.title"), Component.translatable("advancement.bronze.got_trowel.description"), null, AdvancementType.TASK, true, true, false)
        .addCriterion("got_trowel", InventoryChangeTrigger.TriggerInstance.hasItems(MainRegistry.TROWEL.get()))
        .build(ID("bronze/got_trowel")));

    advancementConsumer.accept(Advancement.Builder.advancement()
        .parent(gotBronzeIngotAdvancement)
        .display(MainRegistry.BRONZE_PICKAXE.get(), Component.translatable("advancement.bronze.got_bronze_pickaxe.title"), Component.translatable("advancement.bronze.got_bronze_pickaxe.description"), null, AdvancementType.TASK, true, true, false)
        .addCriterion("got_bronze_pickaxe", InventoryChangeTrigger.TriggerInstance.hasItems(MainRegistry.BRONZE_PICKAXE.get()))
        .build(ID("bronze/got_bronze_pickaxe")));

    advancementConsumer.accept(Advancement.Builder.recipeAdvancement()
        .parent(Identifier.withDefaultNamespace("recipes/root"))
        .addCriterion("got_raw_tin", InventoryChangeTrigger.TriggerInstance.hasItems(MainRegistry.RAW_TIN.get()))
        .rewards(AdvancementRewards.Builder.recipe(recipeKey("crafting/bronze_blend_from_copper_and_tin"))
            .addRecipe(recipeKey("crafting/raw_tin_block"))
            .addRecipe(recipeKey("smelting/tin_ingot_from_smelting_deepslate_tin_ore"))
            .addRecipe(recipeKey("smelting/tin_ingot_from_smelting_raw_tin"))
            .addRecipe(recipeKey("smelting/tin_ingot_from_smelting_tin_ore"))
            .addRecipe(recipeKey("smelting/tin_ingot_from_blasting_deepslate_tin_ore"))
            .addRecipe(recipeKey("smelting/tin_ingot_from_blasting_raw_tin"))
            .addRecipe(recipeKey("smelting/tin_ingot_from_blasting_tin_ore")))
        .build(ID("recipes/got_raw_tin")));

    advancementConsumer.accept(Advancement.Builder.recipeAdvancement()
        .parent(Identifier.withDefaultNamespace("recipes/root"))
        .addCriterion("got_raw_tin_block", InventoryChangeTrigger.TriggerInstance.hasItems(MainRegistry.RAW_TIN_BLOCK.get()))
        .rewards(AdvancementRewards.Builder.recipe(recipeKey("crafting/raw_tin")))
        .build(ID("recipes/got_raw_tin_block")));

    advancementConsumer.accept(Advancement.Builder.recipeAdvancement()
        .parent(Identifier.withDefaultNamespace("recipes/root"))
        .addCriterion("got_bronze_blend_block", InventoryChangeTrigger.TriggerInstance.hasItems(MainRegistry.BRONZE_BLEND_BLOCK.get()))
        .rewards(AdvancementRewards.Builder.recipe(recipeKey("crafting/bronze_blend")))
        .build(ID("recipes/got_bronze_blend_block")));

    advancementConsumer.accept(Advancement.Builder.recipeAdvancement()
        .parent(Identifier.withDefaultNamespace("recipes/root"))
        .addCriterion("got_bronze_block", InventoryChangeTrigger.TriggerInstance.hasItems(MainRegistry.BRONZE_BLOCK.get()))
        .rewards(AdvancementRewards.Builder.recipe(recipeKey("crafting/bronze_ingot")))
        .build(ID("recipes/got_bronze_block")));

    advancementConsumer.accept(Advancement.Builder.recipeAdvancement()
        .parent(Identifier.withDefaultNamespace("recipes/root"))
        .addCriterion("got_bronze_nuggets", InventoryChangeTrigger.TriggerInstance.hasItems(MainRegistry.BRONZE_NUGGET.get()))
        .rewards(AdvancementRewards.Builder.recipe(recipeKey("crafting/bronze_ingot_from_nuggets")))
        .build(ID("recipes/got_bronze_nuggets")));

    advancementConsumer.accept(Advancement.Builder.recipeAdvancement()
        .parent(Identifier.withDefaultNamespace("recipes/root"))
        .addCriterion("got_tin_nuggets", InventoryChangeTrigger.TriggerInstance.hasItems(MainRegistry.TIN_NUGGET.get()))
        .rewards(AdvancementRewards.Builder.recipe(recipeKey("crafting/tin_ingot_from_nuggets")))
        .build(ID("recipes/got_tin_nuggets")));
  }
}
