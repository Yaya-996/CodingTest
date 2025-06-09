package com.codetest.leaderboard.custom;

import com.codetest.leaderboard.RankInfo;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class LeaderboardHelper {
    public static double scoreParse(int score, long timestamp) {
        // 把timestamp转为小数后缀并且对1取互补, 用于保证时间越小的玩家排序值越大
        return score + (1 - 1.0 * timestamp / 1e13);
    }

    public static final String SCRIPT_FETCH_N_AROUND =
                    "local member = ARGV[1]\n" +
                    "local n = tonumber(ARGV[2])\n" +
                    "local rank = redis.call('ZREVRANK', KEYS[1], member)\n" +
                    "if not rank then\n" +
                    "    return nil  -- 玩家不在榜里\n" +
                    "end\n" +
                    "local total = redis.call('ZCARD', KEYS[1])\n" +
                    "local halfN = math.floor(n / 2)\n" +
                    "local startPos = math.max(0, rank - halfN)\n" +
                    "local endPos = math.min(total - 1, startPos + n - 1)\n" +
                    "startPos = math.max(0, endPos - n + 1)\n" +
                    "local results = redis.call('ZREVRANGE', KEYS[1], startPos, endPos, 'WITHSCORES')" + "local output = {}\n" +
                    "for i = 1, #results, 2 do\n" +
                    "    local currentRank = startPos + (i-1)/2\n" +
                    "    table.insert(output, { currentRank + 1, results[i], results[i + 1] })" +
                    "end\n" +
                    "return output";

    public static List<RankInfo> fetchNAround(Jedis jedis, String redisKey, String playerId, int n) {
        Object result = jedis.eval(SCRIPT_FETCH_N_AROUND,
                Collections.singletonList(redisKey),
                Arrays.asList(playerId, String.valueOf(n))
        );
        if (result == null) {
            // 成员不存在的情况, 返回空数组
            return List.of();
        }

        // 解析结果
        List<RankInfo> rankList = new ArrayList<>();
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
