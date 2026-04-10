package com.khazoda.bronze;

import com.khazoda.bronze.config.KhazConfig;
import com.khazoda.bronze.platform.Services;

public final class BronzeCommon {
  public static final KhazConfig.Entry<Integer> SICKLE_HARVEST_RANGE = KhazConfig.integer("sickle_harvest_range", 4, 1, 12, "How far the sickle spreads when mowing or harvesting.");
  public static final KhazConfig CONFIG = KhazConfig.of(Constants.MOD_ID, SICKLE_HARVEST_RANGE);

  private BronzeCommon() {
  }

  public static void init() {
    CONFIG.load();
    if (Services.PLATFORM.isModLoaded(Constants.MOD_ID)) Constants.LOG.info("- Bronze Loaded -");
  }
}
