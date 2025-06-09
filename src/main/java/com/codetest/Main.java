package com.codetest;

import com.alibaba.fastjson.JSONObject;
import com.codetest.leaderboard.RankInfo;
import com.codetest.service.LeaderBoardService;
import com.codetest.service.impl.LeaderboardServiceImpl;

import java.util.Random;

public class Main {
    private static final Random random = new Random(System.currentTimeMillis());

    public static void testUpdateScore() {
        LeaderBoardService service = new LeaderboardServiceImpl();
        for (int i = 1; i <= 100; i++) {
            String playerId = String.format("test#%d", i);
            int score = random.nextInt(100);
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
        LeaderBoardService service = new LeaderboardServiceImpl();
        System.out.println(JSONObject.toJSONString(service.getTopN(30)));
        System.out.println(JSONObject.toJSONString(service.getPlayerRankRange("test#97", 30)));
        System.out.println(JSONObject.toJSONString(service.getPlayerRankRange("test#98", 30)));
    }

    public static void main(String[] args) {
        testGetPlayerRankRange();
    }
}