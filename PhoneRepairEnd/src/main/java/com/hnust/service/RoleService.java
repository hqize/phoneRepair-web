package com.hnust.service;

import com.hnust.pojo.Role;
import com.hnust.util.Result;

import java.util.List;

public interface RoleService {
    // 查询所有角色
    Result<List<Role>> listAllRoles();

}