package com.hp.stalwartdungeonsbugfix.mixin.block;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.block.AwfulGhastAltarBlock;
import net.mcreator.stalwartdungeons.block.ChiseledEndStoneBricksBlock;
import net.mcreator.stalwartdungeons.block.ChorundumOreBlock;
import net.mcreator.stalwartdungeons.block.CrackedEndstoneBricksBlock;
import net.mcreator.stalwartdungeons.block.IgniterBlock;
import net.mcreator.stalwartdungeons.block.NetherKeeperAltarBlock;
import net.mcreator.stalwartdungeons.block.ObsidianStrikerBlock;
import net.mcreator.stalwartdungeons.block.PurpurBricksBlock;
import net.mcreator.stalwartdungeons.block.SheltererAltarBlock;
import net.mcreator.stalwartdungeons.block.SoulBricksBlock;
import net.mcreator.stalwartdungeons.block.SoulMagmaBricksBlock;
import net.mcreator.stalwartdungeons.block.SoulTilesBlock;
import net.mcreator.stalwartdungeons.block.SporeVaporizerBlock;
import net.mcreator.stalwartdungeons.block.TransientBlockCloseBlock;
import net.mcreator.stalwartdungeons.block.TungstenOreBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * 修复 15 个实心不透明整块方块错误覆写 propagatesSkylightDown=true 与 getLightBlock=0 的问题，
 * 避免日光与方块光穿透地牢墙壁和屋顶，同时降低大型地牢生成时的光照引擎传播开销。
 */
@Mixin({
        AwfulGhastAltarBlock.class,
        ChiseledEndStoneBricksBlock.class,
        ChorundumOreBlock.class,
        CrackedEndstoneBricksBlock.class,
        IgniterBlock.class,
        NetherKeeperAltarBlock.class,
        ObsidianStrikerBlock.class,
        PurpurBricksBlock.class,
        SheltererAltarBlock.class,
        SoulBricksBlock.class,
        SoulMagmaBricksBlock.class,
        SoulTilesBlock.class,
        SporeVaporizerBlock.class,
        TransientBlockCloseBlock.class,
        TungstenOreBlock.class
})
public abstract class SolidBlockLightFixMixin {
    @Inject(method = "propagatesSkylightDown", at = @At("HEAD"), cancellable = true)
    private void sd$blockSkylight(BlockState state, BlockGetter reader, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (SdBugfixConfig.isFixSolidBlockLightBlockingEnabled()) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getLightBlock", at = @At("HEAD"), cancellable = true)
    private void sd$fullLightOpacity(BlockState state, BlockGetter worldIn, BlockPos pos, CallbackInfoReturnable<Integer> cir) {
        if (SdBugfixConfig.isFixSolidBlockLightBlockingEnabled()) {
            cir.setReturnValue(worldIn.getMaxLightLevel());
        }
    }
}
