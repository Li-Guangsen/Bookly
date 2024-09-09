package com.lgs.common.dao;


import com.lgs.common.model.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserDao {
    List<User> findAll(User user);
    int deleteByPrimaryKey(Integer id);
    int insert(User user);
    User selectByPrimaryKey(Integer id);
    int updateByPrimaryKey(User user);
    int updatePassword(User user);
}
