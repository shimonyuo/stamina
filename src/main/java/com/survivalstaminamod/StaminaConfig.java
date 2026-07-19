package com.survivalstaminamod;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = SurvivalStaminaMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class StaminaConfig {

    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> ENABLED_BLOCKS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("step_height");

        ENABLED_BLOCKS = builder
                .comment("プレイヤーが登れるようになるブロックのリスト")
                .comment("例: minecraft:grass_block, minecraft:dirt など")
                .defineList("enabledBlocks",
                        List.of(
                                "minecraft:grass_block",
                                "minecraft:dirt",
                                "minecraft:coarse_dirt",
                                "minecraft:rooted_dirt",
                                "minecraft:podzol",
                                "minecraft:mycelium",
                                "minecraft:dirt_path",
                                "minecraft:stone",
                                "minecraft:sand",
                                "minecraft:red_sand",
                                "minecraft:gravel",
                                "minecraft:snow_block",
                                "minecraft:powder_snow",
                                "minecraft:ice",
                                "minecraft:packed_ice",
                                "minecraft:blue_ice",
                                "minecraft:mud",
                                "minecraft:muddy_mangrove_roots",
                                "minecraft:netherrack",
                                "minecraft:end_stone",
                                "minecraft:soul_sand",
                                "minecraft:soul_soil"
                        ),
                        obj -> obj instanceof String);

        builder.pop();

        SPEC = builder.build();
    }

    public static boolean canStepOn(String blockId) {
        return ENABLED_BLOCKS.get().contains(blockId);
    }

    public static boolean canStepOn(BlockState state) {
        if (state == null) return false;
        String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        return canStepOn(blockId);
    }
}