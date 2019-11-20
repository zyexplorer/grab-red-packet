package com.zy.springbootonedatasource.service.impl;

import com.zy.springbootonedatasource.service.LockService;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.concurrent.locks.Lock;

/**
 * @ClassName: LockServiceImpl
 * @Description:
 * @author: Robot
 * @date: 2019/5/29 9:11
 * @version:
 */
@Service
public class LockServiceImpl implements LockService {

    @Value("${spring.redis.host}")
    private String redisServer;

    @Value("${spring.redis.port}")
    private String redisPort;

    @Value("${spring.redis.password}")
    private String redisPassword;

    @Value("${spring.redis.database}")
    private String redisDatabase;

    private static String LOCK_PREFIX = "project:lock";

    private RedissonClient redisson;

    public LockServiceImpl() {
    }

    @PostConstruct
    public void init() {
        Config config = new Config();
        if(StringUtils.isEmpty(redisPort)) {
            redisPort="6379";
        }
        config.useSingleServer().setAddress("redis://" + redisServer + ":" + redisPort).
                setPassword(redisPassword).setDatabase(Integer.parseInt(redisDatabase));
        redisson = Redisson.create(config);
    }

    /**
     * @Title: getDingtalkSyncLock
     * @Description: 获取钉钉同步锁
     * @author: Robot
     * @date: 2019/5/29 9:17
     * @param: []
     * @return: java.util.concurrent.locks.Lock
     */
    @Override
    public Lock getDingtalkSyncLock() {
        Lock lock = redisson.getLock(LOCK_PREFIX + ":dingtalk:sync");
        return lock;
    }


}
