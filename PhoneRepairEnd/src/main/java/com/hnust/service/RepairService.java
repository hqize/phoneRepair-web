package com.hnust.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnust.dto.PageQuery;
import com.hnust.pojo.ReceptionistVO;
import com.hnust.pojo.Repair;
import com.hnust.util.Result;

import java.util.List;
import java.util.Map;

public interface RepairService extends IService<Repair> {
    //根据分页条件查询所有接待记录
    Result<Map<String, Object>> getRepairListByCondition(PageQuery pageQuery);

    //
    List<ReceptionistVO> getAllReceptionist();

    boolean createRepair(Repair repair);

    boolean deleteRepair(Integer repairId, Integer userId, String password);
}