package com.hnust.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnust.dto.ManagementCreateDTO;
import com.hnust.dto.ManagementUpdateDTO;
import com.hnust.pojo.Management;

import java.util.Map;

public interface ManagementService extends IService<Management> {
    Map<String, Object> getAllRepairManagement(Integer userId, String searchKeyword, Integer pageNum, Integer pageSize, String sortField, String sortOrder);

    boolean createRepairManagement(ManagementCreateDTO createDTO);

    String deleteRepairManagement(Integer repairId, Integer userId, String userPasswd);

    String updateRepairManagement(ManagementUpdateDTO updateDTO);
}
