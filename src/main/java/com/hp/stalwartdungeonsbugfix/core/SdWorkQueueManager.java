package com.hp.stalwartdungeonsbugfix.core;

import com.mojang.logging.LogUtils;
import net.minecraftforge.fml.util.thread.SidedThreadGroups;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 坚固地牢服务端延迟任务队列管理器：
 * 1. 强制校验服务端线程组，阻止客户端或渲染/世界生成线程污染服务端队列；
 * 2. 修正 tick <= 0 时的永久泄漏问题；
 * 3. 使用单次遍历与 O(1) 节点移除，避免 ConcurrentLinkedQueue.removeAll 的 O(N*M) 开销；
 * 4. 为每个闭包包裹异常隔离，防止单个延迟任务抛错导致整条队列停摆；
 * 5. 在服务器停止时彻底清空残留任务，避免跨存档串执行。
 */
public final class SdWorkQueueManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ConcurrentLinkedQueue<PendingWork> QUEUE = new ConcurrentLinkedQueue<>();

    private SdWorkQueueManager() {
    }

    public static void enqueue(int ticks, Runnable action) {
        if (action == null) {
            return;
        }
        if (Thread.currentThread().getThreadGroup() != SidedThreadGroups.SERVER) {
            LOGGER.debug("Ignored non-server queueServerWork call from thread {}", Thread.currentThread().getName());
            return;
        }
        QUEUE.add(new PendingWork(action, Math.max(1, ticks)));
    }

    public static void tickEnd() {
        if (QUEUE.isEmpty()) {
            return;
        }
        List<Runnable> ready = null;
        Iterator<PendingWork> iterator = QUEUE.iterator();
        while (iterator.hasNext()) {
            PendingWork work = iterator.next();
            work.remainingTicks--;
            if (work.remainingTicks <= 0) {
                iterator.remove();
                if (ready == null) {
                    ready = new ArrayList<>();
                }
                ready.add(work.action);
            }
        }
        if (ready != null) {
            for (Runnable action : ready) {
                try {
                    action.run();
                } catch (Throwable throwable) {
                    LOGGER.error("Uncaught exception in Stalwart Dungeons delayed server work", throwable);
                }
            }
        }
    }

    public static void clear() {
        int dropped = QUEUE.size();
        QUEUE.clear();
        if (dropped > 0) {
            LOGGER.debug("Cleared {} pending Stalwart Dungeons delayed tasks on server stop", dropped);
        }
    }

    public static int size() {
        return QUEUE.size();
    }

    private static final class PendingWork {
        final Runnable action;
        int remainingTicks;

        PendingWork(Runnable action, int remainingTicks) {
            this.action = action;
            this.remainingTicks = remainingTicks;
        }
    }
}
