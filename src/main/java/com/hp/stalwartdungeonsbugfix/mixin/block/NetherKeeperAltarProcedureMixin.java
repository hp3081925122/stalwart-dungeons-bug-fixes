package com.hp.stalwartdungeonsbugfix.mixin.block;

import com.hp.stalwartdungeonsbugfix.block.SdBlockLogicHelper;
import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModEntities;
import net.mcreator.stalwartdungeons.procedures.NetherKeeperAltarOnBlockRightClickedProcedure;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 修复下界守卫祭坛：仅服务端执行且创造模式不消耗下界之星，智能识别悬挂祭坛/地面祭坛以避免 2.3 格高的 Boss 卡进天花板或地板窒息。
 */
@Mixin(value = NetherKeeperAltarOnBlockRightClickedProcedure.class, remap = false)
public abstract class NetherKeeperAltarProcedureMixin {
    @Inject(method = "execute", at = @At("HEAD"), cancellable = true)
    private static void sd$safeNetherKeeperAltar(LevelAccessor world, double x, double y, double z, Entity entity, CallbackInfo ci) {
        if (!SdBugfixConfig.isFixBossAndMobProceduresEnabled()) {
            return;
        }
        SdBlockLogicHelper.handleBossAltarUse(world, x, y, z, entity, StalwartDungeonsModEntities.NETHER_KEEPER.get(), -1.0, true);
        ci.cancel();
    }
}
