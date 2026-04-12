package com.khazoda.bronze.platform;

import com.khazoda.bronze.config.KhazConfig;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.common.NeoForge;

final class NeoForgeConfigSyncClient {
  private NeoForgeConfigSyncClient() {
  }

  static void registerDisconnectReloadListener(KhazConfig config) {
    NeoForge.EVENT_BUS.addListener((ClientPlayerNetworkEvent.LoggingOut event) -> config.clearServerSyncedValuesAndReload());
  }
}
