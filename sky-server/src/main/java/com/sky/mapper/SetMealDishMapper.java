package com.sky.mapper;

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
}
