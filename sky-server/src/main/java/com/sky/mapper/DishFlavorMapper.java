package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.DishFlavor;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 陈建平
 */
@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味数据
     * @param flavors
     */
    void insertBatch(List<DishFlavor> flavors);


    /**
     * 根据菜品id删除口味表
     * @param dishIds
     */
    /*@Delete("delete from dish_flavor where dish_id = #{dishId}")*/
    void deleteByDishId(List<Long> dishIds);

    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void  deleteByDishId1(Long dishId);
    /**
     * 根据菜品id查询口味表
     * @param dishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getByDishId(Long dishId);

}
