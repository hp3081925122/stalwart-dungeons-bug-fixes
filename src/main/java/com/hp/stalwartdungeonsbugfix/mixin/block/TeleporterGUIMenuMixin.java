package com.hp.stalwartdungeonsbugfix.mixin.block;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModBlocks;
import net.mcreator.stalwartdungeons.world.inventory.TeleporterGUIMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复传送器容器界面：
 * 1. stillValid 不再无条件返回 true，校验方块仍为传送器、维度一致且距离 <= 8 格（64 格平方）；
 * 2. 修正 moveItemStackTo 中 slot.mayPlace 错误检查槽内旧物品的问题，改为检查待移入物品。
 */
@Mixin(TeleporterGUIMenu.class)
public abstract class TeleporterGUIMenuMixin {
    @Shadow(remap = false)
    @Final
    public Level world;

    @Shadow(remap = false)
    public int x;

    @Shadow(remap = false)
    public int y;

    @Shadow(remap = false)
    public int z;

    @Shadow(remap = false)
    private boolean bound;

    @Inject(method = "stillValid", at = @At("HEAD"), cancellable = true)
    private void sd$validateTeleporterContainer(Player player, CallbackInfoReturnable<Boolean> cir) {
        if (!SdBugfixConfig.isFixTrapBlocksAndTeleporterEnabled()) {
            return;
        }
        if (player == null || player.isRemoved() || !this.bound || this.world == null || player.level() != this.world) {
            cir.setReturnValue(false);
            return;
        }
        BlockPos pos = new BlockPos(this.x, this.y, this.z);
        boolean valid = this.world.isLoaded(pos)
                && this.world.getBlockState(pos).is(StalwartDungeonsModBlocks.TELEPORTER.get())
                && player.distanceToSqr(this.x + 0.5, this.y + 0.5, this.z + 0.5) <= 64.0;
        cir.setReturnValue(valid);
    }

    @Redirect(
            method = "moveItemStackTo",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;mayPlace(Lnet/minecraft/world/item/ItemStack;)Z", ordinal = 0)
    )
    private boolean sd$checkIncomingStackMayPlace(Slot slot, ItemStack existingStack, ItemStack incomingStack, int startIndex, int endIndex, boolean reverseDirection) {
        if (!SdBugfixConfig.isFixTrapBlocksAndTeleporterEnabled()) {
            return slot.mayPlace(existingStack);
        }
        return slot.mayPlace(incomingStack);
    }
}
