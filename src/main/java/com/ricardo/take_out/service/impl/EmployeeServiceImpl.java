package com.ricardo.take_out.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ricardo.take_out.common.CustomException;
import com.ricardo.take_out.entity.Employee;
import com.ricardo.take_out.mapper.EmployeeMapper;
import com.ricardo.take_out.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    /**
     * 员工信息分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page<Employee> page(int page, int pageSize, String name) {
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper= new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(!StringUtils.isEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        this.page(pageInfo,queryWrapper);
        return pageInfo;
    }

    /**
     * 根据id删除员工，删除之前需要进行判断
     */
    @Override
    public void delete(Long id) {
        Employee employee = this.getById(id);

        //先查询该员工是否在正常状态，如果是则抛出业务异常
        Integer status = employee.getStatus();

        //如果是在禁用状态,则可以删除
        if( status == 0) {
            //log.info("删除员工{}",employee.getName());
            this.removeById(employee.getId());
        }else {
            //此时应该回滚,因为可能前面的删除了，但是后面的是正在售卖
            throw new CustomException("删除员工处于正常状态,无法删除");
        }
    }
}
