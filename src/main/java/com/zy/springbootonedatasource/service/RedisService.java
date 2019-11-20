package com.zy.springbootonedatasource.service;

import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @InterfaceName: RedisService
 * @Description: redis相关操作接口
 * @author: Robot
 * @date: 2019/5/28 23:36
 * @version:
 */
public interface RedisService {

    boolean existsKey(String key);

    String get(String key);

    void set(String key, String value, long time, TimeUnit timeUnit);

    void renameKey(String oldKey, String newKey);

    boolean renameKeyNotExist(String oldKey, String newKey);

    void deleteKey(String key);

    void deleteKey(String... keys);

    void deleteKey(Collection<String> keys);

    void expireKey(String key, long time, TimeUnit timeUnit);

    void expireKeyAt(String key, Date date);

    long getKeyExpire(String key, TimeUnit timeUnit);

    void persistKey(String key);
}
