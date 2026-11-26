package com.hp.stalwartdungeonsbugfix.client;

import com.hp.stalwartdungeonsbugfix.Stalwartdungeonsbugfix;
import java.nio.file.Files;
import java.nio.file.Path;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AddPackFindersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.resource.PathPackResources;

/**
 * 注册始终启用且位于顶层（Pack.Position.TOP）的内置客户端资源覆写包，
 * 避免 Forge 1.20.1 mod_resources 合并顺序导致 3 面自定义盾牌的举盾模型覆写被原模组反覆盖。
 */
@OnlyIn(Dist.CLIENT)
public final class SdClientPackRegister {
    private SdClientPackRegister() {
    }

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(SdClientPackRegister::onAddPackFinders);
    }

    private static void onAddPackFinders(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.CLIENT_RESOURCES) {
            return;
        }
        var modFile = ModList.get().getModFileById(Stalwartdungeonsbugfix.MOD_ID);
        if (modFile == null) {
            return;
        }
        Path resourcePath = modFile.getFile().findResource("resourcepacks/sd_client_overrides");
        if (!Files.exists(resourcePath)) {
            Stalwartdungeonsbugfix.LOGGER.warn("[StalwartDungeonsBugFix] Built-in client override pack not found: {}", resourcePath);
            return;
        }
        event.addRepositorySource(consumer -> {
            Pack pack = Pack.readMetaAndCreate(
                    "stalwartdungeonsbugfix_client_overrides",
                    Component.literal("Stalwart Dungeons Bug Fixes Overrides"),
                    true,
                    id -> new PathPackResources(id, true, resourcePath),
                    PackType.CLIENT_RESOURCES,
                    Pack.Position.TOP,
                    PackSource.BUILT_IN
            );
            if (pack != null) {
                consumer.accept(pack);
            }
        });
    }
}
