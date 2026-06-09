package com.hnust.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnust.dto.LoginUser;
import com.hnust.pojo.User;
import com.hnust.util.Result;

import java.util.Map;

public interface UserService extends IService<User> {
    //登录
    LoginUser login(String usernameOrEmail, String password);
    // 注册
    Result<String> register(String userName, String userEmail, String userPasswordHash);

    //查询所有用户
    Map<String, Object> getAllUsers(Integer userId, String searchKeyword, Integer pageNum,
                                    Integer pageSize, String sortField, String sortOrder);
    //删除用户
    int deleteUserByUserId(Integer userId);
    //修改用户
    Result<String> updateUser(User user);
}
