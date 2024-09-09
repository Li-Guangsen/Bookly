package com.lgs.dubbo.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lgs.common.model.Category;
import com.lgs.common.model.Product;
import com.lgs.common.service.CategoryService;
import com.lgs.common.service.ProductService;
import com.lgs.common.util.PaginateInfo;
import com.lgs.dubbo.dao.CategoryDao;
import com.lgs.dubbo.dao.ProductDao;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@DubboService
public class ProductServiceImpl implements ProductService {
    private ProductDao productDao;
    private CategoryService categoryService;
    @Autowired
   public void setCategoryService(CategoryService categoryService) {
        this.categoryService = categoryService;
    }
    @Autowired
    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public Map<String,Object> findAll(PaginateInfo paginateInfo, Product product) {
        try (Page<?> __ = PageHelper.startPage(paginateInfo.getPageNo(),paginateInfo.getPageSize())) {
            List<Product> products = productDao.findAll(product);
            PageInfo<Product> pageInfo = new PageInfo<>(products);
            Map<String,Object> map = Map.of("pageNo",pageInfo.getPageNum(),"pageSize",pageInfo.getPageSize(),"total",pageInfo.getTotal());
            List<Product> list = new ArrayList<>(products);
            Map<Integer, Category> categoryMap = categoryService.findAll().stream().collect(
                    HashMap::new,
                    (m, c) -> m.put(c.getId(), c),
                    HashMap::putAll
            );
            for (Product p : list) {
                p.setCategory(categoryMap.get(p.getCategoryId()));
            }
            return Map.of("success",true,"rows", list,"pi",map);
        }

    }

    @Override
    public Product findById(Integer id) {
        return productDao.findById(id);
    }

    @Override
    public boolean save(Product product) {
        return productDao.save(product)>0;
    }

    @Override
    public boolean update(Product product) {

        return productDao.update(product)>0;
    }

    @Override
    public boolean updateStatus(Integer status, Integer[] ids) {
        return productDao.updateStatus(status,ids)>0;
    }

    @Override
    public boolean delete(Integer id) {
        return productDao.delete(id)>0;
    }
}
