package com.hp.stalwartdungeonsbugfix.mixin.core;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.core.SdWorkQueueManager;
import net.mcreator.stalwartdungeons.StalwartDungeonsMod;
import net.minecraftforge.event.TickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复 StalwartDungeonsMod 的服务端延迟队列：
 * 加入服务端线程校验、非正延迟不泄漏、单次遍历 O(1) 移除、异常隔离与跨存档清理。
 * 若检测到通用 MCreator Optimizer 存在，则由 SdBugfixMixinPlugin 自动跳过本 Mixin。
 */
@Mixin(value = StalwartDungeonsMod.class, remap = false)
public abstract class StalwartDungeonsModMixin {
    @Inject(method = "queueServerWork", at = @At("HEAD"), cancellable = true)
    private static void sd$safeQueueServerWork(int tick, Runnable action, CallbackInfo ci) {
        if (!SdBugfixConfig.isOptimizeWorkQueueEnabled()) {
            return;
        }
        SdWorkQueueManager.enqueue(tick, action);
        ci.cancel();
    }

    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void sd$safeTick(TickEvent.ServerTickEvent event, CallbackInfo ci) {
        if (!SdBugfixConfig.isOptimizeWorkQueueEnabled()) {
            return;
        }
        if (event.phase == TickEvent.Phase.END) {
            SdWorkQueueManager.tickEnd();
        }
        ci.cancel();
    }
}
