package me.zyy.reggie.dao.front;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.zyy.reggie.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailDao extends BaseMapper<OrderDetail> {

}