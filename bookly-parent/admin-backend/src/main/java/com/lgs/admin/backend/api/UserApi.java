package com.lgs.admin.backend.api;

import com.github.pagehelper.PageInfo;
import com.lgs.common.model.User;
import com.lgs.common.service.UserService;
import com.lgs.common.util.PaginateInfo;
import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/users", produces = "application/json;charset=utf-8")
public class UserApi {
    private static PasswordEncryptor encryptor = new StrongPasswordEncryptor();
    private UserService userService;
    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    public Map<String,Object> getAdminAll(@RequestParam(name = "pageNo",defaultValue = "1") Integer pageNo,
                                          @RequestParam(name = "pageSize",defaultValue = "1000") Integer pageSize, User user) {
        PaginateInfo paginateInfo = new PaginateInfo(pageNo,pageSize);
        List<User> users = userService.getUserAll(paginateInfo,user);
        PageInfo<User> pageInfo = new PageInfo<>(users);
        Map<String,Object> map = Map.of("pageNo",pageInfo.getPageNum(),"pageSize",pageInfo.getPageSize(),"total",pageInfo.getTotal());
        return  Map.of("success",true,"rows", users,"pi",map);
    }
    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Integer id) {
        return userService.getUserById(id);
    }
    @PostMapping
    public Map<String, Object> addUser(@RequestBody User user) {
        user.setPassword(encryptor.encryptPassword("123456"));
        boolean success = userService.addUser(user);
        return Map.of("success", success);
    }
    @DeleteMapping("/{id}")
    public Map<String, Object> deleteUser(@PathVariable("id") Integer id) {
        boolean success = userService.deleteUser(id);
        return Map.of("success", success);
    }
    @PutMapping
    public Map<String, Object> updateUser(@RequestBody User user) {
        boolean success = userService.updateUser(user);
        return Map.of("success", success);
    }
    @PutMapping("/password")
    public Map<String, Object> updatePassword(@RequestBody User user) {
        user.setPassword(encryptor.encryptPassword(user.getPassword()));
        boolean success = userService.updatePassword(user);
        return Map.of("success", success);
    }


}
