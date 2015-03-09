package com.shadow.socket.core.domain;

import com.google.common.primitives.Shorts;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.Arrays;

/**
 * @author nevermore on 2014/11/26.
 */
public final class Command {
    private final byte[] array = new byte[3];

    public Command(short module, byte cmd) {
        array[0] = (byte) (module >> 8);
        array[1] = (byte) module;
        array[2] = cmd;
    }

    public static Command valueOf(short module, byte cmd) {
        return new Command(module, cmd);
    }

    public byte[] bytes() {
        return Arrays.copyOf(array, array.length);
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
                .append(other.array, this.array)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
                .append(array)
                .hashCode();
    }

    @Override
    public String toString() {
        return "Command[module=" + getModule() + ", cmd=" + getCmd() + "]";
    }

    public short getModule() {
        return Shorts.fromBytes(array[0], array[1]);
    }

    public byte getCmd() {
        return array[2];
    }
}
