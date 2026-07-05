package com.khazoda.bronze.loot;

import com.khazoda.bronze.Constants;
import com.khazoda.bronze.item.TrowelConversions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.AddServerReloadListenersEvent;

@EventBusSubscriber(modid = Constants.MOD_ID)
public final class TrowelConversionReloadNeoForge {
  private TrowelConversionReloadNeoForge() {
  }

  @SuppressWarnings("removal")
  @SubscribeEvent
  public static void onAddServerReloadListeners(AddServerReloadListenersEvent event) {
    event.addListener(TrowelConversions.RELOAD_LISTENER_ID, new TrowelConversions.ReloadListener(event.getRegistryAccess()));
  }
}
