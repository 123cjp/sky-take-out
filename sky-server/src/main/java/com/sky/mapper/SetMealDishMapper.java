package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author 陈建平
 */
@Mapper
public interface SetMealDishMapper {
    /**
     * 根据菜品id查询对应套餐
     * @param dishIds
     * @return
     */
    //select setmeal_id from setmeal_dish where dish_id in(1,2,3,4)
    List<Long> getSetMealDishIds(List<Long> dishIds);

    /**
     * 批量保存套餐和菜品的关联关系
     * @param setMealDishes
     */
    void insertBatch(List<SetmealDish> setMealDishes);
}
