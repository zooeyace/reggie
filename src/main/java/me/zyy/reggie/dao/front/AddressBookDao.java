package me.zyy.reggie.dao.front;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.zyy.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface AddressBookDao extends BaseMapper<AddressBook> {
}
