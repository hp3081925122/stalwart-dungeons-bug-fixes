package com.hp.stalwartdungeonsbugfix.mixin.entity;

import com.hp.stalwartdungeonsbugfix.ai.SdSafeAirborneStrollGoal;
import com.hp.stalwartdungeonsbugfix.ai.SdSafeAwfulGhastChargeGoal;
import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.entity.AwfulGhastEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.GoalSelector;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 修复可怕恶魂的两大 AI 缺陷：
 * 1. 将存在 getTarget() 空指针崩溃的近身冲锋 Goal（AwfulGhastEntity$1）替换为安全版 SdSafeAwfulGhastChargeGoal；
 * 2. 将盲选实心地下岩层的空中漫步 Goal 替换为安全空中巡逻 SdSafeAirborneStrollGoal。
 */
@Mixin(AwfulGhastEntity.class)
public abstract class AwfulGhastEntityMixin {
    @Redirect(
            method = "registerGoals",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/goal/GoalSelector;addGoal(ILnet/minecraft/world/entity/ai/goal/Goal;)V")
    )
    private void sd$replaceUnsafeGhastGoals(GoalSelector selector, int priority, Goal goal) {
        if (SdBugfixConfig.isFixEntityCrashesAndAiEnabled()) {
            if (priority == 1 && !(goal instanceof RangedAttackGoal)) {
                selector.addGoal(priority, new SdSafeAwfulGhastChargeGoal((AwfulGhastEntity) (Object) this));
                return;
            }
            if (goal instanceof RandomStrollGoal) {
                selector.addGoal(priority, new SdSafeAirborneStrollGoal((PathfinderMob) (Object) this, 1.0, 20));
                return;
            }
        }
        selector.addGoal(priority, goal);
    }
}
