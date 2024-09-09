package com.lgs.dubbo.service.impl;

import com.lgs.common.model.Category;
import com.lgs.common.model.Product;
import com.lgs.common.service.CategoryService;
import com.lgs.common.service.ProductService;
import com.lgs.dubbo.dao.CategoryDao;
import com.lgs.dubbo.dao.ProductDao;
import org.apache.dubbo.config.annotation.DubboService;
import org.checkerframework.checker.units.qual.C;
import org.springframework.aop.framework.AopContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.*;

@CacheConfig(cacheNames = "com.lgs.dubbo.service.impl.CategoryServiceImpl")
@DubboService
public class CategoryServiceImpl implements CategoryService {
    private CategoryDao categoryDao;
    private ProductDao productDao;
    @Autowired
    public void setProductDao(ProductDao productDao) {
        this.productDao = productDao;
    }
    @Autowired
    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Cacheable(key = "'findAll'")
    @Override
    public List<Category> findAll() {
        return categoryDao.findAll();
    }

    @Cacheable(key = "'findTree'")
    @Override
    public Category findTree() {
        //获取当前类的动态代理实例
        CategoryService proxy = (CategoryService) AopContext.currentProxy();
        List<Category> categories = proxy.findAll();
        return makeTree(categories);
    }

    @Override
    public Category searchNameTree(Category category) {
        CategoryService proxy = (CategoryService) AopContext.currentProxy();
        List<Category> categories = proxy.findAll();
        return findNameTree(categories, category.getName());
    }


    @Cacheable(key = "'findNames'")
    @Override
    public List<Category> getCategoryNames() {
        return categoryDao.selectNames();
    }

    @Cacheable(key = "'findById_' + #p0")
    @Override
    public Category findById(Integer id) {
        System.out.println("findById"+ id);
        CategoryService proxy = (CategoryService) AopContext.currentProxy();
        Category root = proxy.findTree();
        return deepFindById(root, id);
    }
    @CacheEvict(allEntries = true)
    @Override
    public boolean save(Category category) {
        return categoryDao.save(category) > 0;
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean update(Category category) {
        return categoryDao.update(category) > 0;
    }

    @CacheEvict(allEntries = true)
    @Override
    public boolean delete(Integer id) {
        Product p = new Product();
        p.setCategoryId(id);
        if(productDao.findAll(p).size() > 0)
            return false;
        return categoryDao.delete(id) > 0;
    }

    private static Category makeTree(List<Category> categories) {
        Map<Integer, Category> cache = new HashMap<>();
        for (Category category : categories) {
            cache.put(category.getId(), category);
        }
        List<Category> roots = new ArrayList<>();
        for (Category category : categories) {
            if (category.getParentId() == null) {
                roots.add(category);
            } else {
                Category parent = cache.get(category.getParentId());
                if (parent != null) {
                    if (parent.getChildren() == null) {
                        parent.setChildren(new ArrayList<>());
                    }
                    parent.getChildren().add(category);
                }
            }
        }
        return roots.getFirst();
    }

    private static Category deepFindById(Category root, Integer id) {
        if (root.getId().equals(id)) {
            return root;
        }
        if (root.getChildren() != null) {
            for (Category child : root.getChildren()) {
                Category result = deepFindById(child, id);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static Category findNameTree(List<Category> categories, String name) {
        //名字为空 描述不为空
        if (name == null)
            return null;

        //根据名称查询复合条件的数据
        List<Category> list = categories.stream().filter(c -> c.getName().contains(name)).toList();
        List<Category> ParentList = new ArrayList<>(list);
        List<Category> ChildrenList = new ArrayList<>(list);

        //添加其父类别到List中
        int oldSize;
        do{
            oldSize = ParentList.size();
            list = new ArrayList<>(ParentList);
            for (Category c : list)
                if (c.getParentId() != null)
                    for (Category p : categories)
                        if (Objects.equals(p.getId(), c.getParentId()))
                            if (!ParentList.contains(p))
                                ParentList.add(p);
        }while (ParentList.size() != oldSize);
        for (Category item : ParentList)
            System.out.println(item.getName());
        //添加其子类别到List中
        do {
            oldSize = ChildrenList.size();
            list = new ArrayList<>(ChildrenList);
            for (Category p : list)
                for (Category c : categories)
                    if (Objects.equals(p.getId(), c.getParentId()))
                        if (!ChildrenList.contains(c))
                            ChildrenList.add(c);
        }while (ChildrenList.size() != oldSize);
        System.out.println("ChildrenList:");
        for (Category item : ParentList)
            System.out.println(item.getName());
        //合并两个集合去重
        for (Category item : ParentList)
            if (!ChildrenList.contains(item))
                ChildrenList.add(item);


        return makeTree(ChildrenList);
    }
}
