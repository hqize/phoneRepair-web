package com.hnust.controller;

import com.hnust.dto.ManagementCreateDTO;
import com.hnust.dto.ManagementUpdateDTO;
import com.hnust.service.ManagementService;
import com.hnust.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/management")
public class ManagementController {

    @Autowired
    private ManagementService managementService;

    @GetMapping("/getAllRepairManagement")
    public Result<Map<String, Object>> getAllRepairManagement(
            // 前端传递的参数（与你之前的 getAllRepair 接口参数一致）
            @RequestParam("userId") Integer userId,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "sortField", required = false) String sortField,
            @RequestParam(value = "sortOrder", required = false) String sortOrder) {
        Map<String, Object> data = managementService.getAllRepairManagement(userId, searchKeyword, pageNum, pageSize, sortField, sortOrder);
        return Result.success(data);  // 响应格式：{code:200, msg:"success", data:{...}}

    }

    @PostMapping("/createRepairManagement")  // 请求方法、路径与前端对齐
    public Result<Object> createRepairManagement(@RequestBody ManagementCreateDTO createDTO) {
        // 1. 手动参数非空校验（基础版DTO无注解，需手动判断）
        if (createDTO.getRepairRequestId() == null) {
            return Result.fail("订单ID不能为空", 400);  // 400=参数错误
        }
        if (createDTO.getTechnicianId() == null || createDTO.getTechnicianId() != 1 && createDTO.getTechnicianId() != 4) {
            return Result.fail("维修人员ID不能为空且只能为1或4", 400);
        }

        // 2. 调用Service处理业务逻辑
        boolean isSuccess = managementService.createRepairManagement(createDTO);
        if (isSuccess) {
            return Result.success("维修记录创建成功");  // 成功响应（code=200，适配前端判断）
        } else {
            return Result.fail("新增失败：关联的订单ID不存在", 400);  // 业务错误（如订单ID无效）
        }
    }

    /**
     * 删除维修管理订单
     * 前端请求路径：http://127.0.0.1:8081/repairManagement/deleteRepairManagement
     * 前端参数：通过 params 传递 repairManagementId、userId、userPasswd
     */
    @PostMapping("/deleteRepairManagement")  // 与前端请求路径后缀一致
    public Result<Object> deleteRepairManagement(
            // 接收前端 params 参数，required = true 表示必填
            @RequestParam(value = "repairId", required = true) Integer repairId,
            @RequestParam(value = "userId", required = true) Integer userId,
            @RequestParam(value = "userPassword", required = true) String userPasswd) {

        System.out.println("删除维修管理订单："+repairId);
        System.out.println("----------------repairId: " + repairId);
        System.out.println("----------------userId: " + userId);
        System.out.println("----------------userPasswd: " + userPasswd);

        // 1. 基础参数校验（避免空值）
        if (repairId == null || repairId <= 0) {
            return Result.fail("维修订单ID无效", 400);  // 400=参数错误
        }
        if (userId == null || userId==0) {
            return Result.fail("用户ID不能为空", 400);
        }
        if (userPasswd == null || userPasswd.trim().isEmpty()) {
            return Result.fail("请输入密码进行二次验证", 400);
        }

        // 2. 调用Service层执行删除逻辑

        // 调用Service，返回删除结果（true=成功，false=失败/无权限/订单不存在）
        String resultMsg = managementService.deleteRepairManagement(repairId, userId, userPasswd);
        // 若返回"success"，表示删除成功；否则返回具体错误信息
        if ("success".equals(resultMsg)) {
            return Result.success( "订单删除成功");  // 适配前端 code=200 判断
        } else {
            return Result.fail(resultMsg, 400);  // 返回业务错误（如“无权限删除”）
        }

    }

    @PostMapping("/updateRepairManagement") // 与前端请求路径后缀一致
    public Result<Object> updateRepairManagement(
            // 用@RequestBody接收前端JSON请求体（对应this.currentOrder）
            @RequestBody ManagementUpdateDTO updateDTO) {

        // 1. 手动基础参数校验（核心字段非空）
        // 校验订单ID：不能为空且必须为正数
        if (updateDTO.getRepairId() == null || updateDTO.getRepairId() <= 0) {
            return Result.fail("订单ID不能为空且必须为正数", 400); // 400=参数错误
        }
        // 校验维修人员ID：维修人员ID不能为空且只能为1或4
        if (updateDTO.getTechnicianId() == null  || updateDTO.getTechnicianId() != 1 && updateDTO.getTechnicianId() != 4) {
            return Result.fail("维修人员ID不能为空且只能为1或4", 400);
        }
        // 校验维修价格：若传值，必须为正数（可选字段，不传则不校验）
        if (updateDTO.getRepairPrice() != null && updateDTO.getRepairPrice() <= 0) {
            return Result.fail("维修价格必须为正数", 400);
        }
        // 校验维修备注：若传值，不能是纯空格（可选字段，不传则不校验）
        if (StringUtils.hasText(updateDTO.getRepairNotes())) {
            String trimNotes = updateDTO.getRepairNotes().trim();
            if (trimNotes.isEmpty()) {
                return Result.fail("维修备注不能是纯空格", 400);
            }
            // 修复备注：去除前后空格后重新赋值（避免存无效空格）
            updateDTO.setRepairNotes(trimNotes);
        }

        // 2. 调用Service层执行更新逻辑
        String resultMsg = managementService.updateRepairManagement(updateDTO);
        // 根据Service返回结果，返回对应响应
        if ("success".equals(resultMsg)) {
            return Result.success("订单修改成功"); // 适配前端 code=200 判断
        } else {
            return Result.fail(resultMsg, 400); // 业务错误（如订单不存在、无权限）
        }
    }
}