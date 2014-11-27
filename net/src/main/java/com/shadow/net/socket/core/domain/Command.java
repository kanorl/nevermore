package com.shadow.net.socket.core.domain;

import com.shadow.util.lang.ArrayUtil;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author nevermore on 2014/11/26.
 */
public final class Command {
    private int module;
    private int cmd;

    public static Command valueOf(int module, int cmd) {
        Command command = new Command();
        command.module = module;
        command.cmd = cmd;
        return command;
    }

    public byte[] toBytes() {
        byte[] data = new byte[8];
        int offset = 0;
        ArrayUtil.fill(module, data, offset);
        offset += 4;
        ArrayUtil.fill(cmd, data, offset);
        return data;
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

    public int getModule() {
        return module;
    }

    public void setModule(int module) {
        this.module = module;
    }

    public int getCmd() {
        return cmd;
    }

    public void setCmd(int cmd) {
        this.cmd = cmd;
    }
}
