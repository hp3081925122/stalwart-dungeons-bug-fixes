package com.hp.stalwartdungeonsbugfix.mixin.entity;

import com.hp.stalwartdungeonsbugfix.ai.SdSafeAirborneStrollGoal;
import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.entity.ReinforcedBlazeEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复强化烈焰人飞行 AI 卡顿：
 * 移除多余竞争的地面 RandomStrollGoal，将盲选实心墙体/地下的空中巡逻替换为安全空中巡逻（SdSafeAirborneStrollGoal）。
 */
@Mixin(ReinforcedBlazeEntity.class)
public abstract class ReinforcedBlazeEntityMixin extends Monster {
    protected ReinforcedBlazeEntityMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"), cancellable = true)
    private void sd$optimizeReinforcedBlazeGoals(CallbackInfo ci) {
        if (!SdBugfixConfig.isFixEntityCrashesAndAiEnabled()) {
            return;
        }
        super.registerGoals();
        this.targetSelector.addGoal(1, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        this.targetSelector.addGoal(2, new HurtByTargetGoal(this).setAlertOthers());

        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 1.2, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
            }
        });
        this.goalSelector.addGoal(4, new SdSafeAirborneStrollGoal(this, 1.0, 20));
        this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
        ci.cancel();
    }
}
