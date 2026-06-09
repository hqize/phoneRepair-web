package com.hnust.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnust.pojo.User;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper extends BaseMapper<User> {
    //根据用户id查询用户对象
    @Select("select * from yjx_user where user_id = #{userId}")
    User getById(Integer userId);

    //查询所有用户
    @Select("""
                SELECT 
                    user_id AS userId,
                    user_name AS userName,
                    user_email AS userEmail,
                    user_password_hash AS userPasswordHash,
                    role_id AS roleId,
                    user_bio AS userBio,
                    user_phone AS userPhone,
                    user_gender AS userGender,
                    user_last_active AS userLastActive,
                    user_created_at AS userCreatedAt,
                    user_status AS userStatus
                FROM yjx_user
                WHERE 1=1
                -- 用户ID筛选：仅当userId不为null时添加条件
                AND ( #{userId} IS NULL OR user_id = #{userId} )
                -- 关键词搜索：仅当searchKeyword不为空时添加模糊匹配条件
                AND ( 
                    #{searchKeyword} IS NULL OR #{searchKeyword} = '' OR
                    user_name LIKE CONCAT('%', #{searchKeyword}, '%') OR
                    user_email LIKE CONCAT('%', #{searchKeyword}, '%') OR
                    user_phone LIKE CONCAT('%', #{searchKeyword}, '%')
                )
                -- 排序：与维修管理查询逻辑完全一致
                ORDER BY 
                    CASE WHEN #{sortField} IS NOT NULL AND #{sortField} != '' 
                         THEN CASE #{sortField} 
                              WHEN 'userName' THEN user_name 
                              WHEN 'userCreatedAt' THEN user_created_at 
                              WHEN 'userLastActive' THEN user_last_active 
                              WHEN 'userStatus' THEN user_status 
                              ELSE user_created_at END 
                    ELSE user_created_at END 
                    ${sortOrder != null && 'asc'.equals(sortOrder.toLowerCase()) ? 'ASC' : 'DESC'}
            """)
    IPage<User> selectAllUser(Page<User> page, Integer userId, String searchKeyword, String sortField, String sortOrder);

    //用户根据用户id查询用户对象已经写了,大家可以跟自己情况修改名字
    //删除用户
    @Delete("delete from yjx_user where user_id =#{userId}")
    int deleteUserByUserId(Integer userId);

    //更新用户
    @Update("UPDATE yjx_user " +
            "SET user_name = #{userName}, " +
            "user_email = #{userEmail}, " +
            "role_id = #{roleId}, " +
            "user_bio = #{userBio}, " +
            "user_phone = #{userPhone} " +
            "WHERE user_id = #{userId}")
    int updateUser(User user);

    @Select("SELECT user_password_hash FROM yjx_user WHERE user_id = #{userId}")
    String selectPasswordById(Integer userId);
}
