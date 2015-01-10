package com.shadow.entity.identity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author nevermore on 2015/1/9.
 */
public class IdGenerator {

    private final short platform;
    private final short server;
    private final AtomicLong currentId;

    public IdGenerator(short platform, short server, long currentMaxId) {
        this.platform = platform;
        this.server = server;
        this.currentId = new AtomicLong(currentMaxId);
    }


    public long next() {
        return currentId.incrementAndGet();
    }

    public short getPlatform() {
        return platform;
    }

    public short getServer() {
        return server;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
                .append(platform)
                .append(server)
                .hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        IdGenerator other = (IdGenerator) obj;
        return new EqualsBuilder()
                .append(this.platform, other.getPlatform())
                .append(this.server, other.server)
                .isEquals();
    }
}
