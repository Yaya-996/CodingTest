package com.codetest.service.impl;

import com.codetest.leaderboard.LeaderBoardConst;
import com.codetest.leaderboard.RankInfo;
import com.codetest.leaderboard.RedisUtil;
import com.codetest.service.LeaderBoardService;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;
import redis.clients.jedis.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardServiceImpl implements LeaderBoardService {
    private static Jedis REDIS;

    // 初始化
    public static void init() {
        REDIS = new Jedis("localhost");
    }

    private double scoreParse(int score, long timestamp) {
        // 把timestamp转为小数后缀并且对1取互补, 用于保证时间越小的玩家排序值越大
        return score + (1 - 1.0 * timestamp / 1e13);
    }

    @Override
    public void updateScore(String playerId, int score, long timestamp) {
        REDIS.zadd(LeaderBoardConst.rankingKey(), scoreParse(score, timestamp), playerId);
    }

    @Override
    public RankInfo getPlayerRank(String playerId) {
        KeyValue<Long, Double> res = REDIS.zrevrankWithScore(LeaderBoardConst.rankingKey(), playerId);
        return new RankInfo(playerId, (int) Math.floor(res.getValue()), res.getKey().intValue());
    }

    @Override
    public List<RankInfo> getTopN(int n) {
        List<Tuple> res = REDIS.zrevrangeWithScores(LeaderBoardConst.rankingKey(), 1, n);
        List<RankInfo> rankInfoList = new ArrayList<>();
        for (int i = 0; i < res.size(); i++) {
            int rank = i + 1;
            Tuple t = res.get(i);
            rankInfoList.add(new RankInfo(t.getElement(), (int) Math.floor(t.getScore()), rank));
        }
        return rankInfoList;
    }

    @Override
    public List<RankInfo> getPlayerRankRange(String playerId, int n) {
        // 使用lua脚本执行redis逻辑, 用于保证数据的原子性
        return RedisUtil.fetchNAround(REDIS, LeaderBoardConst.rankingKey(), playerId, n);
    }
}
