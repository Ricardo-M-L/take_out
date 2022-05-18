package com.yj.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yj.reggie.entity.Category;

import java.util.List;

public interface CategoryService extends IService<Category> {

    //分类信息分页查询
    public Page<Category> page(int page, int pageSize);

    //根据ID删除分类
    public void remove(Long id);

    //新增菜品时回显分类数据：根据条件查询分类数据
    public List<Category> list(Category category);
}
