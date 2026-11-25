# Stalwart Dungeons Bug Fixes

Bug fixes and performance improvements for **Stalwart Dungeons**. All fix groups can be toggled in `stalwartdungeonsbugfix-common.toml` and `stalwartdungeonsbugfix-client.toml`.

## Fixed Bugs & Performance Issues

### Crashes & Data Safety
- **Dedicated server `Minecraft.getInstance()` crashes**: Removed client-only calls from `ShulkerCannonEntityIsHurtProcedure`, `ShulkerCannonItemRightclickedProcedure`, and `SporePotionExpiresProcedure`.
- **`ItemStack.EMPTY` singleton corruption**: Fixed `ShulkerCannonItemRightclickedProcedure` mutating `ItemStack.EMPTY.setCount(1)` and wiping the entire held stack instead of shrinking by 1.
- **Awful Ghast `Ticking Entity` NPE crash**: Guarded null/dead targets in `AwfulGhastEntity` charge AI.
- **Broken Boss loot tables**: Fixed `awful_ghast` loot table type (`minecraft:generic` -> `minecraft:entity`) and inverted `min > max` crystal drop counts in `awful_ghast` (`1..4`) and `shelterer_without_armor` (`2..4`).
- **Server delayed work queue (`workQueue`)**: Added server thread-group validation, non-positive delay leak prevention, O(1) removal, exception isolation, and cross-world cleanup on server stop (automatically yields to MCreator Optimizer when installed).

### World Generation & Dungeon Placement Lag
- **Nether dungeons breaking bedrock / floating / overlapping**: Added deterministic chunk-grid spacing, safe Nether Y-range and ground search, dimension checks, and bedrock protection for `AwfulDungeonsFeature` and `KeepingCastleFeature`.
- **End dungeon spawning in void**: Removed `the_void` from `end_dungeon_biome_modifier`, prevented void spawns (`y = -1`), protected the main End island, added chunk spacing, and restored all 4 rotations.
- **Dungeon materialization freeze**: Optimized all 3 dungeon trigger placement procedures (23k–44k blocks) to run server-only, remove the anchor block first, ignore structure blocks, protect bedrock, and use `flag = 2` to avoid massive neighbor-update cascades.

### Server & Client Performance (Lag Fixes)
- **Transient Block idle polling lag (554 blocks per Keeping Castle)**: Replaced permanent 10-tick polling on idle closed Transient Blocks with event-driven `neighborChanged` propagation, fixed `tagCooldown == 5.0` permanent stuck-open leak, and eliminated per-tick `sendBlockUpdated` and 0-volume sound packet spam.
- **Igniter server tick & packet spam (56 blocks per Awful Dungeon)**: Moved Igniter ambient soul-fire particles from permanent server scheduled ticks to standard client-side `animateTick`.
- **Armor helmet per-frame `bakeLayer` FPS drop**: Cached custom models for Tungsten, Warted Tungsten, and Chorundum helmets (resource-reload safe) instead of re-baking and allocating 8 `ModelPart`s every frame.
- **Entity renderer per-frame allocations**: Cached texture `ResourceLocation` instances across all 11 entity renderers and the Shelterer `EyesLayer` `RenderType`.
- **Flying mob pathfinding lag**: Replaced blind underground-drilling random stroll targets on `AwfulGhast`, `Propulk`, and `ReinforcedBlaze` with open-air stroll checks and removed duplicate ground stroll goals.
- **Per-tick `MobEffectEvent.Remove` event storms**: Guarded `Propulk` and all 3 full armor sets (`Tungsten`, `Warted Tungsten`, `Chorundum`) to run server-only and check `hasEffect()` before calling `removeEffect()`.
- **Shield smelting recipe scan hack**: Stopped all 3 custom shields from scanning every smelting recipe on every right-click release (and shooting invisible dummy arrows if a recycling recipe exists).

