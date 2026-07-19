package com.survivalstaminamod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(SurvivalStaminaMod.MOD_ID)
public class SurvivalStaminaMod {

    public static final String MOD_ID = "survivalstaminamod";

    public SurvivalStaminaMod() {

        if (FMLEnvironment.dist.isClient()) {
            MinecraftForge.EVENT_BUS.register(new HudHandler());
            MinecraftForge.EVENT_BUS.register(new StaminaHudRenderer());
            MinecraftForge.EVENT_BUS.register(new Overlay());
            MinecraftForge.EVENT_BUS.register(new HealingOverlayRenderer());
            MinecraftForge.EVENT_BUS.register(new CrawlingOverlayRenderer());
            MinecraftForge.EVENT_BUS.register(new OxygenVignetteOverlay());
        }

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StaminaConfig.SPEC, "survivalstaminamod.toml");
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::commonSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        NetworkHandler.register();
        MinecraftForge.EVENT_BUS.register(StaminaConfig.class);
    }
}