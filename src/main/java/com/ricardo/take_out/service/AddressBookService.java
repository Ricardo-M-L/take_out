package com.ricardo.take_out.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ricardo.take_out.entity.AddressBook;

import java.util.List;


public interface AddressBookService extends IService<AddressBook> {

    //设置默认地址
    public AddressBook setDefault(AddressBook addressBook);

    // 查询默认地址
    public AddressBook getDefault();

    //查询指定用户的全部地址
    public List<AddressBook> list(AddressBook addressBook);
}
