package com.hnust.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //提供get、set方法
@AllArgsConstructor //有参构造器
@NoArgsConstructor //无参构造器
public class LoginUser {
    private Integer userId; //用户id
    private String userName; //用户名称
    private String userEmail; //用户邮箱
    private Integer roleId; //角色id
    private String userBio; //用户备注
    private String userPhone; //用户手机号
}