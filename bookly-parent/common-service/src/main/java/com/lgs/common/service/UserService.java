package com.lgs.common.service;

import com.lgs.common.model.User;
import com.lgs.common.util.PaginateInfo;

import java.util.List;

public interface UserService {
    List<User> getUserAll(PaginateInfo paginateInfo, User user);
    User getUserById(Integer id);
    boolean addUser(User user);
    boolean updateUser(User user);
    boolean updatePassword(User user);
    boolean deleteUser(Integer id);
}
