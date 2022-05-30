package com.yj.reggie.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 分类
 */
@Data
@ApiModel("分类")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Long id;

    //类型 1 菜品分类 2 套餐分类
    @ApiModelProperty("类型 (1:菜品分类; 2:套餐分类)")
    private Integer type;

    //分类名称
    @ApiModelProperty("主键")
    private String name;

    //排序
    @ApiModelProperty("排序")
    private Integer sort;

    //创建时间
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("创建时间")
    private LocalDateTime createTime;

    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty("更新时间")
    private LocalDateTime updateTime;

    //创建者
    @TableField(fill = FieldFill.INSERT)
    @ApiModelProperty("创建者")
    private Long createUser;

    //更新者
    @TableField(fill = FieldFill.INSERT_UPDATE)
    @ApiModelProperty("更新者")
    private Long updateUser;

    //是否删除
   // private Integer isDeleted;

}
