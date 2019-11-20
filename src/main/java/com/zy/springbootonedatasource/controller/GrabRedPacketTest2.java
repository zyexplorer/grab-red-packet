package com.zy.springbootonedatasource.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.util.StopWatch;
import redis.clients.jedis.Jedis;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * <p>Title: GrabRedPacketTest2</p>
 * <p>Description: </p>
 * <p>Company: </p>
 *
 * @author ZY
 * <p> Just go on !!!</p>
 * @date 2019年11月20日  21:47
 */
public class GrabRedPacketTest2 {
    static String host = "localhost";
    static int honBaoCount = 10000;

    static int threadCount = 20;

    static String hongBaoList = "hongBaoList";
    static String hongBaoConsumedList = "hongBaoConsumedList";
    static String hongBaoConsumedMap = "hongBaoConsumedMap";

    static Random random = new Random();

    //	-- 函数：尝试获得红包，如果成功，则返回json字符串，如果不成功，则返回空
//	-- 参数：红包队列名， 已消费的队列名，去重的Map名，用户ID
//	-- 返回值：nil 或者 json字符串，包含用户ID：userId，红包ID：id，红包金额：money
    static String tryGetHongBaoScript =
//			"local bConsumed = redis.call('hexists', KEYS[3], KEYS[4]);\n"
//			+ "print('bConsumed:' ,bConsumed);\n"
            "if redis.call('hexists', KEYS[3], KEYS[4]) ~= 0 then\n"
                    + "return nil\n"
                    + "else\n"
                    + "local hongBao = redis.call('rpop', KEYS[1]);\n"
//			+ "print('hongBao:', hongBao);\n"
                    + "if hongBao then\n"
                    + "local x = cjson.decode(hongBao);\n"
                    + "x['userId'] = KEYS[4];\n"
                    + "local re = cjson.encode(x);\n"
                    + "redis.call('hset', KEYS[3], KEYS[4], KEYS[4]);\n"
                    + "redis.call('lpush', KEYS[2], re);\n"
                    + "return re;\n"
                    + "end\n"
                    + "end\n"
                    + "return nil";
    static StopWatch watch = new StopWatch();

    public static void main(String[] args) throws InterruptedException {
//		testEval();
        generateTestData();
        testTryGetHongBao();
    }

    public static void generateTestData() throws InterruptedException {
        Jedis jedis = new Jedis(host, 6379);
        jedis.auth("H3yuncom");
        jedis.flushAll();
        final CountDownLatch latch = new CountDownLatch(threadCount);
        AtomicInteger count = new AtomicInteger();
        for (int i = 0; i < threadCount; ++i) {
            final int temp = i;
            Thread thread = new Thread(() -> {
                Jedis jedis1 = new Jedis(host, 6379);
                jedis1.auth("H3yuncom");
                int per = honBaoCount / threadCount;
                JSONObject object = new JSONObject();
                for (int j = temp * per; j < (temp + 1) * per; j++) {
                    object.put("id", j);
                    object.put("money", j);
                    jedis1.lpush(hongBaoList, object.toJSONString());
                    count.getAndIncrement();
                }
                latch.countDown();
            });
            thread.start();
        }
        latch.await();
        System.out.println("大红包拆分小红包完毕,总数量：" + count.get());
    }

    public static void testTryGetHongBao() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(threadCount);
        System.err.println("start:" + System.currentTimeMillis() / 1000);
        watch.start();
        for (int i = 0; i < threadCount; ++i) {
            final int temp = i;
            Thread thread = new Thread(() -> {
                Jedis jedis = new Jedis(host, 6379);
                jedis.auth("H3yuncom");
                String sha = jedis.scriptLoad(tryGetHongBaoScript);
                int j = honBaoCount / threadCount * temp;
                while (true) {
                    Object object = jedis.eval(tryGetHongBaoScript, 4, hongBaoList, hongBaoConsumedList, hongBaoConsumedMap, "" + j);
                    j++;
                    if (object != null) {
//							System.out.println("get hongBao:" + object);
                    } else {
                        //已经取完了
                        if (jedis.llen(hongBaoList) == 0) {
                            break;
                        }
                    }
                }
                latch.countDown();
            });
            thread.start();
        }

        latch.await();
        watch.stop();

        System.err.println("time:" + watch.getTotalTimeSeconds());
        System.err.println("speed:" + honBaoCount / watch.getTotalTimeSeconds());
        System.err.println("end:" + System.currentTimeMillis() / 1000);
    }

}

