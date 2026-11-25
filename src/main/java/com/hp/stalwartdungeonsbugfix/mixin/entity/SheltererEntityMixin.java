package com.hp.stalwartdungeonsbugfix.mixin.entity;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.entity.SheltererEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复庇护者死亡时 dropCustomDeathLoot 生成空物品实体（Blocks.AIR）的缺陷。
 */
@Mixin(SheltererEntity.class)
public abstract class SheltererEntityMixin extends Monster {
    protected SheltererEntityMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "dropCustomDeathLoot", at = @At("HEAD"), cancellable = true)
    private void sd$skipAirDrop(DamageSource source, int looting, boolean recentlyHitIn, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixEntityCrashesAndAiEnabled()) {
            return;
        }
        super.dropCustomDeathLoot(source, looting, recentlyHitIn);
        ci.cancel();
    }
}
