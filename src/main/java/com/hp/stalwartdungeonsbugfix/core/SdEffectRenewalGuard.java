package com.hp.stalwartdungeonsbugfix.core;

/**
 * 线程局部药水效果生命周期守卫：
 * 1. 记录 LivingEntity.onEffectUpdated 刷新窗口，防止孢子效果续期时误触发 removeAttributeModifiers 生成下界疣茧；
 * 2. 记录 LivingEntity.onEffectRemoved 中剩余时长 > 0 的提前净化/清除窗口（牛奶、不死图腾、指令清除等），防止反向召唤茧。
 */
public final class SdEffectRenewalGuard {
    private static final ThreadLocal<Integer> REFRESH_DEPTH = ThreadLocal.withInitial(() -> 0);
    private static final ThreadLocal<Integer> EARLY_REMOVAL_DEPTH = ThreadLocal.withInitial(() -> 0);

    private SdEffectRenewalGuard() {
    }

    public static void enterRefresh() {
        REFRESH_DEPTH.set(REFRESH_DEPTH.get() + 1);
    }

    public static void exitRefresh() {
        int next = REFRESH_DEPTH.get() - 1;
        if (next <= 0) {
            REFRESH_DEPTH.remove();
        } else {
            REFRESH_DEPTH.set(next);
        }
    }

    public static boolean isRefreshing() {
        return REFRESH_DEPTH.get() > 0;
    }

    public static void enterEarlyRemoval() {
        EARLY_REMOVAL_DEPTH.set(EARLY_REMOVAL_DEPTH.get() + 1);
    }

    public static void exitEarlyRemoval() {
        int next = EARLY_REMOVAL_DEPTH.get() - 1;
        if (next <= 0) {
            EARLY_REMOVAL_DEPTH.remove();
        } else {
            EARLY_REMOVAL_DEPTH.set(next);
        }
    }

    public static boolean isEarlyRemoval() {
        return EARLY_REMOVAL_DEPTH.get() > 0;
    }
}
