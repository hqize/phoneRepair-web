package com.hnust.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnust.pojo.Management;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface ManagementMapper extends BaseMapper<Management> {

    /**
     * 分页查询维修管理列表（关联维修单表+用户表，获取前端所需所有字段）
     *
     * @param page          分页对象（页码、每页条数）
     * @param userId        当前登录用户ID（用于权限过滤：如管理员查全部，普通用户查自己）
     * @param searchKeyword 搜索关键词（模糊匹配手机型号、维修描述、用户名）
     * @param sortField     排序字段（如 createdAt、repairId）
     * @param sortOrder     排序方向（asc/desc）
     * @return 分页结果（包含 Management 列表和总条数）
     */
    @Select("""
            SELECT 
                -- 1. 维修管理表（yjx_repair_management）字段
                rm.repair_id AS repairId,
                rm.repair_request_id AS repairRequestId,
                rm.technician_id AS technicianId,
                rm.repair_price AS repairPrice,
                rm.payment_status AS paymentStatus,
                rm.repair_notes AS repairNotes,
                rm.created_at AS createdAt,
                -- 2. 关联维修单表（yjx_repair_request）获取手机型号和维修状态
                rr.phone_model AS phoneModel,
                -- 维修状态转文字：根据实际枚举值调整（如 0=待维修，1=维修中，2=已完成）
                CASE rr.request_status 
                    WHEN 0 THEN '待维修' 
                    WHEN 1 THEN '维修中' 
                    WHEN 2 THEN '已完成' 
                    ELSE '未知状态' END AS statusName,
                -- 3. 关联用户表（yjx_user）获取订单用户名
                u.user_name AS userName
            FROM yjx_repair_management rm
            -- 第一次关联：维修管理表 → 维修单表（通过订单ID关联）
            LEFT JOIN yjx_repair_request rr 
                ON rm.repair_request_id = rr.request_id
            -- 第二次关联：维修单表 → 用户表（通过用户ID关联，获取用户名）
            LEFT JOIN yjx_user u 
                ON rr.user_id = u.user_id
            WHERE 1=1
            -- 权限过滤：与你之前的逻辑一致（userId=1/3查全部，其他查自己关联的）
            AND ( #{userId} IN (1,3) OR rm.technician_id = #{userId} )
            -- 搜索关键词：模糊匹配手机型号、维修描述、用户名（前端可能按这些字段搜索）
               AND (\s
                        rr.phone_model LIKE CONCAT('%', #{searchKeyword}, '%')\s
                        OR rm.repair_notes LIKE CONCAT('%', #{searchKeyword}, '%')\s
                        OR u.user_name LIKE CONCAT('%', #{searchKeyword}, '%')\s
                    )
                    -- 排序：与你之前的逻辑一致（支持按创建时间、维修ID排序）
                    ORDER BY\s
                        CASE WHEN #{sortField} IS NOT NULL AND #{sortOrder} IS NOT NULL\s
                             THEN CASE #{sortField}\s
                                  WHEN 'createdAt' THEN rm.created_at\s
                                  WHEN 'repairId' THEN rm.repair_id\s
                                  WHEN 'userName' THEN u.user_name\s
                                  ELSE rm.created_at END\s
                        ELSE rm.created_at END\s
                        ${sortOrder == 'desc' ? 'DESC' : 'ASC'}
                """)
    IPage<Management> selectAllRepairManagement(
            Page<Management> page,
            @Param("userId") Integer userId,
            @Param("searchKeyword") String searchKeyword,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder
    );

    @Select("SELECT COUNT(1) FROM yjx_repair_request WHERE request_id = #{requestId}")
    int countByRequestId(Integer requestId);

    // 手动为 deleteById 写 SQL
    @Delete("DELETE FROM yjx_repair_management WHERE repair_id = #{repairId}")
    int deleteById(Integer repairId);

    //更新维修记录(支付价格,状态,维修备注,更新时间)
    @Update("""
    UPDATE yjx_repair_management 
    SET 
        repair_price = #{repairPrice,jdbcType=DECIMAL},  -- 维修价格
        payment_status = #{paymentStatus,jdbcType=VARCHAR},  -- 支付状态
        repair_notes = #{repairNotes,jdbcType=VARCHAR},  -- 维修备注
        technician_id = #{technicianId,jdbcType=INTEGER},  -- 维修人员id
        updated_at = NOW()  -- 更新时间
    WHERE repair_id = #{repairId,jdbcType=INTEGER}  -- 按订单ID定位
""")
    int updateById(Management management);  // 参数为Management实体，获取要更新的字段值
}