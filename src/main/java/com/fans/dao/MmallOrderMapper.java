package com.fans.dao;

import com.fans.pojo.MmallOrder;
import com.fans.pojo.MmallOrderExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MmallOrderMapper {
    long countByExample(MmallOrderExample example);

    int deleteByExample(MmallOrderExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(MmallOrder record);

    int insertSelective(MmallOrder record);

    List<MmallOrder> selectByExample(MmallOrderExample example);

    MmallOrder selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") MmallOrder record, @Param("example") MmallOrderExample example);

    int updateByExample(@Param("record") MmallOrder record, @Param("example") MmallOrderExample example);

    int updateByPrimaryKeySelective(MmallOrder record);

    int updateByPrimaryKey(MmallOrder record);
}