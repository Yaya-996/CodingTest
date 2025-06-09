package com.codetest.service;

import com.codetest.leaderboard.RankInfo;

import java.util.List;

public interface LeaderBoardService {
    // 更新玩家分数
    void updateScore(String playerId, int score, long timestamp);
    // 获取排名
    RankInfo getPlayerRank(String playerId);
    // 获取最高n名
    List<RankInfo> getTopN(int n);
    // 获取指定rid周边的n名玩家
    List<RankInfo> getPlayerRankRange(String playerId, int n);
}
