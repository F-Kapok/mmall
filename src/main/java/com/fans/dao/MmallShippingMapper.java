package com.fans.dao;

import com.fans.pojo.MmallShipping;
import com.fans.pojo.MmallShippingExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MmallShippingMapper {
    long countByExample(MmallShippingExample example);

    int deleteByExample(MmallShippingExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(MmallShipping record);

    int insertSelective(MmallShipping record);

    List<MmallShipping> selectByExample(MmallShippingExample example);

    MmallShipping selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") MmallShipping record, @Param("example") MmallShippingExample example);

    int updateByExample(@Param("record") MmallShipping record, @Param("example") MmallShippingExample example);

    int updateByPrimaryKeySelective(MmallShipping record);

    int updateByPrimaryKey(MmallShipping record);
}