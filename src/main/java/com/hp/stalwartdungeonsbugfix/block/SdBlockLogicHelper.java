package com.hp.stalwartdungeonsbugfix.block;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModBlocks;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModEntities;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * 方块交互、机关、传送器与祭坛逻辑修复助手。
 */
public final class SdBlockLogicHelper {
    private static final Map<UUID, Long> TELEPORTER_COOLDOWNS = new ConcurrentHashMap<>();

    private SdBlockLogicHelper() {
    }

    public static void clearCooldowns() {
        TELEPORTER_COOLDOWNS.clear();
    }

    /**
     * 修复传送器碰撞逻辑：
     * - 仅在服务端执行；
     * - 仅读取 1 次槽位物品且只读 NBT（绝不调用 getOrCreateTag() 污染空物品）；
     * - 校验珍珠已绑定坐标且目标区块已加载（避免同步强行加载远程未加载区块造成卡顿）；
     * - 为每个实体施加可配置冷却，避免潜行站在传送器上连续自伤或往返震荡。
     */
    public static void handleTeleporterCollision(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null || !(world instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!entity.isShiftKeyDown()) {
            return;
        }

        int cooldownTicks = SdBugfixConfig.getTeleporterCooldownTicks();
        long now = serverLevel.getGameTime();
        if (cooldownTicks > 0) {
            Long nextAllowed = TELEPORTER_COOLDOWNS.get(entity.getUUID());
            if (nextAllowed != null && now < nextAllowed) {
                return;
            }
        }

        BlockPos teleporterPos = BlockPos.containing(x, y, z);
        BlockEntity be = serverLevel.getBlockEntity(teleporterPos);
        if (be == null) {
            return;
        }

        AtomicReference<ItemStack> slotRef = new AtomicReference<>(ItemStack.EMPTY);
        be.getCapability(ForgeCapabilities.ITEM_HANDLER, null).ifPresent(handler -> {
            if (handler.getSlots() > 0) {
                slotRef.set(handler.getStackInSlot(0));
            }
        });

        ItemStack pearl = slotRef.get();
        if (pearl.isEmpty() || !pearl.is(StalwartDungeonsModItems.TELEPORTER_PEARL.get())) {
            return;
        }

        CompoundTag tag = pearl.getTag();
        if (tag == null || !tag.contains("posX") || !tag.contains("posY") || !tag.contains("posZ")) {
            return;
        }

        double targetX = tag.getDouble("posX");
        double targetY = tag.getDouble("posY");
        double targetZ = tag.getDouble("posZ");
        BlockPos targetPos = BlockPos.containing(targetX, targetY, targetZ);

        // 目标区块未加载时仅在玩家触发时允许安全传送（跳过远距离无谓强加载检查空气，避免卡服）
        boolean targetAreaSafe;
        if (serverLevel.isLoaded(targetPos)) {
            BlockState atTarget = serverLevel.getBlockState(targetPos);
            BlockState aboveTarget = serverLevel.getBlockState(targetPos.above());
            targetAreaSafe = (atTarget.isAir() || atTarget.getCollisionShape(serverLevel, targetPos).isEmpty())
                    && (aboveTarget.isAir() || aboveTarget.getCollisionShape(serverLevel, targetPos.above()).isEmpty());
        } else {
            // 未加载区块不允许非玩家实体触发跨区加载，玩家传送前仍执行目标点安全放行
            if (!(entity instanceof ServerPlayer)) {
                return;
            }
            targetAreaSafe = true;
        }

        if (!targetAreaSafe) {
            return;
        }

        if (cooldownTicks > 0) {
            TELEPORTER_COOLDOWNS.put(entity.getUUID(), now + cooldownTicks);
            if (TELEPORTER_COOLDOWNS.size() > 1024) {
                TELEPORTER_COOLDOWNS.entrySet().removeIf(entry -> entry.getValue() <= now);
            }
        }

        serverLevel.sendParticles(ParticleTypes.PORTAL, x + 0.5, y + 0.5, z + 0.5, 8, 0.25, 0.25, 0.25, 0.0);
        entity.teleportTo(targetX + 0.5, targetY, targetZ + 0.5);
        if (entity instanceof ServerPlayer serverPlayer) {
            serverPlayer.connection.teleport(targetX + 0.5, targetY, targetZ + 0.5, serverPlayer.getYRot(), serverPlayer.getXRot());
        }
        serverLevel.playSound(null, targetPos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
        serverLevel.sendParticles(ParticleTypes.PORTAL, targetX + 0.5, targetY + 0.5, targetZ + 0.5, 8, 0.25, 0.25, 0.25, 0.0);

        if (!(entity instanceof Player player && player.isCreative())) {
            entity.hurt(new DamageSource(serverLevel.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.MAGIC)), 2.0F);
        }
    }

