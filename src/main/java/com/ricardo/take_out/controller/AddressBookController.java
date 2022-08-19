package com.ricardo.take_out.controller;

import com.ricardo.take_out.common.R;
import com.ricardo.take_out.entity.AddressBook;
import com.ricardo.take_out.service.AddressBookService;
import com.ricardo.take_out.common.BaseContext;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
@Api(tags ={"地址簿接口相关"})
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新增一个地址
     */
    @PostMapping
    @ApiOperation("新增地址接口")
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
    @ApiOperation("设置默认地址接口")
    public R<AddressBook> setDefault(@RequestBody AddressBook addressBook) {

        AddressBook address = addressBookService.setDefault(addressBook);
        return R.success(address);
    }

    /**
     * 根据id查询地址
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询地址接口")
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
    @ApiOperation("查询默认地址接口")
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
    @ApiOperation("查询指定用户的全部地址接口")
    public R<List<AddressBook>> list(AddressBook addressBook) {

        List<AddressBook> list = addressBookService.list(addressBook);
        //SQL:select * from address_book where user_id = ? order by update_time desc
        return R.success(list);
    }
}
