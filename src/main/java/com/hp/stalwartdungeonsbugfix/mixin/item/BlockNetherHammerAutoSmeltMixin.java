package com.hp.stalwartdungeonsbugfix.mixin.item;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.item.SdItemLogicHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

/**
 * 真正修复下界之锤（Nether Hammer）“烧灼并自动熔炼挖掘方块”的核心特性：
 * 在 Block.getDrops 返回阶段按原版熔炼配方转换掉落物，完整兼容时运、精准采集、创造模式与领地权限。
 */
@Mixin(Block.class)
public abstract class BlockNetherHammerAutoSmeltMixin {
    @Inject(
            method = "getDrops(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/entity/BlockEntity;Lnet/minecraft/world/entity/Entity;Lnet/minecraft/world/item/ItemStack;)Ljava/util/List;",
            at = @At("RETURN"),
            cancellable = true
    )
    private static void sd$applyNetherHammerAutoSmelt(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            @Nullable BlockEntity blockEntity,
            @Nullable Entity entity,
            ItemStack tool,
            CallbackInfoReturnable<List<ItemStack>> cir
    ) {
        if (!SdBugfixConfig.isFixItemProceduresAndMechanicsEnabled()) {
            return;
        }
        List<ItemStack> original = cir.getReturnValue();
        List<ItemStack> smelted = SdItemLogicHelper.smeltDropsWithNetherHammer(level, pos, entity, tool, original);
        if (smelted != original) {
            cir.setReturnValue(smelted);
        }
    }
}
