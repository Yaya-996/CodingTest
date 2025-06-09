package com.codetest.leaderboard.custom;

public class LeaderboardConst {
    public static final String REDIS_KEY_PREFIX = "Leaderboard";
    public static final int SHOW_COUNT = 100; // 展示人数

    /**
     * 获取存储key
     */
    public static String rankingKey() {
        return REDIS_KEY_PREFIX;
    }
}
