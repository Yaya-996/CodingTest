package com.codetest.leaderboard;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.resps.Tuple;

import java.util.*;
import java.util.stream.Collectors;

public class RedisUtil {
    // redis配置和池化过程略, 不是重点所以直接写死连接
    private static final Jedis REDIS = new Jedis("localhost", 6379);

    public static Jedis fetchRedis(String key) {
        return REDIS;
    }




}
