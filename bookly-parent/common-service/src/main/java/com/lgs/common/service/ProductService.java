package com.lgs.common.service;

import com.lgs.common.model.Product;
import com.lgs.common.util.PaginateInfo;

import java.util.List;
import java.util.Map;

public interface ProductService {
    Map<String,Object> findAll(PaginateInfo paginateInfo, Product product);
    Product findById(Integer id);
    boolean save(Product product);
    boolean update(Product product);
    boolean updateStatus(Integer status, Integer[] ids);
    boolean delete(Integer id);
}
