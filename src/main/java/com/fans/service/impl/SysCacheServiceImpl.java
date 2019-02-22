package com.fans.service.impl;

import com.fans.common.CacheKeyConstants;
import com.fans.common.RedisPool;
import com.fans.service.interfaces.SysCacheService;
import com.fans.utils.JsonMapper;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.ShardedJedis;

/**
 * @ClassName SysCacheServiceImpl
 * @Description:  缓存服务实习层
 * @Author fan
 * @Date 2018-11-16 16:35
 * @Version 1.0
 **/
@Service
@Slf4j
public class SysCacheServiceImpl implements SysCacheService {
    @Autowired
    private RedisPool redisPool;

    @Override
    public void saveCache(CacheKeyConstants prefix, String value, Integer timeOut) {
        saveCache(prefix, value, timeOut, "");
    }

    @Override
    public void saveCache(CacheKeyConstants prefix, String value, Integer timeOut, String... keys) {
        if (value == null)
            return;
        ShardedJedis shardedJedis = null;
        try {
            String key = generateCacheKey(prefix, keys);
            shardedJedis = redisPool.instance();
            shardedJedis.setex(key, timeOut, value);
        } catch (Exception e) {
            log.error("save cache exception, prefix:{}, keys:{}", prefix.name(), JsonMapper.obj2String(keys));
        } finally {
            redisPool.safeClose(shardedJedis);
        }
    }

    @Override
    public String getFromCache(CacheKeyConstants prefix, String... keys) {
        ShardedJedis shardedJedis = null;
        String key = generateCacheKey(prefix, keys);
        try {
            shardedJedis = redisPool.instance();
            String result = shardedJedis.get(key);
            return result;
        } catch (Exception e) {
            log.error("get from cache exception, prefix:{}, keys:{}", prefix.name(), JsonMapper.obj2String(keys));
            return null;
        } finally {
            redisPool.safeClose(shardedJedis);
        }
    }

    private String generateCacheKey(CacheKeyConstants prefix, String... keys) {
        String key = prefix.name();
        if (keys != null && keys.length > 0) {
            key += "_" + Joiner.on("_").join(keys);
        }
        return key;
    }
}
