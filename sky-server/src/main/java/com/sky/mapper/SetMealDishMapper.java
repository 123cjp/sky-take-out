package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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
    /**
     * 根据套餐id删除套餐和菜品的关联关系
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetMealId(Long setmealId);

    /**
     * 根据id查询套餐用于修改页面回显数据
     * @param id
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long id);
}
