package com.lgs.common.service;

import com.lgs.common.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> findAll();
    /*
     * 查询类别树
     * return 根节点
     */
    Category findTree();
    Category searchNameTree(Category category);//条件查询
    List<Category> getCategoryNames();
    Category findById(Integer id);
    boolean save(Category category);
    boolean update(Category category);
    boolean delete(Integer id);


}
