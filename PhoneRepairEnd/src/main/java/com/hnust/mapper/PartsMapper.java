package com.hnust.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnust.pojo.Parts;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

public interface PartsMapper extends BaseMapper<Parts> {
    //查询所有配件记录
    /**
     * @param page 分页对象（页码、每页条数）
     * @param userId 当前登录用户ID（用于权限过滤：如管理员查全部，普通用户查自己）
     * @param searchKeyword 搜索关键词（模糊匹配手机型号、维修描述、用户名）
     * @param sortField 排序字段（如 createdAt、repairId）
     * @param sortOrder 排序方向（asc/desc）
     * @return 分页结果（包含 Management 列表和总条数）
     */
    @Select("""
    SELECT 
        ya.*
    FROM yjx_parts ya
    WHERE 1=1
    -- 权限过滤：与你之前的逻辑一致（userId=1/5/8查全部，其他查自己关联的）
    AND ( #{userId} IN (1,5,8) )
    -- 搜索关键词：模糊匹配手机型号、维修描述、用户名（前端可能按这些字段搜索）
    AND ( 
        ya.part_name LIKE CONCAT('%', #{searchKeyword}, '%') 
        OR ya.part_description LIKE CONCAT('%', #{searchKeyword}, '%') 
    )
    -- 排序：与你之前的逻辑一致（支持按创建时间、维修ID排序）
    ORDER BY 
        CASE WHEN #{sortField} IS NOT NULL AND #{sortOrder} IS NOT NULL 
             THEN CASE #{sortField} 
                  WHEN 'createdAt' THEN ya.created_at 
                  WHEN 'part_id' THEN ya.part_id 
                  ELSE ya.created_at END 
        ELSE ya.created_at END 
        ${sortOrder == 'desc' ? 'DESC' : 'ASC'}
""")
    IPage<Parts> list(Page<Parts> page, Integer userId, String searchKeyword, String sortField, String sortOrder);

    //添加组件
    @Insert("INSERT INTO yjx_parts(part_name, part_description, part_price, stock_quantity, supplier_id  ) " +
            "VALUES (#{partName}, #{partDescription}, #{partPrice}, #{stockQuantity}, #{supplierId}) ")
    Integer addParts(Parts parts);

    // 更新配件
    @Update("UPDATE yjx_parts " +
            "SET part_name = #{partName}, " +
            "part_description = #{partDescription}, " +
            "part_price = #{partPrice}, " +
            "stock_quantity = #{stockQuantity}, " +
            "supplier_id = #{supplierId} " +
            "WHERE part_id = #{partId}")
    int updateParts(Parts parts);

    //根据配件id查询配件记录
    @Select("select * from yjx_parts where part_id = #{partId}")
    Parts queryPartsByPartId(Integer partId);

    //删除配件
    @Delete("delete from yjx_parts where part_id =#{partId}")
    int deletePartsByPartId(Integer partId);
}
