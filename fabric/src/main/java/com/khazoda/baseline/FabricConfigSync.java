package com.khazoda.baseline;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class FabricConfigSync {
  private static KhazConfig serverSyncedConfig;
  private static boolean clientboundPayloadTypeRegistered;
  private static boolean serverGameListenersRegistered;

  private FabricConfigSync() {
  }

  public static void registerServerConfigSync(KhazConfig config, KhazConfigSync sync) {
    serverSyncedConfig = config;
    registerClientboundPayloadType(sync);
    registerServerJoinSyncListener(sync);
  }

  private static void registerServerJoinSyncListener(KhazConfigSync sync) {
    if (!serverGameListenersRegistered) {
      serverGameListenersRegistered = true;
      ServerPlayConnectionEvents.JOIN.register((listener, sender, server) -> {
        if (serverSyncedConfig != null && ServerPlayNetworking.canSend(listener, sync.type())) {
          sender.sendPacket(sync.payload(serverSyncedConfig.createServerSyncSnapshot()));
        }
      });
    }
  }

  public static void registerClientboundPayloadType(KhazConfigSync sync) {
    if (clientboundPayloadTypeRegistered) return;
    PayloadTypeRegistry.clientboundPlay().register(sync.type(), sync.codec());
    clientboundPayloadTypeRegistered = true;
  }
}
