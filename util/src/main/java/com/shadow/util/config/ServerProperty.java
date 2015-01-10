package com.shadow.util.config;

import com.shadow.util.codec.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author nevermore on 2015/1/10
 */
@Component
public class ServerProperty {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerProperty.class);

    @Value("${server.socket.port}")
    private String port;
    private String platformName;
    private short platform;
    private List<Short> servers;

    public void setPort(String port) {
        this.port = port;
    }

    public String getPort() {
        return port;
    }

    @SuppressWarnings("unchecked")
    @Value("${server.config.servers}")
    public void setServers(String servers) {
        checkState(this.servers == null, "重复初始化");
        List<Short> list = new ArrayList<>(JsonUtil.toCollection(servers, Set.class, Short.class));
        Collections.sort(list);
        this.servers = Collections.unmodifiableList(list);
    }

    @Value("${server.config.platform.name}")
    public void setPlatform(String platformName) {
        checkArgument(Pattern.compile("[0-9a-zA-Z]+").matcher(platformName).matches(), "属性[server.config.platform.name]的值只能包含数字和字母");
        checkState(this.platformName == null, "重复初始化");
        this.platformName = platformName;
        this.platform = toPlatformId(platformName);

        LOGGER.error("平台配置：name={}, id={}", this.platformName, this.platform);
    }

    private short toPlatformId(String platformName) {
        short id = 0;
        char[] arr = platformName.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            id += arr[i] * i;
        }
        return id;
    }

    public String getPlatformName() {
        return platformName;
    }

    public short getPlatform() {
        return platform;
    }

    public List<Short> getServers() {
        return servers;
    }
}
