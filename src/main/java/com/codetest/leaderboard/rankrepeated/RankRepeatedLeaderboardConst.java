package com.codetest.leaderboard.rankrepeated;

public class RankRepeatedLeaderboardConst {
    public static final String REDIS_KEY_PREFIX = "RRLeaderboard";
    public static final int SHOW_COUNT = 100; // 展示人数

    /**
     * 获取存储key
     */
    public static String rankingKey() {
        return REDIS_KEY_PREFIX;
    }

    /**
     * 获取分数排名存储key
     */
    public static String scoreRankingKey() {
        return REDIS_KEY_PREFIX + "Score";
    }

    /**
     * 获取分数出现频率存储key
     */
    public static String scoreCountKey() {
        return REDIS_KEY_PREFIX + "ScoreCount";
    }
}
