package com.sky.service.impl;

import com.sky.entity.SetmealDish;
import com.sky.mapper.SetMealDishMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 陈建平
 */
@Service
public class SetMealDishMapperImpl implements SetMealDishMapper {
    /**
     * 根据菜品id查询数据
     * @param dishIds
     * @return
     */

    public List<Long> getSetMealDishIds(List<Long> dishIds) {
        return List.of();
    }

    /**
     * 保持菜品和套餐的关联关系
     * @param setMealDishes
     */

    public void insertBatch(List<SetmealDish> setMealDishes) {

    }
    /**
     * 根据套餐id删除套餐和菜品的关联关系
     * @param setmealId
     */
    @Override
    public void deleteBySetMealId(Long setmealId) {

    }

    @Override
    public List<SetmealDish> getBySetmealId(Long id) {
        return List.of();
    }


}
