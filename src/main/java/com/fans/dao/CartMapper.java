package com.fans.dao;

import com.fans.pojo.Cart;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectByUserIdAndProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    List<Cart> selectByUserId(@Param("userId") Integer userId);

    int selectCartProductCheckedStatusByUserId(@Param("userId") Integer userId);

    int deleteBatch(@Param("userId") Integer userId, @Param("productIdList") List<String> productIdList);

    int updateChecked(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("checked") Integer checked);

    int selectCartProductCount(@Param("userId") Integer userId);

    List<Cart> selectCheckedCartByUserId(@Param("userId")Integer userId);
}