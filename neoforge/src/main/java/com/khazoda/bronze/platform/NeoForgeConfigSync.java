package com.khazoda.bronze.platform;

import com.khazoda.bronze.config.KhazConfig;
import com.khazoda.bronze.config.ServerConfigSyncPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public final class NeoForgeConfigSync {
  private static KhazConfig serverSyncedConfig;
  private static boolean serverGameListenersRegistered;
  private static boolean clientGameListenersRegistered;

  private NeoForgeConfigSync() {
  }

  public static void registerServerConfigSync(KhazConfig config) {
    serverSyncedConfig = config;
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

  public static void registerPayloadHandlers(IEventBus modEventBus) {
    modEventBus.addListener(NeoForgeConfigSync::onRegisterPayloadHandlers);
  }

  private static void onRegisterPayloadHandlers(RegisterPayloadHandlersEvent event) {
    event.registrar("1").playToClient(ServerConfigSyncPayload.TYPE, ServerConfigSyncPayload.CODEC, (payload, context) -> {
      if (serverSyncedConfig != null) {
        serverSyncedConfig.applyServerSyncedValues(payload.serverValues());
      }
    });
  }

  private static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
    if (serverSyncedConfig != null && event.getEntity() instanceof ServerPlayer player) {
      PacketDistributor.sendToPlayer(player, new ServerConfigSyncPayload(serverSyncedConfig.createServerSyncSnapshot()));
    }
  }
}
