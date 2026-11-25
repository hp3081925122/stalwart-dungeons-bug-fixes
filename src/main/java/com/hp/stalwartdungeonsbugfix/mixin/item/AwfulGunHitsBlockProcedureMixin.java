package com.hp.stalwartdungeonsbugfix.mixin.item;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.item.SdItemLogicHelper;
import net.mcreator.stalwartdungeons.procedures.AwfulGunBulletHitsBlockProcedure;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复可怕火枪命中方块：仅服务端执行并避免空手时向 Items.AIR 添加冷却。
 */
@Mixin(value = AwfulGunBulletHitsBlockProcedure.class, remap = false)
public abstract class AwfulGunHitsBlockProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeAwfulGunHitBlock(LevelAccessor world, double x, double y, double z, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixItemProceduresAndMechanicsEnabled()) {
            return;
        }
        SdItemLogicHelper.handleAwfulGunHitsBlock(world, x, y, z);
        ci.cancel();
    }
}
