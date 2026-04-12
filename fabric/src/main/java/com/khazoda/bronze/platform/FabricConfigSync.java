package com.khazoda.bronze.platform;

import com.khazoda.bronze.config.KhazConfig;
import com.khazoda.bronze.config.ServerConfigSyncPayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class FabricConfigSync {
  private static KhazConfig serverSyncedConfig;
  private static boolean clientboundPayloadTypeRegistered;
  private static boolean serverGameListenersRegistered;

  private FabricConfigSync() {
  }

  public static void registerServerConfigSync(KhazConfig config) {
    serverSyncedConfig = config;
    registerClientboundPayloadType();
    registerServerJoinSyncListener();
  }

  private static void registerServerJoinSyncListener() {
    if (!serverGameListenersRegistered) {
      serverGameListenersRegistered = true;
      ServerPlayConnectionEvents.JOIN.register((listener, sender, server) -> {
        if (serverSyncedConfig != null && ServerPlayNetworking.canSend(listener, ServerConfigSyncPayload.TYPE)) {
          sender.sendPacket(new ServerConfigSyncPayload(serverSyncedConfig.createServerSyncSnapshot()));
        }
      });
    }
  }

  public static void registerClientboundPayloadType() {
    if (clientboundPayloadTypeRegistered) return;
    PayloadTypeRegistry.clientboundPlay().register(ServerConfigSyncPayload.TYPE, ServerConfigSyncPayload.CODEC);
    clientboundPayloadTypeRegistered = true;
  }
}
