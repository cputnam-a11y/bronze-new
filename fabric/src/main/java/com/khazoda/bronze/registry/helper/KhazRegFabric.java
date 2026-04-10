package com.khazoda.bronze.registry.helper;

import com.khazoda.bronze.registry.MainRegistry;

/**
 * Fabric entrypoint helper for the {@link KhazReg} registry system.
 */
public final class KhazRegFabric {
  private KhazRegFabric() {
  }

  /**
   * Call KhazRegFabric.init() in your Fabric mod constructor's onInitialize() method immediately after YourModCommon.init().
   */
  public static void init() {
    MainRegistry.init();
    MainRegistry.reg.registerAllStatic();
  }
}
