package com.fans.service.impl;

import com.fans.common.ServerResponse;
import com.fans.dao.MmallCategoryMapper;
import com.fans.pojo.MmallCategory;
import com.fans.pojo.MmallCategoryExample;
import com.fans.service.interfaces.ICateGoryService;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName CateGoryServiceImpl
 * @Description: TODO 节点服务实习层
 * @Author fan
 * @Date 2018-12-10 13:38
 * @Version 1.0
 **/
@Slf4j
@Service("iCateGoryService")
public class CateGoryServiceImpl implements ICateGoryService {
    @Autowired
    private MmallCategoryMapper categoryMapper;

    @Override
    public ServerResponse addCateGory(String cateGoryName, Integer parentId) {
        if (parentId == null || StringUtils.isBlank(cateGoryName)) {
            return ServerResponse.failureMsg("请检查入参");
        }
        MmallCategory category = MmallCategory.builder()
                .name(cateGoryName)
                .parentId(parentId)
                .status(true)
                .updateTime(new Date())
                .createTime(new Date())
                .build();
        int result = categoryMapper.insert(category);
        if (result > 0) {
            return ServerResponse.successMsg("添加节点成功！！！");
        }
        return ServerResponse.failureMsg("添加节点失败！！！");
    }

    @Override
    public ServerResponse setCateGoryName(Integer cateGoryId, String cateGoryName) {
        if (cateGoryId == null || StringUtils.isBlank(cateGoryName)) {
            return ServerResponse.failureMsg("请检查入参");
        }
        MmallCategory category = MmallCategory.builder()
                .id(cateGoryId)
                .name(cateGoryName)
                .updateTime(new Date())
                .build();
        int result = categoryMapper.updateByPrimaryKeySelective(category);
        if (result > 0) {
            return ServerResponse.successMsg("更新节点名称成功！！！");
        }
        return ServerResponse.failureMsg("更新节点名称失败！！！");
    }

    @Override
    public ServerResponse<List<MmallCategory>> getChildParallelCateGory(Integer cateGoryId) {
        MmallCategoryExample categoryExample = new MmallCategoryExample();
        MmallCategoryExample.Criteria criteria = categoryExample.createCriteria();
        criteria.andParentIdEqualTo(cateGoryId);
        List<MmallCategory> categories = categoryMapper.selectByExample(categoryExample);
        if (CollectionUtils.isEmpty(categories)) {
            log.info("当前节点的子节点为空！！");
        }
        return ServerResponse.success(categories);
    }

    @Override
    public ServerResponse<List<Integer>> getCateGoryAndChildById(Integer cateGoryId) {
        Set<MmallCategory> categorySet = Sets.newHashSet();
        addChildRecursion(categorySet, cateGoryId);
        List<Integer> categoryList = categorySet.stream().map(MmallCategory::getId).collect(Collectors.toList());
        return ServerResponse.success(categoryList);
    }


    private Set<MmallCategory> addChildRecursion(Set<MmallCategory> categorySet, Integer cateGoryId) {
        MmallCategory category = categoryMapper.selectByPrimaryKey(cateGoryId);
        if (category != null) {
            categorySet.add(category);
        }
        MmallCategoryExample categoryExample = new MmallCategoryExample();
        MmallCategoryExample.Criteria criteria = categoryExample.createCriteria();
        criteria.andParentIdEqualTo(cateGoryId);
        List<MmallCategory> categories = categoryMapper.selectByExample(categoryExample);
        for (MmallCategory mc : categories) {
            addChildRecursion(categorySet, mc.getId());
        }
        return categorySet;
    }
}
