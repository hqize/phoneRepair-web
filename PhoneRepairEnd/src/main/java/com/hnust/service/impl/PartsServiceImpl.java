package com.hnust.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.hnust.mapper.PartsMapper;
import com.hnust.pojo.Parts;
import com.hnust.service.PartsService;
import com.hnust.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
@Service
public class PartsServiceImpl extends ServiceImpl<PartsMapper, Parts> implements PartsService {
    @Autowired
    private PartsMapper partsMapper;
    @Override
    public Map<String, Object> list(Integer userId, String searchKeyword, Integer pageNum, Integer pageSize, String sortField, String sortOrder) {
        // 1. 参数校验与默认值设置（避免null异常）
        pageNum = (pageNum == null || pageNum < 1) ? 1 : pageNum;
        pageSize = (pageSize == null || pageSize < 1) ? 10 : pageSize;
        searchKeyword = (searchKeyword == null) ? "" : searchKeyword.trim();
        // 默认排序：按创建时间降序
        if (sortField == null || sortField.trim().isEmpty()) {
            sortField = "createdAt";
        }
        if (sortOrder == null || (!"asc".equalsIgnoreCase(sortOrder) && !"desc".equalsIgnoreCase(sortOrder))) {
            sortOrder = "desc";
        }

        // 2. 分页查询
        Page<Parts> page = new Page<>(pageNum, pageSize);

        IPage<Parts> partsIPage = partsMapper.list(
                page, userId, searchKeyword, sortField, sortOrder
        );

        // 3. 封装响应格式（与你之前的 getAllRepair 一致，前端可复用逻辑）
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("pageResult", partsIPage.getRecords());  // 维修管理列表
        responseMap.put("count", partsIPage.getTotal());                  // 总条数（用于分页组件）
        return responseMap;
    }

    //创建配件
    @Override
    public Result<String> addPart(Parts parts) {
        //判断参数（名字，价格，数量，供应商id均不为空）
        if (parts.getPartName() == null || parts.getPartName().trim().isEmpty())
            return Result.fail("配件名称不能为空",404);
        if (parts.getPartPrice() == null || parts.getPartPrice() <= 0)
            return Result.fail("配件价格不能小于0",404);
        if (parts.getStockQuantity() == null || parts.getStockQuantity() <= 0)
            return Result.fail("配件数量不能小于0",404);
        if (parts.getSupplierId() == null || parts.getSupplierId() <= 0)
            return Result.fail("供应商id不能小于0",404);

        // 设置默认值
        parts.setPartDescription("测试");
        //插入数据库
        Integer result = partsMapper.addParts(parts);
        if (result != null && result > 0) {
            return Result.success("创建配件成功");
        } else {
            return Result.fail("创建配件失败",404);
        }
    }

    @Override
    public Result<String> updatePart(Parts parts) {
        // 参数校验，确保partId存在
        if (parts.getPartId() == null) {
            return Result.fail("配件ID不能为空",404);
        }
        // 执行更新操作，MyBatis - Plus的updateById方法会根据主键更新实体类中非null的字段
        int result = partsMapper.updateParts(parts);
        if (result >0) {
            return Result.success("配件更新成功");
        } else {
            return Result.fail("配件更新失败，可能配件不存在",404);
        }
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int deletePartsByPartId(Integer partId) {
        if(partId == null) {
            Result.fail("配件id不能为空",404);
        }
        Parts parts = partsMapper.queryPartsByPartId(partId);
        if(parts == null) {
            Result.fail("配件记录不存在",404);
        }
        int rows = partsMapper.deletePartsByPartId(partId);
        if(rows <= 0) {
            Result.fail("删除失败",404);
        }
        return rows;
    }
}
