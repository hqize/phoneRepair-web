package com.hnust.controller;

import com.hnust.pojo.Parts;
import com.hnust.service.PartsService;
import com.hnust.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/parts")
public class PartsController {
    @Autowired
    private PartsService partsService;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            //使用RequestParam注解,接收前端传递的参数
            @RequestParam(value = "userId") Integer userId,
            //required = false 前端可以不是必须传递，后端可以设置默认值
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword,
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "sortField", required = false) String sortField,
            @RequestParam(value = "sortOrder", required = false) String sortOrder
    ) {
        Map<String, Object> data = partsService.list(userId, searchKeyword, pageNum, pageSize, sortField, sortOrder);
        return Result.success(data);
    }

    //增加配件
    @PostMapping("/addPart")
    public Result<String> addPart(@RequestBody Parts parts) {
        return partsService.addPart(parts);
    }

    //修改配件
    @PostMapping("/updatePart")
    public Result<String> updatePart(@RequestBody Parts parts) {
        return partsService.updatePart(parts);
    }

    //删除配件
    @DeleteMapping("/delete/{partId}")
    public Result<?> delete(@PathVariable Integer partId) {
        int result = partsService.deletePartsByPartId(partId);
        if (result > 0) {
            return Result.success("删除成功");
        } else {
            return Result.fail("删除失败，配件不存在",404);
        }
    }
}