package com.snowcattle.game.executor.update.cache;

import com.snowcattle.game.executor.common.utils.Loggers;
import com.snowcattle.game.executor.event.impl.event.UpdateEvent;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

/**
 * Created by jiangwenping on 17/4/26.
 *  updateevent的缓存服务
 */
public class UpdateEventCacheService {

    public static UpdateEventCacheFactory updateEventCacheFactory;

    private static int size;
    private static int maxSize;

    public static void init(){
        setSize(1024);
        setMaxSize(1024 * 32);
    }

    public static void start(){
        int size = getSize();
        int maxSize = getMaxSize();
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        genericObjectPoolConfig.setMaxTotal(maxSize);
        genericObjectPoolConfig.setMaxIdle(maxSize);
        genericObjectPoolConfig.setMinIdle(size);
        long time = 1000 * 30;
        genericObjectPoolConfig.setMaxWaitMillis(time);
        genericObjectPoolConfig.setSoftMinEvictableIdleTimeMillis(time);
        genericObjectPoolConfig.setTestOnReturn(true);

        updateEventCacheFactory = new UpdateEventCacheFactory(new UpdateEventPoolFactory(), genericObjectPoolConfig);
    }

    public static void stop(){
        if(updateEventCacheFactory != null) {
            updateEventCacheFactory.close();
        }
    }

    public static UpdateEvent createUpdateEvent(){
        try {
            return updateEventCacheFactory.borrowObject();
        } catch (Exception e) {
            Loggers.gameExecutorError.error(e.toString(), e);
        }
        return null;
    }

    public static void releaseUpdateEvent(UpdateEvent updateEvent){
            updateEventCacheFactory.returnObject(updateEvent);
    }

    public static int getSize() {
        return size;
    }

    public static void setSize(int size) {
        UpdateEventCacheService.size = size;
    }

    public static int getMaxSize() {
        return maxSize;
    }

    public static void setMaxSize(int maxSize) {
        UpdateEventCacheService.maxSize = maxSize;
    }
}
