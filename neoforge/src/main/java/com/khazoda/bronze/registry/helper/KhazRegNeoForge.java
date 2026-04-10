package com.khazoda.bronze.registry.helper;

import com.khazoda.bronze.registry.MainRegistry;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * NeoForge entrypoint helper for the {@link KhazReg} registry system.
 */
public final class KhazRegNeoForge {
  private KhazRegNeoForge() {
  }

  /**
   * Call KhazRegNeoForge.init(eventBus) in your NeoForge mod constructor immediately after YourModCommon.init().
   */
  public static void init(IEventBus eventBus) {
    MainRegistry.init();
    eventBus.addListener(KhazRegNeoForge::registerRegistries);
    eventBus.addListener(KhazRegNeoForge::verifyRegistriesRegistered);
  }

  private static void registerRegistries(RegisterEvent event) {
    MainRegistry.reg.registerNeoForge(event.getRegistry());
  }

  private static void verifyRegistriesRegistered(FMLCommonSetupEvent event) {
    MainRegistry.reg.verifyAllStaticRegistrations();
  }
}
