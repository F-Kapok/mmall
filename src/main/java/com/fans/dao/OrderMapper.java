package com.fans.dao;

import com.fans.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderNo")Long orderNo);

    Order selectByOrderNo(@Param("orderNo")Long orderNo);

    List<Order> selectByUserId(@Param("userId")Integer userId);

    List<Order> selectAll();
}