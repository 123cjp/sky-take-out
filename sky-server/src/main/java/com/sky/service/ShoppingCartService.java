package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * @author 陈建平
 */
public interface ShoppingCartService {



    /**
     * 添加购物车
     * @param shoppingCartDTO
     */
    void add(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车
     * @return
     */
    List<ShoppingCart> showShoppingCart();

    /**
     * 清空购物车
     */
    void cleanShoppingCart();
    /**
     * 删除购物车一个商品
     * @param shoppingCartDTO
     * @return
     */
    void subShoppingCar(ShoppingCartDTO shoppingCartDTO);
}
