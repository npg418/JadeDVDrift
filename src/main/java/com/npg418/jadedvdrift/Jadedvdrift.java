package com.npg418.jadedvdrift;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Jadedvdrift.MODID)
public class Jadedvdrift {
    public static final String MODID = "jadedvdrift";

    public Jadedvdrift(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.CLIENT, Config.SPEC);
    }
}
