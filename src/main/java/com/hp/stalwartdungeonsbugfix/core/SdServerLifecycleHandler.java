package com.hp.stalwartdungeonsbugfix.core;

import com.hp.stalwartdungeonsbugfix.block.SdBlockLogicHelper;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * 服务端生命周期监听：在存档关闭/服务端停止时清理延迟队列与传送器冷却缓存。
 */
public final class SdServerLifecycleHandler {
    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        SdWorkQueueManager.clear();
        SdBlockLogicHelper.clearCooldowns();
    }
}
