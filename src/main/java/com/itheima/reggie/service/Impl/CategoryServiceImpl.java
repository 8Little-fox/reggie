package com.itheima.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    /**
     * 根据id 删除分类，删除之前需要进行判断
     * @param ids
     */
    @Override
    public void remove(Long ids) {
//        查询当前分类是否关联了菜品/套餐，如果已经关联，抛出异常
//        关联菜品判断
        LambdaQueryWrapper<Dish> dishWrapper = new LambdaQueryWrapper<>();
        dishWrapper.eq(Dish::getCategoryId, ids);
        int count = dishService.count(dishWrapper);

        if(count>0){
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

//        关联套餐判断
        LambdaQueryWrapper<Setmeal> setmealWrapper = new LambdaQueryWrapper<>();
        setmealWrapper.eq(Setmeal::getCategoryId, ids);
        int count2 = setmealService.count();
        if (count2>0){
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        super.removeById(ids);
    }
}