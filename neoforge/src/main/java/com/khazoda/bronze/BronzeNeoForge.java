package com.khazoda.bronze;

import com.khazoda.bronze.registry.MainRegistry;
import com.khazoda.bronze.registry.helper.KhazRegNeoForge;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@Mod(Constants.MOD_ID)
public class BronzeNeoForge {
  public BronzeNeoForge(IEventBus eventBus) {
    BronzeCommon.init();
    KhazRegNeoForge.init(eventBus);
    eventBus.addListener(this::onBuildCreativeModeTabContents);
  }

  private void onBuildCreativeModeTabContents(BuildCreativeModeTabContentsEvent event) {
    if (MainRegistry.BRONZE_TAB.key().equals(event.getTabKey())) {
      MainRegistry.addMainTabItems(event::accept);
    }
  }
}
