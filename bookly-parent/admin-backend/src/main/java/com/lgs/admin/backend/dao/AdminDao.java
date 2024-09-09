package com.lgs.admin.backend.dao;

import org.apache.ibatis.annotations.Mapper;
import com.lgs.admin.backend.model.Admin;

import java.util.List;

@Mapper
public interface AdminDao {
    List<Admin> findAll(Admin admin);
    int deleteByPrimaryKey(Integer id);
    int insert(Admin admin);
    Admin selectByPrimaryKey(Integer id);
    int updateByPrimaryKey(Admin admin);
    int updatePassword(Admin admin);
    Admin findAdminByUsername(String username);


}
