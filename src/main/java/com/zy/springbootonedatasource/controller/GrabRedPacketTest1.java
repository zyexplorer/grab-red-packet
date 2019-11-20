package com.zy.springbootonedatasource.controller;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * <p>Title: GrabRedPacketTest1</p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author ZY
 * <p> Just go on !!!</p>
 * @date 2019年11月20日  22:09
 */
public class GrabRedPacketTest1 {

    public static void main(String[] args) {
        final String packetId = UUID.randomUUID().toString();
        // 红包金额(以分为单位，无精度损失问题)
        int total = 2000;
        // 红包数量
        int count = 7;
        // 拆红包
        int[] packets = splitRedPacket(total, count);
        // 存红包
        saveRedPacket(packetId, packets);

        // 抢红包
        ExecutorService cachedThreadPool = new ThreadPoolExecutor(3, 10, 30L,
                TimeUnit.SECONDS, new SynchronousQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("grab-red-packet-thread-%d").build());
        //ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
        for (int i = 0; i < 20; i++) {
            cachedThreadPool.execute(() -> grabRedPacket(packetId));
        }
    }


    /**
     * 拆红包 1、红包金额要被全部拆分完 2、红包金额不能差的太离谱
     *
     * @param total
     * @param count
     * @return
     */
    public static int[] splitRedPacket(int total, int count) {
        int use = 0;
        int[] array = new int[count];
        Random random = new Random();
        for (int i = 0; i < count; i++) {
            if (i == count - 1) {
                array[i] = total - use;
            } else {
                // 2 红包随机金额浮动系数
                int avg = (total - use) * 2 / (count - i);
                array[i] = 1 + random.nextInt(avg - 1);
            }
            use = use + array[i];
        }
        return array;
    }

    /**
     * 存红包 采用redis的list结构
     *
     * @param packetId
     * @param packets
     */
    public static void saveRedPacket(String packetId, int[] packets) {
        RedisURI redisUri = RedisURI.Builder.redis("localhost")
                .withPassword("H3yuncom")
                .withDatabase(0)
                .build();
        RedisClient redisClient = RedisClient.create(redisUri);
        StatefulRedisConnection<String, String> connection;
        connection = redisClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();
        for (int packet : packets) {
            syncCommands.lpush("Packet:" + packetId, packet + "");
        }
        connection.close();
        redisClient.shutdown();
    }

    /**
     * 抢红包
     *
     * @param packetId
     */
    public static void grabRedPacket(String packetId) {
        RedisURI redisUri = RedisURI.Builder.redis("localhost")
                .withPassword("H3yuncom")
                .withDatabase(0)
                .build();
        RedisClient redisClient = RedisClient.create(redisUri);
        StatefulRedisConnection<String, String> connection = redisClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();
        String res = syncCommands.lpop("Packet:" + packetId);
        System.out.println("抢到红包：" + res);
        connection.close();
        redisClient.shutdown();
    }

}
