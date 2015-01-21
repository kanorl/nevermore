package com.shadow.util.concurrent;

import com.shadow.util.codec.JsonUtil;
import com.shadow.util.execution.LoggedExecution;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author nevermore on 2015/1/3.
 */
public class ExecutorUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorUtil.class);

    public static void shutdownAndAwaitTermination(ExecutorService service, String serviceName, long timeout, TimeUnit unit) {
        LoggedExecution.forName("关闭{}线程池", serviceName).execute(() -> {
            service.shutdown();
            try {
                service.awaitTermination(timeout, unit);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            if (!service.isTerminated()) {
                List<Runnable> tasks = service.shutdownNow();
                LOGGER.error("[{}]线程池无法在规定时间内[{} {}]关闭，未执行的任务：", serviceName, timeout, unit, JsonUtil.toJson(tasks));
            }
        });
    }

    /**
     * 等同于: shutdownAndAwaitTermination(service, serviceName, 10, TimeUnit.MINUTES)
     *
     * @param service
     * @param serviceName
     */
    public static void shutdownAndAwaitTermination(ExecutorService service, String serviceName) {
        shutdownAndAwaitTermination(service, serviceName, 10, TimeUnit.MINUTES);
    }
}
