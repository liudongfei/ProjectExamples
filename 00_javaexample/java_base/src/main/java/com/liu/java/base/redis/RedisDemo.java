package com.liu.java.base.redis;

import redis.clients.jedis.Jedis;

import java.util.Set;

public class RedisDemo {

    /**
     * main.
     * @param args args
     */
    public static void main(String[] args) {
        Jedis jedis = new Jedis("mincdh", 6379);
        Set<String> keys = jedis.keys("*");
        for (String key : keys) {
            System.out.println("key:\t" + key);
        }
        jedis.close();
    }
}
