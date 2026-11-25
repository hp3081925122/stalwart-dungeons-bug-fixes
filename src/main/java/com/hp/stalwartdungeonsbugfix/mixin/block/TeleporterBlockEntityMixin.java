package com.hp.stalwartdungeonsbugfix.mixin.block;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.block.entity.TeleporterBlockBlockEntity;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModItems;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复传送器方块实体允许漏斗/管道塞入任意杂物的问题，与 GUI 槽位限制一致仅接受传送珍珠。
 */
@Mixin(TeleporterBlockBlockEntity.class)
public abstract class TeleporterBlockEntityMixin {
    @Inject(method = "canPlaceItem", at = @At("HEAD"), cancellable = true)
    private void sd$onlyAllowTeleporterPearl(int index, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (!SdBugfixConfig.isFixTrapBlocksAndTeleporterEnabled()) {
            return;
        }
        cir.setReturnValue(index == 0 && stack != null && stack.is(StalwartDungeonsModItems.TELEPORTER_PEARL.get()));
    }
}
