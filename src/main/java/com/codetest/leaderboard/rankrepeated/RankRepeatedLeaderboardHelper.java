package com.codetest.leaderboard.rankrepeated;

import com.codetest.leaderboard.RankInfo;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RankRepeatedLeaderboardHelper {

    public static final String SCRIPT_UPDATE_SCORE_AROUND = "";

    public static List<RankInfo> updateScore(Jedis jedis, List<String> redisKeys, String playerId, double score) {
        Object result = jedis.eval(SCRIPT_UPDATE_SCORE_AROUND,
                redisKeys,
                Arrays.asList(playerId, String.valueOf(score))
        );

        // 解析结果
        try {
            List<Object> resList = (List<Object>) result;

            return resList.stream().map(item -> {
                List<Object> list = (List<Object>) item;
                return new RankInfo(
                        (String) list.get(1),
                        (int) Math.floor(Double.parseDouble((String) list.get(2))),
                        ((Long) list.get(0)).intValue()
                );
            }).collect(Collectors.toList());
        } catch (Exception e) {
            // 解析失败的情况打印log并且抛出异常
            System.out.println("[RedisUtil] exec fetchNAround err: " + e.getMessage());
            e.printStackTrace();

            throw new RuntimeException("[RedisUtil] fetchNAround error");
        }
    }
}
