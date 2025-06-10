package com.codetest.leaderboard.rankrepeated;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson2.JSON;
import com.codetest.leaderboard.RankInfo;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class RankRepeatedLeaderboardHelper {

    public static final String SCRIPT_UPDATE_SCORE = "-- 检查参数数量\n" +
            "if #KEYS ~= 3 or #ARGV ~= 2 then\n" +
            "    return nil\n" +
            "end\n" +
            "local result = {}\n" +
            "-- 定义类型\n" +
            "local rankKey = KEYS[1]\n" +
            "local scoreKey = KEYS[2]\n" +
            "local scoreCountKey = KEYS[3]\n" +
            "local playerId = ARGV[1]\n" +
            "local newScoreMillis = tonumber(ARGV[2])\n" +
            "if newScoreMillis == nil then\n" +
            "    return nil\n" +
            "end\n" +
            "local newScore = math.floor(newScoreMillis)\n" +
            "local oldScoreMillis = redis.call('ZSCORE', rankKey, playerId) or nil\n" +
            "-- 更新rank中的分数\n" +
            "redis.call('ZADD', rankKey, newScoreMillis, playerId)\n" +
            "-- 处理旧分数计数\n" +
            "if oldScoreMillis ~= nil then\n" +
            "    local oldScore =  math.floor(tonumber(oldScoreMillis))\n" +
            "    local oldCount = redis.call('HINCRBY', scoreCountKey, oldScore, -1)\n" +
            "    if oldCount <= 0 then\n" +
            "        redis.call('ZREM', scoreKey, oldScore)\n" +
            "    end\n" +
            "end\n" +
            "-- 处理新分数计数\n" +
            "local newCount = redis.call('HINCRBY', scoreCountKey, newScore, 1)\n" +
            "if newCount == 1 then\n" +
            "    redis.call('ZADD', scoreKey, newScore, newScore)\n" +
            "end\n" +
            "-- 返回新分数和旧分数\n" +
            "return result";

    public static void updateScore(Jedis jedis, List<String> redisKeys, String playerId, double score) {
        jedis.eval(SCRIPT_UPDATE_SCORE,
                redisKeys,
                Arrays.asList(playerId, String.valueOf(score))
        );
    }

    public static final String SCRIPT_FETCH_RANK = "-- 检查参数数量\n" +
            "if #KEYS ~= 2 or #ARGV ~= 1 then\n" +
            "    return nil\n" +
            "end\n" +
            "local result = {}\n" +
            "-- 定义类型\n" +
            "local rankKey = KEYS[1]\n" +
            "local scoreKey = KEYS[2]\n" +
            "local playerId = ARGV[1]\n" +
            "-- 获取玩家带时间戳的分数\n" +
            "local scoreMillis = redis.call('ZSCORE', rankKey, playerId) or nil\n" +
            "if scoreMillis == nil then\n" +
            "    return { -1, -1 }\n" +
            "end\n" +
            "-- 提取实际分数（去除时间戳）\n" +
            "local score = math.floor(scoreMillis)\n" +
            "local rank = redis.call('ZREVRANK', scoreKey, score)\n" +
            "return { score, rank + 1 }";

    public static RankInfo fetchRank(Jedis jedis, List<String> redisKeys, String playerId) {
        Object object = jedis.eval(SCRIPT_FETCH_RANK,
                redisKeys,
                Collections.singletonList(playerId)
        );
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(object));
        return new RankInfo(playerId, jsonArray.getInteger(0), jsonArray.getInteger(1));
    }

    public static final String SCRIPT_FETCH_TOP = "-- 检查参数数量\n" +
            "if #KEYS ~= 2 or #ARGV ~= 1 then\n" +
            "    return nil\n" +
            "end\n" +
            "local output = {}\n" +
            "-- 定义类型\n" +
            "local rankKey = KEYS[1]\n" +
            "local scoreKey = KEYS[2]\n" +
            "local n = ARGV[1]\n" +
            "-- 获取玩家排名\n" +
            "local results = redis.call('ZREVRANGE', KEYS[1], 0, n - 1, 'WITHSCORES')\n" +
            "-- 查询排名最高的玩家实际的排名\n" +
            "if #results < 2 then\n" +
            "    return {{}}\n" +
            "end\n" +
            "local scoreMillis = results[2]\n" +
            "local score = math.floor(scoreMillis)\n" +
            "local startRank = 1\n" +
            "for i = 1, #results, 2 do\n" +
            "    local currScore = math.floor(results[i + 1])\n" +
            "    if score ~= currScore then\n" +
            "        startRank = startRank + 1\n" +
            "        score = currScore\n" +
            "    end\n" +
            "    local currentRank = startRank\n" +
            "    table.insert(output, { currentRank, results[i], currScore })\n" +
            "end\n" +
            "return output";
    public static List<RankInfo> fetchTop(Jedis jedis, List<String> redisKeys, int n) {
        Object object = jedis.eval(SCRIPT_FETCH_TOP,
                redisKeys,
                List.of(String.valueOf(n))
        );
        List<RankInfo> result = new ArrayList<>();
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(object));
        for (int i = 0; i < jsonArray.size(); i ++) {
            JSONArray data = jsonArray.getJSONArray(i);
            result.add(new RankInfo(data.getString(1), data.getInteger(2), data.getInteger(0)));
        }
        return result;
    }

    public static final String SCRIPT_FETCH_AROUND = "-- 检查参数数量\n" +
            "if #KEYS ~= 2 or #ARGV ~= 2 then\n" +
            "    return nil\n" +
            "end\n" +
            "local output = {}\n" +
            "-- 定义类型\n" +
            "local rankKey = KEYS[1]\n" +
            "local scoreKey = KEYS[2]\n" +
            "local playerId = ARGV[1]\n" +
            "local n = ARGV[2]\n" +
            "-- 获取玩家排名\n" +
            "local rank = redis.call('ZREVRANK', rankKey, playerId) or nil\n" +
            "if rank == nil then\n" +
            "    return {{}}\n" +
            "end\n" +
            "-- 计算获取的玩家\n" +
            "local total = redis.call('ZCARD', KEYS[1])\n" +
            "local halfN = math.floor(n / 2)\n" +
            "local startPos = math.max(0, rank - halfN)\n" +
            "local endPos = math.min(total - 1, startPos + n - 1)\n" +
            "startPos = math.max(0, endPos - n + 1)\n" +
            "local results = redis.call('ZREVRANGE', KEYS[1], startPos, endPos, 'WITHSCORES')\n" +
            "-- 查询排名最高的玩家实际的排名\n" +
            "if #results < 2 then\n" +
            "    return {{}}\n" +
            "end\n" +
            "local scoreMillis = results[2]\n" +
            "local score = math.floor(scoreMillis)\n" +
            "local startRank = redis.call('ZREVRANK', scoreKey, score) + 1\n" +
            "for i = 1, #results, 2 do\n" +
            "    local currScore = math.floor(results[i + 1])\n" +
            "    if score ~= currScore then\n" +
            "        startRank = startRank + 1\n" +
            "        score = currScore\n" +
            "    end\n" +
            "    local currentRank = startRank\n" +
            "    table.insert(output, { currentRank, results[i], currScore })\n" +
            "end\n" +
            "return output";

    public static List<RankInfo> fetchAround(Jedis jedis, List<String> redisKeys, String playerId, int n) {
        Object object = jedis.eval(SCRIPT_FETCH_AROUND,
                redisKeys,
                List.of(playerId, String.valueOf(n))
        );
        List<RankInfo> result = new ArrayList<>();
        JSONArray jsonArray = JSONArray.parseArray(JSON.toJSONString(object));
        for (int i = 0; i < jsonArray.size(); i ++) {
            JSONArray data = jsonArray.getJSONArray(i);
            result.add(new RankInfo(data.getString(1), data.getInteger(2), data.getInteger(0)));
        }
        return result;
    }
}
