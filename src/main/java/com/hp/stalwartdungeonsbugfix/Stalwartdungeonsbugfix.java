package com.hp.stalwartdungeonsbugfix;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.core.SdServerLifecycleHandler;
import com.hp.stalwartdungeonsbugfix.world.SdWorldgenRegistry;
import com.mojang.logging.LogUtils;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

/**
 * 坚固地牢（Stalwart Dungeons）综合 Bug 修复与性能优化模组入口。
 */
@Mod(Stalwartdungeonsbugfix.MOD_ID)
public final class Stalwartdungeonsbugfix {
    public static final String MOD_ID = "stalwartdungeonsbugfix";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Stalwartdungeonsbugfix(FMLJavaModLoadingContext context) {
        SdWorldgenRegistry.register(context.getModEventBus());
        context.registerConfig(ModConfig.Type.COMMON, SdBugfixConfig.COMMON_SPEC, "stalwartdungeonsbugfix-common.toml");
        context.registerConfig(ModConfig.Type.CLIENT, SdBugfixConfig.CLIENT_SPEC, "stalwartdungeonsbugfix-client.toml");
        MinecraftForge.EVENT_BUS.register(new SdServerLifecycleHandler());
        LOGGER.info("Initialized Stalwart Dungeons Bug Fixes ({})", MOD_ID);
    }
}
