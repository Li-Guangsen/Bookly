package com.lgs.admin.backend.api;

import com.lgs.common.model.Product;
import com.lgs.common.service.ProductService;
import com.lgs.common.util.PaginateInfo;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/products", produces = "application/json;charset=utf-8")
public class ProductApi {
    @DubboReference
    private ProductService productService;
    @GetMapping("/all")
    public Map<String, Object> findAll(@RequestParam(name = "pageNo",defaultValue = "1") Integer pageNo,
                                       @RequestParam(name = "pageSize",defaultValue = "1000") Integer pageSize, Product product) {
        PaginateInfo paginateInfo = new PaginateInfo(pageNo, pageSize);
        Map<String,Object> map = this.productService.findAll(paginateInfo, product);
        return map;
    }
    @PostMapping
    public Map<String, Object> save(@RequestBody Product product) {
        boolean success =  this.productService.save(product);
        return Map.of("success", success);
    }
    @PutMapping
    public Map<String, Object> update(@RequestBody Product product) {
        boolean success = this.productService.update(product);
        return Map.of("success", success);
    }
    @PutMapping("/{status}")
    public Map<String, Object> updateStatus(@PathVariable("status") Integer status, @RequestBody Integer[] ids) {
        boolean success = this.productService.updateStatus(status, ids);
        return Map.of("success", success);
    }
    @GetMapping("/{id}")
    public Product findById(@PathVariable("id") Integer id) {
        return this.productService.findById(id);
    }
    @DeleteMapping("/{id}")
    public Map<String, Object>delete(@PathVariable("id") Integer id) {
        boolean success = this.productService.delete(id);
        return Map.of("success", success);
    }
}
