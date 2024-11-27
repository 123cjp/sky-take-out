package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

import java.util.List;

/**
 * @author 陈建平
 */
public interface SetMealService {

    /**
     * 新增菜品
     * @param setmealDTO
     */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 批量删除套餐
     * @param ids
     */
    void delectBacth(List<Long> ids);

    /**
     * 根据id修改套餐和菜品
     * @param setmealDTO
     */
    void updateWithDish(SetmealDTO setmealDTO);



    /**
     * 根据id查询套餐用于修改页面回显数据
     * @param id
     * @return
     */
    SetmealVO getByIdWithDish(Long id);

}
