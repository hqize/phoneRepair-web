package com.hnust.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.hnust.dto.ManagementCreateDTO;
import com.hnust.dto.ManagementUpdateDTO;
import com.hnust.mapper.ManagementMapper;
import com.hnust.mapper.UserMapper;
import com.hnust.pojo.Management;
import com.hnust.pojo.User;
import com.hnust.service.ManagementService;
import com.hnust.util.Md5Password;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ManagementServiceImpl extends ServiceImpl<ManagementMapper, Management> implements ManagementService {
    @Autowired
    private ManagementMapper managementMapper;


    /**
     * 获取维修管理列表（支持分页、搜索、排序、权限过滤）
     *
     * @param userId        当前登录用户ID
     * @param searchKeyword 搜索关键词
     * @param pageNum       页码
     * @param pageSize      每页条数
     * @param sortField     排序字段
     * @param sortOrder     排序方向
     * @return 响应结果（包含列表和总条数，适配前端格式）
     */
    public Map<String, Object> getAllRepairManagement(
            Integer userId,
            String searchKeyword,
            Integer pageNum,
            Integer pageSize,
            String sortField,
            String sortOrder) {
        // 1. 参数校验与默认值设置（避免null异常）
        pageNum = (pageNum == null || pageNum < 1) ? 1 : pageNum;//页码
        pageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize;//每页条数
        searchKeyword = (searchKeyword == null) ? "" : searchKeyword.trim();//搜索关键词
        // 默认排序：按创建时间降序
        if (sortField == null || sortField.trim().isEmpty()) {
            sortField = "createdAt";
        }
        if (sortOrder == null || (!"asc".equalsIgnoreCase(sortOrder) && !"desc".equalsIgnoreCase(sortOrder))) {
            sortOrder = "desc";
        }

        // 2. 分页查询
        Page<Management> page = new Page<>(pageNum, pageSize);

        IPage<Management> managementPage = managementMapper.selectAllRepairManagement(
                page, userId, searchKeyword, sortField, sortOrder
        );
        //new一个map集合
        Map<String, Object> map = new HashMap<>();
        map.put("repairManagementList", managementPage.getRecords()); //获取全部数据
        map.put("count", managementPage.getTotal()); //获取总记录数
        return map;
    }

    //增加维修管理记录
    //增加维修管理记录
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public boolean createRepairManagement(ManagementCreateDTO createDTO) {
        // 1. 校验：前端传递的 repairRequestId（订单ID）是否在维修单表中存在
        Integer requestId = createDTO.getRepairRequestId();
        // 调用RepairRequestMapper查询该订单ID的数量（1=存在，0=不存在）
        int orderCount = managementMapper.countByRequestId(requestId);
        if (orderCount == 0) {
            return false;  // 订单ID不存在，新增失败
        }

        // 2. DTO转实体：将前端参数映射到Management实体（补全默认值）
        Management management = new Management();
        management.setRepairRequestId(requestId);  // 订单ID（来自DTO）
        management.setRepairNotes(createDTO.getRepairNotes());  // 维修描述（来自DTO）
        management.setTechnicianId(createDTO.getTechnicianId());  // 维修人员ID（来自DTO，即当前用户）
        management.setRepairPrice(100.00);  // 默认价格（可根据业务调整，如从维修单表关联）
        management.setPaymentStatus("待支付");  // 默认支付状态（与前端表格展示选项一致）
        management.setCreatedAt(LocalDateTime.now());  // 创建时间（当前时间）
        management.setUpdatedAt(LocalDateTime.now());  // 更新时间（当前时间）

        // 3. 插入数据库（使用MyBatis-Plus的通用save方法，无需写SQL）
        return this.save(management);  // save成功返回true，失败返回false
    }

    @Autowired
    private UserMapper userMapper;

    @Override
    public String deleteRepairManagement(Integer repairId, Integer userId, String userPasswd) {
        // 1. 校验要删除的订单是否存在
        Management management = managementMapper.selectById(repairId);
        if (management == null) {
            return "要删除的维修订单不存在";  // 返回错误信息，Controller会返回给前端
        }

        // 2. 校验用户密码是否正确（二次验证，假设数据库密码是MD5加密存储）
        User user = userMapper.getById(userId);  // 根据userId查询用户信息
        if (user == null) {
            return "当前用户不存在";
        }
        // 密码比对：前端传的明文密码 → MD5加密后，与数据库存储的加密密码对比
        String encryptedPasswd = Md5Password.generateMD5(userPasswd);
        if (!encryptedPasswd.equals(user.getUserPasswordHash())) {  // 假设User类的密码字段是userPasswd
            return "密码错误，无法删除";
        }

        // 4. 所有校验通过，执行数据库删除（根据ID删除）
        int deleteCount = managementMapper.deleteById(repairId);
        return deleteCount > 0 ? "success" : "删除失败，请重试";  // 返回结果给Controller
    }

    // 1. 数据库payment_status的enum有效值列表（必须与数据库定义完全一致！）
// 例如：数据库enum定义为('待支付','已支付','退款中')，则列表需对应
    private static final List<String> VALID_PAYMENT_STATUS = Arrays.asList("待支付", "支付中", "支付完成", "支付异常");

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public String updateRepairManagement(ManagementUpdateDTO updateDTO) {
        // 2. 校验1：要更新的订单是否存在（根据repairId查询）
        Integer repairId = updateDTO.getRepairId();
        Management existingOrder = managementMapper.selectById(repairId);
        if (existingOrder == null) {
            return "要更新的订单不存在"; // 返回错误信息，Controller会返回给前端
        }

        // 3. 校验2：当前用户（technicianId）是否有权更新该订单
        Integer currentUserId = updateDTO.getTechnicianId();
        // 3.1 先校验当前用户是否存在
        User currentUser = userMapper.getById(currentUserId);
        if (currentUser == null) {
            return "当前用户不存在";
        }

        // 4. 校验3：支付状态是否为有效值（若前端传递了该字段）
        String paymentStatus = updateDTO.getPaymentStatus();
        if (StringUtils.hasText(paymentStatus)) {
            if (!VALID_PAYMENT_STATUS.contains(paymentStatus)) {
                return "支付状态无效，仅支持：" + String.join("、", VALID_PAYMENT_STATUS);
            }
        }

        // 5. 构建更新对象：仅更新前端传递的非空字段（避免覆盖原有值）
        Management orderToUpdate = new Management();
        orderToUpdate.setRepairId(repairId); // 订单ID（必传，定位要更新的订单）
        orderToUpdate.setTechnicianId(updateDTO.getTechnicianId());
        // 5.1 维修价格：前端传了非空值才更新
        if (updateDTO.getRepairPrice() != null) {
            orderToUpdate.setRepairPrice(updateDTO.getRepairPrice());
        }
        // 5.2 支付状态：前端传了非空值才更新
        if (StringUtils.hasText(paymentStatus)) {
            orderToUpdate.setPaymentStatus(paymentStatus);
        }
        // 5.3 维修备注：前端传了非空值才更新（Controller已处理纯空格问题）
        if (StringUtils.hasText(updateDTO.getRepairNotes())) {
            orderToUpdate.setRepairNotes(updateDTO.getRepairNotes());
        }
        // 6. 执行数据库更新（MyBatis-Plus通用方法，按ID更新非空字段）
        int updateCount = managementMapper.updateById(orderToUpdate);
        return updateCount > 0 ? "success" : "更新失败，请重试";
    }
}