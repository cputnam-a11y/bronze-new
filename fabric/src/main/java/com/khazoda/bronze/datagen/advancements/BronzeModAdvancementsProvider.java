package com.khazoda.bronze.datagen.advancements;

import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public class BronzeModAdvancementsProvider extends FabricAdvancementProvider {
  public BronzeModAdvancementsProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registryLookup) {
    super(output, registryLookup);
  }

  @Override
  public void generateAdvancement(HolderLookup.Provider provider, Consumer<AdvancementHolder> consumer) {
    BronzeAdvancements.generate(consumer);
  }
}
