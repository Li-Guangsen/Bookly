package com.lgs.common.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.lgs.common.dao.UserDao;
import com.lgs.common.model.User;
import com.lgs.common.service.UserService;
import com.lgs.common.util.PaginateInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private UserDao userDao;
    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }
    @Override
    public List<User> getUserAll(PaginateInfo paginateInfo, User user) {
        try (Page<?> __ = PageHelper.startPage(paginateInfo.getPageNo(),paginateInfo.getPageSize())) {
            return userDao.findAll(user);
        }
    }
    @Override
    public User getUserById(Integer id) {
        return userDao.selectByPrimaryKey(id);
    }

    @Override
    public boolean addUser(User user) {
        return userDao.insert(user)>0;
    }

    @Override
    public boolean updateUser(User user) {
        return userDao.updateByPrimaryKey(user)>0;
    }

    @Override
    public boolean updatePassword(User user) {
        return userDao.updatePassword(user)>0;
    }

    @Override
    public boolean deleteUser(Integer id) {
        return userDao.deleteByPrimaryKey(id)>0;
    }
}
