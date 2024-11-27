package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.mapper.SetMealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetMealService;
import com.sky.vo.SetmealVO;
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

    /**
     * 批量删除套餐
     *
     * @param ids
     */
    @Transactional
    public void delectBacth(List<Long> ids) {
        //判断是否可以出售
        ids.forEach(id -> {
            Setmeal setmeal = setMealMapper.getById(id);
            if(StatusConstant.ENABLE == setmeal.getStatus()) {
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        });

        ids.forEach(setmealId -> {
            //删除套餐表中的数据
            setMealMapper.deleteById(setmealId);
            //删除套餐菜品关系表中的数据
            setMealDishMapper.deleteBySetMealId(setmealId);
        });
    }

    /**
     * 根据id修改套餐和菜品
     * @param setmealDTO
     */
    public void updateWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setMealMapper.update(setmeal);

    }

    /**
     * 根据id查询套餐用于修改页面回显数据
     * @param id
     * @return
     */
    public SetmealVO getByIdWithDish(Long id) {
        Setmeal setmeal = setMealMapper.getById(id);
        List<SetmealDish> setmealDishes = setMealDishMapper.getBySetmealId(id);

        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }




}
