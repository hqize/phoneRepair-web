package com.hnust.service.impl;

import com.hnust.mapper.RoleMapper;
import com.hnust.pojo.Role;
import com.hnust.service.RoleService;
import com.hnust.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public Result<List<Role>> listAllRoles() {
        List<Role> Roles = roleMapper.listAllRoles();
        return Result.success(Roles);

    }
}