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
                .comment("プレイヤーが登れるようになるブロックのリスト（リソースロケーション形式）")
                .comment("例: minecraft:grass_block, minecraft:dirt など")
                .defineList("enabledBlocks",
                        List.of(
                                // 草・土系（最も一般的）
                                "minecraft:grass_block",
                                "minecraft:dirt",
                                "minecraft:coarse_dirt",
                                "minecraft:rooted_dirt",        // 追加（マングローブなど）
                                "minecraft:podzol",             // 追加（巨大タイガなど）
                                "minecraft:mycelium",           // 追加（キノコ島）
                                "minecraft:dirt_path",          // 追加（村や自然生成）
                                "minecraft:stone",

                                // 砂系
                                "minecraft:sand",
                                "minecraft:red_sand",           // 追加（Badlands）
                                "minecraft:gravel",             // 追加（川岸や山岳）

                                // 雪・凍土系（雪の多いバイオームで広く生成）
                                "minecraft:snow_block",         // 追加（雪原の表面）
                                "minecraft:powder_snow",        // 追加（粉雪）
                                "minecraft:ice",                // 追加（凍った海や湖）
                                "minecraft:packed_ice",         // 追加（氷河バイオーム）
                                "minecraft:blue_ice",           // 追加（一部の氷河）

                                // 湿地・泥系
                                "minecraft:mud",                // 追加（沼地、マングローブ）
                                "minecraft:muddy_mangrove_roots", // 追加（マングローブ）

                                // その他自然表面
                                "minecraft:netherrack",         // Netherの基本表面
                                "minecraft:end_stone",          // Endの基本表面
                                "minecraft:soul_sand",          // Nether（Soul Sand Valley）
                                "minecraft:soul_soil"           // Nether（Soul Sand Valley）
                        ),
                        obj -> obj instanceof String);

        builder.pop();

        SPEC = builder.build();
    }

    /**
     * 指定したブロックIDが登れるかどうか
     */
    public static boolean canStepOn(String blockId) {
        return ENABLED_BLOCKS.get().contains(blockId);
    }

    /**
     * BlockStateから登れるかどうか判定
     */
    public static boolean canStepOn(BlockState state) {
        if (state == null) return false;
        String blockId = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        return canStepOn(blockId);
    }
}