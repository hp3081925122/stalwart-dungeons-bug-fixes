package com.hp.stalwartdungeonsbugfix.mixin.entity;

import net.mcreator.stalwartdungeons.entity.AwfulGhastEntity;
import net.mcreator.stalwartdungeons.entity.PropulkEntity;
import net.mcreator.stalwartdungeons.entity.ReinforcedBlazeEntity;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 取消可怕恶魂、推进虫与强化烈焰人 dropCustomDeathLoot 中的硬编码掉落，
 * 对应掉落已统一合并至标准 JSON 实体战利品表（entities/awful_ghast.json、propulk.json、reinforced_blaze.json），
 * 使 JEI/JER 能完整显示掉落且正常受抢夺附魔与数据包控制。
 */
@Mixin({
        AwfulGhastEntity.class,
        PropulkEntity.class,
        ReinforcedBlazeEntity.class
})
public abstract class EntityLootDataDrivenMixin extends Monster {
    protected EntityLootDataDrivenMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "dropCustomDeathLoot", at = @At("HEAD"), cancellable = true)
    private void sd$useDataDrivenEntityLoot(DamageSource source, int looting, boolean recentlyHitIn, CallbackInfo ci) {
        super.dropCustomDeathLoot(source, looting, recentlyHitIn);
        ci.cancel();
    }
}
