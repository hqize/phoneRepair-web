package com.hnust.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor//构造函数
@NoArgsConstructor//无参构造
@TableName("yjx_user")//表名
public class User {
    @TableId(type = IdType.AUTO)//主键
    private Integer userId; //用户id
    private String userName; //用户名称
    private String userEmail; //用户邮箱
    private String userPasswordHash; //用户密码
    private Integer roleId; //角色id
    private String userBio; //用户备注
    private String userPhone; //用户手机号
    private String userGender; //用户性别
    private LocalDateTime userLastActive; //最后活跃时间
    private LocalDateTime userCreatedAt; //创建时间
    private String userStatus;
}