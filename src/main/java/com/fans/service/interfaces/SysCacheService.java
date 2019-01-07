package com.fans.service.interfaces;


import com.fans.common.CacheKeyConstants;

/**
 * @InterfaceName SysCacheService
 * @Description:  缓存服务接口
 * @Author fan
 * @Date 2018-11-16 16:31
 * @Version 1.0
 **/
public interface SysCacheService {
    void saveCache(CacheKeyConstants prefix, String value, Integer timeOut);

    void saveCache(CacheKeyConstants prefix, String value, Integer timeOut, String... keys);

    String getFromCache(CacheKeyConstants prefix, String... keys);
}
