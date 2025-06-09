package com.codetest.leaderboard;

public class LeaderBoardConst {
    public static final String REDIS_KEY_PREFIX = "leaderboard";
    public static final int SHOW_COUNT = 100; // 展示人数

    /**
     * 获取存储key
     */
    public static String rankingKey() {
        return REDIS_KEY_PREFIX;
    }
}
