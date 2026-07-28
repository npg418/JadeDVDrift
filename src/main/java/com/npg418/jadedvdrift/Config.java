package com.npg418.jadedvdrift;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue ENABLE;
    public static final ForgeConfigSpec.DoubleValue SPEED_AMPLIFIER;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("DVDBounce");
        ENABLE = builder
                .comment("Whether to allow Jade HUD to move around")
                .define("enable", true);
        SPEED_AMPLIFIER = builder
                .comment("DVD bounce effect speed multiplier (1.0 = normal speed)")
                .defineInRange("speedAmplifier", 1.0, 0.0, 20.0);


        SPEC = builder.build();
    }
}
