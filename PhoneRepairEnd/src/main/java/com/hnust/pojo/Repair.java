package com.hnust.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@TableName(value = "yjx_repair_request")  // 指定数据库表名
@Builder  // 构造器
@AllArgsConstructor  // 全参构造函数
@NoArgsConstructor  // 无参构造函数
public class Repair {
    @TableId// 指定主键
    private Integer requestId; //维修单号
    private Integer userId; //提交订单的用户编号
    private Integer receptionistId; //接待人员的编号
    private String phoneModel; //手机型号
    private String phoneIssueDescription; //问题描述
    private Integer requestStatus; //维修状态
    private LocalDateTime createdAt; //订单创建时间
    private LocalDateTime updatedAt; //订单更新时间
// 以下字段为非数据库字段
@TableField(exist = false)  // 关键：告诉MyBatis-Plus该字段不存在于数据库表中
    private String receptionistName; //前台接待姓名
}