package com.hnust.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnust.mapper.SupplierMapper;
import com.hnust.mapper.UserMapper;
import com.hnust.pojo.Supplier;
import com.hnust.service.SupplierService;
import com.hnust.util.Md5Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierMapper supplierMapper;
    private final UserMapper userMapper;

    @Autowired
    public SupplierServiceImpl(SupplierMapper supplierMapper, UserMapper userMapper) {
        this.supplierMapper = supplierMapper;
        this.userMapper = userMapper;
    }

    /**
     * 分页查询供应商管理记录
     */
    @Override
    public Map<String, Object> getAllSupplierManagement(String searchKeyword, Integer pageNum, Integer pageSize, String sortField, String sortOrder) {
        Page<Supplier> page = new Page<>(pageNum, pageSize);
        IPage<Supplier> supplierIPage = supplierMapper.selectSupplierByCondition(
                page,
                searchKeyword,
                sortField,
                sortOrder
        );
        Map<String, Object> result = new HashMap<>();
        result.put("supplierManagementList", supplierIPage.getRecords());
        result.put("count", supplierIPage.getTotal());
        return result;
    }

    /**
     * 创建新的供应商管理记录
     * 核心改动：在插入前，显式地将 supplierManagementId 设为 null，
     * 以确保 MyBatis-Plus 和数据库的自增机制正常工作。
     */
    @Override
    public boolean createSupplierManagement(Supplier supplier) {
        // 核心修改：将 supplierManagementId 设为 null，让数据库自增
        supplier.setSupplierManagementId(null);
        // 设置创建和更新时间
        supplier.setCreatedAt(LocalDateTime.now());
        supplier.setUpdatedAt(LocalDateTime.now());
        // 插入数据库，MyBatis-Plus 会自动处理自增主键
        return supplierMapper.insert(supplier) > 0;
    }

    /**
     * 更新供应商管理记录
     */
    @Override
    public boolean updateSupplierManagement(Supplier supplier) {
        supplier.setUpdatedAt(LocalDateTime.now());
        return supplierMapper.updateById(supplier) > 0;
    }

    /**
     * 删除供应商管理记录，需进行密码校验
     */
    @Override
    public boolean deleteSupplierManagement(Integer supplierManagementId, Integer userId, String userPasswd) {
        String storedPassword = userMapper.selectPasswordById(userId);
        if (storedPassword == null) {
            return false;
        }
        String hashedUserPasswd = Md5Password.generateMD5(userPasswd);
        if (!storedPassword.equals(hashedUserPasswd)) {
            return false;
        }
        return supplierMapper.deleteByManagementId(supplierManagementId) > 0;
    }
}