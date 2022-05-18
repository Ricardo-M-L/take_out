package com.yj.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yj.reggie.common.R;
import com.yj.reggie.entity.Employee;

public interface EmployeeService extends IService<Employee> {

    //员工信息分页查询
    public Page<Employee> page(int page, int pageSize, String name);

    //根据ID删除员工
    public void delete(Long id);

}
