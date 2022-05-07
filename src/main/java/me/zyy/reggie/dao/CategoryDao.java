package me.zyy.reggie.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import me.zyy.reggie.entity.Category;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryDao extends BaseMapper<Category> {
}
