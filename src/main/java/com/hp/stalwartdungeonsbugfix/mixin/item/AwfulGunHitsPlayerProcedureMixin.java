package com.hp.stalwartdungeonsbugfix.mixin.item;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.item.SdItemLogicHelper;
import net.mcreator.stalwartdungeons.procedures.AwfulGunBulletHitsPlayerProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复可怕火枪命中玩家：仅服务端放火消除幽灵火，空手时不给 Items.AIR 加冷却。
 */
@Mixin(value = AwfulGunBulletHitsPlayerProcedure.class, remap = false)
public abstract class AwfulGunHitsPlayerProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeAwfulGunHitPlayer(LevelAccessor world, double x, double y, double z, Entity entity, Entity immediatesourceentity, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixItemProceduresAndMechanicsEnabled()) {
            return;
        }
        SdItemLogicHelper.handleAwfulGunHitsPlayer(world, x, y, z, entity, immediatesourceentity);
        ci.cancel();
    }
}
