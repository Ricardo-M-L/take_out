package com.yj.reggie.controller;

import com.yj.reggie.common.BaseContext;
import com.yj.reggie.common.R;
import com.yj.reggie.entity.AddressBook;
import com.yj.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地址簿管理
 */
@Slf4j
@RestController
@RequestMapping("/addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增一个地址
     */
    @PostMapping
    public R<AddressBook> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId()); //给地址设置当前用户id
        log.info("addressBook:{}", addressBook);
        addressBookService.save(addressBook);
        return R.success(addressBook);
    }

    /**
     * 设置默认地址
     */
    @PutMapping("default")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {

        AddressBook address = addressBookService.setDefault(addressBook);
        return R.success(address);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    public R get(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null) {
            return R.success(addressBook);
        } else {
            return R.error("没有找到该对象");
        }
    }

    /**
     * 查询默认地址
     */
    @GetMapping("default")
    public R<AddressBook> getDefault() {
        AddressBook defaultAddressBook = addressBookService.getDefault();

        if (null == defaultAddressBook) {
            return R.error("没有找到该对象");
        } else {
            return R.success(defaultAddressBook);
        }
    }

    /**
     * 查询指定用户的全部地址
     */
    @GetMapping("/list")
    public R<List<AddressBook>> list(AddressBook addressBook) {

        List<AddressBook> list = addressBookService.list(addressBook);
        //SQL:select * from address_book where user_id = ? order by update_time desc
        return R.success(list);
    }
}
