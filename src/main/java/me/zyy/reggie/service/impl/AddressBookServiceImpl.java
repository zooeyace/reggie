package me.zyy.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.zyy.reggie.dao.front.AddressBookDao;
import me.zyy.reggie.entity.AddressBook;
import me.zyy.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookDao, AddressBook> implements AddressBookService {
}
