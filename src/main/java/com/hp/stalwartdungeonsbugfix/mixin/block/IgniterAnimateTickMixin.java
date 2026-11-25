package com.hp.stalwartdungeonsbugfix.mixin.block;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.block.IgniterBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 为点燃器补充原版标准的客户端 animateTick 本地灵魂火焰粒子，显式挂钩父类 Block.animateTick 以确保 SRG 映射完整。
 */
@Mixin(Block.class)
public abstract class IgniterAnimateTickMixin {
    @Inject(method = "animateTick", at = @At("HEAD"))
    private void sd$igniterClientParticles(BlockState state, Level level, BlockPos pos, RandomSource random, CallbackInfo ci) {
        if ((Object) this instanceof IgniterBlock && SdBugfixConfig.isOptimizeIgniterClientParticlesEnabled()) {
            if (random.nextInt(2) == 0) {
                double px = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
                double py = pos.getY() + 1.15 + random.nextDouble() * 0.1;
                double pz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.2;
                level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, px, py, pz, 0.0, 0.01, 0.0);
            }
        }
    }
}
