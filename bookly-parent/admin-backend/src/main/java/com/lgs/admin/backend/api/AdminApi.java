package com.lgs.admin.backend.api;

import com.github.pagehelper.PageInfo;
import com.lgs.admin.backend.model.Admin;
import com.lgs.admin.backend.service.AdminService;
import com.lgs.common.model.Account;
import com.lgs.common.util.PaginateInfo;
import com.wf.captcha.SpecCaptcha;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jasypt.util.password.PasswordEncryptor;
import org.jasypt.util.password.StrongPasswordEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import utils.JwtUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/v1/admins", produces = "application/json;charset=utf-8")
public class AdminApi {
    private static PasswordEncryptor encryptor = new StrongPasswordEncryptor();
    @Value("${jwt.secret.key}")
    private String secretKey;
    private AdminService adminService;
    @Autowired
    public void setAdminService(AdminService adminService) {
        this.adminService = adminService;
    }
    @GetMapping
    public Map<String,Object> getAdminAll(@RequestParam(name = "pageNo",defaultValue = "1") Integer pageNo,
                                          @RequestParam(name = "pageSize",defaultValue = "1000") Integer pageSize, Admin admin) {
        PaginateInfo paginateInfo = new PaginateInfo(pageNo,pageSize);
        List<Admin> admins = adminService.findAll(paginateInfo,admin);
        PageInfo<Admin> pageInfo = new PageInfo<>(admins);
        Map<String,Object> map = Map.of("pageNo",pageInfo.getPageNum(),"pageSize",pageInfo.getPageSize(),"total",pageInfo.getTotal());
        return  Map.of("success",true,"rows", admins,"pi",map);
    }
    @GetMapping("/{id}")
    public Admin getUserById(@PathVariable("id") Integer id) {
        return adminService.findById(id);
    }
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Account account, HttpSession session){
        if(!StringUtils.hasText(account.getCaptcha())){
            return Map.of("success", false, "error", "验证码不可为空");
        }
        String captcha = session.getAttribute("captcha").toString();
        if(!account.getCaptcha().equalsIgnoreCase(captcha)){
            return Map.of("success", false, "error", "验证码错误");
        }
        if(!StringUtils.hasText(account.getUsername())){
            return Map.of("success", false, "error", "用户名不可为空");
        }
        Admin admin = adminService.findByAdminName(account.getUsername());
        if (admin == null) {
            return Map.of("success", false, "error", "用户名不存在");
        }
//        System.out.println(user.getPassword()+" "+account.getPassword());
//        System.out.println(encryptor.encryptPassword("123456"));
        if(!encryptor.checkPassword(account.getPassword(), admin.getPassword())){
            return Map.of("success", false, "error", "密码错误");
        }
        //颁发token
        Map<String,Object> data = Map.of("password", account.getPassword());
        String jwt = JwtUtils.encode(data,secretKey,admin.getUsername());
        return Map.of("success", true, "jwt", jwt);
    }
    @GetMapping("/captcha")
    public void captcha(HttpServletResponse resp, HttpSession session) throws IOException {
        //生成验证码
        SpecCaptcha cap = new SpecCaptcha(170, 35, 4);
        //设置响应头
        resp.setContentType("image/gif");
        resp.setHeader("Pragma", "No-cache");
        resp.setHeader("Cache-Control", "no-cache");
        resp.setDateHeader("Expires", 0L);
        //存储验证码到session
        session.setAttribute("captcha", cap.text().toLowerCase());
        cap.out(resp.getOutputStream());
    }
    @GetMapping("/username")
    public String getUsernameFromToken()  {
        String username = JwtUtils.getUsernameFromToken();
//        System.out.println("username: " + username);
        return username;
    }
    @PostMapping
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody Admin admin) {
        admin.setPassword(encryptor.encryptPassword("123456"));
        boolean success = adminService.save(admin);
        return ResponseEntity.ok(Map.of("success", success));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable("id") Integer id) {
        boolean success = adminService.delete(id);
        return ResponseEntity.ok(Map.of("success", success));
    }
    @PutMapping
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody Admin admin) {
        boolean success = adminService.update(admin);
        return ResponseEntity.ok(Map.of("success", success));
    }
    @PutMapping("/password")
    public ResponseEntity<Map<String, Object>> updatePassword(@RequestBody Admin admin) {
        admin.setPassword(encryptor.encryptPassword(admin.getPassword()));
        boolean success = adminService.updatePassword(admin);
        return ResponseEntity.ok(Map.of("success", success));
    }
}
