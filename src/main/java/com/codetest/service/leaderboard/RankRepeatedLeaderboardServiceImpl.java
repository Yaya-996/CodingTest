package com.codetest.service.leaderboard;

import com.codetest.leaderboard.custom.LeaderboardHelper;
import com.codetest.leaderboard.RankInfo;
import com.codetest.leaderboard.RedisUtil;
import com.codetest.leaderboard.rankrepeated.RankRepeatedLeaderboardConst;
import com.codetest.leaderboard.rankrepeated.RankRepeatedLeaderboardHelper;
import redis.clients.jedis.Jedis;

import java.util.List;

public class RankRepeatedLeaderboardServiceImpl implements LeaderBoardService {
    private static final Jedis REDIS = RedisUtil.fetchRedis("");

    @Override
    public void updateScore(String playerId, int score, long timestamp) {
        RankRepeatedLeaderboardHelper.updateScore(REDIS,
                List.of(
                        RankRepeatedLeaderboardConst.rankingKey(),
                        RankRepeatedLeaderboardConst.scoreRankingKey(),
                        RankRepeatedLeaderboardConst.scoreCountKey()
                ),
                playerId,
                LeaderboardHelper.scoreParse(score, timestamp));
    }

    @Override
    public RankInfo getPlayerRank(String playerId) {
        return RankRepeatedLeaderboardHelper.fetchRank(REDIS, List.of(
                RankRepeatedLeaderboardConst.rankingKey(),
                RankRepeatedLeaderboardConst.scoreRankingKey()
        ), playerId);
    }

    @Override
    public List<RankInfo> getTopN(int n) {
        return RankRepeatedLeaderboardHelper.fetchTop(REDIS, List.of(
                RankRepeatedLeaderboardConst.rankingKey(),
                RankRepeatedLeaderboardConst.scoreRankingKey()
        ), n);
    }

    @Override
    public List<RankInfo> getPlayerRankRange(String playerId, int n) {
        return RankRepeatedLeaderboardHelper.fetchAround(REDIS, List.of(
                RankRepeatedLeaderboardConst.rankingKey(),
                RankRepeatedLeaderboardConst.scoreRankingKey()
        ), playerId, n);
    }
}
