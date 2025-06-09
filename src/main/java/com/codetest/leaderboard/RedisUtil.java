package com.codetest.leaderboard;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RedisUtil {
    public static final String SCRIPT_FETCH_N_AROUND =
            "local member = ARGV[1]\n" +
            "local n = tonumber(ARGV[2])\n" +
            "local rank = redis.call('ZRANK', KEYS[1], member)\n" +
            "if not rank then\n" +
            "    return nil  -- 成员不存在\n" +
            "end\n" +
            "local total = redis.call('ZCARD', KEYS[1])\n" +
            "local half_n = math.floor(n / 2)\n" +
            "local start_i = math.max(0, rank - half_n)\n" +
            "local end_i = math.min(total - 1, start_i + n - 1)\n" +
            "start_i = math.max(0, end_i - n + 1)\n" +
            "return redis.call('ZRANGE', KEYS[1], start_i, end_i, 'WITHSCORES')";

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
            List<Tuple> tuples = parseRedisResult((List<Object>) result);

            for (int i = 0; i < tuples.size(); i++) {
                Tuple tuple = tuples.get(i);
                rankList.add(new RankInfo(
                        tuple.getElement(),
                        (int) Math.floor(tuple.getScore()),
                        i + 1 // 排名从1开始
                ));
            }
            return rankList;
        } catch (Exception e) {
            // 解析失败的情况打印log并且抛出异常
            System.out.println("[RedisUtil] exec fetchNAround err: " + e.getMessage());
            e.printStackTrace();

            throw new RuntimeException("[RedisUtil] fetchNAround error");
        }
    }

    private static List<Tuple> parseRedisResult(List<Object> result) {
        List<Tuple> tuples = new ArrayList<>();
        for (int i = 0; i < result.size(); i += 2) {
            String member = (String) result.get(i);
            double score = Double.parseDouble((String) result.get(i + 1));
            tuples.add(new Tuple(member, score));
        }
        return tuples;
    }
}
