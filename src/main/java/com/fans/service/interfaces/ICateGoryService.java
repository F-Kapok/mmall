package com.fans.service.interfaces;

import com.fans.common.ServerResponse;
import com.fans.pojo.MmallCategory;

import java.util.List;

/**
 * @InterfaceName ICateGoryService
 * @Description: TODO 节点服务层
 * @Author fan
 * @Date 2018-12-10 13:38
 * @Version 1.0
 **/
public interface ICateGoryService {
    ServerResponse addCateGory(String cateGoryName, Integer parentId);

    ServerResponse setCateGoryName(Integer cateGoryId, String cateGoryName);

    ServerResponse<List<MmallCategory>> getChildParallelCateGory(Integer cateGoryId);

    ServerResponse<List<Integer>> getCateGoryAndChildById(Integer cateGoryId);
}
