package com.shadow.entity.id;

import com.google.common.collect.Range;

/**
 * @author nevermore on 2015/1/10
 */
public class EntityIdRule {

    private static final int MAX_LEN = 63;// 排除符号位
    private static final int PLATFORM_LEN = 11;
    private static final int SERVER_LEN = 14;


    public static Range<Long> idRange(short platform, short server) {
        final long min = (long) platform << (MAX_LEN - PLATFORM_LEN) | (long) server << (MAX_LEN - PLATFORM_LEN - SERVER_LEN);
        final long max = min | (1L << (MAX_LEN - PLATFORM_LEN - SERVER_LEN)) - 1;
        return Range.closed(min, max);
    }

    public static short platform(long id) {
        return (short) ((id >> (MAX_LEN - PLATFORM_LEN)) & ((1 << PLATFORM_LEN) - 1));
    }

    public static short server(long id) {
        return (short) (id >> (MAX_LEN - PLATFORM_LEN - SERVER_LEN) & ((1 << SERVER_LEN) - 1));
    }

    public static Range<Short> platformRange() {
        return Range.closed((short) 1, (short) ((1 << PLATFORM_LEN) - 1));
    }

    public static void main(String[] args) {
        short platform = (1 << PLATFORM_LEN) - 1;
        short serverId = (1 << SERVER_LEN) - 1;

        Range<Long> range = idRange(platform, serverId);
        System.out.println(range);

        System.out.println(range.upperEndpoint() - range.lowerEndpoint());

        System.out.println(platform(range.lowerEndpoint()));
        System.out.println(server(range.lowerEndpoint()));

        System.out.println((long) platform << (MAX_LEN - PLATFORM_LEN));
        System.out.println(idRange((short) 1, (short) 1));
    }
}
