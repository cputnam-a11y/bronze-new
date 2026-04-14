package com.khazoda.baseline;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class NeoForgeConfigSync {
  private static KhazConfig serverSyncedConfig;
  private static KhazConfigSync sync;
  private static boolean serverGameListenersRegistered;
  private static boolean clientGameListenersRegistered;

  private NeoForgeConfigSync() {
  }

  public static void registerServerConfigSync(KhazConfig config, KhazConfigSync sync) {
    serverSyncedConfig = config;
    NeoForgeConfigSync.sync = sync;
    registerServerLoginSyncListener();
    registerClientDisconnectReloadListener(config);
  }

  private static void registerServerLoginSyncListener() {
    if (!serverGameListenersRegistered) {
      serverGameListenersRegistered = true;
      NeoForge.EVENT_BUS.addListener(NeoForgeConfigSync::onPlayerLoggedIn);
    }
  }

  private static void registerClientDisconnectReloadListener(KhazConfig config) {
    if (!clientGameListenersRegistered && FMLLoader.getCurrent().getDist().name().equals("CLIENT")) {
      clientGameListenersRegistered = true;
      NeoForgeConfigSyncClient.registerDisconnectReloadListener(config);
    }
  }

  public static void registerPayloadHandlers(IEventBus modEventBus, KhazConfigSync sync) {
    modEventBus.addListener((RegisterPayloadHandlersEvent event) -> onRegisterPayloadHandlers(event, sync));
  }

  private static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event, KhazConfigSync sync) {
    event.registrar("1").playToClient(sync.type(), sync.codec(), (payload, context) -> {
      if (serverSyncedConfig != null) {
        serverSyncedConfig.applyServerSyncedValues(payload.serverValues());
      }
    });
  }

  private static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    if (serverSyncedConfig != null && sync != null && event.getEntity() instanceof ServerPlayer player) {
      PacketDistributor.sendToPlayer(player, sync.payload(serverSyncedConfig.createServerSyncSnapshot()));
    }
  }
}
