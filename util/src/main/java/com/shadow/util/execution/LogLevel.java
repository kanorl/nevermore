package com.shadow.util.execution;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author nevermore on 2015/1/21
 */
public enum LogLevel {

    DEBUG {
        @Override
        public void before(String taskName) {
            LOGGER.debug(START_MSG_PATTERN, taskName);
        }

        @Override
        public void after(String taskName, long startTime) {
            LOGGER.debug(FINISH_MSG_PATTERN, taskName, System.currentTimeMillis() - startTime);
        }

        @Override
        public boolean isEnabled() {
            return LOGGER.isDebugEnabled();
        }
    },
    INFO {
        @Override
        public void before(String taskName) {
            LOGGER.info(START_MSG_PATTERN, taskName);
        }

        @Override
        public void after(String taskName, long startTime) {
            LOGGER.info(FINISH_MSG_PATTERN, taskName, System.currentTimeMillis() - startTime);
        }

        @Override
        public boolean isEnabled() {
            return LOGGER.isInfoEnabled();
        }
    },
    ERROR {
        @Override
        public void before(String taskName) {
            LOGGER.error(START_MSG_PATTERN, taskName);
        }

        @Override
        public void after(String taskName, long startTime) {
            LOGGER.error(FINISH_MSG_PATTERN, taskName, System.currentTimeMillis() - startTime);
        }

        @Override
        public boolean isEnabled() {
            return LOGGER.isErrorEnabled();
        }
    };

    private static final String START_MSG_PATTERN = "开始 {}......";
    private static final String FINISH_MSG_PATTERN = "完成 {}，耗时 {}ms.";

    public static final Logger LOGGER = LoggerFactory.getLogger(LogLevel.class);

    public abstract void before(String taskName);

    public abstract void after(String taskName, long startTime);

    public abstract boolean isEnabled();
}
