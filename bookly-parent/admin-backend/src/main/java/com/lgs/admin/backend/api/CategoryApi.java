package com.lgs.admin.backend.api;

import com.lgs.common.model.Category;
import com.lgs.common.service.CategoryService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/categories", produces = "application/json;charset=utf-8")
public class CategoryApi {
    @DubboReference
    private CategoryService categoryService;

    @GetMapping("/all")
    public Map<String, Object> findAll() {
        List<Category> categories = this.categoryService.findAll();
        return Map.of("success",true,"data", categories);
    }
    @GetMapping("/tree")
    public Map<String, Object> findTree() {
        Category category = this.categoryService.findTree();
        return Map.of("success",true,"data", category);
    }
    @GetMapping("/nameTree")
    public Map<String, Object> searchNameTree(Category category) {
        System.out.println(category);
        System.out.println(category.getName());
        Category tree = this.categoryService.searchNameTree(category);
        if(tree == null) {
            return Map.of("success",false,"data", "未找到该类别");
        }
        return Map.of("success",true,"data", tree);
    }
    @GetMapping("/{id}")
    public Map<String, Object> findById(@PathVariable("id") Integer id) {
        Category category = this.categoryService.findById(id);
        return Map.of("success",true,"data", category);
    }
    @GetMapping("/names")
    public Map<String, Object> findByNames() {
        List<Category> categories = this.categoryService.getCategoryNames();
        return Map.of("success",true,"data", categories);
    }
    @PostMapping
    public Map<String, Object> save(@RequestBody Category category) {
        boolean success = this.categoryService.save(category);
        return Map.of("success",success);
    }
    @PutMapping
    public Map<String, Object> update(@RequestBody Category category) {
        boolean success = this.categoryService.update(category);
        return Map.of("success",success);
    }
    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable("id") Integer id) {
        boolean success = this.categoryService.delete(id);
        return Map.of("success",success);
    }
}