    /**
     * 瞬态方块（关闭态）邻接传播检查：仅在服务端检查 6 邻居是否存在开启态瞬态方块，去掉无意义的 0 音量音效包。
     */
    public static boolean hasOpenTransientNeighbor(LevelAccessor world, BlockPos pos) {
        Block openBlock = StalwartDungeonsModBlocks.TRANSIENT_BLOCK_OPEN.get();
        for (Direction direction : Direction.values()) {
            if (world.getBlockState(pos.relative(direction)).is(openBlock)) {
                return true;
            }
        }
        return false;
    }

    public static void handleTransientCloseTick(LevelAccessor world, double x, double y, double z) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos pos = BlockPos.containing(x, y, z);
        if (hasOpenTransientNeighbor(serverLevel, pos)) {
            serverLevel.setBlock(pos, StalwartDungeonsModBlocks.TRANSIENT_BLOCK_OPEN.get().defaultBlockState(), Block.UPDATE_ALL);
            serverLevel.sendParticles(ParticleTypes.FLAME, x + 0.5, y + 0.5, z + 0.5, 3, 0.2, 0.2, 0.2, 0.0);
        }
    }

    public static void handleTransientCloseRedstoneOrUse(LevelAccessor world, double x, double y, double z) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos pos = BlockPos.containing(x, y, z);
        serverLevel.setBlock(pos, StalwartDungeonsModBlocks.TRANSIENT_BLOCK_OPEN.get().defaultBlockState(), Block.UPDATE_ALL);
        serverLevel.playSound(null, pos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 1.0F, 2.0F);
        serverLevel.sendParticles(ParticleTypes.FLAME, x + 0.5, y + 0.5, z + 0.5, 3, 0.2, 0.2, 0.2, 0.0);
    }

    /**
     * 修复开启态瞬态方块定时关闭：
     * - 将 == 5.0 修正为 >= 5.0，防止异常值导致永久开启与无限 tick 泄漏；
     * - 去掉每刻无意义的 sendBlockUpdated 区块重绘与网络同步风暴；
     * - 去掉 0 音量音效包与替换方块后的无效写入。
     */
    public static void handleTransientOpenTick(LevelAccessor world, double x, double y, double z) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }
        BlockPos pos = BlockPos.containing(x, y, z);
        BlockEntity be = serverLevel.getBlockEntity(pos);
        double cooldown = be != null ? be.getPersistentData().getDouble("tagCooldown") : 5.0;
        if (cooldown >= 5.0) {
            serverLevel.setBlock(pos, StalwartDungeonsModBlocks.TRANSIENT_BLOCK_CLOSE.get().defaultBlockState(), Block.UPDATE_ALL);
            serverLevel.sendParticles(ParticleTypes.FLAME, x + 0.5, y + 0.5, z + 0.5, 3, 0.2, 0.2, 0.2, 0.0);
        } else if (be != null) {
            be.getPersistentData().putDouble("tagCooldown", cooldown + 1.0);
            be.setChanged();
        }
    }

    /**
     * 修复眩晕雕像：仅服务端触发，排除旁观者模式玩家。
     */
    public static void handleGiddyStatueTick(LevelAccessor world, double x, double y, double z) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }
        List<Player> players = serverLevel.getEntitiesOfClass(
                Player.class,
                AABB.ofSize(new Vec3(x + 0.5, y, z + 0.5), 5.0, 5.0, 5.0),
                player -> player.isAlive() && !player.isSpectator()
        );
        if (players.isEmpty()) {
            return;
        }
        BlockPos pos = BlockPos.containing(x, y, z);
        serverLevel.sendParticles(ParticleTypes.FLAME, x + 0.5, y + 1.0, z + 0.5, 16, 0.4, 0.4, 0.4, 0.0);
        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, x + 0.5, y + 1.0, z + 0.5, 8, 0.4, 0.4, 0.4, 0.0);
        StalwartDungeonsModEntities.GIDDY_BLAZE.get().spawn(serverLevel, BlockPos.containing(x + 0.5, y, z + 0.5), MobSpawnType.MOB_SUMMONED);
        serverLevel.playSound(null, pos, SoundEvents.LAVA_EXTINGUISH, SoundSource.NEUTRAL, 1.0F, 0.6F);
        serverLevel.destroyBlock(pos, false);
    }

    /**
     * 修复孢子喷发器：消除每刻 4 次匿名类分配，排除旁观者，且限制周围孢子投射物数量上限。
     */
    public static void handleSporeVaporizerTick(LevelAccessor world, double x, double y, double z) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }
        AABB playerBox = AABB.ofSize(new Vec3(x + 0.5, y + 0.5, z + 0.5), 16.0, 16.0, 16.0);
        if (serverLevel.getEntitiesOfClass(Player.class, playerBox, player -> player.isAlive() && !player.isSpectator()).isEmpty()) {
            return;
        }

        int cap = SdBugfixConfig.getMaxNearbySporeProjectiles();
        int nearbySpores = serverLevel.getEntitiesOfClass(
                net.mcreator.stalwartdungeons.entity.SporeProjectileEntity.class,
                playerBox,
                Entity::isAlive
        ).size();
        if (nearbySpores >= cap) {
            return;
        }

        BlockPos pos = BlockPos.containing(x, y, z);
        Direction facing = resolveHorizontalFacing(serverLevel.getBlockState(pos));
        double spawnX = x + 0.5;
        double spawnY = y + 0.5;
        double spawnZ = z + 0.5;
        double vx = 0.0;
        double vz = 0.0;
        switch (facing) {
            case NORTH -> {
                spawnZ = z - 0.25;
                vz = -0.35;
            }
            case SOUTH -> {
                spawnZ = z + 1.25;
                vz = 0.35;
            }
            case WEST -> {
                spawnX = x - 0.25;
                vx = -0.35;
            }
            case EAST -> {
                spawnX = x + 1.25;
                vx = 0.35;
            }
            default -> {
                return;
            }
        }

        Entity projectile = StalwartDungeonsModEntities.SPORE_PROJECTILE.get()
                .spawn(serverLevel, BlockPos.containing(spawnX, spawnY, spawnZ), MobSpawnType.MOB_SUMMONED);
        if (projectile != null) {
            projectile.setDeltaMovement(vx, 0.0, vz);
        }
    }

    private static Direction resolveHorizontalFacing(BlockState state) {
        Property<?> property = state.getBlock().getStateDefinition().getProperty("facing");
        if (property != null && state.getValue(property) instanceof Direction dir) {
            return dir;
        }
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        }
        return Direction.NORTH;
    }

    /**
     * 修复灵魂岩浆砖踩踏伤害：仅服务端扣血，并遵循原版岩浆块潜行/冰霜行者免疫规则。
     */
    public static void handleSoulMagmaStep(Entity entity) {
        if (entity == null || entity.level().isClientSide()) {
            return;
        }
        if (entity.fireImmune() || entity.isSteppingCarefully()) {
            return;
        }
        if (entity instanceof LivingEntity living && EnchantmentHelper.hasFrostWalker(living)) {
            return;
        }
        entity.hurt(
                new DamageSource(entity.level().registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.HOT_FLOOR)),
                1.0F
        );
    }

    /**
     * 修复燃烧药水每刻扣血：仅在服务端判定伤害与粒子。
     */
    public static void handleBurningPotionTick(LevelAccessor world, Entity entity) {
        if (entity == null || !(world instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!entity.fireImmune() && serverLevel.random.nextFloat() < 0.01F) {
            entity.hurt(
                    new DamageSource(serverLevel.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(DamageTypes.IN_FIRE)),
                    1.0F
            );
            serverLevel.sendParticles(ParticleTypes.SOUL_FIRE_FLAME, entity.getX(), entity.getY() + 0.5, entity.getZ(), 8, 0.2, 0.2, 0.2, 0.0);
        }
    }

    /**
     * 修复黑曜石击发器：仅服务端运行，正确校验 TNT 冷却，且创造模式不消耗 TNT。
     */
    public static void handleObsidianStrikerUse(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (!(entity instanceof LivingEntity living) || !(world instanceof ServerLevel serverLevel)) {
            return;
        }
        ItemStack mainHand = living.getMainHandItem();
        if (!mainHand.is(Blocks.TNT.asItem())) {
            return;
        }
        if (living instanceof Player player && player.getCooldowns().isOnCooldown(mainHand.getItem())) {
            return;
        }
        serverLevel.explode(null, x + 0.5, y + 9.0, z + 0.5, 2.5F, Level.ExplosionInteraction.NONE);
        if (living instanceof Player player) {
            player.getCooldowns().addCooldown(mainHand.getItem(), 20);
            if (!player.isCreative()) {
                mainHand.shrink(1);
            }
        }
    }

    /**
     * 修复三座 Boss 祭坛：
     * - 仅服务端生成与消耗下界之星；
     * - 创造模式不消耗下界之星；
     * - 下界守卫祭坛智能判断悬挂祭坛（守望城堡天花板）与地面祭坛：悬挂时生成在下方净空处避免卡死天花板，地面放置时生成在上方。
     */
    public static void handleBossAltarUse(LevelAccessor world, double x, double y, double z, Entity entity, EntityType<?> bossType, double defaultYOffset, boolean smartNetherKeeperPlacement) {
        if (!(entity instanceof LivingEntity living) || !(world instanceof ServerLevel serverLevel)) {
            return;
        }
        ItemStack mainHand = living.getMainHandItem();
        if (!mainHand.is(Items.NETHER_STAR)) {
            return;
        }

        BlockPos altarPos = BlockPos.containing(x, y, z);
        BlockPos spawnPos;
        if (smartNetherKeeperPlacement) {
            // 守望城堡中的祭坛镶嵌在天花板上（下方 3 格为空气），先检查下方是否净空以防止 2.3 格高的下界守卫卡进屋顶窒息
            BlockPos belowClear = altarPos.below(3);
            if (isSpaceClear(serverLevel, belowClear, 3)) {
                spawnPos = belowClear;
            } else if (isSpaceClear(serverLevel, altarPos.above(), 3)) {
                spawnPos = altarPos.above();
            } else {
                spawnPos = BlockPos.containing(x + 0.5, y + defaultYOffset, z + 0.5);
            }
        } else {
            spawnPos = BlockPos.containing(x + 0.5, y + defaultYOffset, z + 0.5);
        }

        Entity spawned = bossType.spawn(serverLevel, spawnPos, MobSpawnType.MOB_SUMMONED);
        if (spawned != null) {
            spawned.setYRot(serverLevel.random.nextFloat() * 360.0F);
        }
        serverLevel.playSound(null, altarPos, SoundEvents.WITHER_SPAWN, SoundSource.NEUTRAL, 1.0F, 0.5F);
        if (living instanceof Player player && !player.isCreative()) {
            mainHand.shrink(1);
        }
    }

    private static boolean isSpaceClear(ServerLevel level, BlockPos basePos, int height) {
        for (int dy = 0; dy < height; dy++) {
            BlockPos check = basePos.above(dy);
            if (!level.getBlockState(check).getCollisionShape(level, check).isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
