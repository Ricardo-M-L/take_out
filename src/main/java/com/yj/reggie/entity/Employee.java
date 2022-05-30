package com.yj.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 员工
 */
@Data
@ApiModel("员工")
public class Employee implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("员工账号")
    private String username;

    @ApiModelProperty("员工姓名")
    private String name;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("手机号")
    private String phone;

    @ApiModelProperty("性别(0:女; 1:男)")
    private String sex;

    @ApiModelProperty("身份证号码")
    private String idNumber;   //身份证号码

    @ApiModelProperty("状态(0:禁用; 1:正常)")
    private Integer status;

    @TableField(fill =FieldFill.INSERT )   //插入时填充字段
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    @TableField(fill =FieldFill.INSERT_UPDATE)   //插入/更新时填充字段
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    @TableField(fill = FieldFill.INSERT)   //插入时填充字段
    @ApiModelProperty("创建者")
    private Long createUser;

    @TableField(fill = FieldFill.INSERT_UPDATE)  //插入/更新时填充字段
    @ApiModelProperty("更新者")
    private Long updateUser;

}
