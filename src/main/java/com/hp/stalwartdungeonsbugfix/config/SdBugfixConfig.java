package com.hp.stalwartdungeonsbugfix.config;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * 坚固地牢 Bug 修复与性能优化模组配置。
 * 所有修复项均可按模块开关，并支持调整地牢间距、高度、实体上限与冷却参数。
 */
public final class SdBugfixConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ForgeConfigSpec CLIENT_SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_GLOBAL_FIXES;

    // 核心队列修复
    public static final ForgeConfigSpec.BooleanValue OPTIMIZE_WORK_QUEUE;

    // 世界生成与结构放置修复
    public static final ForgeConfigSpec.BooleanValue USE_NATIVE_STRUCTURES;
    public static final ForgeConfigSpec.BooleanValue FIX_DUNGEON_WORLDGEN;
    public static final ForgeConfigSpec.IntValue AWFUL_DUNGEON_SPACING_CHUNKS;
    public static final ForgeConfigSpec.IntValue KEEPING_CASTLE_SPACING_CHUNKS;
    public static final ForgeConfigSpec.IntValue END_DUNGEON_SPACING_CHUNKS;
    public static final ForgeConfigSpec.IntValue NETHER_DUNGEON_MIN_Y;
    public static final ForgeConfigSpec.IntValue NETHER_DUNGEON_MAX_Y;
    public static final ForgeConfigSpec.IntValue END_DUNGEON_MIN_SURFACE_Y;
    public static final ForgeConfigSpec.BooleanValue OPTIMIZE_DUNGEON_STRUCTURE_PLACEMENT;

    // 方块修复与性能优化
    public static final ForgeConfigSpec.BooleanValue FIX_BLOCK_MAP_COLORS;
    public static final ForgeConfigSpec.BooleanValue FIX_HARDCODED_BLOCK_HARVEST;
    public static final ForgeConfigSpec.BooleanValue FIX_SOLID_BLOCK_LIGHT_BLOCKING;
    public static final ForgeConfigSpec.BooleanValue OPTIMIZE_TRANSIENT_BLOCK_TICKING;
    public static final ForgeConfigSpec.BooleanValue OPTIMIZE_IGNITER_CLIENT_PARTICLES;
    public static final ForgeConfigSpec.BooleanValue FIX_TRAP_BLOCKS_AND_TELEPORTER;
    public static final ForgeConfigSpec.IntValue MAX_NEARBY_SPORE_PROJECTILES;
    public static final ForgeConfigSpec.IntValue TELEPORTER_COOLDOWN_TICKS;

    // 实体 AI、崩溃与刷怪上限修复
    public static final ForgeConfigSpec.BooleanValue FIX_ENTITY_CRASHES_AND_AI;
    public static final ForgeConfigSpec.BooleanValue FIX_ENTITY_MOB_CATEGORIES;
    public static final ForgeConfigSpec.BooleanValue FIX_BOSS_AND_MOB_PROCEDURES;
    public static final ForgeConfigSpec.IntValue MAX_NEARBY_GIDDY_BLAZES;

    // 物品与工具机制修复
    public static final ForgeConfigSpec.BooleanValue FIX_ITEM_PROCEDURES_AND_MECHANICS;
    public static final ForgeConfigSpec.BooleanValue FIX_CUSTOM_SHIELDS;
    public static final ForgeConfigSpec.BooleanValue FIX_SMITHING_RECIPES;
    public static final ForgeConfigSpec.BooleanValue THUNDER_STRIKE_PROTECT_ATTACKER;
    public static final ForgeConfigSpec.BooleanValue THUNDER_STRIKE_REQUIRE_SNEAK;

    // 客户端渲染缓存与界面优化
    public static final ForgeConfigSpec.BooleanValue CACHE_ARMOR_HELMET_MODELS;
    public static final ForgeConfigSpec.BooleanValue CACHE_ENTITY_RENDERER_TEXTURES;
    public static final ForgeConfigSpec.BooleanValue FIX_ITEM_TOOLTIP_TYPOS;

    static {
        ForgeConfigSpec.Builder common = new ForgeConfigSpec.Builder();

        common.comment("总开关 / Master Switch").push("general");
        ENABLE_GLOBAL_FIXES = common
                .comment("是否启用坚固地牢 Bug 修复与性能优化总开关 / Master toggle for all Stalwart Dungeons bug fixes and optimizations.")
                .define("enableGlobalFixes", true);
        common.pop();

        common.comment("核心调度队列 / Core Work Queue").push("core");
        OPTIMIZE_WORK_QUEUE = common
                .comment("修复服务端延迟队列缺少线程校验、异常中断、非正延迟永久泄漏与跨存档残留问题（检测到 MCreator Optimizer 时自动让位） / Fix queueServerWork thread safety, exception isolation, O(1) removal, non-positive delay leak, and cross-world cleanup.")
                .define("optimizeWorkQueue", true);
        common.pop();

        common.comment("世界生成与结构放置 / Worldgen and Structure Placement").push("worldgen");
        USE_NATIVE_STRUCTURES = common
                .comment("将三座地牢转为原生 Minecraft Structure（支持 /locate structure、探险家指南针、FTB Quests 与分区块渐进生成并停用旧触发方块 Feature；关闭时回退至修复后的 Feature 生成） / Replace legacy placed_feature trigger-block generation with native Minecraft Structures (supports /locate structure, Explorer's Compass, FTB Quests, and chunk-by-chunk worldgen without pop-in freezes).")
                .define("useNativeStructures", true);
        FIX_DUNGEON_WORLDGEN = common
                .comment("修复下界与末地地牢生成：防止重叠互切、防止击穿下界基岩顶/底、减少悬空与岩浆淹没、排除末地虚空群系与虚空坐标并修复四方向旋转 / Fix Nether/End dungeon worldgen: prevent overlaps, protect Nether bedrock roof/floor, avoid midair/lava spawns, exclude the_void and End void coordinates.")
                .define("fixDungeonWorldgen", true);
        AWFUL_DUNGEON_SPACING_CHUNKS = common
                .comment("可怕地牢的最小网格间距（区块） / Minimum chunk grid spacing for Awful Dungeons.")
                .defineInRange("awfulDungeonSpacingChunks", 20, 6, 128);
        KEEPING_CASTLE_SPACING_CHUNKS = common
                .comment("守望城堡的最小网格间距（区块） / Minimum chunk grid spacing for Keeping Castles.")
                .defineInRange("keepingCastleSpacingChunks", 20, 6, 128);
        END_DUNGEON_SPACING_CHUNKS = common
                .comment("末地地牢的最小网格间距（区块） / Minimum chunk grid spacing for End Dungeons.")
                .defineInRange("endDungeonSpacingChunks", 24, 6, 128);
        NETHER_DUNGEON_MIN_Y = common
                .comment("下界地牢触发方块允许生成的最低 Y 坐标 / Minimum spawn Y for Nether dungeon anchor blocks.")
                .defineInRange("netherDungeonMinY", 34, 16, 90);
        NETHER_DUNGEON_MAX_Y = common
                .comment("下界地牢触发方块允许生成的最高 Y 坐标（保证顶部低于基岩顶 Y=127） / Maximum spawn Y for Nether dungeon anchor blocks to protect the Y=127 bedrock roof.")
                .defineInRange("netherDungeonMaxY", 96, 40, 104);
        END_DUNGEON_MIN_SURFACE_Y = common
                .comment("末地地牢生成所需的最低地形表面高度 / Minimum End island surface Y required to place an End Dungeon.")
                .defineInRange("endDungeonMinSurfaceY", 45, 10, 120);
        OPTIMIZE_DUNGEON_STRUCTURE_PLACEMENT = common
                .comment("优化三座地牢触发方块与刷怪笼投掷物的结构放置：仅服务端执行、忽略结构方块、保护基岩、改为 flag=2 消除数万方块邻接更新卡顿 / Optimize dungeon structure placement: server-only, ignore structure blocks, protect bedrock, and use flag 2 to avoid massive neighbor-update stalls.")
                .define("optimizeDungeonStructurePlacement", true);
        common.pop();

        common.comment("方块修复与性能优化 / Blocks and Performance").push("blocks");
        FIX_BLOCK_MAP_COLORS = common
                .comment("在方块注册阶段为全部 39 个坚固地牢方块补齐原版 MapColor，修复 Xaero 大小地图与原版地图隐形 / Assign accurate vanilla MapColor to all 39 Stalwart Dungeons blocks to fix invisibility on Xaero maps and vanilla maps.")
                .define("fixBlockMapColors", true);
        FIX_HARDCODED_BLOCK_HARVEST = common
                .comment("将 23 个硬编码 PickaxeItem 判断的方块恢复为原版标签挖掘体系（兼容匠魂、Tetra 等模块化工具） / Delegate hardcoded PickaxeItem canHarvestBlock checks on 23 blocks back to vanilla tag-based harvesting.")
                .define("fixHardcodedBlockHarvest", true);
        FIX_SOLID_BLOCK_LIGHT_BLOCKING = common
                .comment("修复 15 个实心不透明方块误返回透光/遮光度为 0 导致的地牢漏光与光照引擎性能开销 / Fix 15 solid opaque blocks wrongly returning propagatesSkylightDown=true and getLightBlock=0.")
                .define("fixSolidBlockLightBlocking", true);
        OPTIMIZE_TRANSIENT_BLOCK_TICKING = common
                .comment("修复瞬态方块超时永久敞开、每刻广播方块更新、0 音量音效发包及每城堡 554 块闲置 10t 轮询卡顿 / Fix Transient Block stuck-open bug, sendBlockUpdated spam, 0-volume sound packets, and idle 10-tick polling lag across 554 blocks per castle.")
                .define("optimizeTransientBlockTicking", true);
        OPTIMIZE_IGNITER_CLIENT_PARTICLES = common
                .comment("将点燃器服务端永久 10t 粒子定时任务与发包改为客户端 animateTick 原生渲染 / Move Igniter ambient soul flame particles from permanent server scheduled ticks to client-side animateTick.")
                .define("optimizeIgniterClientParticles", true);
        FIX_TRAP_BLOCKS_AND_TELEPORTER = common
                .comment("修复传送器 GUI 无限距离、槽位错判、漏斗塞杂物、碰撞 22 匿名类/客户端执行/未绑定回原点/强制加载区块/无冷却连伤，以及传送珍珠悬停空 NBT 污染、眩晕雕像/孢子喷发器受旁观者触发和灵魂岩浆砖无视潜行 / Fix Teleporter GUI/BE/collision/pearl NBT pollution, Giddy Statue, Spore Vaporizer, and Soul Magma Bricks.")
                .define("fixTrapBlocksAndTeleporter", true);
        MAX_NEARBY_SPORE_PROJECTILES = common
                .comment("孢子喷发器周围 16 格内允许同时存在的最大孢子投射物数量 / Max SporeProjectile entities allowed within 16 blocks of a Spore Vaporizer.")
                .defineInRange("maxNearbySporeProjectiles", 12, 2, 64);
        TELEPORTER_COOLDOWN_TICKS = common
                .comment("传送器对单个实体的传送冷却（tick） / Per-entity cooldown in ticks after using a Teleporter block.")
                .defineInRange("teleporterCooldownTicks", 40, 0, 200);
        common.pop();

        common.comment("实体 AI、崩溃与过程脚本修复 / Entities, AI and Procedures").push("entities");
        FIX_ENTITY_CRASHES_AND_AI = common
                .comment("修复可怕恶魂目标空指针崩溃、飞行怪盲选实心块寻路卡顿与重复漫步目标、无甲庇护者 AI 优先级倒置、下界守卫驯服攻击主人与繁殖空指针、庇护者死亡误删掉落与重复刷二阶段、潜影加农炮与孢子投射物爆炸免疫漏判 PLAYER_EXPLOSION、漂浮投射物命中失效及 4 种投掷物可拾取刷物品 / Fix Awful Ghast target NPE, airborne stroll lag, SheltererWithoutArmor inverted AI priorities, Nether Keeper taming/breeding bugs, Shelterer death drops, Shulker Cannon & Spore Projectile explosion immunity missing PLAYER_EXPLOSION, Levitation projectile hit, and projectile pickup dupes.")
                .define("fixEntityCrashesAndAi", true);
        FIX_ENTITY_MOB_CATEGORIES = common
                .comment("修正 propulk 实体分类为 MONSTER（防止抢占友好生物刷怪上限）与 spore_projectile 分类为 MISC（防止占用敌对刷怪上限） / Fix MobCategory of propulk (CREATURE -> MONSTER) and spore_projectile (MONSTER -> MISC).")
                .define("fixEntityMobCategories", true);
        FIX_BOSS_AND_MOB_PROCEDURES = common
                .comment("修复潜影加农炮与孢子到期调用 Minecraft.getInstance 导致专服崩溃、破坏 ItemStack.EMPTY、推进虫自施飘浮与护甲每刻事件风暴、下界守卫无限召怪/血量硬编码/错误粒子坐标、祭坛把 Boss 生成在地板内及无视冷却创造消耗 / Fix dedicated-server Minecraft.getInstance crashes, ItemStack.EMPTY mutation, removeEffect event storms, Nether Keeper minion cap/HP threshold/particles, Spore refresh/early-cure cocoon spawn, Boss Altars, Obsidian Striker, and Shulker Cannon.")
                .define("fixBossAndMobProcedures", true);
        MAX_NEARBY_GIDDY_BLAZES = common
                .comment("下界守卫周围 24 格内允许同时存在的最大眩晕烈焰人数量 / Max Giddy Blazes allowed within 24 blocks of a Nether Keeper before summoning pauses.")
                .defineInRange("maxNearbyGiddyBlazes", 6, 1, 32);
        common.pop();

        common.comment("物品、附魔与工具机制修复 / Items, Enchantments and Tools").push("items");
        FIX_ITEM_PROCEDURES_AND_MECHANICS = common
                .comment("修复下界之锤自动熔炼因方块已被破坏为空气而失效、三面盾牌松手扫描全服熔炼配方并可能误射隐形箭、远古之火与盾牌事件客户端错误、雷击附魔每帧分配与铁砧误附魔、锤类闪电未设施法者及可怕火枪幽灵火 / Fix Nether Hammer broken auto-smelt, 3 shields smelting recipe scan hack, Ancient Fire, SdShieldPr, Thunder Strike enchantment, hammer lightning cause, and Awful Gun ghost fire.")
                .define("fixItemProceduresAndMechanics", true);
        FIX_CUSTOM_SHIELDS = common
                .comment("修复钨盾/疣化钨盾/紫颂盾在 1.20.1 缺失 ToolActions.SHIELD_BLOCK 导致完全无法格挡的问题，补齐铁砧修理材料并避免与原版盾牌耐久消耗重复扣血 / Restore real shield blocking (ToolActions.SHIELD_BLOCK), anvil repair ingredients, and vanilla durability/disable behavior on Tungsten, Warted Tungsten, and Chorundum shields.")
                .define("fixCustomShields", true);
        FIX_SMITHING_RECIPES = common
                .comment("修复 1.20.1 锻造台空模板配方被 Forge 误判为不完整（isIncomplete）导致 JEI/配方书异常的问题 / Prevent template-free Stalwart Dungeons smithing recipes (Warted Tungsten upgrades) from being flagged as incomplete in 1.20.1.")
                .define("fixSmithingRecipes", true);
        THUNDER_STRIKE_PROTECT_ATTACKER = common
                .comment("雷击附魔触发近战落雷时保护攻击者本人免受自己召唤的闪电伤害 / Protect the attacker from being hurt by lightning bolts summoned by their own Thunder Strike hammer.")
                .define("thunderStrikeProtectAttacker", true);
        THUNDER_STRIKE_REQUIRE_SNEAK = common
                .comment("雷击附魔是否保持原模组必须潜行攻击才触发落雷的设定（设为 false 时普通攻击即可按概率触发） / Whether Thunder Strike requires sneaking while attacking (original mod behavior). Set false to trigger on normal hammer attacks.")
                .define("thunderStrikeRequireSneak", true);
        common.pop();

        COMMON_SPEC = common.build();

        ForgeConfigSpec.Builder client = new ForgeConfigSpec.Builder();
        client.comment("客户端渲染与提示优化 / Client Rendering and Tooltip Optimizations").push("client");
        CACHE_ARMOR_HELMET_MODELS = client
                .comment("缓存钨/疣化钨/紫颂锭头盔模型，消除每帧重复 bakeLayer 与大量对象分配 / Cache custom helmet models for Tungsten, Warted Tungsten, and Chorundum helmets to stop per-frame bakeLayer allocations.")
                .define("cacheArmorHelmetModels", true);
        CACHE_ENTITY_RENDERER_TEXTURES = client
                .comment("缓存 11 个实体渲染器的贴图 ResourceLocation 与庇护者发光层 RenderType / Cache ResourceLocation instances in 11 entity renderers and EyesLayer RenderType in SheltererRenderer.")
                .define("cacheEntityRendererTextures", true);
        FIX_ITEM_TOOLTIP_TYPOS = client
                .comment("修正下界之锤、三套护甲与可怕匕首硬编码 Tooltip 中的英文拼写错误（Brun/Imunise/ennemy） / Fix hardcoded English tooltip typos on Nether Hammer, armor sets, and Awful Dagger.")
                .define("fixItemTooltipTypos", true);
        client.pop();

        CLIENT_SPEC = client.build();
    }

    private SdBugfixConfig() {
    }

    private static boolean commonEnabled(ForgeConfigSpec.BooleanValue value) {
        if (!COMMON_SPEC.isLoaded()) {
            return true;
        }
        return ENABLE_GLOBAL_FIXES.get() && value.get();
    }

    private static boolean clientEnabled(ForgeConfigSpec.BooleanValue value) {
        if (COMMON_SPEC.isLoaded() && !ENABLE_GLOBAL_FIXES.get()) {
            return false;
        }
        if (!CLIENT_SPEC.isLoaded()) {
            return true;
        }
        return value.get();
    }

    public static boolean isOptimizeWorkQueueEnabled() {
        return commonEnabled(OPTIMIZE_WORK_QUEUE);
    }

    public static boolean isUseNativeStructuresEnabled() {
        return commonEnabled(USE_NATIVE_STRUCTURES);
    }

    public static boolean isFixDungeonWorldgenEnabled() {
        return commonEnabled(FIX_DUNGEON_WORLDGEN);
    }

    public static boolean isOptimizeDungeonStructurePlacementEnabled() {
        return commonEnabled(OPTIMIZE_DUNGEON_STRUCTURE_PLACEMENT);
    }

    public static boolean isFixBlockMapColorsEnabled() {
        return commonEnabled(FIX_BLOCK_MAP_COLORS);
    }

    public static boolean isFixHardcodedBlockHarvestEnabled() {
        return commonEnabled(FIX_HARDCODED_BLOCK_HARVEST);
    }

    public static boolean isFixSolidBlockLightBlockingEnabled() {
        return commonEnabled(FIX_SOLID_BLOCK_LIGHT_BLOCKING);
    }

    public static boolean isOptimizeTransientBlockTickingEnabled() {
        return commonEnabled(OPTIMIZE_TRANSIENT_BLOCK_TICKING);
    }

    public static boolean isOptimizeIgniterClientParticlesEnabled() {
        return commonEnabled(OPTIMIZE_IGNITER_CLIENT_PARTICLES);
    }

    public static boolean isFixTrapBlocksAndTeleporterEnabled() {
        return commonEnabled(FIX_TRAP_BLOCKS_AND_TELEPORTER);
    }

    public static boolean isFixEntityCrashesAndAiEnabled() {
        return commonEnabled(FIX_ENTITY_CRASHES_AND_AI);
    }

    public static boolean isFixEntityMobCategoriesEnabled() {
        return commonEnabled(FIX_ENTITY_MOB_CATEGORIES);
    }

    public static boolean isFixBossAndMobProceduresEnabled() {
        return commonEnabled(FIX_BOSS_AND_MOB_PROCEDURES);
    }

    public static boolean isFixItemProceduresAndMechanicsEnabled() {
        return commonEnabled(FIX_ITEM_PROCEDURES_AND_MECHANICS);
    }

    public static boolean isFixCustomShieldsEnabled() {
        return commonEnabled(FIX_CUSTOM_SHIELDS);
    }

    public static boolean isFixSmithingRecipesEnabled() {
        return commonEnabled(FIX_SMITHING_RECIPES);
    }

    public static boolean isThunderStrikeProtectAttackerEnabled() {
        return commonEnabled(THUNDER_STRIKE_PROTECT_ATTACKER);
    }

    public static boolean isThunderStrikeRequireSneak() {
        return !COMMON_SPEC.isLoaded() || THUNDER_STRIKE_REQUIRE_SNEAK.get();
    }

    public static boolean isCacheArmorHelmetModelsEnabled() {
        return clientEnabled(CACHE_ARMOR_HELMET_MODELS);
    }

    public static boolean isCacheEntityRendererTexturesEnabled() {
        return clientEnabled(CACHE_ENTITY_RENDERER_TEXTURES);
    }

    public static boolean isFixItemTooltipTyposEnabled() {
        return clientEnabled(FIX_ITEM_TOOLTIP_TYPOS);
    }

    public static int getAwfulDungeonSpacingChunks() {
        return COMMON_SPEC.isLoaded() ? AWFUL_DUNGEON_SPACING_CHUNKS.get() : 20;
    }

    public static int getKeepingCastleSpacingChunks() {
        return COMMON_SPEC.isLoaded() ? KEEPING_CASTLE_SPACING_CHUNKS.get() : 20;
    }

    public static int getEndDungeonSpacingChunks() {
        return COMMON_SPEC.isLoaded() ? END_DUNGEON_SPACING_CHUNKS.get() : 24;
    }

    public static int getNetherDungeonMinY() {
        return COMMON_SPEC.isLoaded() ? NETHER_DUNGEON_MIN_Y.get() : 34;
    }

    public static int getNetherDungeonMaxY() {
        return COMMON_SPEC.isLoaded() ? NETHER_DUNGEON_MAX_Y.get() : 96;
    }

    public static int getEndDungeonMinSurfaceY() {
        return COMMON_SPEC.isLoaded() ? END_DUNGEON_MIN_SURFACE_Y.get() : 45;
    }

    public static int getMaxNearbySporeProjectiles() {
        return COMMON_SPEC.isLoaded() ? MAX_NEARBY_SPORE_PROJECTILES.get() : 12;
    }

    public static int getTeleporterCooldownTicks() {
        return COMMON_SPEC.isLoaded() ? TELEPORTER_COOLDOWN_TICKS.get() : 40;
    }

    public static int getMaxNearbyGiddyBlazes() {
        return COMMON_SPEC.isLoaded() ? MAX_NEARBY_GIDDY_BLAZES.get() : 6;
    }
}
