package com.shadow.socket.core.domain;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.nio.ByteBuffer;

/**
 * @author nevermore on 2014/11/26.
 */
public final class Command {
    private short module;
    private byte cmd;

    public static Command valueOf(short module, byte cmd) {
        Command command = new Command();
        command.module = module;
        command.cmd = cmd;
        return command;
    }

    public byte[] toBytes() {
        return ByteBuffer.allocate(3).putShort(module).put(cmd).array();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Command other = (Command) obj;
        return new EqualsBuilder()
                .append(other.module, this.module)
                .append(other.cmd, this.cmd)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
                .append(module)
                .append(cmd)
                .hashCode();
    }

    @Override
    public String toString() {
        return "Command[module=" + module + ", cmd=" + cmd + "]";
    }

    public short getModule() {
        return module;
    }

    public byte getCmd() {
        return cmd;
    }
}
