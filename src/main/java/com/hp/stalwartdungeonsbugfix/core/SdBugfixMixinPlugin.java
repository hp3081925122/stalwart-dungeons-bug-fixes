package com.hp.stalwartdungeonsbugfix.core;

import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

/**
 * Mixin 配置插件：在检测到通用 MCreator Optimizer 时主动让出 workQueue 队列 Mixin，避免重复拦截冲突。
 */
public final class SdBugfixMixinPlugin implements IMixinConfigPlugin {
    private boolean mcrOptimizerServicePresent;

    @Override
    public void onLoad(String mixinPackage) {
        this.mcrOptimizerServicePresent = isClassPresent("com.hp.mcroptimizer.service.McrTransformationService");
    }

    private static boolean isClassPresent(String className) {
        String resourcePath = className.replace('.', '/') + ".class";
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (loader != null && loader.getResource(resourcePath) != null) {
            return true;
        }
        return SdBugfixMixinPlugin.class.getClassLoader().getResource(resourcePath) != null;
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.endsWith(".StalwartDungeonsModMixin") && this.mcrOptimizerServicePresent) {
            return false;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
