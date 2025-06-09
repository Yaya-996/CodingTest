package com.codetest.service.impl;

import com.codetest.leaderboard.custom.LeaderboardConst;
import com.codetest.leaderboard.custom.LeaderboardHelper;
import com.codetest.leaderboard.RankInfo;
import com.codetest.leaderboard.RedisUtil;
import com.codetest.leaderboard.rankrepeated.RankRepeatedLeaderboardConst;
import com.codetest.leaderboard.rankrepeated.RankRepeatedLeaderboardHelper;
import com.codetest.service.LeaderBoardService;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;
import redis.clients.jedis.util.KeyValue;

import java.util.ArrayList;
import java.util.List;

public class RankRepeatedLeaderboardServiceImpl implements LeaderBoardService {
    private static final Jedis REDIS = RedisUtil.fetchRedis("");

    @Override
    public void updateScore(String playerId, int score, long timestamp) {
        RankRepeatedLeaderboardHelper.updateScore(REDIS,
                List.of(
                        RankRepeatedLeaderboardConst.rankingKey(),
                        RankRepeatedLeaderboardConst.scoreRankingKey()
                ),
                playerId,
                LeaderboardHelper.scoreParse(score, timestamp));
    }

    @Override
    public RankInfo getPlayerRank(String playerId) {
        // TODO
        return null;
    }

    @Override
    public List<RankInfo> getTopN(int n) {
        // TODO
        return null;
    }

    @Override
    public List<RankInfo> getPlayerRankRange(String playerId, int n) {
        // TODO
        return null;
    }
}
