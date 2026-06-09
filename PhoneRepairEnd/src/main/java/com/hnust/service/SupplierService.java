package com.hnust.service;

import com.hnust.pojo.Supplier;

import java.util.Map;

public interface SupplierService {

    /**
     * 分页查询所有供应商管理记录
     * @param searchKeyword 搜索关键词
     * @param pageNum 当前页码
     * @param pageSize 每页记录数
     * @param sortField 排序字段
     * @param sortOrder 排序方向
     * @return 分页结果，包含总记录数和数据列表
     */
    Map<String, Object> getAllSupplierManagement(String searchKeyword, Integer pageNum, Integer pageSize, String sortField, String sortOrder);

    /**
     * 创建新的供应商管理记录
     * @param supplier 供应商记录数据
     * @return 是否成功
     */
    boolean createSupplierManagement(Supplier supplier);

    /**
     * 更新供应商管理记录
     * @param supplier 供应商记录数据
     * @return 是否成功
     */
    boolean updateSupplierManagement(Supplier supplier);

    /**
     * 删除供应商管理记录
     * @param supplierManagementId 记录ID
     * @param userId 操作用户ID
     * @param userPasswd 操作用户密码
     * @return 是否成功
     */
    boolean deleteSupplierManagement(Integer supplierManagementId, Integer userId, String userPasswd);
}