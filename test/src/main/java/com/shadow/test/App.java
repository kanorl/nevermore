package com.shadow.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.TimeUnit;

/**
 * @author nevermore on 2015/3/1
 */
public class App {
    private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    @SuppressWarnings("resource")
	public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx;
        try {
            ctx = new ClassPathXmlApplicationContext("/applicationContext.xml");
        } catch (Exception e) {
            LOGGER.error("应用上下文初始化失败", e);
            throw new RuntimeException(e);
        }
        ctx.registerShutdownHook();
        ctx.start();

        while (ctx.isActive()) {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                LOGGER.error(e.getMessage(), e);
            }
        }

        LOGGER.error("服务器已关闭......");
        System.exit(0);
    }
}
