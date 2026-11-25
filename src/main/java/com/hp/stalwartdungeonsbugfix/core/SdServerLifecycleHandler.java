package com.hp.stalwartdungeonsbugfix.core;

import com.hp.stalwartdungeonsbugfix.block.SdBlockLogicHelper;
import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.item.SdItemLogicHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.UUID;

/**
 * 服务端事件监听：
 * 1. 存档关闭/服务端停止时清理延迟队列与传送器冷却缓存；
 * 2. 保护雷击附魔召唤者与地面掉落物不被近战落雷误伤或烧毁。
 */
public final class SdServerLifecycleHandler {
    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        SdWorkQueueManager.clear();
        SdBlockLogicHelper.clearCooldowns();
    }

    @SubscribeEvent
    public void onEntityStruckByLightning(EntityStruckByLightningEvent event) {
        if (!SdBugfixConfig.isThunderStrikeProtectAttackerEnabled()) {
            return;
        }
        LightningBolt bolt = event.getLightning();
        if (bolt == null) {
            return;
        }
        CompoundTag data = bolt.getPersistentData();
        if (!data.getBoolean(SdItemLogicHelper.TAG_THUNDER_STRIKE_BOLT)) {
            return;
        }
        Entity victim = event.getEntity();
        if (victim instanceof ItemEntity) {
            event.setCanceled(true);
            return;
        }
        if (data.hasUUID(SdItemLogicHelper.TAG_THUNDER_STRIKE_OWNER)) {
            UUID ownerId = data.getUUID(SdItemLogicHelper.TAG_THUNDER_STRIKE_OWNER);
            if (ownerId.equals(victim.getUUID())) {
                event.setCanceled(true);
            }
        }
    }
}
