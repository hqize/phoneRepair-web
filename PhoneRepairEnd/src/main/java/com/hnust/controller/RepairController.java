package com.hnust.controller;

import com.hnust.dto.PageQuery;
import com.hnust.pojo.ReceptionistVO;
import com.hnust.pojo.Repair;
import com.hnust.service.RepairService;
import com.hnust.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/repair")
public class RepairController {

    @Autowired
    private RepairService repairService;

    //获取所有前台接待记录
    @GetMapping("/getAllRepair") // 与前端请求路径完全一致
    //@ModelAttribute将请求数据绑定到模型对象
    public Result<Map<String, Object>> getAllRepair(@ModelAttribute PageQuery PageQuery) {
        return repairService.getRepairListByCondition(PageQuery);
    }

    @GetMapping("/getAllReceptionist")
    public Result<List<ReceptionistVO>> getAllReceptionist() {
        // 1. 调用Service获取接待人员列表
        List<ReceptionistVO> receptionists = repairService.getAllReceptionist();
        // 2. 返回成功响应（code=200，data=接待人员列表）
        return Result.success(receptionists);
    }

    @PostMapping("/createRepair")
    public Result<Object> createRepair(@RequestBody Repair repair) {
        // 调用Service层创建订单
        boolean isCreated = repairService.createRepair(repair);
        if (isCreated) {
            return Result.success("订单创建成功");
        } else {
            return Result.fail("订单创建失败", 500);
        }
    }

    @PostMapping("/deleteRepair")
    public Result<Object> deleteRepair(
            @RequestParam("repairId") Integer repairId,
            @RequestParam("userId") Integer userId,
            @RequestParam("password") String password) {

        // 调用Service删除
        boolean isDeleted = repairService.deleteRepair(repairId, userId, password);
        if (isDeleted) {
            return Result.success(null); // 成功响应（code=200）
        } else {
            return Result.fail("订单不存在或无删除权限", 403); // 无权限/无数据（code=403）
        }
    }
}