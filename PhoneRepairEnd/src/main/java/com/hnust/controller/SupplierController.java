package com.hnust.controller;


import com.hnust.pojo.Supplier;
import com.hnust.service.SupplierService;
import com.hnust.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/supplier")
public class SupplierController {

    @Autowired
    private SupplierService supplierService;

    /**
     * GET /supplier/getAllSupplierManagement
     * 分页获取所有供应商管理记录
     */
    @GetMapping("/getAllSupplierManagement")
    public Result<Map<String, Object>> getAllSupplierManagement(
            @RequestParam(required = false) String searchKeyword,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(defaultValue = "created_at") String sortField,
            @RequestParam(defaultValue = "desc") String sortOrder) {

        // 参数合法性校验，排序字段限制
        if (!"supplier_management_id".equals(sortField) && !"supplier_id".equals(sortField) &&
                !"part_id".equals(sortField) && !"created_at".equals(sortField)) {
            return Result.fail("非法的排序字段", 400);
        }

        Map<String, Object> result = supplierService.getAllSupplierManagement(
                searchKeyword,
                pageNum,
                pageSize,
                sortField,
                sortOrder
        );
        // 使用你的 success 方法，并传入 data
        return Result.success(result);

    }

    /**
     * POST /supplier/createSupplierManagement
     * 新增供应商管理记录
     */
    @PostMapping("/createSupplierManagement")
    public Result<Void> createSupplierManagement(@RequestBody Supplier supplier) {
        if (supplierService.createSupplierManagement(supplier)) {
            return Result.success(null); // 新增成功，data 为 null
        } else {
            return Result.fail("新增失败，请检查数据", 400);
        }

    }

    /**
     * POST /supplier/updateSupplierManagement
     * 修改供应商管理记录
     */
    @PostMapping("/updateSupplierManagement")
    public Result<Void> updateSupplierManagement(@RequestBody Supplier supplier) {

        if (supplierService.updateSupplierManagement(supplier)) {
            return Result.success(null); // 修改成功，data 为 null
        } else {
            return Result.fail("修改失败，请检查数据", 400);
        }

    }

    /**
     * POST /supplier/deleteSupplierManagement
     * 删除供应商管理记录，需要密码校验
     */
    @PostMapping("/deleteSupplierManagement")
    public Result<Void> deleteSupplierManagement(@RequestParam Integer supplierManagementId,
                                                 @RequestParam Integer userId,
                                                 @RequestParam String userPasswd) {

        if (supplierService.deleteSupplierManagement(supplierManagementId, userId, userPasswd)) {
            return Result.success(null); // 删除成功，data 为 null
        } else {
            return Result.fail("删除失败，密码错误或记录不存在", 400);
        }

    }
}