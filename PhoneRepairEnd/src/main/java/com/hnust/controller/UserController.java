package com.hnust.controller;

import com.hnust.dto.LoginUser;
import com.hnust.dto.RegisterUser;
import com.hnust.pojo.User;
import com.hnust.service.UserService;
import com.hnust.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController // 标记为控制器，返回JSON数据
@RequestMapping("/user") // 接口前缀：/user
public class UserController {

    @Autowired // 自动注入UserService实例
    private UserService userService;

    /**
     * 登录接口：接收账号/邮箱和密码
     */
    @PostMapping("/login") // 处理POST请求：/user/login
    public Result<LoginUser> login(
            @RequestParam String usernameOrEmail, // 接收前端传入的账号/邮箱
            @RequestParam String password         // 接收前端传入的密码
    ) {
        // 调用Service层的登录逻辑
        LoginUser loginUser = userService.login(usernameOrEmail, password);
        // 根据结果返回响应
        if (loginUser == null) {
            return Result.fail("用户名或密码错误", 400); // 失败：状态码400
        }
        return Result.success(loginUser); // 成功：返回用户信息
    }

    /**
     * 用户注册接口
     * 通过接收注册用户信息，调用用户服务完成用户注册流程
     * @param registerUser 包含用户注册信息的数据传输对象，包含用户名、邮箱和密码哈希
     * @return 返回注册结果，成功时返回成功信息，失败时返回相应的错误信息
     */
    @PostMapping("/createUser")
    public Result<String> register(@RequestBody

                                   RegisterUser registerUser) {
        // 调用 Service 时，从 DTO 中获取参数
        return userService.register(
                registerUser.getUserName(),
                registerUser.getUserEmail(),
                registerUser.getUserPasswordHash()
        );

    }

    /**
     * 查询所有用户
     */
    @GetMapping("/getAllUsers")
    public Result<Map<String, Object>> getAllUser(
            @RequestParam(value = "userId", required = false) Integer userId,
            @RequestParam(value = "seachKeyword", required = false) String seachKeyword,
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestParam(value = "pageSize", required = false) Integer pageSize,
            @RequestParam(value = "sortField", required = false) String sortField,
            @RequestParam(value = "sortPart", required = false) String sortPart
    ) {
        Map<String, Object> data = userService.getAllUsers(userId, seachKeyword, pageNum, pageSize, sortField, sortPart);
        return Result.success(data);
    }

    //删除用户
    @DeleteMapping("/delete/{userId}")
    public Result<?> delete(@PathVariable Integer userId) {
        int result = userService.deleteUserByUserId(userId);
        if (result > 0) {
            return Result.success("删除成功");
        } else {
            return Result.fail("删除失败，配件不存在", 404);
        }
    }

    //修改用户
    @PostMapping("/updateUser")
    public Result<String> updateUser(@RequestBody User user) {
        return userService.updateUser(user);
    }
}
