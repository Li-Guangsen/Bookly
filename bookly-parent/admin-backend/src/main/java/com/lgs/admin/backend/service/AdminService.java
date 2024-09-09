package com.lgs.admin.backend.service;

import com.lgs.admin.backend.model.Admin;
import com.lgs.common.util.PaginateInfo;

import java.util.List;

public interface AdminService {
    List<Admin> findAll(PaginateInfo paginateInfo, Admin admin);
    Admin findById(Integer id);
    Admin findByAdminName(String username);
    boolean save(Admin admin);
    boolean update(Admin admin);
    boolean updatePassword(Admin admin);
    boolean delete(Integer id);

}
