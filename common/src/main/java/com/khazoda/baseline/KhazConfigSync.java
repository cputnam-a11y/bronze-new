package com.khazoda.baseline;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.Map;
import java.util.Objects;

public final class KhazConfigSync {
  private final CustomPacketPayload.Type<ServerConfigSyncPayload> type;
  private final StreamCodec<RegistryFriendlyByteBuf, ServerConfigSyncPayload> codec;

  private KhazConfigSync(Identifier payloadId) {
    this.type = new CustomPacketPayload.Type<>(Objects.requireNonNull(payloadId, "payloadId"));
    this.codec = CustomPacketPayload.codec(ServerConfigSyncPayload::write, buffer -> ServerConfigSyncPayload.read(this, buffer));
  }

  public static KhazConfigSync create(Identifier payloadId) {
    return new KhazConfigSync(payloadId);
  }

  public CustomPacketPayload.Type<ServerConfigSyncPayload> type() {
    return type;
  }

  public StreamCodec<RegistryFriendlyByteBuf, ServerConfigSyncPayload> codec() {
    return codec;
  }

  public ServerConfigSyncPayload payload(Map<String, String> serverValues) {
    return new ServerConfigSyncPayload(this, serverValues);
  }
}
