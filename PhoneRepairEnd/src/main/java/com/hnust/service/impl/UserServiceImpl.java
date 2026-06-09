package com.hnust.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hnust.dto.LoginUser;
import com.hnust.mapper.UserMapper;
import com.hnust.pojo.User;
import com.hnust.service.UserService;
import com.hnust.util.Md5Password;
import com.hnust.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service // 标记为服务层组件
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public LoginUser login(String usernameOrEmail, String password) {
        // 1. 根据账号或邮箱查询用户（MyBatis-Plus的条件构造器）\
        //创建一个 QueryWrapper 对象，用于构建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_name", usernameOrEmail) // 匹配用户名
                .or() // 或
                .eq("user_email", usernameOrEmail); // 匹配邮箱
        //最终执行查询，返回符合条件的第一个用户对象
        User user = this.getOne(queryWrapper); // 执行查询

        // 2. 校验用户是否存在
        if (user == null) {
            return null; // 用户名/邮箱不存在
        }

        // 3. 校验密码（MD5加密后比对，避免明文存储密码）
        String encryptedPassword = Md5Password.generateMD5(password);
        if (!encryptedPassword.equals(user.getUserPasswordHash())) {
            return null; // 密码错误
        }

        // 4. 封装登录用户信息（返回给前端，隐藏敏感字段如密码）
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getUserId());
        loginUser.setUserName(user.getUserName());
        loginUser.setUserEmail(user.getUserEmail());
        loginUser.setUserBio(user.getUserBio());//用户备注
        loginUser.setRoleId(user.getRoleId()); // 角色ID（用于后续权限控制）
        loginUser.setUserPhone(user.getUserPhone());

        return loginUser; // 登录成功，返回用户信息
    }


    @Override
    public Result<String> register(String userName, String userEmail, String userPasswordHash) {
        //判断用户名是否已存在
        User user = this.getOne(new
                QueryWrapper<User>().eq("user_name", userName));

        if (user != null) {
            return Result.fail("用户名已存在", 400);
        }
        //判断邮箱是否已存在
        user = this.getOne(new QueryWrapper<User>
                ().eq("user_email", userEmail));
        if (user != null) {
            return Result.fail("邮箱已存在", 400);
        }
        User newUser = new User();
        newUser.setUserName(userName);
        newUser.setUserEmail(userEmail);

        newUser.setUserPasswordHash(Md5Password.generateMD5(userPasswordHash));
        newUser.setRoleId(2);
        boolean save = this.save(newUser);
        return save ? Result.success("注册成功") :
                Result.fail("注册失败", 500);
    }

    //查询所有用户
    //分页查询
    @Override
    public Map<String, Object> getAllUsers(Integer userId, String searchKeyword, Integer pageNum, Integer pageSize, String sortField, String sortOrder) {
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
        Page<User> page = new Page<>(pageNum, pageSize);
        IPage<User> userIPage = userMapper.selectAllUser(page, userId, searchKeyword, sortField, sortOrder);

        // 3. 封装响应格式（与你之前的 getAllRepair 一致，前端可复用逻辑）
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("userList", userIPage.getRecords());  // 维修管理列表
        responseMap.put("count", userIPage.getTotal());                  // 总条数（用于分页组件）
        return responseMap;
    }

    //删除用户
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public int deleteUserByUserId(Integer userId) {
        if (userId == null) {
            Result.fail("用户Id不能为空", 404);
        }
        User user = userMapper.getById(userId);
        if (user == null) {
            Result.fail("用户记录不存在", 404);
        }
        int rows = userMapper.deleteUserByUserId(userId);
        if (rows <= 0) {
            Result.fail("删除失败", 404);
        }
        return rows;
    }

    //更新用户
    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public Result<String> updateUser(User user) {
        // 参数校验，确保userId存在
        if (user.getUserId() == null) {
            return Result.fail("用户ID不能为空",404);
        }
        // 执行更新操作，MyBatis - Plus的updateById方法会根据主键更新实体类中非null的字段
        int result = userMapper.updateUser(user);
        if (result > 0) {
            return Result.success("用户更新成功");
        } else {
            return Result.fail("用户更新失败，可能用户不存在",404);
        }
    }
}