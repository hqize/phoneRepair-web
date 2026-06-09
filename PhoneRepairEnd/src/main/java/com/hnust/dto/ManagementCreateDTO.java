package com.hnust.dto;

import lombok.Data;


@Data  // 仅用lombok的@Data生成getter/setter，无其他校验注解
public class ManagementCreateDTO {
    // 1. 订单ID：对应前端传递的 repairRequestId（关联 yjx_repair_request 表的 request_id）
    private Integer repairRequestId;

    // 2. 维修描述：对应前端传递的 repairNotes
    private String repairNotes;

    // 3. 维修人员ID：对应前端传递的 technicianId（即当前登录用户ID）
    private Integer technicianId;
}