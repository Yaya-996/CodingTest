package com.codetest;

import com.alibaba.fastjson.JSONObject;
import com.codetest.leaderboard.RankInfo;
import com.codetest.service.leaderboard.LeaderBoardService;
import com.codetest.service.leaderboard.LeaderboardEnum;

import java.util.Random;

public class Main {
    private static final Random random = new Random(System.currentTimeMillis());

//    public static final LeaderBoardService service = LeaderboardEnum.LEADERBOARD.getService();
    public static final LeaderBoardService service = LeaderboardEnum.RANK_REPEATED_LEADERBOARD.getService();

    public static void testUpdateScore() {
        for (int i = 1; i <= 100; i++) {
            String playerId = String.format("test#%d", i);
            int score = random.nextInt(10);
            service.updateScore(playerId, score, System.currentTimeMillis());
        }

        RankInfo rankInfo1 = service.getPlayerRank("test#1");
        System.out.println(JSONObject.toJSONString(rankInfo1));
        RankInfo rankInfoError = service.getPlayerRank("test#-1");
        System.out.println(JSONObject.toJSONString(rankInfoError));

        System.out.println(JSONObject.toJSONString(service.getTopN(30)));

        System.out.println(JSONObject.toJSONString(service.getPlayerRankRange("test#50", 30)));
    }

    public static void testGetPlayerRankRange() {
        System.out.println(JSONObject.toJSONString(service.getTopN(30)));
        System.out.println(JSONObject.toJSONString(service.getPlayerRankRange("test#97", 30)));
        System.out.println(JSONObject.toJSONString(service.getPlayerRankRange("test#98", 30)));
    }

    public static void main(String[] args) {
        testUpdateScore();
//        testGetPlayerRankRange();
    }
}