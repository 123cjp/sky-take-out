package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.mapper.SetMealDishMapper;
import com.sky.vo.DishVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 陈建平
 */
@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetMealDishMapper setMealDishMapper;

    /**
     * 新增菜品和对应口味
     *
     * @param dishDTO
     */

    @Transactional
    public void saveWithFlavor(DishDTO dishDTO) {
        //向菜品表插入1条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);


        //向口味表插入n条数据
        //获取insert语句的主键值
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
     * 菜品分页查询
     *
     * @param dishPageQueryDTO
     * @return
     */
    @Transactional
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(), dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 菜品批量删除
     *
     * @param ids
     */
    @Transactional
    public void deleteBatch(List<Long> ids) {

        //判断是否可以删除--是否处于出售
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {//可以用常量类
                //起售状态---不能删除
                //直接抛一个业务异常
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        //判断当前菜品是否被关联
        List<Long> setMealIds = setMealDishMapper.getSetMealDishIds(ids);
        if (setMealIds != null && setMealIds.size() > 0) {
            //有关联
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        //删除菜品数据
        /*
        for (Long id : ids) {
            dishMapper.deleteById(id);
            //删除口味数据
            dishFlavorMapper.deleteByDishId(id);
        }
        */
        //sql:delete from dish where id in (?,?,?)
        //sql:delete from dish_flavor where id in (?,?,?)

        //根据菜品id集合批量删除菜品数据
        //sql:delete from dish where id in (?,?,?)
        dishMapper.deleteById(ids);
        //根据菜品id集合批量删除关联的口味数据
        //sql:delete from dish_flavor where id in (?,?,?)
        dishFlavorMapper.deleteByDishId(ids);

    }

    /**
     * 根据id查询菜品
     *
     * @param id
     */

    public DishVO getByIdWithFlavor(Long id) {
        //1.根据id查询菜品数据
        Dish dish = dishMapper.getById(id);
        //2.根据菜品id查询口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getByDishId(id);
        //3.封装vo
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     *根据id修改菜品信息和口味数据
     * @param dishDTO
     */

    public void updateWithFlavor(DishDTO dishDTO) {
        //菜品表--修改菜品数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);


        //口味表--可以先删除在插入
        //1.删除口味
        dishFlavorMapper.deleteByDishId1(dishDTO.getId());
        //2.重新插入数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            dishFlavorMapper.insertBatch(flavors);
        }

    }

    /**
     * 根据分类id查询菜品
     * @param categoryId
     * @return
     */

    public List<Dish> list(Long categoryId) {
        Dish dish = Dish.builder()
                .categoryId(categoryId)
                .status(StatusConstant.ENABLE)
                .build();;
        return dishMapper.list(dish);
    }
}
