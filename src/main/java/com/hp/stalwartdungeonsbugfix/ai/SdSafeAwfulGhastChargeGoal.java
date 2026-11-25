package com.hp.stalwartdungeonsbugfix.ai;

import net.mcreator.stalwartdungeons.entity.AwfulGhastEntity;
import net.mcreator.stalwartdungeons.procedures.AwfulGhastFrontAttackProcedure;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

/**
 * 安全版可怕恶魂近身冲锋 Goal：
 * 在 canUse / canContinueToUse / start / tick 全路径校验目标非空且存活，彻底消除原版 AwfulGhastEntity$1 的 Ticking Entity 空指针崩溃。
 */
public final class SdSafeAwfulGhastChargeGoal extends Goal {
    private final AwfulGhastEntity ghast;

    public SdSafeAwfulGhastChargeGoal(AwfulGhastEntity ghast) {
        this.ghast = ghast;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.ghast.getTarget();
        return target != null
                && target.isAlive()
                && !this.ghast.getMoveControl().hasWanted()
                && AwfulGhastFrontAttackProcedure.execute(this.ghast);
    }

    @Override
    public boolean canContinueToUse() {
        LivingEntity target = this.ghast.getTarget();
        return this.ghast.getMoveControl().hasWanted() && target != null && target.isAlive();
    }

    @Override
    public void start() {
        LivingEntity target = this.ghast.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        Vec3 eyePos = target.getEyePosition(1.0F);
        this.ghast.getMoveControl().setWantedPosition(eyePos.x, eyePos.y, eyePos.z, 2.2);
    }

    @Override
    public void tick() {
        LivingEntity target = this.ghast.getTarget();
        if (target == null || !target.isAlive()) {
            return;
        }
        if (this.ghast.getBoundingBox().intersects(target.getBoundingBox())) {
            this.ghast.doHurtTarget(target);
        } else {
            double distSqr = this.ghast.distanceToSqr(target);
            if (distSqr < 32.0) {
                Vec3 eyePos = target.getEyePosition(1.0F);
                this.ghast.getMoveControl().setWantedPosition(eyePos.x, eyePos.y, eyePos.z, 2.2);
            }
        }
    }
}
