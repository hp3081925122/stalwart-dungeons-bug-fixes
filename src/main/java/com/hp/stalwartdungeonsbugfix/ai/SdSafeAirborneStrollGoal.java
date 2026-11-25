package com.hp.stalwartdungeonsbugfix.ai;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

/**
 * 安全空中随机巡逻 Goal：仅在空气方块内挑选巡逻目标，防止飞行怪盲选地下实心岩层发起必败三维 A* 寻路导致卡顿。
 */
public final class SdSafeAirborneStrollGoal extends RandomStrollGoal {
    private static final int MAX_ATTEMPTS = 8;
    private static final int HORIZONTAL_RANGE = 16;
    private static final int VERTICAL_RANGE = 10;

    public SdSafeAirborneStrollGoal(PathfinderMob mob, double speedModifier, int interval) {
        super(mob, speedModifier, interval);
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        Level level = this.mob.level();
        RandomSource random = this.mob.getRandom();
        double baseX = this.mob.getX();
        double baseY = this.mob.getY();
        double baseZ = this.mob.getZ();
        int minY = level.getMinBuildHeight() + 2;
        int maxY = level.getMaxBuildHeight() - 3;

        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            double tx = baseX + (random.nextDouble() * 2.0 - 1.0) * HORIZONTAL_RANGE;
            double ty = baseY + (random.nextDouble() * 2.0 - 1.0) * VERTICAL_RANGE;
            double tz = baseZ + (random.nextDouble() * 2.0 - 1.0) * HORIZONTAL_RANGE;
            int iy = (int) Math.floor(ty);
            if (iy < minY || iy > maxY) {
                continue;
            }
            cursor.set(Math.floor(tx), iy, Math.floor(tz));
            if (level.isLoaded(cursor) && level.isEmptyBlock(cursor) && level.isEmptyBlock(cursor.above())) {
                return new Vec3(tx, ty, tz);
            }
        }
        return null;
    }
}
