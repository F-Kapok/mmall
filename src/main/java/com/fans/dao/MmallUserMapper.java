package com.fans.dao;

import com.fans.pojo.MmallUser;
import com.fans.pojo.MmallUserExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface MmallUserMapper {
    long countByExample(MmallUserExample example);

    int deleteByExample(MmallUserExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(MmallUser record);

    int insertSelective(MmallUser record);

    List<MmallUser> selectByExample(MmallUserExample example);

    MmallUser selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") MmallUser record, @Param("example") MmallUserExample example);

    int updateByExample(@Param("record") MmallUser record, @Param("example") MmallUserExample example);

    int updateByPrimaryKeySelective(MmallUser record);

    int updateByPrimaryKey(MmallUser record);
}