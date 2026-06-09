package com.hnust.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hnust.pojo.Parts;
import org.apache.ibatis.annotations.Param;

public interface PartsMapper extends BaseMapper<Parts> {

    IPage<Parts> list(Page<Parts> page,
                      @Param("userId") Integer userId,
                      @Param("searchKeyword") String searchKeyword,
                      @Param("sortField") String sortField,
                      @Param("sortOrder") String sortOrder);

    Integer addParts(Parts parts);

    int updateParts(Parts parts);

    Parts queryPartsByPartId(Integer partId);

    int deletePartsByPartId(Integer partId);
}
