package com.hnust.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hnust.pojo.Parts;
import com.hnust.util.Result;

import java.util.Map;

public interface PartsService extends IService<Parts> {
    //查询配件
    Map<String, Object> list(Integer userId, String searchKeyword, Integer pageNum, Integer pageSize, String sortField, String sortOrder);

    //增加配件
    Result<String> addPart(Parts parts);

    //更新配件
    Result<String> updatePart(Parts parts);

    //删除配件
    int deletePartsByPartId(Integer partId);
}
