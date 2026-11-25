package com.hp.stalwartdungeonsbugfix.mixin.entity;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.entity.NetherKeeperEntity;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.FollowOwnerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复下界守卫 AI 与繁殖缺陷：
 * 1. 将误加进 goalSelector 的 OwnerHurtByTargetGoal 移回 targetSelector；
 * 2. 驯服后不再通过 NearestAttackableTargetGoal(Player) 攻击主人与其他玩家；
 * 3. 合并重复冲突的两个 MeleeAttackGoal；
 * 4. 修复 getBreedOffspring 未判空且在设置坐标前就按 (0,0,0) 调用 finalizeSpawn 的问题。
 */
@Mixin(NetherKeeperEntity.class)
public abstract class NetherKeeperEntityMixin extends TamableAnimal {
    protected NetherKeeperEntityMixin(EntityType<? extends TamableAnimal> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "registerGoals", at = @At("HEAD"), cancellable = true)
    private void sd$fixNetherKeeperGoals(CallbackInfo ci) {
        if (!SdBugfixConfig.isFixEntityCrashesAndAiEnabled()) {
            return;
        }
        super.registerGoals();
        this.goalSelector.addGoal(1, new FollowOwnerGoal(this, 0.4, 10.0F, 2.0F, false));
        this.goalSelector.addGoal(2, new MeleeAttackGoal(this, 0.65, false) {
            @Override
            protected double getAttackReachSqr(LivingEntity entity) {
                return Math.max(16.0, this.mob.getBbWidth() * this.mob.getBbWidth() + entity.getBbWidth());
            }
        });
        this.goalSelector.addGoal(3, new RandomStrollGoal(this, 0.3));
        this.goalSelector.addGoal(4, new RandomLookAroundGoal(this));

        this.targetSelector.addGoal(1, new OwnerHurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new OwnerHurtTargetGoal(this));
        this.targetSelector.addGoal(3, new HurtByTargetGoal(this).setAlertOthers());
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(
                this,
                Player.class,
                10,
                false,
                false,
                target -> !this.isTame()
        ));
        ci.cancel();
    }

    @Inject(method = "getBreedOffspring", at = @At("HEAD"), cancellable = true)
    private void sd$safeBreedOffspring(ServerLevel serverWorld, AgeableMob ageable, CallbackInfoReturnable<AgeableMob> cir) {
        if (!SdBugfixConfig.isFixEntityCrashesAndAiEnabled()) {
            return;
        }
        NetherKeeperEntity child = StalwartDungeonsModEntities.NETHER_KEEPER.get().create(serverWorld);
        if (child != null) {
            child.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
            child.finalizeSpawn(serverWorld, serverWorld.getCurrentDifficultyAt(this.blockPosition()), MobSpawnType.BREEDING, null, null);
        }
        cir.setReturnValue(child);
    }
}
