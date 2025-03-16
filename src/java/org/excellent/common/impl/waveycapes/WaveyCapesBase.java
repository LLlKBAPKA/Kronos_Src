package org.excellent.common.impl.waveycapes;

import org.excellent.common.impl.waveycapes.config.Config;

public class WaveyCapesBase {
    public static WaveyCapesBase INSTANCE;
    public static Config config;

    public void init() {
        INSTANCE = this;
        config = new Config();
    }
}
