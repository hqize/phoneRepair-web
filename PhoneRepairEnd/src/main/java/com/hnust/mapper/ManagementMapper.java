package com.hnust.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnust.pojo.Management;
import org.apache.ibatis.annotations.Param;

public interface ManagementMapper extends BaseMapper<Management> {

    IPage<Management> selectAllRepairManagement(
            Page<Management> page,
            @Param("userId") Integer userId,
            @Param("searchKeyword") String searchKeyword,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder
    );

    int countByRequestId(Integer requestId);

    int deleteById(Integer repairId);

    int updateById(Management management);
}