package me.zyy.reggie.dao.front;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.zyy.reggie.entity.ShoppingCart;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ShoppingCartDao extends BaseMapper<ShoppingCart> {
}
