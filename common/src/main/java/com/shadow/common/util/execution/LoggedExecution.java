package com.shadow.common.util.execution;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.util.concurrent.Callable;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author nevermore on 2015/1/21
 */
public class LoggedExecution {
    private static final Logger LOGGER = LoggerFactory.getLogger(LoggedExecution.class);

    private String taskName;
    private Object[] taskNameArgs;
    private LogLevel logLevel = LogLevel.INFO;

    private LoggedExecution(String taskName) {
        this(taskName, null);
    }

    private LoggedExecution(String taskName, Object[] taskNameArgs) {
        checkArgument(StringUtils.isNotEmpty(taskName), "任务名称不能为空");
        this.taskName = taskName;
        this.taskNameArgs = taskNameArgs;
    }

    public static LoggedExecution forName(String name) {
        return new LoggedExecution(name);
    }

    public static LoggedExecution forName(String taskName, Object... taskNameArgs) {
        return new LoggedExecution(taskName, taskNameArgs);
    }

    public LoggedExecution logLevel(LogLevel logLevel) {
        this.logLevel = logLevel;
        return this;
    }

    public void execute(Runnable task) {
        String taskName = this.taskName;
        if (logLevel.isEnabled()) {
            if (taskNameArgs != null) {
                taskName = MessageFormatter.format(taskName, taskNameArgs).getMessage();
            }
            logLevel.before(taskName);
        }

        long startTime = System.currentTimeMillis();
        try {
            task.run();
        } catch (Exception e) {
            LOGGER.error("任务[{}]执行失败：{}", taskName, e.getMessage());
            return;
        }

        if (logLevel.isEnabled()) {
            logLevel.after(taskName, startTime);
        }
    }

    public <T> T execute(Callable<T> task) {
        String taskName = this.taskName;
        if (logLevel.isEnabled()) {
            if (taskNameArgs != null) {
                taskName = MessageFormatter.format(taskName, taskNameArgs).getMessage();
            }
            logLevel.before(taskName);
        }

        long startTime = System.currentTimeMillis();
        T retVal;
        try {
            retVal = task.call();
        } catch (Exception e) {
            LOGGER.error("任务[{}]执行失败：{}", taskName, e.getMessage());
            return null;
        }

        if (logLevel.isEnabled()) {
            logLevel.after(taskName, startTime);
        }
        return retVal;
    }
}
