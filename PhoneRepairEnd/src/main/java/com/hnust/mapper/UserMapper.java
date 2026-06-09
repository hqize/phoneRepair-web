package com.hnust.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnust.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    User getById(Integer userId);

    IPage<User> selectAllUser(Page<User> page,
                              @Param("userId") Integer userId,
                              @Param("searchKeyword") String searchKeyword,
                              @Param("sortField") String sortField,
                              @Param("sortOrder") String sortOrder);

    int deleteUserByUserId(Integer userId);

    int updateUser(User user);

    String selectPasswordById(Integer userId);
}
