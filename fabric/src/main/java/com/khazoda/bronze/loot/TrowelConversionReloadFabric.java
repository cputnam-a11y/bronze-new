package com.khazoda.bronze.loot;

import com.khazoda.bronze.item.TrowelConversions;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.PackType;

public final class TrowelConversionReloadFabric {
  private TrowelConversionReloadFabric() {
  }

  @SuppressWarnings("deprecation")
  public static void init() {
    ResourceManagerHelper.get(PackType.SERVER_DATA)
        .registerReloadListener(TrowelConversions.RELOAD_LISTENER_ID, FabricReloadListener::new);
  }

  private static final class FabricReloadListener extends TrowelConversions.ReloadListener implements IdentifiableResourceReloadListener {
    private FabricReloadListener(HolderLookup.Provider registries) {
      super(registries);
    }

    @Override
    public Identifier getFabricId() {
      return TrowelConversions.RELOAD_LISTENER_ID;
    }
  }
}
