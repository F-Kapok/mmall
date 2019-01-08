package com.fans.service.impl;

import com.fans.common.CommonConstants;
import com.fans.common.ResponseCode;
import com.fans.common.ServerResponse;
import com.fans.dao.CartMapper;
import com.fans.dao.MmallProductMapper;
import com.fans.pojo.Cart;
import com.fans.pojo.MmallProduct;
import com.fans.service.interfaces.ICartService;
import com.fans.utils.BigDecimalUtil;
import com.fans.utils.PropertiesUtil;
import com.fans.vo.CartProductVo;
import com.fans.vo.CartVo;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * @ClassName CartServiceImpl
 * @Description:
 * @Author fan
 * @Date 2019-01-08 10:40
 * @Version 1.0
 **/
@Service(value = "iCartService")
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;
    @Autowired
    private MmallProductMapper productMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse addCart(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == 0) {
            return ServerResponse.failureCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Date date = new Date();
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null) {
            //新增一条记录
            Cart targetCart = Cart.builder()
                    .quantity(count)
                    .checked(CommonConstants.Cart.CHECKED)
                    .productId(productId)
                    .userId(userId)
                    .createTime(date)
                    .updateTime(date)
                    .build();
            cartMapper.insert(targetCart);
        } else {
            count = cart.getQuantity() + count;
            cart.setQuantity(count);
            cart.setUpdateTime(date);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return this.list(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse updateCart(Integer userId, Integer productId, Integer count) {
        if (productId == null || count == 0) {
            return ServerResponse.failureCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(count);
            cart.setUpdateTime(new Date());
        }
        cartMapper.updateByPrimaryKeySelective(cart);
        return this.list(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ServerResponse deleteCart(Integer userId, String productIds) {
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        if (CollectionUtils.isEmpty(productIdList)) {
            return ServerResponse.failureCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        cartMapper.deleteBatch(userId, productIdList);
        return this.list(userId);
    }

    @Override
    public ServerResponse selectOrUnSelect(Integer userId, Integer productId, Integer checked) {
        cartMapper.updateChecked(userId, productId, checked);
        return this.list(userId);
    }

    @Override
    public ServerResponse getCartProductCount(Integer userId) {
        if (userId == null) {
            return ServerResponse.success(0);
        }
        Integer count = cartMapper.selectCartProductCount(userId);
        return ServerResponse.success(count);
    }

    private CartVo getCartVoLimit(Integer userId) {
        CartVo cartVo = CartVo.builder().build();
        List<Cart> cartList = cartMapper.selectByUserId(userId);
        List<CartProductVo> cartProductVoList = Lists.newArrayList();
        BigDecimal cartTotalPrice = new BigDecimal(BigInteger.ZERO.toString());
        if (CollectionUtils.isNotEmpty(cartList)) {
            for (Cart cart : cartList) {
                CartProductVo cartProductVo = new CartProductVo();
                cartProductVo.setId(cart.getId());
                cartProductVo.setUserId(cart.getUserId());
                cartProductVo.setProductId(cart.getProductId());
                MmallProduct product = productMapper.selectByPrimaryKey(cart.getProductId());
                if (product != null) {
                    cartProductVo.setProductMainImage(product.getMainImage());
                    cartProductVo.setProductName(product.getName());
                    cartProductVo.setProductSubtitle(product.getSubtitle());
                    cartProductVo.setProductStatus(product.getStatus());
                    cartProductVo.setProductPrice(product.getPrice());
                    cartProductVo.setProductStock(product.getStock());
                    //判断库存
                    int buyLimitCount = 0;
                    if (product.getStock() >= cart.getQuantity()) {
                        //库存充足
                        buyLimitCount = cart.getQuantity();
                        cartProductVo.setLimitQuantity(CommonConstants.Cart.LIMIT_NUM_SUCCESS);
                    } else {
                        buyLimitCount = product.getStock();
                        cartProductVo.setLimitQuantity(CommonConstants.Cart.LIMIT_NUM_FAIL);
                        //购物车中更新有效库存
                        Cart targetCart = Cart.builder()
                                .id(cart.getId())
                                .quantity(buyLimitCount)
                                .build();
                        cartMapper.updateByPrimaryKeySelective(targetCart);
                    }
                    cartProductVo.setQuantity(buyLimitCount);
                    //计算总价
                    cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity().doubleValue()));
                    cartProductVo.setProductChecked(cart.getChecked());
                }
                if (cart.getChecked() == CommonConstants.Cart.CHECKED) {
                    //如果勾选中 增加到整个购物车总价中
                    cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
                }
                cartProductVoList.add(cartProductVo);
            }
        }
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setAllChecked(this.getAllCheckedStatus(userId));
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setImageHost(PropertiesUtil.loadProperties("ftp", "url"));
        return cartVo;
    }

    private boolean getAllCheckedStatus(Integer userId) {
        if (userId == null) {
            return false;
        }
        return cartMapper.selectCartProductCheckedStatusByUserId(userId) == 0;

    }

    @Override
    public ServerResponse list(Integer userId) {
        CartVo cartVo = this.getCartVoLimit(userId);
        return ServerResponse.success(cartVo);
    }
}
