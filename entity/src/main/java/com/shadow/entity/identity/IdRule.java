package com.shadow.entity.identity;

/**
 * @author nevermore on 2015/1/10
 */
public class IdRule {

    private static final int platformLen = 11;
    private static final int serverLen = 14;
    private static final int maxLen = 63;

    public static IdRange range(short platform, short server) {
        long basic = (long) platform << (maxLen - platformLen);
        basic |= (long) server << (maxLen - platformLen - serverLen);
        long min = basic;
        long max = basic | (1L << (maxLen - platformLen - serverLen)) - 1;
        return new IdRange(min, max);
    }

    public static short platform(long id) {
        return (short) ((id >> (maxLen - platformLen)) & ((1 << platformLen) - 1));
    }

    public static short server(long id) {
        return (short) (id >> (maxLen - platformLen - serverLen) & ((1 << serverLen) - 1));
    }

    public static void main(String[] args) {
        short platform = (1 << platformLen) - 1;
        short serverId = (1 << serverLen) - 1;

        IdRange range = range(platform, serverId);
        System.out.println(range);

        System.out.println(range.getMax() - range.getMin());

        System.out.println(platform(range.getMin()));
        System.out.println(server(range.getMin()));
    }
}
