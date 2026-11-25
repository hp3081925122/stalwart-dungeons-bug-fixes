package com.hp.stalwartdungeonsbugfix.entity;

import com.hp.stalwartdungeonsbugfix.config.SdBugfixConfig;
import com.hp.stalwartdungeonsbugfix.core.SdEffectRenewalGuard;
import net.mcreator.stalwartdungeons.entity.CannonPrEntity;
import net.mcreator.stalwartdungeons.entity.GiddyBlazeEntity;
import net.mcreator.stalwartdungeons.entity.NetherWartCocoonEntity;
import net.mcreator.stalwartdungeons.entity.ShulkerCannonEntity;
import net.mcreator.stalwartdungeons.entity.SporeProjectileEntity;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModEntities;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModItems;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

/**
 * Boss、怪物、孢子效果与潜影加农炮过程逻辑修复助手。
 */
public final class SdEntityLogicHelper {
    private static final String SHELTERER_PHASE2_SPAWNED_TAG = "sd_bugfix_phase2_spawned";

    private SdEntityLogicHelper() {
    }

    /**
     * 修复下界守卫每刻逻辑：
     * - 仅在服务端执行；
     * - 将硬编码血量 100.0 改为最大生命值 2/3 比例（原版 150 血对应 100，兼容整合包调整血量）；
     * - 为眩晕烈焰人召唤增加周围数量上限与战斗目标校验，防止无限刷怪卡服；
     * - 修复 4 个方向召唤时 3 处写死在 x+2.5 的火焰粒子坐标；
     * - 高血量分支不再感染已驯服主人，并去掉每刻 Stream 排序分配。
     */
    public static void handleNetherKeeperTick(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (!(entity instanceof LivingEntity living) || !(world instanceof ServerLevel serverLevel) || !living.isAlive()) {
            return;
        }

        float maxHealth = Math.max(1.0F, living.getMaxHealth());
        boolean lowHealthPhase = living.getHealth() <= maxHealth * (2.0F / 3.0F);
        TamableAnimal tamable = entity instanceof TamableAnimal t ? t : null;

        if (lowHealthPhase) {
            // 已驯服且无敌对目标时不主动刷敌对烈焰人
            if (tamable != null && tamable.isTame() && (tamable.getTarget() == null || !tamable.getTarget().isAlive())) {
                return;
            }
            double cooldown = entity.getPersistentData().getDouble("spawnCouldown");
            if (cooldown >= 200.0) {
                entity.getPersistentData().putDouble("spawnCouldown", 0.0);

                // 仅当存在存活战斗目标或附近有非旁观玩家时才召唤小怪，防止挂机无限堆积
                boolean hasCombatContext = (living instanceof Mob mob && mob.getTarget() != null && mob.getTarget().isAlive())
                        || !serverLevel.getEntitiesOfClass(
                                Player.class,
                                AABB.ofSize(new Vec3(x, y, z), 32.0, 24.0, 32.0),
                                player -> player.isAlive() && !player.isSpectator()
                        ).isEmpty();
                if (!hasCombatContext) {
                    return;
                }

                int cap = SdBugfixConfig.getMaxNearbyGiddyBlazes();
                int nearbyBlazes = serverLevel.getEntitiesOfClass(
                        GiddyBlazeEntity.class,
                        AABB.ofSize(new Vec3(x, y, z), 48.0, 32.0, 48.0),
                        Entity::isAlive
                ).size();
                if (nearbyBlazes >= cap) {
                    return;
                }

                serverLevel.playSound(null, BlockPos.containing(x, y, z), SoundEvents.LAVA_EXTINGUISH, SoundSource.NEUTRAL, 1.0F, 0.6F);
                int[][] offsets = {{2, 0}, {-2, 0}, {0, 2}, {0, -2}};
                for (int[] offset : offsets) {
                    if (nearbyBlazes >= cap) {
                        break;
                    }
                    double ox = x + offset[0];
                    double oz = z + offset[1];
                    if (serverLevel.isEmptyBlock(BlockPos.containing(ox, y, oz))) {
                        Entity blaze = StalwartDungeonsModEntities.GIDDY_BLAZE.get()
                                .spawn(serverLevel, BlockPos.containing(ox, y - 0.4, oz), MobSpawnType.MOB_SUMMONED);
                        if (blaze != null) {
                            blaze.setDeltaMovement(0.0, 0.4, 0.0);
                            nearbyBlazes++;
                        }
                        double px = ox + 0.5;
                        double pz = oz + 0.5;
                        serverLevel.sendParticles(ParticleTypes.FLAME, px, y + 1.0, pz, 16, 0.4, 0.4, 0.4, 0.0);
                        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, px, y + 1.0, pz, 8, 0.4, 0.4, 0.4, 0.0);
                    }
                }
            } else {
                entity.getPersistentData().putDouble("spawnCouldown", cooldown + 1.0);
            }
        } else {
            MobEffect spore = StalwartDungeonsModMobEffects.SPORE.get();
            AABB box = new AABB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5);
            List<LivingEntity> targets = serverLevel.getEntitiesOfClass(
                    LivingEntity.class,
                    box,
                    candidate -> candidate != entity && candidate.isAlive() && !candidate.hasEffect(spore)
            );
            for (LivingEntity candidate : targets) {
                if (tamable != null && tamable.isTame() && tamable.isOwnedBy(candidate)) {
                    continue;
                }
                if (candidate instanceof Player player && (player.isSpectator() || player.isCreative())) {
                    continue;
                }
                candidate.addEffect(new MobEffectInstance(spore, 600, 0));
                serverLevel.sendParticles(ParticleTypes.CRIMSON_SPORE, x + 0.5, y + 0.5, z + 0.5, 8, 0.4, 0.4, 0.4, 0.0);
            }
        }
    }

    /**
     * 修复推进虫每刻逻辑：
     * 原版错误地把飘浮效果加给推进虫自己并立刻每刻调用 removeEffect 造成事件风暴；
     * 修复为仅服务端给接触到的非旁观玩家施加飘浮，且自身仅在确实拥有飘浮时才移除。
     */
    public static void handlePropulkTick(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (!(entity instanceof LivingEntity self) || !(world instanceof ServerLevel serverLevel) || !self.isAlive()) {
            return;
        }
        AABB box = new AABB(x - 1.0, y - 1.0, z - 1.0, x + 1.0, y + 1.0, z + 1.0);
        List<Player> players = serverLevel.getEntitiesOfClass(
                Player.class,
                box,
                player -> player.isAlive() && !player.isSpectator() && !player.isCreative()
        );
        for (Player player : players) {
            if (!player.hasEffect(MobEffects.LEVITATION)) {
                player.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 200, 2));
            }
        }
        if (self.hasEffect(MobEffects.LEVITATION)) {
            self.removeEffect(MobEffects.LEVITATION);
        }
    }

    /**
     * 修复庇护者死亡转二阶段：
     * - 仅服务端执行并加入幂等标记，防止重复生成多只无甲庇护者；
     * - 不再暴力删除周围 1.5 格内所有掉落物（保护玩家装备与战利品）。
     */
    public static void handleSheltererDies(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null || !(world instanceof ServerLevel serverLevel)) {
            return;
        }
        if (entity.getPersistentData().getBoolean(SHELTERER_PHASE2_SPAWNED_TAG)) {
            return;
        }
        entity.getPersistentData().putBoolean(SHELTERER_PHASE2_SPAWNED_TAG, true);

        serverLevel.playSound(null, BlockPos.containing(x, y, z), SoundEvents.BEACON_DEACTIVATE, SoundSource.NEUTRAL, 2.0F, 1.0F);
        Entity phase2 = StalwartDungeonsModEntities.SHELTERER_WITHOUT_ARMOR.get()
                .spawn(serverLevel, BlockPos.containing(entity.getX(), entity.getY() + 1.0, entity.getZ()), MobSpawnType.MOB_SUMMONED);
        if (phase2 != null) {
            phase2.setDeltaMovement(0.0, 0.0, 0.0);
        }
        if (!entity.isRemoved()) {
            entity.discard();
        }
    }

    /**
     * 修复孢子药水到期生成下界疣茧：
     * - 跳过刷新窗口（onEffectUpdated）与提前净化窗口（牛奶/图腾/指令清除，剩余时长 > 0）；
     * - 彻底移除客户端类 Minecraft.getInstance() 专用服务端崩溃隐患；
     * - 直接将受害者骑乘到刚生成的茧上，无需全实体扫描排序。
     */
    public static void handleSporePotionExpires(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null || !(world instanceof ServerLevel serverLevel)) {
            return;
        }
        if (SdEffectRenewalGuard.isRefreshing() || SdEffectRenewalGuard.isEarlyRemoval()) {
            return;
        }
        if (!entity.isAlive() || entity instanceof NetherWartCocoonEntity || entity instanceof SporeProjectileEntity) {
            return;
        }
        if (entity instanceof Player player && (player.isSpectator() || player.isCreative())) {
            return;
        }
        if (!(entity instanceof LivingEntity living) || living.getMaxHealth() > 100.0F) {
            return;
        }
        if (hasFullWartedTungstenArmor(living)) {
            return;
        }

        AABB checkBox = AABB.ofSize(new Vec3(entity.getX(), entity.getY(), entity.getZ()), 1.2, 1.2, 1.2);
        if (!serverLevel.getEntitiesOfClass(NetherWartCocoonEntity.class, checkBox, Entity::isAlive).isEmpty()) {
            return;
        }

        serverLevel.playSound(null, BlockPos.containing(x, y, z), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.NEUTRAL, 1.0F, 1.0F);
        Entity cocoon = StalwartDungeonsModEntities.NETHER_WART_COCOON.get()
                .spawn(serverLevel, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), MobSpawnType.MOB_SUMMONED);
        if (cocoon != null) {
            entity.startRiding(cocoon, true);
        }
    }

    public static boolean hasFullWartedTungstenArmor(LivingEntity living) {
        return living.getItemBySlot(EquipmentSlot.HEAD).is(StalwartDungeonsModItems.WARTED_TUNGSTEN_HELMET.get())
                && living.getItemBySlot(EquipmentSlot.CHEST).is(StalwartDungeonsModItems.WARTED_TUNGSTEN_CHESTPLATE.get())
                && living.getItemBySlot(EquipmentSlot.LEGS).is(StalwartDungeonsModItems.WARTED_TUNGSTEN_LEGGINGS.get())
                && living.getItemBySlot(EquipmentSlot.FEET).is(StalwartDungeonsModItems.WARTED_TUNGSTEN_BOOTS.get());
    }

    /**
     * 修复下界疣茧每刻逻辑：
     * - 入水销毁后立刻 return，防止继续执行并将玩家骑乘到已销毁实体上；
     * - 仅在服务端执行效果施加与骑乘绑定，排除旁观者与创造模式玩家，去掉 Stream 排序开销。
     */
    public static void handleNetherWartCocoonTick(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (!(entity instanceof LivingEntity cocoon) || !(world instanceof ServerLevel serverLevel) || !cocoon.isAlive()) {
            return;
        }
        if (cocoon.isInWater()) {
            cocoon.ejectPassengers();
            cocoon.discard();
            return;
        }

        // 若已被包裹的玩家切换为创造或旁观模式，立即解除骑乘，避免被茧锁死无法下车
        if (cocoon.isVehicle()) {
            for (Entity passenger : List.copyOf(cocoon.getPassengers())) {
                if (passenger instanceof Player player && (player.isSpectator() || player.isCreative())) {
                    player.stopRiding();
                }
            }
        }

        cocoon.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5, 244, false, false));

        AABB box = new AABB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5);
        List<LivingEntity> candidates = serverLevel.getEntitiesOfClass(
                LivingEntity.class,
                box,
                target -> target.isAlive()
                        && !(target instanceof NetherWartCocoonEntity)
                        && (target instanceof Player || target instanceof Mob || target instanceof Animal)
        );

        for (LivingEntity target : candidates) {
            if (target instanceof Player player && (player.isSpectator() || player.isCreative())) {
                continue;
            }
            if (target.isPassenger() && target.getVehicle() == cocoon) {
                target.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 5, 0, false, false));
                target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 5, 1, false, false));
                target.addEffect(new MobEffectInstance(MobEffects.WITHER, 5, 1, false, false));
                target.setShiftKeyDown(false);

                cocoon.setYRot(target.getYRot());
                cocoon.setXRot(target.getXRot());
                cocoon.setYBodyRot(cocoon.getYRot());
                cocoon.setYHeadRot(cocoon.getYRot());
                cocoon.yRotO = cocoon.getYRot();
                cocoon.xRotO = cocoon.getXRot();
                cocoon.yBodyRotO = cocoon.getYRot();
                cocoon.yHeadRotO = cocoon.getYRot();
            } else if (!cocoon.isVehicle() && !target.isPassenger()) {
                target.startRiding(cocoon);
            }
            cocoon.addEffect(new MobEffectInstance(MobEffects.SLOW_FALLING, 5, 244, false, false));
        }
    }

    /**
     * 修复孢子瓶落地：将碎瓶音效与孢子粒子移出实体循环（空地正常播放、人群不再 N 倍刷屏），仅服务端执行。
     */
    public static void handleSporeBottleHitsBlock(LevelAccessor world, double x, double y, double z) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }
        serverLevel.sendParticles(ParticleTypes.CRIMSON_SPORE, x, y, z, 6, 0.2, 0.1, 0.2, 0.0);
        serverLevel.playSound(null, BlockPos.containing(x, y, z), SoundEvents.GLASS_BREAK, SoundSource.NEUTRAL, 0.5F, 1.0F);

        MobEffect spore = StalwartDungeonsModMobEffects.SPORE.get();
        AABB box = new AABB(x - 1.5, y - 1.5, z - 1.5, x + 1.5, y + 1.5, z + 1.5);
        for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, box, LivingEntity::isAlive)) {
            if (target instanceof SporeProjectileEntity || target instanceof NetherWartCocoonEntity) {
                continue;
            }
            if (target instanceof Player player && (player.isSpectator() || player.isCreative())) {
                continue;
            }
            if (!target.hasEffect(spore)) {
                target.addEffect(new MobEffectInstance(spore, 600, 0));
            }
        }
    }

    /**
     * 修复孢子投射物每刻逻辑：落地 discard 后立刻 return，仅服务端执行，不再感染自身或茧。
     */
    public static void handleSporeProjectileTick(LevelAccessor world, double x, double y, double z, Entity entity) {
        if (entity == null) {
            return;
        }
        entity.fallDistance = 0.0F;
        if (!(world instanceof ServerLevel serverLevel) || !entity.isAlive()) {
            return;
        }
        serverLevel.sendParticles(ParticleTypes.CRIMSON_SPORE, x, y, z, 3, 0.1, 0.1, 0.1, 0.0);
        if (entity.onGround()) {
            entity.discard();
            return;
        }
        applySporeContact(serverLevel, x, y, z, entity);
    }

    public static void handleSporeProjectilePlayerCollide(LevelAccessor world, double x, double y, double z) {
        if (world instanceof ServerLevel serverLevel) {
            applySporeContact(serverLevel, x, y, z, null);
        }
    }

    private static void applySporeContact(ServerLevel serverLevel, double x, double y, double z, Entity self) {
        MobEffect spore = StalwartDungeonsModMobEffects.SPORE.get();
        AABB box = new AABB(x - 0.5, y - 0.5, z - 0.5, x + 0.5, y + 0.5, z + 0.5);
        for (LivingEntity target : serverLevel.getEntitiesOfClass(LivingEntity.class, box, LivingEntity::isAlive)) {
            if (target == self || target instanceof SporeProjectileEntity || target instanceof NetherWartCocoonEntity) {
                continue;
            }
            if (target instanceof Player player && (player.isSpectator() || player.isCreative())) {
                continue;
            }
            if (!target.hasEffect(spore)) {
                target.addEffect(new MobEffectInstance(spore, 300, 0));
            }
        }
    }

    /**
     * 修复右键空瓶收集孢子投射物：仅服务端执行，校验 !isRemoved() 防刷瓶，创造模式不扣玻璃瓶。
     */
    public static void handleSporeProjectileRightClick(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceEntity) {
        if (entity == null || sourceEntity == null || !(world instanceof ServerLevel serverLevel) || entity.isRemoved()) {
            return;
        }
        if (!(sourceEntity instanceof LivingEntity living)) {
            return;
        }
        ItemStack mainHand = living.getMainHandItem();
        if (!mainHand.is(Items.GLASS_BOTTLE)) {
            return;
        }
        if (living instanceof Player player) {
            ItemHandlerHelper.giveItemToPlayer(player, new ItemStack(StalwartDungeonsModItems.SPORE_BOTTLE.get()));
            if (!player.isCreative()) {
                mainHand.shrink(1);
            }
        }
        serverLevel.playSound(null, BlockPos.containing(x, y, z), SoundEvents.BOTTLE_FILL, SoundSource.NEUTRAL, 0.5F, 1.0F);
        entity.discard();
    }

    /**
     * 修复潜影加农炮受击回收逻辑：
     * - 彻底移除 Minecraft.getInstance() 专用服务端崩溃；
     * - 仅在服务端、实体未移除且攻击者为玩家时触发（保留玩家徒手敲击 6 下回收加农炮的原设计，避免非玩家伤害破坏）。
     */
    public static void handleShulkerCannonHurt(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceEntity) {
        if (entity == null || !(world instanceof ServerLevel serverLevel) || entity.isRemoved()) {
            return;
        }
        if (!(sourceEntity instanceof Player player)) {
            return;
        }
        if (player.isCreative()) {
            entity.discard();
            return;
        }
        double hp = entity.getPersistentData().getDouble("tagHealth");
        if (hp <= 0.0) {
            ItemEntity drop = new ItemEntity(serverLevel, x, y, z, new ItemStack(StalwartDungeonsModItems.SHULKER_CANNON_ITEM.get()));
            drop.setPickUpDelay(10);
            serverLevel.addFreshEntity(drop);
            entity.discard();
        } else {
            entity.getPersistentData().putDouble("tagHealth", hp - 1.0);
        }
    }

    /**
     * 修复右键放置潜影加农炮物品：
     * - 移除 Minecraft.getInstance() 专用服务端崩溃；
     * - 彻底修复 ItemStack.EMPTY.setCount(1) 污染全局空物品单例的严重 Bug；
     * - 仅服务端放置并扣减 1 个手持物品（创造模式不消耗）。
     */
    public static void handleShulkerCannonItemRightClick(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
        if (!(entity instanceof LivingEntity living) || itemstack == null || itemstack.isEmpty()) {
            return;
        }
        ItemStack mainHand = living.getMainHandItem();
        if (!mainHand.is(itemstack.getItem())) {
            return;
        }
        living.swing(InteractionHand.MAIN_HAND, true);
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }

        serverLevel.playSound(null, BlockPos.containing(x, y, z), SoundEvents.STONE_PLACE, SoundSource.NEUTRAL, 1.0F, 1.0F);
        Entity cannon = StalwartDungeonsModEntities.SHULKER_CANNON.get()
                .spawn(serverLevel, BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()), MobSpawnType.MOB_SUMMONED);
        if (cannon != null) {
            cannon.setYRot(entity.getYRot());
            cannon.setYBodyRot(entity.getYRot());
            cannon.setYHeadRot(entity.getYRot());
            cannon.setDeltaMovement(0.0, 0.0, 0.0);
        }

        if (!(living instanceof Player player && player.isCreative())) {
            mainHand.shrink(1);
            if (living instanceof Player player) {
                player.getInventory().setChanged();
            }
        }
    }

    /**
     * 修复骑乘潜影加农炮开炮逻辑：
     * - 正确检查 TNT 冷却（原版只加冷却从不检查）；
     * - 仅服务端生成炮弹、扣减 TNT（创造模式不扣）、单次射线检测计算后坐力（原版重复执行 3 次 clip）。
     */
    public static void handleShulkerCannonShoot(LevelAccessor world, Entity entity) {
        if (!(entity instanceof LivingEntity living) || !(world instanceof ServerLevel serverLevel)) {
            return;
        }
        Entity vehicle = entity.getVehicle();
        if (!(vehicle instanceof ShulkerCannonEntity)) {
            return;
        }
        ItemStack mainHand = living.getMainHandItem();
        if (!mainHand.is(Blocks.TNT.asItem()) || !living.getOffhandItem().isEmpty()) {
            return;
        }
        if (living instanceof Player player && player.getCooldowns().isOnCooldown(mainHand.getItem())) {
            return;
        }

        if (living instanceof Player player) {
            player.getCooldowns().addCooldown(mainHand.getItem(), 40);
        }

        CannonPrEntity projectile = new CannonPrEntity(StalwartDungeonsModEntities.CANNON_PR.get(), serverLevel);
        projectile.setOwner(vehicle);
        projectile.setBaseDamage(0.0);
        projectile.setKnockback(0);
        projectile.setSilent(true);
        projectile.pickup = AbstractArrow.Pickup.DISALLOWED;
        projectile.setPos(vehicle.getX(), vehicle.getEyeY() - 0.1, vehicle.getZ());
        Vec3 look = vehicle.getLookAngle();
        projectile.shoot(look.x, look.y, look.z, 2.0F, 0.0F);
        serverLevel.addFreshEntity(projectile);

        if (!(living instanceof Player player && player.isCreative())) {
            mainHand.shrink(1);
            if (living instanceof Player player) {
                player.getInventory().setChanged();
            }
        }

        serverLevel.playSound(null, vehicle.blockPosition(), SoundEvents.DRAGON_FIREBALL_EXPLODE, SoundSource.NEUTRAL, 0.5F, 1.0F);
        serverLevel.sendParticles(ParticleTypes.LARGE_SMOKE, vehicle.getX(), vehicle.getY(), vehicle.getZ(), 10, 0.5, 0.5, 0.5, 0.0);
        living.swing(InteractionHand.MAIN_HAND, true);

        Vec3 eye = vehicle.getEyePosition(1.0F);
        Vec3 end = eye.add(vehicle.getViewVector(1.0F).scale(16.0));
        BlockHitResult hit = serverLevel.clip(new ClipContext(eye, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, vehicle));
        BlockPos hitPos = hit.getBlockPos();
        vehicle.setDeltaMovement(new Vec3(
                (hitPos.getX() + 0.5 - vehicle.getX()) * -0.06,
                (hitPos.getY() + 0.5 - vehicle.getY()) * -0.06,
                (hitPos.getZ() + 0.5 - vehicle.getZ()) * -0.06
        ));
    }

    /**
     * 可怕恶魂近战/远程阶段切换改为百分比血量（原版 200 血对应 100 血，即 50%）。
     */
    public static boolean isAwfulGhastLowHealth(Entity entity) {
        if (!(entity instanceof LivingEntity living)) {
            return false;
        }
        return living.getHealth() < living.getMaxHealth() * 0.5F;
    }
}
