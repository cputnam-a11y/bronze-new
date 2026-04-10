package com.khazoda.bronze.datagen;

import com.khazoda.bronze.registry.MainRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;

import java.util.concurrent.CompletableFuture;

public class BronzeModItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
  public BronzeModItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
    super(output, registryLookup);
  }

  @Override
  protected void addTags(HolderLookup.Provider provider) {
    builder(ItemTags.TRIMMABLE_ARMOR)
        .add(MainRegistry.TIN_HELMET.key())
        .add(MainRegistry.TIN_CHESTPLATE.key())
        .add(MainRegistry.TIN_LEGGINGS.key())
        .add(MainRegistry.TIN_BOOTS.key())
        .add(MainRegistry.BRONZE_HELMET.key())
        .add(MainRegistry.BRONZE_CHESTPLATE.key())
        .add(MainRegistry.BRONZE_LEGGINGS.key())
        .add(MainRegistry.BRONZE_BOOTS.key());
  }
}
