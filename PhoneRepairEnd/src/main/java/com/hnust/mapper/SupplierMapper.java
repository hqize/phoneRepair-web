package com.hnust.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnust.pojo.Supplier;
import org.apache.ibatis.annotations.Param;

public interface SupplierMapper extends BaseMapper<Supplier> {

    IPage<Supplier> selectSupplierByCondition(
            Page<Supplier> page,
            @Param("searchKeyword") String searchKeyword,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder
    );

    int deleteByManagementId(@Param("supplierManagementId") Integer supplierManagementId);

    int insert(Supplier supplier);

    int updateById(Supplier supplier);
}