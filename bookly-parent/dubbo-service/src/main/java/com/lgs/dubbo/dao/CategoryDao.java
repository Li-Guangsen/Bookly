package com.lgs.dubbo.dao;

import com.lgs.common.model.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryDao {
    List<Category> findAll();
    Category findById(Integer id);
    List<Category> selectNames();
    int save(Category category);
    int update(Category category);
    int delete(Integer id);
}
