package com.yj.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yj.reggie.common.R;
import com.yj.reggie.entity.AddressBook;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;


public interface AddressBookService extends IService<AddressBook> {

    //设置默认地址
    public AddressBook setDefault(AddressBook addressBook);

    // 查询默认地址
    public AddressBook getDefault();

    //查询指定用户的全部地址
    public List<AddressBook> list(AddressBook addressBook);
}
