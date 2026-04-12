package com.khazoda.bronze.config;

import com.khazoda.bronze.Constants;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public record ServerConfigSyncPayload(Map<String, String> serverValues) implements CustomPacketPayload {
  public static final Type<ServerConfigSyncPayload> TYPE = new Type<>(Constants.ID("config_sync"));
  public static final StreamCodec<RegistryFriendlyByteBuf, ServerConfigSyncPayload> CODEC = CustomPacketPayload.codec(ServerConfigSyncPayload::write, ServerConfigSyncPayload::read);

  public ServerConfigSyncPayload {
    serverValues = Collections.unmodifiableMap(new LinkedHashMap<>(serverValues));
  }

  private static ServerConfigSyncPayload read(RegistryFriendlyByteBuf buffer) {
    return new ServerConfigSyncPayload(buffer.readMap(LinkedHashMap::new, input -> input.readUtf(), input -> input.readUtf()));
  }

  private void write(RegistryFriendlyByteBuf buffer) {
    buffer.writeMap(serverValues, (output, value) -> output.writeUtf(value), (output, value) -> output.writeUtf(value));
  }

  @Override
  public Type<? extends CustomPacketPayload> type() {
    return TYPE;
  }
}
