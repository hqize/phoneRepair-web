package com.hnust.mapper;

import com.hnust.pojo.Role;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface RoleMapper {
    //查询所有角色
    @Select("SELECT * FROM yjx_role")
    List<Role> listAllRoles();
}