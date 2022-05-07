package me.zyy.reggie.dao.front;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.zyy.reggie.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDao extends BaseMapper<Orders> {

}