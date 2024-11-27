package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 陈建平
 */
@Service
public class SetMealServiceImpl implements SetMealService {
    @Autowired
    private SetMealMapper setMealMapper;
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;


    /**
     * 新增套餐
     *
     * @param setmealDTO
     */
    @Transactional
    @Override
    public void saveWithDish(SetmealDTO setmealDTO) {
        //请求参数不需要这么多
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setMealMapper.insert(setmeal);


        //获取生成的套餐id
        Long setmealId = setmeal.getId();

        List<SetmealDish> setMealDishes = setmealDTO.getSetmealDishes();
        setMealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(setmealId);
        });

        //保存套餐和菜品的关联关系
        setMealDishMapper.insertBatch(setMealDishes);

    }

    /**
     * 分页查询套餐
     *
     * @param setmealPageQueryDTO
     * @return
     */

    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        PageHelper.startPage(setmealPageQueryDTO.getPage(), setmealPageQueryDTO.getPageSize());
        Page<Setmeal> page = setMealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }
}
