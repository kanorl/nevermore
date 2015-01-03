package com.shadow.util.concurrent;

import com.google.common.util.concurrent.MoreExecutors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author nevermore on 2015/1/3.
 */
public class ExecutorUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorUtil.class);

    public static void shutdownAndAwaitTermination(ExecutorService service, String serviceName, long timeout, TimeUnit unit) {
        LOGGER.info("开始[关闭{}线程池]", serviceName);
        if (!MoreExecutors.shutdownAndAwaitTermination(service, timeout, unit)) {
            LOGGER.error("[{}]线程池无法在规定时间内[{} {}]关闭", serviceName, timeout, unit);
        }
        LOGGER.info("完成[关闭{}线程池]", serviceName);
    }
}
