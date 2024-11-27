package com.sky.service.impl;

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
}
