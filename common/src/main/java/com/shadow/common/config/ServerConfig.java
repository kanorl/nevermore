package com.shadow.common.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.shadow.common.util.codec.JsonUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

import static com.google.common.base.Preconditions.checkState;

/**
 * @author nevermore on 2015/1/10
 */
@Component
public class ServerConfig implements InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(ServerConfig.class);

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
        Map<Short, Set<Short>> map = JsonUtil.fromJson(servers, new TypeReference<Map<Short, Set<Short>>>() {
        });
        Map<Short, List<Short>> serverMap = Maps.newHashMapWithExpectedSize(map.size());
        map.forEach((k, v) -> {
            List<Short> serverList = new ArrayList<>(v);
            Collections.sort(serverList);
            serverMap.put(k, Collections.unmodifiableList(serverList));
        });

        if (serverMap.isEmpty()) {
            throw new IllegalStateException("服标识未设置");
        }

        this.servers = serverMap;
        LOGGER.error("服标识：{}", this.servers);
    }

    @Value("${server.config.platforms}")
    public void setPlatforms(String platforms) {
        checkState(this.platforms == null, "重复初始化");
        this.platforms = JsonUtil.fromJson(platforms, new TypeReference<Map<Short, String>>() {
        });

        LOGGER.error("平台标识：platforms={}", this.platforms);
    }

    public Set<Short> getPlatforms() {
        return Collections.unmodifiableSet(platforms.keySet());
    }

    public List<Short> getServers(short platform) {
        return servers.getOrDefault(platform, Collections.emptyList());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // check config
        Preconditions.checkState(CollectionUtils.isEqualCollection(platforms.keySet(), servers.keySet()), "平台标识不匹配: platforms=%s, servers=%s", platforms, servers);
    }
}
