package com.itheima.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 员工实体
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
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;


}
