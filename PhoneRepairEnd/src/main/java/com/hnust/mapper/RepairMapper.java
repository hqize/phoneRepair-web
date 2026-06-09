package com.hnust.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnust.pojo.ReceptionistVO;
import com.hnust.pojo.Repair;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface RepairMapper extends BaseMapper<Repair> {

    IPage<Repair> selectRepairByCondition(
            Page<Repair> page,
            @Param("userId") Integer userId,
            @Param("searchKeyword") String searchKeyword,
            @Param("sortField") String sortField,
            @Param("sortOrder") String sortOrder
    );

    List<ReceptionistVO> getAllReceptionist();

    int insert(Repair repair);

    int deleteRepairByIdAndUserId(@Param("repairId") Integer repairId, @Param("userId") Integer userId);
}
