package com.itheima.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.CustomException;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.dto.SetmealDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐，同时需要保存套餐和菜品的关联关系
     * @param setmealDto
     */
    @Override
    public void saveWithDish(SetmealDto setmealDto) {
//        保存套餐的基本信息
        this.save(setmealDto);

//        菜品id
        Long setmealId = setmealDto.getId();
        List<SetmealDish> dishes =setmealDto.getSetmealDishes();
        dishes.stream().map((item) ->{
            item.setSetmealId(setmealId);
            return item;
        }).collect(Collectors.toList());

        setmealDishService.saveBatch(dishes);
    }
    /**
     * 根据id 查询菜品信息和对应的口味信息
     * @param id
     * @return
     */
    @Override
    public SetmealDto getByIdWithFlavor(Long id) {
//        获取套餐id
        Setmeal setmeal = this.getById(id);

        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal, setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmeal.getId());

        List<SetmealDish> setmealDishes=setmealDishService.list(queryWrapper);

        setmealDto.setSetmealDishes(setmealDishes);
        return setmealDto;
    }

    /**
     * 更新套餐信息，同时更新对应的套餐菜品信息
     * @param setmealDto
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateWithFlavor(SetmealDto setmealDto) {
        this.updateById(setmealDto);

        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId, setmealDto.getId());

        setmealDishService.remove(queryWrapper);

        List<SetmealDish> flavors=setmealDto.getSetmealDishes();
        flavors.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(flavors);
    }

    /**
     * 删除套餐，同时需要删除套餐和菜品的关联数据
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
//        查询套餐状态，确定是否删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);

        int count = this.count(queryWrapper);

//        如果不能删除，抛出一个业务异常
        if(count>0){
            throw new CustomException("套餐正在售卖中，不能删除");
        }
//        如果可以删除，先删除套餐表中的数据
        this.removeByIds(ids);

//        删除关系表中的数据
        LambdaQueryWrapper<SetmealDish> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.in(SetmealDish::getSetmealId, ids);
        setmealDishService.remove(queryWrapper1);

//        String[] split=ids.split(",");
//        for (String id:split) {
//            this.removeById(Long.parseLong(id));
//            setmealDishService.remove(new LambdaQueryWrapper<SetmealDish>().eq(SetmealDish::getSetmealId, id));
//        }
    }

    /**
     * 更新套餐状态
     * @param ids
     * @param status
     */
    @Override
    public void updateStatus(String ids,@PathVariable Integer status) {
        String[] idList = ids.split(",");
        for (String id : idList) {
            Setmeal setmeal = new Setmeal();
            setmeal.setId(Long.parseLong(id));
            setmeal.setStatus(status);
            this.updateById(setmeal);
        }
    }
}
