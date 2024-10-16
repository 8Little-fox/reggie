package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工实体
 *
 * 在开发业务功能前，先将需要用到的类和接口基本结构创建好
 * 1: 实体类 entity/Employee
 * 2: Mapper接口 mapper/EmployeeMapper
 * 3: 业务层接口 service/Impl/EmployeeServiceImpl
 * 4: 控制层 controller/EmployeeController
 */
@Data
public class Employee implements Serializable {
    private static final long serialVersionUID=1L;

    private Long id;
    private String name;
    private String username;

    @ApiModelProperty(value = "密码")
    private String password;

    private String phone;

    @ApiModelProperty(value = "性别")
    private String sex;

    private String idNumber;
    private Integer status;

    /**
     * 对应工具类 common/MyMetaObjectHandler.java
     */
    @TableField(fill = FieldFill.INSERT) // 插入时填充字段
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE) //插入和更新时填充字段
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;


}
