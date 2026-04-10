package com.khazoda.bronze.loot;

import com.khazoda.bronze.registry.LootTables;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;

public final class LootTableModificationFabric {
  private LootTableModificationFabric() {
  }

  public static void init() {
    LootTableEvents.MODIFY.register((id, tableBuilder, source, registryLookup) -> {
      if (!source.isBuiltin()) return;
      LootTables.modifyLootTable(id.identifier(), tableBuilder);
    });
  }
}