### Blocks, Maps, Tags & Modular Tool Compatibility
- **Xaero & vanilla map invisibility**: Assigned accurate vanilla `MapColor`s to all 39 blocks (previously `MapColor.NONE`).
- **Modular tool compatibility (Tinkers' Construct / Tetra / etc.)**: Removed hardcoded `instanceof PickaxeItem` checks from all 23 blocks and delegated to standard tag-based harvesting (`mineable/pickaxe` and `needs_{stone,iron,diamond}_tool`).
- **Solid dungeon walls leaking skylight**: Fixed 15 opaque solid blocks that wrongly returned `propagatesSkylightDown = true` and `getLightBlock = 0`.
- **Missing standard tags**: Completed missing `minecraft` and `forge` tags for blocks (including 13 brick blocks missing from `mineable/pickaxe`), ores, tools, armor, shields, `forge:bosses`, and `minecraft:skeletons`.
- **Teleporter & Pearl bugs**: Fixed empty `{}` NBT pollution on hover (`TeleporterPearlItem`) and collision; fixed `TeleporterGUIMenu#stillValid` staying open forever; fixed `moveItemStackTo` slot validation and hopper junk insertion; prevented unbound `(0, 0, 0)` teleports, forced unloaded-chunk loads, and rapid self-damage via configurable cooldown.

### Entities, Bosses & Items
- **Nether Hammer auto-smelt restored**: Fixed auto-smelt completely failing because `mineBlock` ran after the block was already replaced with `AIR`; now properly smelts drops in `Block#getDrops` with Silk Touch, Fortune, and Creative compatibility.
- **Boss & Mob AI fixes**: Fixed `SheltererWithoutArmor` inverted AI priorities (`RandomStrollGoal` overriding `MeleeAttackGoal`); fixed `NetherKeeper` attacking its owner after taming, wrong `OwnerHurtByTargetGoal` selector, duplicate melee goals, breeding at `(0, 0, 0)`, unbounded `GiddyBlaze` summoning, hardcoded HP threshold, and 3 wrong flame particle coordinates; fixed `AwfulGhast` percentage HP threshold; fixed `Shelterer` death deleting nearby player items, spawning `Blocks.AIR`, and duplicating phase-2 bosses; fixed `Propulk` applying Levitation to itself; fixed `LevitationProjectile` failing to apply Levitation on hit.
- **Spore effect & Cocoon fixes**: Prevented `SporeMobEffect` from prematurely spawning `NetherWartCocoon` on refresh or early cure (milk/totem/commands); fixed cocoon water-discard missing `return`, spore projectile self-infection, spore bottle sound/particle loop bug, and spore collection duplication.
- **Mob categories & dupes**: Fixed `Propulk` (`CREATURE` -> `MONSTER`) and `SporeProjectile` (`MONSTER` -> `MISC`) spawn caps; disallowed arrow pickup duplication on all 4 consumable throwables (`AwfulDungeonSpawner`, `KeepingCastleSpawner`, `EndDungeonSpawner`, `SporeBottle`).
- **Altars, traps & equipment**: Fixed `NetherKeeperAltar` ceiling/floor clearance so the boss no longer suffocates in the roof; made all 3 altars, `ObsidianStriker`, `ShulkerCannon`, `AncientFire`, `AwfulGun`, `GiddyStatue`, `SporeVaporizer`, `SoulMagmaBricks`, `Burning`, and shield durability server-authoritative, cooldown-enforced, and Creative-safe; fixed `ThunderStrike` enchantment anvil restriction, caching, and lightning `setCause`.

---

# 坚固地牢 Bug 修复与优化

针对 **Stalwart Dungeons（坚固地牢）** 的全量 Bug 修复与性能优化附属模组。所有修复模块与数值上限均可在 `stalwartdungeonsbugfix-common.toml` 与 `stalwartdungeonsbugfix-client.toml` 中独立开关与调整。

## 已修复的 Bug 与卡顿问题

### 崩溃与数据安全
- **专用服务端 `Minecraft.getInstance()` 崩溃**：移除 `ShulkerCannonEntityIsHurtProcedure`、`ShulkerCannonItemRightclickedProcedure` 与 `SporePotionExpiresProcedure` 中的客户端专属调用。
- **`ItemStack.EMPTY` 单例被污染**：修复放置潜影加农炮时调用 `ItemStack.EMPTY.setCount(1)` 污染全局空物品并吞掉整组物品的严重问题。
- **可怕恶魂 `Ticking Entity` 空指针崩溃**：为可怕恶魂冲锋 AI 全路径补充目标非空与存活校验。
- **Boss 战利品表损坏**：修复 `awful_ghast` 战利品表类型错误（`minecraft:generic` -> `minecraft:entity`）以及 `awful_ghast`（`1..4`）、`shelterer_without_armor`（`2..4`）中 `min > max` 的掉落数错误。
- **服务端延迟队列（`workQueue`）缺陷**：补充服务端线程校验、非正延迟不泄漏、单次遍历 O(1) 移除、单任务异常隔离与关服清理（检测到 `MCreator Optimizer` 时自动让位）。

### 世界生成与大型地牢卡顿
- **下界地牢击穿基岩 / 悬空 / 重叠互切**：为可怕地牢与守望城堡增加基于种子的区块网格间距、下界安全高度与地面搜索、维度校验及基岩保护，同时消除模板多线程竞争。
- **末地地牢生成在虚空**：从 `end_dungeon_biome_modifier` 移除 `the_void`，阻止在虚空（`y = -1`）与末地主岛生成，补齐网格间距与全部四方向旋转。
- **触发放置大型地牢卡顿**：优化三座地牢触发块放置 2.3 万~4.4 万方块流程，仅服务端执行、先清除触发块、忽略结构方块、保护基岩并改为 `flag = 2` 避免数万次邻接更新风暴。

### 服务端与客户端卡顿优化
- **瞬态方块闲置轮询卡顿（每座守望城堡 554 块）**：取消关闭态永久 10t 定时轮询，改为 `neighborChanged` 事件驱动传播（闲置 0 tick）；修复开启态 `tagCooldown == 5.0` 永久卡开、每刻 `sendBlockUpdated` 广播风暴及 0 音量音效发包。
- **点燃器服务端发包卡顿（每座可怕地牢 56 块）**：将灵魂火焰粒子从服务端永久 10t 定时任务迁移至原版客户端 `animateTick`。
- **自定义头盔每帧 `bakeLayer` 掉帧**：缓存钨、疣化钨与紫颂锭头盔模型（兼容 F3+T 资源重载），消除每帧重复烘焙与 8 个 `ModelPart` 分配。
- **实体渲染器每帧对象分配**：静态缓存全部 11 个实体渲染器的贴图 `ResourceLocation` 与庇护者发光层 `RenderType`。
- **飞行怪寻路卡顿**：替换 `AwfulGhast`、`Propulk`、`ReinforcedBlaze` 盲选地下实心岩层的空中随机漫步目标，并移除重复竞争的地面漫步目标。
- **每刻 `MobEffectEvent.Remove` 事件风暴**：推进虫与三套护甲（钨、疣化钨、紫颂锭）改为仅服务端且先检查 `hasEffect()` 再移除效果。
- **三面盾牌松手全服扫描熔炼配方**：取消盾牌松开右键时扫描全服熔炼配方的无效占位逻辑，同时避免整合包添加回炉配方时误射隐形箭并扣耐久。

### 方块、地图显示、标签与模块化工具兼容
- **Xaero 地图与原版地图隐形**：为全部 39 个缺失地图色（`MapColor.NONE`）的方块补齐匹配材质的原版 `MapColor`。
- **模块化工具兼容（匠魂 / Tetra 等）**：移除 23 个方块硬编码的 `instanceof PickaxeItem` 判定，统一回归原版/Forge 标准标签挖掘体系。
- **实心方块漏光**：修复 15 个实心不透明方块误返回 `propagatesSkylightDown = true` 与 `getLightBlock = 0` 导致的地牢漏光与光照计算开销。
- **标准标签补全**：补齐 13 个遗漏在 `mineable/pickaxe` 外的石砖方块，补全三档 `needs_*_tool`、楼梯/台阶/墙/灵魂疾行、矿物、工具、盾牌、护甲、`forge:bosses` 及 `minecraft:skeletons` 标签。
- **传送器与传送珍珠缺陷**：消除传送珍珠悬停与传送器碰撞写入空 `{}` NBT 污染；修复传送器 GUI 无限距离不关闭、快捷移动槽位错判及漏斗塞杂物；阻止未绑定珍珠传送到 `(0, 0, 0)` 与强制加载未加载区块，增加可配置传送冷却。

### 实体、Boss 与物品玩法修复
- **下界之锤自动熔炼彻底修复**：修复原版在方块被破坏为空气后才读取方块状态导致自动熔炼完全失效的 Bug，改为在 `Block#getDrops` 阶段转换掉落物，兼容时运、精准采集与创造模式。
- **Boss 与怪物 AI 修复**：修复无甲庇护者 AI 优先级倒置（漫步盖过近战追击）；修复下界守卫驯服后攻击主人、`OwnerHurtByTargetGoal` 错加进 `goalSelector`、重复近战目标、繁殖在 `(0, 0, 0)` 初始化、眩晕烈焰人无上限召唤、血量硬编码与 3 处火焰粒子坐标写死；修复可怕恶魂百分比半血判定；修复庇护者死亡误删玩家掉落物、掉落空气方块与重复刷二阶段 Boss；修复推进虫给自己施加飘浮；修复漂浮投射物命中实体不给飘浮。
- **孢子效果与下界疣茧修复**：阻止孢子效果刷新或被牛奶/不死图腾/指令提前净化时误生成下界疣茧；修复茧入水销毁缺 `return`、孢子投射物自我感染、孢子瓶音效粒子误放循环内与右键收集重复刷瓶。
- **生物分类与刷物品漏洞**：修正 `propulk`（`CREATURE` -> `MONSTER`）与 `spore_projectile`（`MONSTER` -> `MISC`）刷怪分类；禁止 4 种一次性投掷物（三座地牢刷怪笼投掷物与孢子瓶）拾取刷物品。
- **祭坛、机关与装备修复**：修复下界守卫悬挂/地面祭坛生成净空避免 Boss 卡屋顶窒息；修复三祭坛、黑曜石击发器、潜影加农炮、远古之火、可怕火枪、眩晕雕像、孢子喷发器、灵魂岩浆砖、燃烧效果与盾牌耐久的双端同步、冷却校验、创造消耗与旁观者误触发问题；修复雷击附魔铁砧限制、数组重复分配与闪电归因。
