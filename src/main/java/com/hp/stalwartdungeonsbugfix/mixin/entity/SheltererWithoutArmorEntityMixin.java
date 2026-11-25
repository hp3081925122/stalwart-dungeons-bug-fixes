package com.hp.stalwartdungeonsbugfix.mixin.entity;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.entity.SheltererWithoutArmorEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
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
 * 修复无甲庇护者（二阶段 Boss）AI 优先级严重倒置的问题：
 * 原版 RandomStrollGoal 优先级为 1、MeleeAttackGoal 优先级为 2，导致 Boss 无视玩家四处游荡；
 * 修正为近战追击优先于随机游荡，并优先保证浮水目标。
 */
@Mixin(SheltererWithoutArmorEntity.class)
public abstract class SheltererWithoutArmorEntityMixin extends Monster {
    protected SheltererWithoutArmorEntityMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"), cancellable = true)
    private void sd$fixPhase2BossAiPriority(CallbackInfo ci) {
        if (!SdBugfixConfig.isFixEntityCrashesAndAiEnabled()) {
            return;
        }
        super.registerGoals();
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.2, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth();
            }
        });
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, false, false));
        ci.cancel();
    }
}
