package com.zy.springbootonedatasource.service;

import java.util.concurrent.locks.Lock;

/**
 * @InterfaceName: LockService
 * @Description: redis分布式锁
 *      在需要上锁的场景，一种场景就定义一种锁
 * @author: Robot
 * @date: 2019/5/29 9:06
 * @version:
 */
public interface LockService {
    /**
     * @Title: getDingtalkSyncLock
     * @Description: 触发钉钉信息同步场景，加锁防止一次同步未完成又进行第二次同步
     * @author: Robot
     * @date: 2019/5/29 11:37
     * @param: []
     * @return: java.util.concurrent.locks.Lock
     */
    Lock getDingtalkSyncLock();

}
