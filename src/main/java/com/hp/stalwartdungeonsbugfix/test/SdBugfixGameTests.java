package com.hp.stalwartdungeonsbugfix.test;

import com.hp.stalwartdungeonsbugfix.Stalwartdungeonsbugfix;
import com.hp.stalwartdungeonsbugfix.ai.SdSafeAwfulGhastChargeGoal;
import com.hp.stalwartdungeonsbugfix.core.SdWorkQueueManager;
import com.hp.stalwartdungeonsbugfix.item.SdItemLogicHelper;
import com.hp.stalwartdungeonsbugfix.world.SdBedrockProtectProcessor;
import com.hp.stalwartdungeonsbugfix.world.SdDungeonPiece;
import com.hp.stalwartdungeonsbugfix.world.SdWorldgenHelper;
import com.mojang.datafixers.util.Pair;
import io.netty.buffer.Unpooled;
import net.mcreator.stalwartdungeons.StalwartDungeonsMod;
import net.mcreator.stalwartdungeons.block.entity.TeleporterBlockBlockEntity;
import net.mcreator.stalwartdungeons.entity.AwfulDungeonSpawnerEntity;
import net.mcreator.stalwartdungeons.entity.AwfulGhastEntity;
import net.mcreator.stalwartdungeons.entity.LevitationProjectileEntity;
import net.mcreator.stalwartdungeons.entity.NetherKeeperEntity;
import net.mcreator.stalwartdungeons.entity.NetherWartCocoonEntity;
import net.mcreator.stalwartdungeons.entity.PropulkEntity;
import net.mcreator.stalwartdungeons.entity.SheltererEntity;
import net.mcreator.stalwartdungeons.entity.SheltererWithoutArmorEntity;
import net.mcreator.stalwartdungeons.entity.ShulkerCannonEntity;
import net.mcreator.stalwartdungeons.entity.SporeBottleEntity;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModBlocks;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModEnchantments;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModEntities;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModItems;
import net.mcreator.stalwartdungeons.init.StalwartDungeonsModMobEffects;
import net.mcreator.stalwartdungeons.procedures.AncientFireRightClickedInAirProcedure;
import net.mcreator.stalwartdungeons.procedures.AwfulDungeonsSpawnBlockAddedProcedure;
import net.mcreator.stalwartdungeons.procedures.AwfulGhastAltarOnBlockRightClickedProcedure;
import net.mcreator.stalwartdungeons.procedures.AwfulGhastFrontAttackProcedure;
import net.mcreator.stalwartdungeons.procedures.AwfulGhastShootAtDistanceProcedure;
import net.mcreator.stalwartdungeons.procedures.AwfulGunBulletHitsBlockProcedure;
import net.mcreator.stalwartdungeons.procedures.AwfulGunBulletHitsPlayerProcedure;
import net.mcreator.stalwartdungeons.procedures.BurningOnPotionActiveTickProcedure;
import net.mcreator.stalwartdungeons.procedures.ChorundumArmorBodyTickEventProcedure;
import net.mcreator.stalwartdungeons.procedures.EnderGlobeSpawnUpdateTickProcedure;
import net.mcreator.stalwartdungeons.procedures.GiddyStatueUpdateTickProcedure;
import net.mcreator.stalwartdungeons.procedures.IronHammerLivingEntityIsHitWithToolProcedure;
import net.mcreator.stalwartdungeons.procedures.KeepingCastleSpawnUpdateTickProcedure;
import net.mcreator.stalwartdungeons.procedures.NetherHammerBlockDestroyedWithToolProcedure;
import net.mcreator.stalwartdungeons.procedures.NetherHammerLivingEntityIsHitWithToolProcedure;
import net.mcreator.stalwartdungeons.procedures.NetherKeeperAltarOnBlockRightClickedProcedure;
import net.mcreator.stalwartdungeons.procedures.NetherWartCocoonOnEntityTickUpdateProcedure;
import net.mcreator.stalwartdungeons.procedures.ObsidianStrikerOnBlockRightClickedProcedure;
import net.mcreator.stalwartdungeons.procedures.PropulkOnEntityTickUpdateProcedure;
import net.mcreator.stalwartdungeons.procedures.SdShieldPrProcedure;
import net.mcreator.stalwartdungeons.procedures.SheltererAltarOnBlockRightClickedProcedure;
import net.mcreator.stalwartdungeons.procedures.SheltererEntityDiesProcedure;
import net.mcreator.stalwartdungeons.procedures.ShulkerCannonEntityIsHurtProcedure;
import net.mcreator.stalwartdungeons.procedures.ShulkerCannonItemRightclickedProcedure;
import net.mcreator.stalwartdungeons.procedures.ShulkerCannonShootProcedure;
import net.mcreator.stalwartdungeons.procedures.SoulMagmaBricksEntityWalksOnTheBlockProcedure;
import net.mcreator.stalwartdungeons.procedures.SporeBottleBulletHitsBlockProcedure;
import net.mcreator.stalwartdungeons.procedures.SporeOnEntityTickUpdateProcedure;
import net.mcreator.stalwartdungeons.procedures.SporeProjectilePlayerCollidesWithThisEntityProcedure;
import net.mcreator.stalwartdungeons.procedures.SporeProjectileRightClickedOnEntityProcedure;
import net.mcreator.stalwartdungeons.procedures.SporeVaporizerUpdateTickProcedure;
import net.mcreator.stalwartdungeons.procedures.TeleporterBlockEntityCollidesInTheBlockProcedure;
import net.mcreator.stalwartdungeons.procedures.TransientBlockCloseRedstoneOnProcedure;
import net.mcreator.stalwartdungeons.procedures.TransientBlockCloseUpdateTickProcedure;
import net.mcreator.stalwartdungeons.procedures.TransientBlockOpenUpdateTickProcedure;
import net.mcreator.stalwartdungeons.procedures.TungstenBodyTickEventProcedure;
import net.mcreator.stalwartdungeons.procedures.TungstenShieldCanUseRangedItemProcedure;
import net.mcreator.stalwartdungeons.procedures.WartedTungstenBodyTickEventProcedure;
import net.mcreator.stalwartdungeons.world.inventory.TeleporterGUIMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomStrollGoal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.RegisterGameTestsEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 坚固地牢 Bug 修复与性能优化模组自动化 GameTest 套件。
 * 覆盖队列、MapColor、标签与模块化工具挖掘、实心方块遮光、瞬态方块/点燃器零闲置卡顿、
 * 传送器安全与珍珠 NBT 防污染、孢子效果刷新/净化、Boss 与怪物 AI、潜影加农炮、下界之锤自动熔炼、战利品表与世界生成安全。
 */
