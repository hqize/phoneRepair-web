package com.hnust.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnust.dto.PageQuery;
import com.hnust.mapper.RepairMapper;
import com.hnust.pojo.ReceptionistVO;
import com.hnust.pojo.Repair;
import com.hnust.pojo.User;
import com.hnust.service.RepairService;
import com.hnust.service.UserService;
import com.hnust.util.Md5Password;
import com.hnust.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RepairServiceImpl extends ServiceImpl<RepairMapper, Repair> implements RepairService {
    @Autowired
    private RepairMapper repairMapper;

    @Override
    public Result<Map<String, Object>> getRepairListByCondition(PageQuery pageQuery) {
        // 1. 处理分页参数：避免null或负数，设置默认值（页码默认1，每页默认10条）
        int pageNum = pageQuery.getPageNum() == null || pageQuery.getPageNum() < 1
                ? 1 : pageQuery.getPageNum();
        int pageSize = pageQuery.getPageSize() == null || pageQuery.getPageSize() < 1
                ? 10 : pageQuery.getPageSize();
        // 注意：Page泛型用 维修单实体类（yjxRepairRequest）
        Page<Repair> page = new Page<>(pageNum, pageSize);

        // 2. 处理搜索关键词：避免null导致SQL语法错误，null时设为空字符串
        String searchKeyword = pageQuery.getSearchKeyword() == null
                ? "" : pageQuery.getSearchKeyword().trim();

        // 3. 处理排序参数：默认按“创建时间（createdAt）降序”，避免null
        String sortField = pageQuery.getSortField();
        String sortOrder = pageQuery.getSortOrder();
        // 若未传排序字段/方向，设置默认值（按创建时间降序）
        if (sortField == null || sortField.trim().isEmpty()) {
            sortField = "createdAt"; // 对应数据库字段 created_at（MP会自动驼峰转下划线）
        }
        if (sortOrder == null || (!"asc".equalsIgnoreCase(sortOrder) && !"desc".equalsIgnoreCase(sortOrder))) {
            sortOrder = "desc"; // 默认降序
        }
        // 4. 调用Mapper层查询：传递分页参数 + 权限条件 + 搜索/排序条件
        // 核心权限逻辑：roleId=1或3查全部，其他查自己（通过Mapper的SQL实现过滤）
        IPage<Repair> Page = repairMapper.selectRepairByCondition(
                page,                          // 分页对象（控制页码、每页条数）
                pageQuery.getUserId(), // 当前登录用户ID（用于权限过滤）
                searchKeyword,                 // 搜索关键词（模糊查手机型号/问题描述）
                sortField,                     // 排序字段（如createdAt、requestId）
                sortOrder                      // 排序方向（asc/desc）
        );

        // 5. 封装响应数据：与前端期望的结构对齐（repairRequest列表 + count总条数）
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("repairRequest", Page.getRecords()); // 维修单列表数据
        responseMap.put("count", Page.getTotal());           // 总记录数（用于分页组件）
        // 6. 返回成功响应（用自定义Result工具类，含code、msg、data）
        return Result.success(responseMap);
    }

    @Override
    public List<ReceptionistVO> getAllReceptionist() {
        return repairMapper.getAllReceptionist();
    }

    @Override
    public boolean createRepair(Repair repair) {
        // 设置订单默认状态（例如：待处理）
        repair.setRequestStatus(1);

        // 设置创建和更新时间
        LocalDateTime now = LocalDateTime.now();
        repair.setCreatedAt(now);
        repair.setUpdatedAt(now);

        // 插入数据库
        int rows = repairMapper.insert(repair);
        return rows > 0;

    }

    //自动注入userService层对象
    @Autowired
    private UserService userService;//自动注入userService层对象
    @Override
    public boolean deleteRepair(Integer repairId, Integer userId, String password) {
        // 1. 校验参数：避免null导致异常
        if (repairId == null || userId == null || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("删除参数不能为空");
        }

        // 2. 验证密码正确性：从数据库查询用户真实密码，与前端传递的密码比对
        User user = userService.getById(userId); // User是用户实体类，password
        if (user == null) {
            throw new RuntimeException("当前用户不存在");
        }

        if (!Md5Password.generateMD5(password).equals(user.getUserPasswordHash())) { // 实际项目需替换为加密比对逻辑
            throw new RuntimeException("密码错误，删除失败");
        }

        // 3. 调用Mapper删除：返回影响行数（1=成功，0=无数据/无权限）
        int affectedRows = baseMapper.deleteRepairByIdAndUserId(repairId, userId);
        System.out.println("----------------affectedRows: " + affectedRows);
        return affectedRows > 0; // 影响行数>0表示删除成功
    }

}