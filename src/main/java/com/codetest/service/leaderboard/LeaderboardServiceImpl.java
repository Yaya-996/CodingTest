package com.codetest.service.leaderboard;

import com.codetest.leaderboard.custom.LeaderboardConst;
import com.codetest.leaderboard.custom.LeaderboardHelper;
import com.codetest.leaderboard.RankInfo;
import com.codetest.leaderboard.RedisUtil;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;
import redis.clients.jedis.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class LeaderboardServiceImpl implements LeaderBoardService {
    private static final Jedis REDIS = RedisUtil.fetchRedis("");

    @Override
    public void updateScore(String playerId, int score, long timestamp) {
        REDIS.zadd(LeaderboardConst.rankingKey(), LeaderboardHelper.scoreParse(score, timestamp), playerId);
    }

    @Override
    public RankInfo getPlayerRank(String playerId) {
        KeyValue<Long, Double> res = REDIS.zrevrankWithScore(LeaderboardConst.rankingKey(), playerId);
        if (res == null) {
            // 没有获取到玩家数据的情况使用默认值或者报错
            return new RankInfo(playerId, 0, -1);
        }
        return new RankInfo(playerId, (int) Math.floor(res.getValue()), res.getKey().intValue() + 1);
    }

    @Override
    public List<RankInfo> getTopN(int n) {
        List<Tuple> res = REDIS.zrevrangeWithScores(LeaderboardConst.rankingKey(), 0, n - 1);
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
        return LeaderboardHelper.fetchNAround(REDIS, LeaderboardConst.rankingKey(), playerId, n);
    }
}