@GameTestHolder(Stalwartdungeonsbugfix.MOD_ID)
@PrefixGameTestTemplate(false)
@Mod.EventBusSubscriber(modid = Stalwartdungeonsbugfix.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SdBugfixGameTests {
    private static final String EMPTY_TEMPLATE = "empty";

    private SdBugfixGameTests() {
    }

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        event.register(SdBugfixGameTests.class);
    }

    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Stalwartdungeonsbugfix.MOD_ID)
    public static void testWorkQueueSafetyAndNonLeak(GameTestHelper helper) {
        AtomicInteger counter = new AtomicInteger(0);
        // 测试 tick <= 0 不泄漏、异常隔离与正常顺序执行
        StalwartDungeonsMod.queueServerWork(0, () -> {
            throw new RuntimeException("Intentional test failure in delayed work");
        });
        StalwartDungeonsMod.queueServerWork(1, counter::incrementAndGet);
        StalwartDungeonsMod.queueServerWork(-5, counter::incrementAndGet);

        // 使用独立的非 SERVER 线程组模拟客户端/工作线程提交，应被安全忽略
        ThreadGroup nonServerGroup = new ThreadGroup("sd-non-server-group");
        Thread worker = new Thread(nonServerGroup, () -> StalwartDungeonsMod.queueServerWork(1, counter::incrementAndGet), "sd-non-server-test");
        worker.start();
        try {
            worker.join(2000L);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        helper.runAfterDelay(3, () -> {
            helper.assertTrue(counter.get() == 2, "Expected exactly 2 server tasks executed, got " + counter.get());
            helper.assertTrue(SdWorkQueueManager.size() == 0, "Expected no leaked tasks remaining in queue, got " + SdWorkQueueManager.size());
            helper.succeed();
        });
    }

    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Stalwartdungeonsbugfix.MOD_ID)
    public static void testAllBlocksHaveMapColorAndOpacity(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 2, 2));
        int sdBlockCount = 0;

        for (Block block : ForgeRegistries.BLOCKS.getValues()) {
            ResourceLocation key = ForgeRegistries.BLOCKS.getKey(block);
            if (key != null && "stalwart_dungeons".equals(key.getNamespace())) {
                sdBlockCount++;
                MapColor color = block.defaultBlockState().getMapColor(level, pos);
                helper.assertTrue(color != MapColor.NONE, "Block missing MapColor: " + key);
            }
        }
        helper.assertTrue(sdBlockCount == 39, "Expected 39 Stalwart Dungeons blocks, found " + sdBlockCount);

        // 验证原本透光的实心方块现已正确阻挡天光与方块光
        Block[] opaqueChecks = {
                StalwartDungeonsModBlocks.SOUL_BRICKS.get(),
                StalwartDungeonsModBlocks.PURPUR_BRICKS.get(),
                StalwartDungeonsModBlocks.CRACKED_ENDSTONE_BRICKS.get(),
                StalwartDungeonsModBlocks.CHISELED_END_STONE_BRICKS.get(),
                StalwartDungeonsModBlocks.TUNGSTEN_ORE.get(),
                StalwartDungeonsModBlocks.CHORUNDUM_ORE.get(),
                StalwartDungeonsModBlocks.TRANSIENT_BLOCK_CLOSE.get()
        };
        for (Block block : opaqueChecks) {
            BlockState state = block.defaultBlockState();
            helper.assertFalse(state.propagatesSkylightDown(level, pos), "Solid block should not propagate skylight: " + ForgeRegistries.BLOCKS.getKey(block));
            helper.assertTrue(state.getLightBlock(level, pos) == level.getMaxLightLevel(), "Solid block light opacity should be 15: " + ForgeRegistries.BLOCKS.getKey(block));
        }

        // 验证补全的方块与实体标签已加载
        helper.assertTrue(StalwartDungeonsModBlocks.NETHER_BRICKS_AND_NETHERRACK.get().defaultBlockState().is(BlockTags.MINEABLE_WITH_PICKAXE),
                "nether_bricks_and_netherrack should be in mineable/pickaxe");
        helper.assertTrue(StalwartDungeonsModBlocks.TUNGSTEN_ORE.get().defaultBlockState().is(BlockTags.NEEDS_IRON_TOOL),
                "tungsten_ore should be in needs_iron_tool");
        helper.assertTrue(StalwartDungeonsModBlocks.CHORUNDUM_ORE.get().defaultBlockState().is(BlockTags.NEEDS_DIAMOND_TOOL),
                "chorundum_ore should be in needs_diamond_tool");

        TagKey<EntityType<?>> forgeBosses = TagKey.create(Registries.ENTITY_TYPE, ResourceLocation.fromNamespaceAndPath("forge", "bosses"));
        helper.assertTrue(StalwartDungeonsModEntities.AWFUL_GHAST.get().is(forgeBosses), "awful_ghast should be in forge:bosses");
        helper.assertTrue(StalwartDungeonsModEntities.NETHER_KEEPER.get().is(forgeBosses), "nether_keeper should be in forge:bosses");
        helper.assertTrue(StalwartDungeonsModEntities.SHELTERER.get().is(forgeBosses), "shelterer should be in forge:bosses");
        helper.assertTrue(StalwartDungeonsModEntities.SHELTERER_WITHOUT_ARMOR.get().is(forgeBosses), "shelterer_without_armor should be in forge:bosses");
        helper.assertTrue(StalwartDungeonsModEntities.INCOMPLETE_WITHER.get().is(EntityTypeTags.SKELETONS), "incomplete_wither should be in minecraft:skeletons");
        helper.succeed();
    }

    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Stalwartdungeonsbugfix.MOD_ID)
    public static void testTagBasedHarvestWithoutHardcodedPickaxe(GameTestHelper helper) {
        BlockPos rel = new BlockPos(2, 2, 2);
        BlockPos abs = helper.absolutePos(rel);
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockSurvivalPlayer();

        BlockState soulBricks = StalwartDungeonsModBlocks.SOUL_BRICKS.get().defaultBlockState();
        BlockState tungstenOre = StalwartDungeonsModBlocks.TUNGSTEN_ORE.get().defaultBlockState();
        BlockState chorundumOre = StalwartDungeonsModBlocks.CHORUNDUM_ORE.get().defaultBlockState();

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STONE_PICKAXE));
        helper.assertTrue(soulBricks.canHarvestBlock(level, abs, player), "Stone pickaxe should harvest soul_bricks");
        helper.assertFalse(tungstenOre.canHarvestBlock(level, abs, player), "Stone pickaxe should not harvest tungsten_ore");

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.IRON_PICKAXE));
        helper.assertTrue(tungstenOre.canHarvestBlock(level, abs, player), "Iron pickaxe should harvest tungsten_ore");
        helper.assertFalse(chorundumOre.canHarvestBlock(level, abs, player), "Iron pickaxe should not harvest chorundum_ore");

        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_PICKAXE));
        helper.assertTrue(chorundumOre.canHarvestBlock(level, abs, player), "Diamond pickaxe should harvest chorundum_ore");

        // 构造手持非 PickaxeItem（木棍）但通过原版/Forge 标准 hasCorrectToolForDrops 校验的玩家，证明硬编码 instanceof PickaxeItem 已彻底移除
        Player modularToolPlayer = new Player(level, abs, 0.0F, new com.mojang.authlib.GameProfile(java.util.UUID.randomUUID(), "sd_modular_tester")) {
            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return false;
            }

            @Override
            public boolean hasCorrectToolForDrops(BlockState state) {
                return state.is(BlockTags.MINEABLE_WITH_PICKAXE);
            }
        };
        modularToolPlayer.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STICK));
        helper.assertTrue(soulBricks.canHarvestBlock(level, abs, modularToolPlayer),
                "Non-PickaxeItem modular tool must harvest soul_bricks via standard Forge/vanilla API");
        helper.assertTrue(chorundumOre.canHarvestBlock(level, abs, modularToolPlayer),
                "Non-PickaxeItem modular tool must harvest chorundum_ore via standard Forge/vanilla API");
        helper.succeed();
    }

    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Stalwartdungeonsbugfix.MOD_ID)
    public static void testTransientBlockAndIgniterNoIdleTicks(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos igniterPos = helper.absolutePos(new BlockPos(2, 2, 2));
        BlockPos closePos1 = helper.absolutePos(new BlockPos(4, 2, 2));
        BlockPos closePos2 = helper.absolutePos(new BlockPos(5, 2, 2));

        level.setBlock(igniterPos, StalwartDungeonsModBlocks.IGNITER.get().defaultBlockState(), 3);
        helper.assertFalse(level.getBlockTicks().hasScheduledTick(igniterPos, StalwartDungeonsModBlocks.IGNITER.get()),
                "Igniter should not schedule permanent server tick loop");

        level.setBlock(closePos1, StalwartDungeonsModBlocks.TRANSIENT_BLOCK_CLOSE.get().defaultBlockState(), 3);
        level.setBlock(closePos2, StalwartDungeonsModBlocks.TRANSIENT_BLOCK_CLOSE.get().defaultBlockState(), 3);
        helper.assertFalse(level.getBlockTicks().hasScheduledTick(closePos1, StalwartDungeonsModBlocks.TRANSIENT_BLOCK_CLOSE.get()),
                "Idle closed TransientBlock must not schedule server ticks");
        helper.assertFalse(level.getBlockTicks().hasScheduledTick(closePos2, StalwartDungeonsModBlocks.TRANSIENT_BLOCK_CLOSE.get()),
                "Idle closed TransientBlock #2 must not schedule server ticks");

        // 触发第 1 块开启，相邻第 2 块应由 neighborChanged 自动安排传播 tick
        TransientBlockCloseRedstoneOnProcedure.execute(level, closePos1.getX(), closePos1.getY(), closePos1.getZ());
        helper.assertTrue(level.getBlockState(closePos1).is(StalwartDungeonsModBlocks.TRANSIENT_BLOCK_OPEN.get()),
                "TransientBlock #1 should become open");
        helper.assertTrue(level.getBlockTicks().hasScheduledTick(closePos2, StalwartDungeonsModBlocks.TRANSIENT_BLOCK_CLOSE.get()),
                "Adjacent TransientBlock #2 should schedule propagation tick on neighbor open");

        // 验证 tagCooldown > 5.0 时不再永久卡开
        BlockEntity be = level.getBlockEntity(closePos1);
        helper.assertTrue(be != null, "Open TransientBlock should have BlockEntity");
        be.getPersistentData().putDouble("tagCooldown", 99.0);
        TransientBlockOpenUpdateTickProcedure.execute(level, closePos1.getX(), closePos1.getY(), closePos1.getZ());
        helper.assertTrue(level.getBlockState(closePos1).is(StalwartDungeonsModBlocks.TRANSIENT_BLOCK_CLOSE.get()),
                "Open TransientBlock with tagCooldown > 5 must close cleanly without sticking open");
        helper.succeed();
    }

    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Stalwartdungeonsbugfix.MOD_ID)
    public static void testTeleporterPearlAndMenuSafety(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ItemStack freshPearl = new ItemStack(StalwartDungeonsModItems.TELEPORTER_PEARL.get());
        List<Component> tooltip = new ArrayList<>();
        freshPearl.getItem().appendHoverText(freshPearl, level, tooltip, TooltipFlag.Default.NORMAL);
        helper.assertFalse(freshPearl.hasTag(), "Hovering over clean TeleporterPearl must NOT pollute empty {} NBT");

        BlockPos tpPos = helper.absolutePos(new BlockPos(3, 2, 3));
        level.setBlock(tpPos, StalwartDungeonsModBlocks.TELEPORTER.get().defaultBlockState(), 3);
        BlockEntity be = level.getBlockEntity(tpPos);
        helper.assertTrue(be instanceof TeleporterBlockBlockEntity, "Teleporter BE missing");
        TeleporterBlockBlockEntity tpBe = (TeleporterBlockBlockEntity) be;
        helper.assertFalse(tpBe.canPlaceItem(0, new ItemStack(Items.DIRT)), "Teleporter BE must reject non-pearl items");
        helper.assertTrue(tpBe.canPlaceItem(0, freshPearl), "Teleporter BE must accept teleporter_pearl");

        Player player = helper.makeMockPlayer();
        player.setPos(tpPos.getX() + 0.5, tpPos.getY() + 1.0, tpPos.getZ() + 0.5);
        TeleporterGUIMenu menu = new TeleporterGUIMenu(1, player.getInventory(), new FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(tpPos));
        helper.assertTrue(menu.stillValid(player), "Teleporter menu should be valid when nearby");

        player.setPos(tpPos.getX() + 50.0, tpPos.getY() + 1.0, tpPos.getZ() + 0.5);
        helper.assertFalse(menu.stillValid(player), "Teleporter menu must become invalid when far away");

        player.setPos(tpPos.getX() + 0.5, tpPos.getY() + 1.0, tpPos.getZ() + 0.5);
        level.setBlock(tpPos, Blocks.AIR.defaultBlockState(), 3);
        helper.assertFalse(menu.stillValid(player), "Teleporter menu must become invalid when block is broken");
        helper.succeed();
    }

    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Stalwartdungeonsbugfix.MOD_ID)
    public static void testSporeEffectRefreshEarlyCureAndNaturalExpiry(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(4, 2, 4));
        Pig pig = EntityType.PIG.create(level);
        helper.assertTrue(pig != null, "Failed to create test pig");
        pig.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
        level.addFreshEntity(pig);

        AABB search = new AABB(pos).inflate(3.0);
        // 1. 施加并刷新孢子效果（onEffectUpdated），绝不应生成茧
        pig.addEffect(new MobEffectInstance(StalwartDungeonsModMobEffects.SPORE.get(), 100, 0));
        pig.addEffect(new MobEffectInstance(StalwartDungeonsModMobEffects.SPORE.get(), 200, 0));
        helper.assertTrue(level.getEntitiesOfClass(NetherWartCocoonEntity.class, search).isEmpty(),
                "Refreshing Spore effect must NOT prematurely spawn NetherWartCocoon");

        // 2. 提前净化/移除剩余时长 > 0 的孢子效果，绝不应生成茧
        pig.removeEffect(StalwartDungeonsModMobEffects.SPORE.get());
        helper.assertTrue(level.getEntitiesOfClass(NetherWartCocoonEntity.class, search).isEmpty(),
                "Early curing Spore effect (duration > 0) must NOT spawn NetherWartCocoon");

        // 3. 施加 2 tick 短时效孢子并等待自然到期，应正常生成茧并包裹目标
        pig.addEffect(new MobEffectInstance(StalwartDungeonsModMobEffects.SPORE.get(), 2, 0));
        helper.runAfterDelay(5, () -> {
            List<NetherWartCocoonEntity> cocoons = level.getEntitiesOfClass(NetherWartCocoonEntity.class, search);
            helper.assertTrue(cocoons.size() == 1, "Natural expiry of Spore should spawn 1 NetherWartCocoon, found " + cocoons.size());
            helper.succeed();
        });
    }

    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Stalwartdungeonsbugfix.MOD_ID)
    public static void testEntityCategoriesAiAndBossFixes(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(4, 3, 4));

        // 1. MobCategory 修正
        helper.assertTrue(StalwartDungeonsModEntities.PROPULK.get().getCategory() == MobCategory.MONSTER,
                "Propulk category should be MONSTER");
        helper.assertTrue(StalwartDungeonsModEntities.SPORE_PROJECTILE.get().getCategory() == MobCategory.MISC,
                "SporeProjectile category should be MISC");

        // 2. AwfulGhast 空目标冲锋 Goal 不崩 & 百分比半血判定
        AwfulGhastEntity ghast = StalwartDungeonsModEntities.AWFUL_GHAST.get().create(level);
        helper.assertTrue(ghast != null, "Failed to create AwfulGhast");
        ghast.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
        level.addFreshEntity(ghast);
        ghast.setHealth(ghast.getMaxHealth() * 0.4F);
        helper.assertTrue(AwfulGhastFrontAttackProcedure.execute(ghast), "AwfulGhast below 50% HP should trigger front attack");
        SdSafeAwfulGhastChargeGoal chargeGoal = new SdSafeAwfulGhastChargeGoal(ghast);
        ghast.setTarget(null);
        chargeGoal.start();
        chargeGoal.tick();

        // 3. SheltererWithoutArmor AI 优先级：MeleeAttackGoal < RandomStrollGoal
        SheltererWithoutArmorEntity p2 = StalwartDungeonsModEntities.SHELTERER_WITHOUT_ARMOR.get().create(level);
        helper.assertTrue(p2 != null, "Failed to create SheltererWithoutArmor");
        int meleePrio = 999;
        int strollPrio = -1;
        for (WrappedGoal wg : p2.goalSelector.getAvailableGoals()) {
            if (wg.getGoal() instanceof MeleeAttackGoal) {
                meleePrio = Math.min(meleePrio, wg.getPriority());
            } else if (wg.getGoal() instanceof RandomStrollGoal) {
                strollPrio = Math.max(strollPrio, wg.getPriority());
            }
        }
        helper.assertTrue(meleePrio < strollPrio, "SheltererWithoutArmor MeleeAttackGoal priority (" + meleePrio + ") must beat RandomStrollGoal (" + strollPrio + ")");

        // 4. NetherKeeper 繁殖定位安全 & 驯服不攻击主人
        NetherKeeperEntity keeper = StalwartDungeonsModEntities.NETHER_KEEPER.get().create(level);
        helper.assertTrue(keeper != null, "Failed to create NetherKeeper");
        keeper.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
        level.addFreshEntity(keeper);
        AgeableMob child = keeper.getBreedOffspring(level, keeper);
        helper.assertTrue(child != null && child.blockPosition().distSqr(keeper.blockPosition()) <= 4.0,
                "NetherKeeper breed offspring should initialize at parent position, not (0,0,0)");

        // 5. Shelterer 死亡转二阶段幂等且保留附近掉落物
        SheltererEntity shelterer = StalwartDungeonsModEntities.SHELTERER.get().create(level);
        helper.assertTrue(shelterer != null, "Failed to create Shelterer");
        shelterer.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
        level.addFreshEntity(shelterer);
        ItemEntity playerDrop = new ItemEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, new ItemStack(Items.DIAMOND));
        level.addFreshEntity(playerDrop);
        SheltererEntityDiesProcedure.execute(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, shelterer);
        SheltererEntityDiesProcedure.execute(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, shelterer);
        helper.assertFalse(playerDrop.isRemoved(), "Shelterer death must NOT delete nearby ItemEntity drops");
        int spawnedP2 = level.getEntitiesOfClass(SheltererWithoutArmorEntity.class, new AABB(pos).inflate(4.0)).size();
        helper.assertTrue(spawnedP2 == 1, "Shelterer death must spawn exactly 1 phase-2 boss, got " + spawnedP2);
        helper.succeed();
    }

    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Stalwartdungeonsbugfix.MOD_ID)
    public static void testShulkerCannonServerSafetyAndNoEmptyStackMutation(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(4, 2, 4));
        Player survivalPlayer = helper.makeMockSurvivalPlayer();
        survivalPlayer.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

        ItemStack cannonStack = new ItemStack(StalwartDungeonsModItems.SHULKER_CANNON_ITEM.get(), 2);
        survivalPlayer.setItemInHand(InteractionHand.MAIN_HAND, cannonStack);
        ShulkerCannonItemRightclickedProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ(), survivalPlayer, cannonStack);

        helper.assertTrue(ItemStack.EMPTY.isEmpty() && ItemStack.EMPTY.getCount() == 0,
                "ItemStack.EMPTY singleton must NEVER be mutated");
        helper.assertTrue(survivalPlayer.getMainHandItem().getCount() == 1,
                "Placing 1 Shulker Cannon from stack of 2 in survival should leave 1, got " + survivalPlayer.getMainHandItem().getCount());

        List<ShulkerCannonEntity> cannons = level.getEntitiesOfClass(ShulkerCannonEntity.class, new AABB(pos).inflate(3.0));
        helper.assertTrue(cannons.size() == 1, "Expected 1 ShulkerCannonEntity spawned");
        ShulkerCannonEntity cannon = cannons.get(0);
        cannon.getPersistentData().putDouble("tagHealth", 0.0);
        // 服务端直接调用受击回收过程，验证不触发 Minecraft.getInstance() 崩溃
        ShulkerCannonEntityIsHurtProcedure.execute(level, cannon.getX(), cannon.getY(), cannon.getZ(), cannon, survivalPlayer);
        helper.assertTrue(cannon.isRemoved(), "ShulkerCannon should be discarded and drop item on final player hit");
        helper.succeed();
    }

    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Stalwartdungeonsbugfix.MOD_ID)
    public static void testPropulkArmorEventStormAndProjectiles(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(4, 2, 4));

        // 1. 监听 MobEffectEvent.Remove 计数，验证护甲与 Propulk 均不制造空删事件风暴
        AtomicInteger removeEvents = new AtomicInteger(0);
        Consumer<MobEffectEvent.Remove> listener = evt -> removeEvents.incrementAndGet();
        MinecraftForge.EVENT_BUS.addListener(listener);
        try {
            Player player = helper.makeMockPlayer();
            player.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
            player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(StalwartDungeonsModItems.TUNGSTEN_HELMET.get()));
            player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(StalwartDungeonsModItems.TUNGSTEN_CHESTPLATE.get()));
            player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(StalwartDungeonsModItems.TUNGSTEN_LEGGINGS.get()));
            player.setItemSlot(EquipmentSlot.FEET, new ItemStack(StalwartDungeonsModItems.TUNGSTEN_BOOTS.get()));
            TungstenBodyTickEventProcedure.execute(player);

            player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(StalwartDungeonsModItems.WARTED_TUNGSTEN_HELMET.get()));
            player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(StalwartDungeonsModItems.WARTED_TUNGSTEN_LEGGINGS.get()));
            player.setItemSlot(EquipmentSlot.FEET, new ItemStack(StalwartDungeonsModItems.WARTED_TUNGSTEN_BOOTS.get()));
            WartedTungstenBodyTickEventProcedure.execute(player);

            player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(StalwartDungeonsModItems.CHORUNDUM_ARMOR_HELMET.get()));
            player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(StalwartDungeonsModItems.CHORUNDUM_ARMOR_LEGGINGS.get()));
            player.setItemSlot(EquipmentSlot.FEET, new ItemStack(StalwartDungeonsModItems.CHORUNDUM_ARMOR_BOOTS.get()));
            ChorundumArmorBodyTickEventProcedure.execute(player);

            PropulkEntity propulk = StalwartDungeonsModEntities.PROPULK.get().create(level);
            helper.assertTrue(propulk != null, "Failed to create Propulk");
            propulk.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
            level.addFreshEntity(propulk);
            PropulkOnEntityTickUpdateProcedure.execute(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, propulk);
            helper.assertFalse(propulk.hasEffect(MobEffects.LEVITATION), "Propulk must not apply Levitation to itself");
            helper.assertTrue(removeEvents.get() == 0, "Expected 0 redundant MobEffectEvent.Remove events, got " + removeEvents.get());
        } finally {
            MinecraftForge.EVENT_BUS.unregister(listener);
        }

        // 2. 投掷物禁拾取防刷物品
        SporeBottleEntity bottle = new SporeBottleEntity(StalwartDungeonsModEntities.SPORE_BOTTLE.get(), level);
        bottle.setPos(pos.getX(), pos.getY() + 2, pos.getZ());
        bottle.tick();
        helper.assertTrue(bottle.pickup == AbstractArrow.Pickup.DISALLOWED, "SporeBottleEntity must disallow pickup");

        AwfulDungeonSpawnerEntity spawner = new AwfulDungeonSpawnerEntity(StalwartDungeonsModEntities.AWFUL_DUNGEON_SPAWNER.get(), level);
        spawner.setPos(pos.getX(), pos.getY() + 2, pos.getZ());
        spawner.tick();
        helper.assertTrue(spawner.pickup == AbstractArrow.Pickup.DISALLOWED, "AwfulDungeonSpawnerEntity must disallow pickup");

        // 3. 漂浮投射物真实飞行命中实体施加飘浮效果
        Pig pig = EntityType.PIG.create(level);
        helper.assertTrue(pig != null, "Failed to create pig for levitation projectile test");
        pig.moveTo(pos.getX() + 1.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
        level.addFreshEntity(pig);
        LevitationProjectileEntity proj = new LevitationProjectileEntity(StalwartDungeonsModEntities.LEVITATION_PROJECTILE.get(), level);
        proj.setPos(pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5);
        proj.setDeltaMovement(1.5, 0.0, 0.0);
        level.addFreshEntity(proj);
        proj.tick();
        helper.assertTrue(pig.hasEffect(MobEffects.LEVITATION), "LevitationProjectileEntity must apply Levitation on entity hit");
        helper.succeed();
    }

    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Stalwartdungeonsbugfix.MOD_ID)
    public static void testNetherHammerAutoSmeltShieldAndThunderStrike(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(3, 2, 3));

        // 1. 下界之锤自动熔炼铁矿 -> 铁锭，且精准采集不熔炼
        ItemStack netherHammer = new ItemStack(StalwartDungeonsModItems.NETHER_HAMMER.get());
        List<ItemStack> smeltedDrops = Block.getDrops(Blocks.IRON_ORE.defaultBlockState(), level, pos, null, null, netherHammer);
        helper.assertTrue(smeltedDrops.stream().anyMatch(s -> s.is(Items.IRON_INGOT)),
                "Nether Hammer auto-smelt must smelt IRON_ORE drop into IRON_INGOT");

        ItemStack silkHammer = new ItemStack(StalwartDungeonsModItems.NETHER_HAMMER.get());
        silkHammer.enchant(Enchantments.SILK_TOUCH, 1);
        List<ItemStack> silkDrops = Block.getDrops(Blocks.IRON_ORE.defaultBlockState(), level, pos, null, null, silkHammer);
        helper.assertTrue(silkDrops.stream().anyMatch(s -> s.is(Items.IRON_ORE)),
                "Nether Hammer with Silk Touch must preserve IRON_ORE without smelting");

        // 2. 盾牌松手不再扫描熔炼配方或射箭
        helper.assertFalse(TungstenShieldCanUseRangedItemProcedure.execute(level, new ItemStack(StalwartDungeonsModItems.TUNGSTEN_SHIELD.get())),
                "TungstenShieldCanUseRangedItemProcedure must always return false");

        // 3. 雷击附魔仅允许战锤（铁砧同样受限）
        helper.assertTrue(StalwartDungeonsModEnchantments.THUNDER_STRIKE.get().canEnchant(new ItemStack(StalwartDungeonsModItems.IRON_HAMMER.get())),
                "Thunder Strike must allow Iron Hammer");
        helper.assertFalse(StalwartDungeonsModEnchantments.THUNDER_STRIKE.get().canEnchant(new ItemStack(Items.DIAMOND_SWORD)),
                "Thunder Strike must reject Diamond Sword in anvil");
        helper.succeed();
    }

    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Stalwartdungeonsbugfix.MOD_ID)
    public static void testLootTablesAndWorldgenSafety(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(3, 2, 3));

        // 1. 验证修复后的两张 Boss 战利品表正常掉落且数量在合理区间
        LootTable ghastTable = level.getServer().getLootData().getLootTable(ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "entities/awful_ghast"));
        LootTable sheltererTable = level.getServer().getLootData().getLootTable(ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "entities/shelterer_without_armor"));
        helper.assertTrue(ghastTable != LootTable.EMPTY, "awful_ghast loot table should load");
        helper.assertTrue(sheltererTable != LootTable.EMPTY, "shelterer_without_armor loot table should load");

        AwfulGhastEntity ghast = StalwartDungeonsModEntities.AWFUL_GHAST.get().create(level);
        helper.assertTrue(ghast != null, "Failed to create AwfulGhast for loot test");
        DamageSource dmg = level.damageSources().generic();
        LootParams params = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, ghast)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.DAMAGE_SOURCE, dmg)
                .create(LootContextParamSets.ENTITY);

        for (int i = 0; i < 20; i++) {
            List<ItemStack> gDrops = ghastTable.getRandomItems(params);
            int crystals = gDrops.stream().filter(s -> s.is(StalwartDungeonsModItems.AWFUL_CRYSTAL.get())).mapToInt(ItemStack::getCount).sum();
            helper.assertTrue(crystals >= 1 && crystals <= 4, "awful_crystal drop count must be in [1, 4], got " + crystals);

            List<ItemStack> sDrops = sheltererTable.getRandomItems(params);
            int voidCrystals = sDrops.stream().filter(s -> s.is(StalwartDungeonsModItems.VOID_CRYSTAL.get())).mapToInt(ItemStack::getCount).sum();
            helper.assertTrue(voidCrystals >= 2 && voidCrystals <= 4, "void_crystal drop count must be in [2, 4], got " + voidCrystals);
        }

        // 2. 验证网格间距判定无相邻冲突
        int anchorsInCell = 0;
        for (int cx = 0; cx < 20; cx++) {
            for (int cz = 0; cz < 20; cz++) {
                if (SdWorldgenHelper.isGridAnchorChunk(123456789L, cx, cz, 20, 0x51A7B3C9L)) {
                    anchorsInCell++;
                }
            }
        }
        helper.assertTrue(anchorsInCell == 1, "Expected exactly 1 dungeon anchor per 20x20 chunk cell, got " + anchorsInCell);

        // 3. 验证基岩保护结构处理器拒绝覆盖基岩
        level.setBlock(pos, Blocks.BEDROCK.defaultBlockState(), 3);
        StructureTemplate.StructureBlockInfo info = new StructureTemplate.StructureBlockInfo(pos, Blocks.STONE.defaultBlockState(), null);
        StructureTemplate.StructureBlockInfo processed = SdBedrockProtectProcessor.INSTANCE.processBlock(level, pos, pos, info, info, new StructurePlaceSettings());
        helper.assertTrue(processed == null, "SdBedrockProtectProcessor must block overwriting BEDROCK");
        helper.succeed();
    }

    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Stalwartdungeonsbugfix.MOD_ID)
    public static void testAllRemainingProcedureMixinsApplyAndRunSafely(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(4, 3, 4));
        Player survivalPlayer = helper.makeMockSurvivalPlayer();
        survivalPlayer.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);

        // 1. 远古之火熔炼副手粗铁 -> 铁锭，仅消耗副手并添加冷却
        ItemStack ancientFire = new ItemStack(StalwartDungeonsModItems.ANCIENT_FIRE.get());
        ItemStack rawIron = new ItemStack(Items.RAW_IRON, 2);
        survivalPlayer.setItemInHand(InteractionHand.MAIN_HAND, ancientFire);
        survivalPlayer.setItemInHand(InteractionHand.OFF_HAND, rawIron);
        AncientFireRightClickedInAirProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ(), survivalPlayer, ancientFire);
        helper.assertTrue(survivalPlayer.getOffhandItem().getCount() == 1, "AncientFire should consume 1 from offhand");
        helper.assertTrue(survivalPlayer.getInventory().countItem(Items.IRON_INGOT) >= 1, "AncientFire should yield smelted IRON_INGOT");

        // 2. 黑曜石击发器强制冷却（第二次立即触发不应再扣 TNT）
        ItemStack tnt = new ItemStack(Blocks.TNT, 2);
        survivalPlayer.setItemInHand(InteractionHand.MAIN_HAND, tnt);
        ObsidianStrikerOnBlockRightClickedProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ(), survivalPlayer);
        ObsidianStrikerOnBlockRightClickedProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ(), survivalPlayer);
        helper.assertTrue(survivalPlayer.getMainHandItem().getCount() == 1, "ObsidianStriker must enforce TNT cooldown");

        // 3. 灵魂岩浆砖潜行免疫
        survivalPlayer.setShiftKeyDown(true);
        float hpBefore = survivalPlayer.getHealth();
        SoulMagmaBricksEntityWalksOnTheBlockProcedure.execute(survivalPlayer);
        helper.assertTrue(survivalPlayer.getHealth() == hpBefore, "Sneaking player must not take damage on SoulMagmaBricks");

        // 4. 传送器碰撞未绑定珍珠不应传送到 (0,0,0) 或污染空 NBT
        BlockPos tpPos = helper.absolutePos(new BlockPos(2, 2, 2));
        level.setBlock(tpPos, StalwartDungeonsModBlocks.TELEPORTER.get().defaultBlockState(), 3);
        BlockEntity tpBe = level.getBlockEntity(tpPos);
        if (tpBe instanceof TeleporterBlockBlockEntity teleporterBe) {
            ItemStack unbound = new ItemStack(StalwartDungeonsModItems.TELEPORTER_PEARL.get());
            teleporterBe.setItem(0, unbound);
            double prevX = survivalPlayer.getX();
            TeleporterBlockEntityCollidesInTheBlockProcedure.execute(level, tpPos.getX(), tpPos.getY(), tpPos.getZ(), survivalPlayer);
            helper.assertTrue(Math.abs(survivalPlayer.getX() - prevX) < 0.01, "Unbound TeleporterPearl must NOT teleport entity to (0,0,0)");
            helper.assertFalse(teleporterBe.getItem(0).hasTag(), "Teleporter collision must NOT pollute empty NBT on pearl");
        }

        // 5. 可怕火枪空手命中不应给 Items.AIR 加冷却
        survivalPlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        survivalPlayer.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        AwfulGunBulletHitsBlockProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ());
        helper.assertFalse(survivalPlayer.getCooldowns().isOnCooldown(Items.AIR), "AwfulGun must NOT add cooldown to Items.AIR");

        // 6. 触发其余全部已 Mixin 过程类的类加载与安全空/边界调用，确保 100% 服务端 Mixin 注入成功零异常
        AwfulGhastShootAtDistanceProcedure.execute(survivalPlayer);
        BurningOnPotionActiveTickProcedure.execute(level, survivalPlayer);
        TransientBlockCloseUpdateTickProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ());
        GiddyStatueUpdateTickProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ());
        SporeVaporizerUpdateTickProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ());
        AwfulGhastAltarOnBlockRightClickedProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ(), survivalPlayer);
        NetherKeeperAltarOnBlockRightClickedProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ(), survivalPlayer);
        SheltererAltarOnBlockRightClickedProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ(), survivalPlayer);
        SporeBottleBulletHitsBlockProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ());
        SporeOnEntityTickUpdateProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ(), null);
        SporeProjectilePlayerCollidesWithThisEntityProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ());
        SporeProjectileRightClickedOnEntityProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ(), null, survivalPlayer);
        ShulkerCannonShootProcedure.execute(level, survivalPlayer);
        SdShieldPrProcedure.execute(survivalPlayer, 5.0);
        AwfulGunBulletHitsPlayerProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ(), null, null);
        IronHammerLivingEntityIsHitWithToolProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ(), survivalPlayer, ItemStack.EMPTY);
        NetherHammerLivingEntityIsHitWithToolProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ(), survivalPlayer, survivalPlayer, ItemStack.EMPTY);
        NetherHammerBlockDestroyedWithToolProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ());
        // 仅加载三座地牢触发过程类验证 Mixin 注入，传 null 世界安全返回
        AwfulDungeonsSpawnBlockAddedProcedure.execute(null, 0, 0, 0);
        KeepingCastleSpawnUpdateTickProcedure.execute(null, 0, 0, 0);
        EnderGlobeSpawnUpdateTickProcedure.execute(null, 0, 0, 0);

        helper.succeed();
    }

    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Stalwartdungeonsbugfix.MOD_ID)
    public static void testNativeStructuresRegisteredAndLocatable(GameTestHelper helper) {
        ServerLevel overworld = helper.getLevel();
        Registry<Structure> structureRegistry = overworld.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Registry<StructureSet> setRegistry = overworld.registryAccess().registryOrThrow(Registries.STRUCTURE_SET);

        ResourceLocation awfulId = ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "awful_dungeon");
        ResourceLocation keepingId = ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "keeping_castle");
        ResourceLocation endId = ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "end_dungeon");

        Holder<Structure> awfulHolder = structureRegistry.getHolder(ResourceKey.create(Registries.STRUCTURE, awfulId)).orElse(null);
        Holder<Structure> keepingHolder = structureRegistry.getHolder(ResourceKey.create(Registries.STRUCTURE, keepingId)).orElse(null);
        Holder<Structure> endHolder = structureRegistry.getHolder(ResourceKey.create(Registries.STRUCTURE, endId)).orElse(null);

        helper.assertTrue(awfulHolder != null, "Native structure stalwart_dungeons:awful_dungeon must be registered");
        helper.assertTrue(keepingHolder != null, "Native structure stalwart_dungeons:keeping_castle must be registered");
        helper.assertTrue(endHolder != null, "Native structure stalwart_dungeons:end_dungeon must be registered");
        helper.assertTrue(setRegistry.containsKey(ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "nether_dungeons")),
                "Structure set stalwart_dungeons:nether_dungeons must be registered");
        helper.assertTrue(setRegistry.containsKey(endId),
                "Structure set stalwart_dungeons:end_dungeon must be registered");

        // 验证 SdDungeonPiece 四方向旋转的包围盒与 NBT 序列化反序列化完全一致
        ResourceLocation[] templates = {
                awfulId,
                keepingId,
                ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "shell_globe")
        };
        StructurePieceSerializationContext serCtx = StructurePieceSerializationContext.fromLevel(overworld);
        for (ResourceLocation tplId : templates) {
            for (Rotation rot : Rotation.values()) {
                SdDungeonPiece piece = new SdDungeonPiece(overworld.getStructureManager(), tplId, new BlockPos(100, 50, 200), rot);
                CompoundTag saved = piece.createTag(serCtx);
                SdDungeonPiece loaded = new SdDungeonPiece(overworld.getStructureManager(), saved);
                BoundingBox origBox = piece.getBoundingBox();
                BoundingBox loadBox = loaded.getBoundingBox();
                helper.assertTrue(origBox.equals(loadBox), "SdDungeonPiece NBT roundtrip bounding box mismatch for " + tplId + " rot=" + rot);
                helper.assertTrue(loaded.getRotation() == rot, "SdDungeonPiece NBT roundtrip rotation mismatch for " + tplId);
            }
        }

        // 在真实的下界与末地维度实测原生结构定位（与 /locate structure 走同一底层路径）
        ServerLevel nether = overworld.getServer().getLevel(Level.NETHER);
        ServerLevel end = overworld.getServer().getLevel(Level.END);
        helper.assertTrue(nether != null && end != null, "Nether and End levels must exist for native structure locate test");

        Pair<BlockPos, Holder<Structure>> locatedAwful = nether.getChunkSource().getGenerator()
                .findNearestMapStructure(nether, HolderSet.direct(awfulHolder), BlockPos.ZERO, 100, false);
        helper.assertTrue(locatedAwful != null, "Native /locate must find stalwart_dungeons:awful_dungeon in Nether");

        Pair<BlockPos, Holder<Structure>> locatedKeeping = nether.getChunkSource().getGenerator()
                .findNearestMapStructure(nether, HolderSet.direct(keepingHolder), BlockPos.ZERO, 100, false);
        helper.assertTrue(locatedKeeping != null, "Native /locate must find stalwart_dungeons:keeping_castle in Nether");

        Pair<BlockPos, Holder<Structure>> locatedEnd = end.getChunkSource().getGenerator()
                .findNearestMapStructure(end, HolderSet.direct(endHolder), new BlockPos(1500, 64, 0), 100, false);
        helper.assertTrue(locatedEnd != null, "Native /locate must find stalwart_dungeons:end_dungeon in End");
        long endDistSq = (long) locatedEnd.getFirst().getX() * locatedEnd.getFirst().getX()
                + (long) locatedEnd.getFirst().getZ() * locatedEnd.getFirst().getZ();
        helper.assertTrue(endDistSq >= 1000L * 1000L, "Located End dungeon must stay outside main End island (>= 1000 blocks)");

        helper.succeed();
    }

    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Stalwartdungeonsbugfix.MOD_ID)
    public static void testCustomShieldsBlockingAndRepair(GameTestHelper helper) {
        ItemStack tungsten = new ItemStack(StalwartDungeonsModItems.TUNGSTEN_SHIELD.get());
        ItemStack warted = new ItemStack(StalwartDungeonsModItems.WARTED_TUNGSTEN_SHIELD.get());
        ItemStack chorundum = new ItemStack(StalwartDungeonsModItems.CHORUNDUM_SHIELD.get());

        helper.assertTrue(tungsten.canPerformAction(ToolActions.SHIELD_BLOCK), "Tungsten shield must support ToolActions.SHIELD_BLOCK");
        helper.assertTrue(warted.canPerformAction(ToolActions.SHIELD_BLOCK), "Warted shield must support ToolActions.SHIELD_BLOCK");
        helper.assertTrue(chorundum.canPerformAction(ToolActions.SHIELD_BLOCK), "Chorundum shield must support ToolActions.SHIELD_BLOCK");

        helper.assertTrue(tungsten.getItem().isValidRepairItem(tungsten, new ItemStack(StalwartDungeonsModItems.TUNGSTEN_INGOT.get())),
                "Tungsten shield should repair with tungsten_ingot");
        helper.assertTrue(warted.getItem().isValidRepairItem(warted, new ItemStack(StalwartDungeonsModItems.TUNGSTEN_INGOT.get())),
                "Warted shield should repair with tungsten_ingot");
        helper.assertTrue(chorundum.getItem().isValidRepairItem(chorundum, new ItemStack(StalwartDungeonsModItems.CHORUNDUM.get())),
                "Chorundum shield should repair with chorundum");

        Player player = helper.makeMockSurvivalPlayer();
        player.setItemInHand(InteractionHand.OFF_HAND, tungsten);
        player.startUsingItem(InteractionHand.OFF_HAND);
        for (int i = 0; i < 6; i++) {
            player.tick();
        }
        helper.assertTrue(player.isBlocking(), "Player raising Tungsten Shield for >5 ticks must have isBlocking() == true");

        // 验证旧的 SdShieldPrProcedure 不再与原版格挡重复扣耐久
        int dmgBefore = tungsten.getDamageValue();
        SdShieldPrProcedure.execute(player, 10.0);
        helper.assertTrue(tungsten.getDamageValue() == dmgBefore, "SdShieldPrProcedure must yield to vanilla shield durability when fixCustomShields is enabled");

        helper.succeed();
    }

    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Stalwartdungeonsbugfix.MOD_ID)
    public static void testSevenMissingBlockLootTablesNowDrop(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(3, 2, 3));
        ItemStack pickaxe = new ItemStack(Items.IRON_PICKAXE);

        Block[] slabs = {
                StalwartDungeonsModBlocks.NETHER_BRICKS_AND_NETHERRACK_SLAB.get(),
                StalwartDungeonsModBlocks.NETHER_BRICKS_AND_SOUL_SAND_SLAB.get(),
                StalwartDungeonsModBlocks.PURPUR_BRICKS_AND_ENDSTONE_SLAB.get()
        };
        for (Block slab : slabs) {
            List<ItemStack> singleDrops = Block.getDrops(slab.defaultBlockState(), level, pos, null, null, pickaxe);
            int singleCnt = singleDrops.stream().filter(s -> s.is(slab.asItem())).mapToInt(ItemStack::getCount).sum();
            helper.assertTrue(singleCnt == 1, "Single slab must drop 1 item: " + ForgeRegistries.BLOCKS.getKey(slab) + ", got " + singleCnt);

            BlockState doubleState = slab.defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE);
            List<ItemStack> doubleDrops = Block.getDrops(doubleState, level, pos, null, null, pickaxe);
            int doubleCnt = doubleDrops.stream().filter(s -> s.is(slab.asItem())).mapToInt(ItemStack::getCount).sum();
            helper.assertTrue(doubleCnt == 2, "Double slab must drop 2 items: " + ForgeRegistries.BLOCKS.getKey(slab) + ", got " + doubleCnt);
        }

        Block[] solids = {
                StalwartDungeonsModBlocks.NETHER_BRICKS_AND_NETHERRACK_STAIRS.get(),
                StalwartDungeonsModBlocks.NETHER_BRICKS_AND_SOUL_SAND_STAIRS.get(),
                StalwartDungeonsModBlocks.PURPUR_BRICKS_AND_ENDSTONE.get(),
                StalwartDungeonsModBlocks.PURPUR_BRICKS_AND_ENDSTONE_STAIRS.get()
        };
        for (Block block : solids) {
            List<ItemStack> drops = Block.getDrops(block.defaultBlockState(), level, pos, null, null, pickaxe);
            int cnt = drops.stream().filter(s -> s.is(block.asItem())).mapToInt(ItemStack::getCount).sum();
            helper.assertTrue(cnt == 1, "Block must drop itself: " + ForgeRegistries.BLOCKS.getKey(block) + ", got " + cnt);
        }

        helper.succeed();
    }

    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Stalwartdungeonsbugfix.MOD_ID)
    public static void testSmithingRecipesSheltererFallCocoonCreativeAndThunderGuard(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(4, 2, 4));

        // 1. 全部 6 个锻造台配方均不再被标记为 isIncomplete，且下界合金锤与疣化钨盾均可正常匹配合成
        List<SmithingRecipe> sdSmithing = level.getRecipeManager().getAllRecipesFor(RecipeType.SMITHING).stream()
                .filter(r -> "stalwart_dungeons".equals(r.getId().getNamespace()))
                .toList();
        helper.assertTrue(sdSmithing.size() == 6, "Expected 6 Stalwart Dungeons smithing recipes, found " + sdSmithing.size());
        for (SmithingRecipe recipe : sdSmithing) {
            helper.assertFalse(recipe.isIncomplete(), "Smithing recipe must not be marked incomplete: " + recipe.getId());
        }

        SimpleContainer netheriteInv = new SimpleContainer(3);
        netheriteInv.setItem(0, new ItemStack(Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE));
        netheriteInv.setItem(1, new ItemStack(StalwartDungeonsModItems.DIAMOND_HAMMER.get()));
        netheriteInv.setItem(2, new ItemStack(Items.NETHERITE_INGOT));
        SmithingRecipe netheriteMatch = level.getRecipeManager().getRecipeFor(RecipeType.SMITHING, netheriteInv, level).orElse(null);
        helper.assertTrue(netheriteMatch != null && netheriteMatch.assemble(netheriteInv, level.registryAccess()).is(StalwartDungeonsModItems.NETHERITE_HAMMER.get()),
                "Netherite Hammer smithing recipe must work with Netherite Upgrade Smithing Template");

        SimpleContainer wartedInv = new SimpleContainer(3);
        wartedInv.setItem(0, ItemStack.EMPTY);
        wartedInv.setItem(1, new ItemStack(StalwartDungeonsModItems.TUNGSTEN_SHIELD.get()));
        wartedInv.setItem(2, new ItemStack(StalwartDungeonsModItems.SPORE_BOTTLE.get()));
        SmithingRecipe wartedMatch = level.getRecipeManager().getRecipeFor(RecipeType.SMITHING, wartedInv, level).orElse(null);
        helper.assertTrue(wartedMatch != null && wartedMatch.assemble(wartedInv, level.registryAccess()).is(StalwartDungeonsModItems.WARTED_TUNGSTEN_SHIELD.get()),
                "Warted Tungsten Shield smithing recipe must work without template");

        // 2. 无甲庇护者二阶段 Boss 免疫摔落伤害
        SheltererWithoutArmorEntity p2 = StalwartDungeonsModEntities.SHELTERER_WITHOUT_ARMOR.get().create(level);
        helper.assertTrue(p2 != null, "Failed to create SheltererWithoutArmor");
        p2.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
        level.addFreshEntity(p2);
        float hpBefore = p2.getHealth();
        boolean hurtResult = p2.hurt(level.damageSources().fall(), 50.0F);
        helper.assertFalse(hurtResult, "SheltererWithoutArmor must be immune to FALL damage");
        helper.assertTrue(p2.getHealth() == hpBefore, "SheltererWithoutArmor health must stay unchanged on FALL damage");

        // 3. 下界疣茧自动弹出创造模式玩家，不锁死下车
        NetherWartCocoonEntity cocoon = StalwartDungeonsModEntities.NETHER_WART_COCOON.get().create(level);
        helper.assertTrue(cocoon != null, "Failed to create NetherWartCocoon");
        cocoon.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0.0F, 0.0F);
        level.addFreshEntity(cocoon);
        Player creativePlayer = helper.makeMockPlayer();
        creativePlayer.startRiding(cocoon, true);
        helper.assertTrue(creativePlayer.getVehicle() == cocoon, "Precondition: creative player mounted cocoon");
        NetherWartCocoonOnEntityTickUpdateProcedure.execute(level, cocoon.getX(), cocoon.getY(), cocoon.getZ(), cocoon);
        helper.assertTrue(creativePlayer.getVehicle() == null, "NetherWartCocoon must eject Creative/Spectator players");

        // 4. 雷击附魔保护召唤者本人与地面掉落物免受近战落雷误伤，同时正常劈中怪物
        LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
        helper.assertTrue(bolt != null, "Failed to create LightningBolt");
        bolt.getPersistentData().putBoolean(SdItemLogicHelper.TAG_THUNDER_STRIKE_BOLT, true);
        bolt.getPersistentData().putUUID(SdItemLogicHelper.TAG_THUNDER_STRIKE_OWNER, creativePlayer.getUUID());

        EntityStruckByLightningEvent ownerEvt = new EntityStruckByLightningEvent(creativePlayer, bolt);
        MinecraftForge.EVENT_BUS.post(ownerEvt);
        helper.assertTrue(ownerEvt.isCanceled(), "Thunder Strike lightning must not hurt its own attacker");

        ItemEntity drop = new ItemEntity(level, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.DIAMOND));
        EntityStruckByLightningEvent itemEvt = new EntityStruckByLightningEvent(drop, bolt);
        MinecraftForge.EVENT_BUS.post(itemEvt);
        helper.assertTrue(itemEvt.isCanceled(), "Thunder Strike lightning must not destroy ItemEntity drops");

        Pig enemy = EntityType.PIG.create(level);
        helper.assertTrue(enemy != null, "Failed to create target Pig");
        EntityStruckByLightningEvent enemyEvt = new EntityStruckByLightningEvent(enemy, bolt);
        MinecraftForge.EVENT_BUS.post(enemyEvt);
        helper.assertFalse(enemyEvt.isCanceled(), "Thunder Strike lightning must still strike enemy targets");

        // 5. 验证 7 张带抢夺加成的实体战利品表全部加载正常
        String[] entityTables = {
                "entities/awful_ghast",
                "entities/shelterer_without_armor",
                "entities/shelterer",
                "entities/nether_keeper",
                "entities/giddy_blaze",
                "entities/reinforced_blaze",
                "entities/incomplete_wither"
        };
        for (String path : entityTables) {
            LootTable table = level.getServer().getLootData().getLootTable(ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", path));
            helper.assertTrue(table != LootTable.EMPTY, "Entity loot table must load cleanly: " + path);
        }

        helper.succeed();
    }

    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Stalwartdungeonsbugfix.MOD_ID)
    public static void testDataDrivenLootTablesAndLocalizedTexts(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos pos = helper.absolutePos(new BlockPos(2, 2, 2));

        // 1. 验证数据驱动方块战利品表（双层台阶掉 2 个，transient_block_open 掉 transient_block_close）
        BlockState doubleSlab = StalwartDungeonsModBlocks.PURPUR_BRICKS_SLAB.get().defaultBlockState().setValue(SlabBlock.TYPE, SlabType.DOUBLE);
        LootParams.Builder pickBuilder = new LootParams.Builder(level)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.TOOL, new ItemStack(Items.DIAMOND_PICKAXE));
        List<ItemStack> slabDrops = doubleSlab.getDrops(pickBuilder);
        helper.assertTrue(slabDrops.size() == 1 && slabDrops.get(0).getCount() == 2,
                "Double purpur_bricks_slab must drop 2 slabs via JSON loot table");

        BlockState transientOpen = StalwartDungeonsModBlocks.TRANSIENT_BLOCK_OPEN.get().defaultBlockState();
        List<ItemStack> transientDrops = transientOpen.getDrops(pickBuilder);
        helper.assertTrue(transientDrops.stream().anyMatch(s -> s.is(StalwartDungeonsModBlocks.TRANSIENT_BLOCK_CLOSE.get().asItem())),
                "transient_block_open must drop transient_block_close via JSON loot table");

        // 2. 验证 propulk 与 shulker_cannon 实体 JSON 战利品表已加载且正常掉落
        LootTable propulkTable = level.getServer().getLootData().getLootTable(ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "entities/propulk"));
        helper.assertTrue(propulkTable != LootTable.EMPTY, "entities/propulk loot table must exist");
        PropulkEntity propulk = StalwartDungeonsModEntities.PROPULK.get().create(level);
        propulk.setPos(pos.getX(), pos.getY(), pos.getZ());
        LootParams entParams = new LootParams.Builder(level)
                .withParameter(LootContextParams.THIS_ENTITY, propulk)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(pos))
                .withParameter(LootContextParams.DAMAGE_SOURCE, level.damageSources().generic())
                .create(LootContextParamSets.ENTITY);
        helper.assertTrue(propulkTable.getRandomItems(entParams).stream().anyMatch(s -> s.is(Items.SHULKER_SHELL)),
                "propulk JSON loot table must drop shulker_shell");

        LootTable cannonTable = level.getServer().getLootData().getLootTable(ResourceLocation.fromNamespaceAndPath("stalwart_dungeons", "entities/shulker_cannon"));
        helper.assertTrue(cannonTable != LootTable.EMPTY, "entities/shulker_cannon loot table must exist");
        helper.assertTrue(cannonTable.getRandomItems(entParams).stream().anyMatch(s -> s.is(StalwartDungeonsModItems.SHULKER_CANNON_ITEM.get())),
                "shulker_cannon JSON loot table must drop shulker_cannon_item");

        // 3. 验证原硬编码 Tooltip 与传送器方块实体标题均已改为 TranslatableContents
        List<Component> hammerTip = new ArrayList<>();
        StalwartDungeonsModItems.NETHER_HAMMER.get().appendHoverText(
                new ItemStack(StalwartDungeonsModItems.NETHER_HAMMER.get()), level, hammerTip, TooltipFlag.Default.NORMAL);
        helper.assertTrue(hammerTip.size() == 2
                        && hammerTip.get(0).getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents tc0
                        && "tooltip.stalwart_dungeons.when_used".equals(tc0.getKey()),
                "NetherHammer tooltip must use translation keys");

        List<Component> daggerTip = new ArrayList<>();
        StalwartDungeonsModItems.AWFUL_DAGGER.get().appendHoverText(
                new ItemStack(StalwartDungeonsModItems.AWFUL_DAGGER.get()), level, daggerTip, TooltipFlag.Default.NORMAL);
        helper.assertTrue(daggerTip.size() == 2
                        && daggerTip.get(0).getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents tc1
                        && "tooltip.stalwart_dungeons.when_hit".equals(tc1.getKey()),
                "AwfulDagger tooltip must use translation keys");

        level.setBlock(pos, StalwartDungeonsModBlocks.TELEPORTER.get().defaultBlockState(), 3);
        TeleporterBlockBlockEntity tpBe = (TeleporterBlockBlockEntity) level.getBlockEntity(pos);
        helper.assertTrue(tpBe != null
                        && tpBe.getDisplayName().getContents() instanceof net.minecraft.network.chat.contents.TranslatableContents tc2
                        && "block.stalwart_dungeons.teleporter".equals(tc2.getKey()),
                "TeleporterBlockBlockEntity displayName must use block translation key");

        helper.succeed();
    }

    @GameTest(template = EMPTY_TEMPLATE, templateNamespace = Stalwartdungeonsbugfix.MOD_ID)
    public static void testMcrCatalogAuditFixesAndTopOverridePack(GameTestHelper helper) {
        org.spongepowered.asm.mixin.MixinEnvironment.getCurrentEnvironment().audit();

        ServerLevel level = helper.getLevel();
        BlockPos absPos = helper.absolutePos(new BlockPos(2, 3, 2));

        // 1. 验证全部 21 种实体追踪半径已收敛（弹射物 6、常规实体 8、Boss 10）
        int checked = 0;
        for (var entry : StalwartDungeonsModEntities.REGISTRY.getEntries()) {
            int range = entry.get().clientTrackingRange();
            helper.assertTrue(range >= 4 && range <= 10,
                    "Stalwart Dungeons entity tracking range must be 4..10 chunks, got " + range + " for " + entry.getId());
            checked++;
        }
        helper.assertTrue(checked == 21, "Expected 21 Stalwart Dungeons entities, got " + checked);
        helper.assertTrue(StalwartDungeonsModEntities.AWFUL_GHAST.get().clientTrackingRange() == 10, "Awful Ghast tracking range must be 10");
        helper.assertTrue(StalwartDungeonsModEntities.AWFUL_GUN.get().clientTrackingRange() == 6, "Awful Gun projectile tracking range must be 6");
        helper.assertTrue(StalwartDungeonsModEntities.PROPULK.get().clientTrackingRange() == 8, "Propulk tracking range must be 8");

        // 2. 验证原未补齐的 AbstractArrow 实体已统一禁用拾取并具备最大飞行寿命清理
        net.mcreator.stalwartdungeons.entity.AwfulGunEntity bullet = new net.mcreator.stalwartdungeons.entity.AwfulGunEntity(
                StalwartDungeonsModEntities.AWFUL_GUN.get(), level);
        bullet.setPos(absPos.getX() + 0.5, absPos.getY(), absPos.getZ() + 0.5);
        level.addFreshEntity(bullet);
        bullet.tick();
        helper.assertTrue(bullet.pickup == AbstractArrow.Pickup.DISALLOWED, "AwfulGunEntity pickup must be DISALLOWED");
        bullet.tickCount = 400;
        bullet.tick();
        helper.assertTrue(bullet.isRemoved(), "AwfulGunEntity reaching 400 ticks must discard");

        LevitationProjectileEntity lev = new LevitationProjectileEntity(StalwartDungeonsModEntities.LEVITATION_PROJECTILE.get(), level);
        lev.setPos(absPos.getX() + 0.5, absPos.getY(), absPos.getZ() + 0.5);
        level.addFreshEntity(lev);
        lev.tick();
        helper.assertTrue(lev.pickup == AbstractArrow.Pickup.DISALLOWED, "LevitationProjectileEntity pickup must be DISALLOWED");
        lev.discard();

        // 3. 验证顶层内置客户端覆盖资源包资产路径完整
        var modFile = net.minecraftforge.fml.ModList.get().getModFileById(Stalwartdungeonsbugfix.MOD_ID);
        helper.assertTrue(modFile != null, "ModFile must exist");
        helper.assertTrue(java.nio.file.Files.exists(modFile.getFile().findResource("resourcepacks/sd_client_overrides/pack.mcmeta")),
                "TOP override pack pack.mcmeta must exist");
        helper.assertTrue(java.nio.file.Files.exists(modFile.getFile().findResource(
                        "resourcepacks/sd_client_overrides/assets/stalwart_dungeons/models/item/tungsten_shield.json")),
                "TOP override pack tungsten_shield.json must exist");

        helper.succeed();
    }
}
