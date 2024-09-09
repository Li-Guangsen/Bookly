package com.lgs.admin.backend.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lgs.admin.backend.dao.AdminDao;
import com.lgs.admin.backend.model.Admin;
import com.lgs.admin.backend.service.AdminService;
import com.lgs.common.util.PaginateInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class AdminServiceImpl implements AdminService {
    private AdminDao adminDao;
    @Autowired
    public void setAdminDao(AdminDao adminDao) {
        this.adminDao = adminDao;
    }
    @Override
    public List<Admin> findAll(PaginateInfo paginateInfo, Admin admin) {
        try (Page<?> __ = PageHelper.startPage(paginateInfo.getPageNo(),paginateInfo.getPageSize())) {
            return adminDao.findAll(admin);
        }
    }
    @Override
    public Admin findById(Integer id) {
        return adminDao.selectByPrimaryKey(id);
    }

    @Override
    public Admin findByAdminName(String username) {
        return adminDao.findAdminByUsername(username);
    }

    @Override
    public boolean save(Admin admin) {
        return adminDao.insert(admin)>0;
    }

    @Override
    public boolean update(Admin admin) {
        return adminDao.updateByPrimaryKey(admin)>0;
    }

    @Override
    public boolean updatePassword(Admin admin) {
        return adminDao.updatePassword(admin)>0;
    }

    @Override
    public boolean delete(Integer id) {
        return adminDao.deleteByPrimaryKey(id)>0;
    }
}
