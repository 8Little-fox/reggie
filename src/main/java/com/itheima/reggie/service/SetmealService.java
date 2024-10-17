package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Setmeal;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
//    新增套餐，同时需要保存套餐和菜品的关联关系
    public void saveWithDish(SetmealDto setmealDto);

    //    根据id 查询菜品信息和对应的口味信息
    public SetmealDto getByIdWithFlavor(Long id);

    //    更新菜品信息，同时更新对应的口味信息
    public void  updateWithFlavor(SetmealDto setmealDto);

    //    删除套餐，同时需要删除套餐和菜品的关联数据
    public void removeWithDish(List<Long> ids);
    //    更新菜单状态
    public void updateStatus(String ids, Integer status);
}