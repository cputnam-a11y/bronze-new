package com.khazoda.bronze.datagen;

import com.khazoda.bronze.Constants;
import com.khazoda.bronze.datagen.advancements.BronzeModAdvancementsProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import org.jetbrains.annotations.Nullable;

public class BronzeModDataGenerator implements DataGeneratorEntrypoint {
  @Override
  public void onInitializeDataGenerator(FabricDataGenerator generator) {
    FabricDataGenerator.Pack pack = generator.createPack();
    pack.addProvider(BronzeModItemTagProvider::new);
    pack.addProvider(BronzeModModelProvider::new);
    pack.addProvider(BronzeModAdvancementsProvider::new);
    pack.addProvider(BronzeModRecipeProvider::new);
  }

  @Override
  public @Nullable String getEffectiveModId() {
    return Constants.MOD_ID;
  }
}
