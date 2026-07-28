package com.npg418.jadedvdrift;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.BooleanValue ENABLE;
    public static final ForgeConfigSpec.DoubleValue SPEED_AMPLIFIER;
    public static final ForgeConfigSpec.BooleanValue COLOR_ENABLE;
    public static final ForgeConfigSpec.DoubleValue HUE_STEP;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("DVDBounce");
        ENABLE = builder
                .comment("Whether to allow Jade HUD to move around")
                .define("enable", true);
        SPEED_AMPLIFIER = builder
                .comment("DVD bounce effect speed multiplier (1.0 = normal speed)")
                .defineInRange("speedAmplifier", 1.0, 0.0, 20.0);
        builder.pop();

        builder.push("Color");
        COLOR_ENABLE = builder
                .comment("Whether to change Jade HUD color on bounce")
                .define("colorEnable", true);
        HUE_STEP = builder
                .comment("Hue shift per bounce (0.0 = no change, 1.0 = full cycle)")
                .defineInRange("hueStep", 0.16666667, 0.0, 1.0);
        builder.pop();

        SPEC = builder.build();
    }
}
