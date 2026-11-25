package com.hp.stalwartdungeonsbugfix.mixin.block;

import com.hp.stalwartdungeonsbugfix.block.SdBlockMapColorHelper;
import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 在坚固地牢方块注册构造阶段一次性为全部 39 个缺失 mapColor 的方块补齐材质对应的原版 MapColor，
 * 零运行期额外开销，彻底修复 Xaero 世界地图、小地图与原版地图看不见或穿透坚固地牢方块的问题。
 */
@Mixin(BlockBehaviour.class)
public abstract class BlockMapColorFixMixin {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void sd$assignMissingBlockMapColor(BlockBehaviour.Properties properties, CallbackInfo ci) {
        if (properties == null || !SdBugfixConfig.isFixBlockMapColorsEnabled()) {
            return;
        }
        Class<?> selfClass = this.getClass();
        if (!"net.mcreator.stalwartdungeons.block".equals(selfClass.getPackageName())) {
            return;
        }
        MapColor mapped = SdBlockMapColorHelper.resolveMapColor(selfClass.getSimpleName());
        if (mapped != null) {
            properties.mapColor(mapped);
        }
    }
}
