package com.lgs.dubbo.dao;

import com.lgs.common.model.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ProductDao {
    List<Product> findAll(@Param("pro") Product product);
    Product findById(Integer id);
    int save(Product product);
    int update(Product product);
    int delete(Integer id);
    int updateStatus(@Param("status") Integer status, @Param("ids") Integer[] ids);
}
