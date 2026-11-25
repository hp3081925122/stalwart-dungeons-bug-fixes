package com.hp.stalwartdungeonsbugfix.item;

import net.mcreator.stalwartdungeons.init.StalwartDungeonsModEnchantments;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModItems;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModMobEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * 物品、护甲、附魔、盾牌与下界之锤逻辑修复助手。
 */
public final class SdItemLogicHelper {
    private static volatile Set<Item> thunderStrikeHammers;

    private SdItemLogicHelper() {
    }

    /**
     * 缓存支持雷击附魔的 8 把战锤，避免每次附魔台/铁砧校验重复创建 ItemStack 数组与 Ingredient。
     */
    public static boolean isThunderStrikeHammer(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return false;
        }
        Set<Item> set = thunderStrikeHammers;
        if (set == null) {
            set = Set.of(
                    StalwartDungeonsModItems.WOODEN_HAMMER.get(),
                    StalwartDungeonsModItems.STONE_HAMMER.get(),
                    StalwartDungeonsModItems.IRON_HAMMER.get(),
                    StalwartDungeonsModItems.GOLDEN_HAMMER.get(),
                    StalwartDungeonsModItems.DIAMOND_HAMMER.get(),
                    StalwartDungeonsModItems.NETHERITE_HAMMER.get(),
                    StalwartDungeonsModItems.TUNGSTEN_HAMMER.get(),
                    StalwartDungeonsModItems.NETHER_HAMMER.get()
            );
            thunderStrikeHammers = set;
        }
        return set.contains(stack.getItem());
    }

    /**
     * 修复三套护甲每刻无条件调用 removeEffect 引发的客户端/服务端 MobEffectEvent.Remove 事件风暴。
     */
    public static void handleChorundumArmorTick(Entity entity) {
        if (!(entity instanceof LivingEntity living) || living.level().isClientSide()) {
            return;
        }
        if (living.getItemBySlot(EquipmentSlot.HEAD).is(StalwartDungeonsModItems.CHORUNDUM_ARMOR_HELMET.get())
                && living.getItemBySlot(EquipmentSlot.LEGS).is(StalwartDungeonsModItems.CHORUNDUM_ARMOR_LEGGINGS.get())
                && living.getItemBySlot(EquipmentSlot.FEET).is(StalwartDungeonsModItems.CHORUNDUM_ARMOR_BOOTS.get())
                && living.hasEffect(MobEffects.LEVITATION)) {
            living.removeEffect(MobEffects.LEVITATION);
        }
    }

    public static void handleTungstenArmorTick(Entity entity) {
        if (!(entity instanceof LivingEntity living) || living.level().isClientSide()) {
            return;
        }
        if (living.getItemBySlot(EquipmentSlot.HEAD).is(StalwartDungeonsModItems.TUNGSTEN_HELMET.get())
                && living.getItemBySlot(EquipmentSlot.LEGS).is(StalwartDungeonsModItems.TUNGSTEN_LEGGINGS.get())
                && living.getItemBySlot(EquipmentSlot.FEET).is(StalwartDungeonsModItems.TUNGSTEN_BOOTS.get())) {
            MobEffect burning = StalwartDungeonsModMobEffects.BURNING.get();
            if (living.hasEffect(burning)) {
                living.removeEffect(burning);
            }
            if (living.hasEffect(MobEffects.WITHER)) {
                living.removeEffect(MobEffects.WITHER);
            }
            if (living.isOnFire()) {
                living.clearFire();
            }
        }
    }

    public static void handleWartedTungstenArmorTick(Entity entity) {
        if (!(entity instanceof LivingEntity living) || living.level().isClientSide()) {
            return;
        }
        if (living.getItemBySlot(EquipmentSlot.HEAD).is(StalwartDungeonsModItems.WARTED_TUNGSTEN_HELMET.get())
                && living.getItemBySlot(EquipmentSlot.LEGS).is(StalwartDungeonsModItems.WARTED_TUNGSTEN_LEGGINGS.get())
                && living.getItemBySlot(EquipmentSlot.FEET).is(StalwartDungeonsModItems.WARTED_TUNGSTEN_BOOTS.get())) {
            MobEffect spore = StalwartDungeonsModMobEffects.SPORE.get();
            if (living.hasEffect(spore)) {
                living.removeEffect(spore);
            }
            if (living.hasEffect(MobEffects.WITHER)) {
                living.removeEffect(MobEffects.WITHER);
            }
        }
    }

    /**
     * 修复远古之火右键熔炼副手物品：
     * - 仅服务端运行；
     * - 检查物品冷却且仅查询 1 次熔炼配方；
     * - 精确消耗副手物品 1 个（而非扫描整个背包任意同名槽位），创造模式不损耐久。
     */
    public static void handleAncientFireUse(LevelAccessor world, double x, double y, double z, Entity entity, ItemStack itemstack) {
        if (!(entity instanceof LivingEntity living) || !(world instanceof ServerLevel serverLevel)) {
            return;
        }
        if (!living.getMainHandItem().is(StalwartDungeonsModItems.ANCIENT_FIRE.get())) {
            return;
        }
        if (living instanceof Player player && player.getCooldowns().isOnCooldown(itemstack.getItem())) {
            return;
        }
        ItemStack offhand = living.getOffhandItem();
        if (offhand.isEmpty()) {
            return;
        }

        Optional<SmeltingRecipe> recipeOpt = serverLevel.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(offhand), serverLevel);
        if (recipeOpt.isEmpty()) {
            return;
        }

        ItemStack result = recipeOpt.get().getResultItem(serverLevel.registryAccess()).copy();
        if (result.isEmpty()) {
            return;
        }
        result.setCount(1);

        if (living instanceof Player player) {
            ItemHandlerHelper.giveItemToPlayer(player, result);
            if (!player.isCreative()) {
                offhand.shrink(1);
                if (itemstack.hurt(2, serverLevel.random, player instanceof ServerPlayer sp ? sp : null)) {
                    itemstack.shrink(1);
                    itemstack.setDamageValue(0);
                }
            }
            player.getCooldowns().addCooldown(itemstack.getItem(), 10);
        } else {
            offhand.shrink(1);
        }

        serverLevel.playSound(null, BlockPos.containing(x, y, z), SoundEvents.LAVA_EXTINGUISH, SoundSource.NEUTRAL, 0.5F, 0.5F);
        serverLevel.sendParticles(ParticleTypes.FLAME, x, y + 1.0, z, 8, 0.2, 0.2, 0.2, 0.0);
    }

    /**
     * 修复三面自定义盾牌格挡耐久消耗事件：
     * - 仅服务端执行，避免客户端幽灵扣耐久；
     * - 跳过不可格挡伤害与创造模式玩家；
     * - 精确扣减当前举盾手（getUseItem）对应的盾牌耐久。
     */
    public static void handleShieldAttackEvent(@Nullable LivingAttackEvent event, Entity entity, double amount) {
        if (!(entity instanceof LivingEntity living) || living.level().isClientSide() || !living.isBlocking()) {
            return;
        }
        if (event != null && event.getSource().is(DamageTypeTags.BYPASSES_SHIELD)) {
            return;
        }
        if (living instanceof Player player && player.isCreative()) {
            return;
        }
        ItemStack activeShield = living.getUseItem();
        if (!isCustomShield(activeShield)) {
            if (isCustomShield(living.getMainHandItem())) {
                activeShield = living.getMainHandItem();
            } else if (isCustomShield(living.getOffhandItem())) {
                activeShield = living.getOffhandItem();
            } else {
                return;
            }
        }
        int damage = (int) Math.floor(amount * 0.7);
        if (damage <= 0) {
            return;
        }
        ServerPlayer sp = living instanceof ServerPlayer serverPlayer ? serverPlayer : null;
        if (activeShield.hurt(damage, living.getRandom(), sp)) {
            activeShield.shrink(1);
            activeShield.setDamageValue(0);
        }
    }

    private static boolean isCustomShield(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        Item item = stack.getItem();
        return item == StalwartDungeonsModItems.TUNGSTEN_SHIELD.get()
                || item == StalwartDungeonsModItems.WARTED_TUNGSTEN_SHIELD.get()
                || item == StalwartDungeonsModItems.CHORUNDUM_SHIELD.get();
    }

    /**
     * 修复可怕火枪命中玩家/方块：
     * - 空手时不再向 Items.AIR 添加冷却；
     * - 仅服务端放置火焰（消除客户端幽灵火）。
     */
    public static void handleAwfulGunHitsPlayer(LevelAccessor world, double x, double y, double z, Entity entity, Entity projectile) {
        if (entity == null || projectile == null || !(world instanceof ServerLevel serverLevel)) {
            return;
        }
        if (entity instanceof Player player) {
            addNonEmptyCooldown(player, player.getMainHandItem(), 150);
            addNonEmptyCooldown(player, player.getOffhandItem(), 150);
        }
        serverLevel.explode(null, x, y, z, 2.0F, Level.ExplosionInteraction.NONE);
        if (entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(StalwartDungeonsModMobEffects.BURNING.get(), 200, 0));
        }
        tryPlaceFire(serverLevel, BlockPos.containing(x, y, z));
        tryPlaceFire(serverLevel, BlockPos.containing(x, y, z + 1.0));
        tryPlaceFire(serverLevel, BlockPos.containing(x, y, z - 1.0));
        tryPlaceFire(serverLevel, BlockPos.containing(x + 1.0, y, z));
        tryPlaceFire(serverLevel, BlockPos.containing(x - 1.0, y, z));
        if (!projectile.isRemoved()) {
            projectile.discard();
        }
    }

    public static void handleAwfulGunHitsBlock(LevelAccessor world, double x, double y, double z) {
        if (!(world instanceof ServerLevel serverLevel)) {
            return;
        }
        AABB box = new AABB(x - 2.0, y - 2.0, z - 2.0, x + 2.0, y + 2.0, z + 2.0);
        for (Player player : serverLevel.getEntitiesOfClass(Player.class, box, Player::isAlive)) {
            addNonEmptyCooldown(player, player.getMainHandItem(), 150);
            addNonEmptyCooldown(player, player.getOffhandItem(), 150);
        }
        serverLevel.explode(null, x, y, z, 2.0F, Level.ExplosionInteraction.NONE);
    }

    private static void addNonEmptyCooldown(Player player, ItemStack stack, int ticks) {
        if (!stack.isEmpty()) {
            player.getCooldowns().addCooldown(stack.getItem(), ticks);
        }
    }

    private static void tryPlaceFire(ServerLevel level, BlockPos pos) {
        BlockPos below = pos.below();
        if (level.isEmptyBlock(pos) && level.getBlockState(below).isFaceSturdy(level, below, Direction.UP)) {
            level.setBlock(pos, Blocks.FIRE.defaultBlockState(), 3);
        }
    }

    /**
     * 修复战锤雷击附魔触发：判空 LightningBolt.create 并设置施法者 ServerPlayer（正确归因击杀与成就）。
     */
    public static void handleHammerThunderStrike(LevelAccessor world, double x, double y, double z, @Nullable Entity target, Entity source, ItemStack stack, boolean igniteTarget) {
        if (source == null || !(world instanceof ServerLevel serverLevel)) {
            return;
        }
        if (igniteTarget && target != null) {
            target.setSecondsOnFire(12);
        }
        if (!source.isShiftKeyDown() || stack == null || stack.isEmpty()) {
            return;
        }
        int level = stack.getEnchantmentLevel(StalwartDungeonsModEnchantments.THUNDER_STRIKE.get());
        if (level <= 0) {
            return;
        }
        double chance = level == 1 ? 0.1 : (level == 2 ? 0.4 : 0.6);
        if (serverLevel.random.nextDouble() < chance) {
            LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(serverLevel);
            if (bolt != null) {
                bolt.moveTo(Vec3.atBottomCenterOf(BlockPos.containing(x, y, z)));
                if (source instanceof ServerPlayer serverPlayer) {
                    bolt.setCause(serverPlayer);
                }
                serverLevel.addFreshEntity(bolt);
            }
        }
    }

    /**
     * 修复下界之锤自动熔炼方块掉落：
     * 原版在 mineBlock 中才读取方块状态，此时方块已被原版破坏为空气导致自动熔炼完全失效；
     * 现于 Block.getDrops 阶段拦截并熔炼实际掉落物，兼容时运与精准采集，不重复掉落。
     */
    public static List<ItemStack> smeltDropsWithNetherHammer(ServerLevel level, BlockPos pos, @Nullable Entity entity, ItemStack tool, List<ItemStack> originalDrops) {
        if (tool == null || tool.isEmpty() || !tool.is(StalwartDungeonsModItems.NETHER_HAMMER.get())) {
            return originalDrops;
        }
        if (entity instanceof Player player && player.isCreative()) {
            return originalDrops;
        }
        if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0) {
            return originalDrops;
        }
        if (originalDrops.isEmpty()) {
            return originalDrops;
        }

        boolean smeltedAny = false;
        List<ItemStack> result = new ArrayList<>(originalDrops.size());
        for (ItemStack drop : originalDrops) {
            if (drop.isEmpty()) {
                continue;
            }
            Optional<SmeltingRecipe> recipeOpt = level.getRecipeManager()
                    .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(drop), level);
            if (recipeOpt.isPresent()) {
                ItemStack smelted = recipeOpt.get().getResultItem(level.registryAccess()).copy();
                if (!smelted.isEmpty()) {
                    smelted.setCount(smelted.getCount() * drop.getCount());
                    result.add(smelted);
                    smeltedAny = true;
                    continue;
                }
            }
            result.add(drop);
        }

        if (smeltedAny) {
            level.sendParticles(ParticleTypes.FLAME, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10, 0.25, 0.25, 0.25, 0.0);
        }
        return result;
    }

    /**
     * 修复传送珍珠悬停提示调用 getOrCreateTag() 导致全新物品被写入空 {} NBT 破坏堆叠的问题。
     */
    public static void appendTeleporterPearlTooltip(ItemStack stack, List<Component> tooltip) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("posX") || !tag.contains("posY") || !tag.contains("posZ")) {
            tooltip.add(Component.literal("§7[0.0 0.0 0.0]"));
            return;
        }
        double posX = tag.getDouble("posX");
        double posY = tag.getDouble("posY");
        double posZ = tag.getDouble("posZ");
        tooltip.add(Component.literal("§7[" + posX + " " + posY + " " + posZ + "]"));
    }
}
