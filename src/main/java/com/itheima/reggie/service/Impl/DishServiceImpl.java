package com.itheima.reggie.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.dto.DishDto;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
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
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品，同时保存对应的口味数据
     *
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
//        保存菜品基本信息到菜品表 dish
        this.save(dishDto);

//       菜品id
        Long dishId = dishDto.getId();

//        菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        // 封装flavors信息并且批量保存
        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());

//        保存菜品口味信息导菜品口味表 dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id 查询菜品信息和对应的口味信息
     * @param id
     * @return
     */

    @Override
    public DishDto getByIdWithFlavor(Long id) {
//        根据菜品基本信息。从dish表查询
        Dish dish = this.getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

//        查询当前菜品对应的口味信息，从 dish_flavor 表查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dish.getId());

        List<DishFlavor> flavors=dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);
        return dishDto;
    }

    /**
     * 更新菜品信息，同时更新对应的口味信息
     * @param dishDto
     */

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
//        更新dish 表基本信息
        this.updateById(dishDto);

//        清理当前菜品对应的口味数据。 dish_flavor  表进行delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId, dishDto.getId());

        dishFlavorService.remove(queryWrapper);
//        添加当前提交过来的口味数据。 dish_flavor  表进行insert操作
        List<DishFlavor> flavors=dishDto.getFlavors();

        flavors = flavors.stream().map((item) -> {
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 根据id 删除菜品
     * @param ids
     */
    @Override
    public void remove(String ids) {
        String[] split=ids.split(",");
        for (String id:split) {
            this.removeById(Long.parseLong(id));
            dishFlavorService.remove(new LambdaQueryWrapper<DishFlavor>().eq(DishFlavor::getDishId, id));
        }
    }

    /**
     * 更新菜单状态
     * @param ids
     * @param status 0 停售 1 起售
     */
    @Override
    public void updateStatus(String ids,@PathVariable Integer status) {
        String[] idList = ids.split(",");
        for (String id : idList) {
            Dish dish = new Dish();
            dish.setId(Long.parseLong(id));
            dish.setStatus(status);
            this.updateById(dish);
        }
    }
}