package org.excellent.common.impl.waveycapes.config;


import org.excellent.common.impl.waveycapes.enums.CapeMovement;
import org.excellent.common.impl.waveycapes.enums.CapeStyle;
import org.excellent.common.impl.waveycapes.enums.WindMode;

public class Config {
    public WindMode windMode = WindMode.NONE;
    public CapeStyle capeStyle = CapeStyle.SMOOTH;
    public CapeMovement capeMovement = CapeMovement.BASIC_SIMULATION;
    public int gravity = 25;
    public int heightMul = 6;
    public int straveMul = 2;
}
