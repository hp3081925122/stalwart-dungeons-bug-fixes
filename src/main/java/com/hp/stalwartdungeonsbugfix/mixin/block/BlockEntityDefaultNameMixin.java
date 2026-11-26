package com.hp.stalwartdungeonsbugfix.mixin.block;

import net.mcreator.stalwartdungeons.block.entity.TeleporterBlockBlockEntity;
import net.mcreator.stalwartdungeons.block.entity.TransientBlockCloseBlockEntity;
import net.mcreator.stalwartdungeons.block.entity.TransientBlockOpenBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 将坚固地牢 3 个方块实体写死英文的 getDefaultName / getDisplayName 改为复用对应方块的原生翻译键。
 */
@Mixin({
        TeleporterBlockBlockEntity.class,
        TransientBlockCloseBlockEntity.class,
        TransientBlockOpenBlockEntity.class
})
public abstract class BlockEntityDefaultNameMixin extends RandomizableContainerBlockEntity {
    protected BlockEntityDefaultNameMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "getDefaultName", at = @At("HEAD"), cancellable = true)
    private void sd$localizedDefaultName(CallbackInfoReturnable<Component> cir) {
        cir.setReturnValue(this.getBlockState().getBlock().getName());
    }

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void sd$localizedDisplayName(CallbackInfoReturnable<Component> cir) {
        cir.setReturnValue(this.hasCustomName() ? this.getCustomName() : this.getBlockState().getBlock().getName());
    }
}
