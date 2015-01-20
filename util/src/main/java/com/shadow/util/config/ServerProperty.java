package com.shadow.util.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import com.shadow.util.codec.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author nevermore on 2015/1/10
 */
@Component
public class ServerProperty {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerProperty.class);

    @Value("${server.socket.port}")
    private String port;
    private Map<Short, String> platforms;
    private Map<Short, List<Short>> servers;

    public String getPort() {
        return port;
    }

    @Value("${server.config.servers}")
    public void setServers(String servers) {
        checkState(this.servers == null, "重复初始化");
        Map<String, Set<Short>> map = JsonUtil.fromJson(servers, new TypeReference<Map<String, Set<Short>>>() {
        });
        Map<Short, List<Short>> serverMap = Maps.newHashMapWithExpectedSize(map.size());
        map.forEach((k, v) -> {
            List<Short> serverList = new ArrayList<>(v);
            Collections.sort(serverList);
            serverMap.put(toPlatform(k), Collections.unmodifiableList(serverList));
        });

        if (serverMap.isEmpty()) {
            throw new IllegalStateException("服标识未设置");
        }

        this.servers = serverMap;
        LOGGER.error("服标识：{}", this.servers);
    }

    @Value("${server.config.platforms}")
    public void setPlatforms(String platformString) {
        checkState(this.platforms == null, "重复初始化");
        this.platforms = Maps.uniqueIndex(Splitter.on(",").split(platformString), this::toPlatform);

        LOGGER.error("平台标识：platforms=", this.platforms);
    }

    private short toPlatform(String platformName) {
        short id = 0;
        char[] arr = platformName.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            id += arr[i] * i;
        }
        return id;
    }

    public Set<Short> getPlatforms() {
        return Collections.unmodifiableSet(platforms.keySet());
    }

    public List<Short> getServers(short platform) {
        return servers.getOrDefault(platform, Collections.emptyList());
    }
}
