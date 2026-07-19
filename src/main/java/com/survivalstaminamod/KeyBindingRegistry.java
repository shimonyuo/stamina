package com.survivalstaminamod;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = SurvivalStaminaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyBindingRegistry {

    public static final KeyMapping TEST_CRAWL_KEY = new KeyMapping(
            "key.survivalstaminamod.testcrawl",
            GLFW.GLFW_KEY_C,
            "key.categories.survivalstaminamod"
    );

    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(TEST_CRAWL_KEY);
    }
}