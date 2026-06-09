# KUAI 手机维修管理系统

一个基于 Spring Boot + Vue.js + Element UI 的手机维修店业务管理系统，涵盖前台接待、维修管理、配件管理、用户管理和供应商管理等核心业务模块。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| 后端框架 | Spring Boot | 3.5.5 |
| ORM 框架 | MyBatis-Plus | 3.5.5 |
| 数据库 | MySQL | 8.0+ |
| 分页插件 | PageHelper | 4.1.3 |
| 前端框架 | Vue.js | 2.x |
| UI 组件库 | Element UI | - |
| HTTP 客户端 | Axios | - |
| Java 版本 | JDK | 17 |
| 构建工具 | Maven | - |

## 项目结构

```
phoneRepair/
├── PhoneRepairEnd/                 # 后端 Spring Boot 项目
│   ├── pom.xml                     # Maven 配置
│   └── src/main/java/com/hnust/
│       ├── PhoneRepairEndApplication.java  # 启动入口
│       ├── config/                 # 配置类
│       │   ├── CorsConfig.java     # 跨域配置
│       │   └── MyBatisConfig.java  # MyBatis-Plus 分页插件配置
│       ├── controller/             # 控制器层（REST API）
│       │   ├── UserController.java       # 用户管理接口
│       │   ├── RepairController.java     # 前台接待接口
│       │   ├── ManagementController.java # 维修管理接口
│       │   ├── PartsController.java      # 配件管理接口
│       │   ├── SupplierController.java   # 供应商管理接口
│       │   └── RoleController.java       # 角色查询接口
│       ├── service/                # 服务层接口
│       │   ├── UserService.java
│       │   ├── RepairService.java
│       │   ├── ManagementService.java
│       │   ├── PartsService.java
│       │   ├── SupplierService.java
│       │   └── RoleService.java
│       ├── service/impl/           # 服务层实现
│       │   ├── UserServiceImpl.java
│       │   ├── RepairServiceImpl.java
│       │   ├── ManagementServiceImpl.java
│       │   ├── PartsServiceImpl.java
│       │   ├── SupplierServiceImpl.java
│       │   └── RoleServiceImpl.java
│       ├── mapper/                 # 数据访问层（MyBatis Mapper）
│       │   ├── UserMapper.java
│       │   ├── RepairMapper.java
│       │   ├── ManagementMapper.java
│       │   ├── PartsMapper.java
│       │   ├── SupplierMapper.java
│       │   └── RoleMapper.java
│       ├── pojo/                   # 实体类（对应数据库表）
│       │   ├── User.java           # yjx_user
│       │   ├── Repair.java         # yjx_repair_request
│       │   ├── Management.java     # yjx_repair_management
│       │   ├── Parts.java          # yjx_parts
│       │   ├── Supplier.java       # yjx_supplier_management
│       │   ├── Role.java           # yjx_role
│       │   └── ReceptionistVO.java # 接待人员VO
│       ├── dto/                    # 数据传输对象
│       │   ├── LoginUser.java      # 登录响应DTO
│       │   ├── RegisterUser.java   # 注册请求DTO
│       │   ├── PageQuery.java       # 分页查询参数DTO
│       │   ├── ManagementCreateDTO.java # 维修管理创建DTO
│       │   └── ManagementUpdateDTO.java # 维修管理更新DTO
│       ├── util/                   # 工具类
│       │   ├── Result.java         # 统一响应结果封装
│       │   └── Md5Password.java    # MD5 加盐加密
│       └── handler/
│           └── GlobalExceptionHandler.java # 全局异常处理
├── PhoneRepairFront/              # 前端页面（纯静态）
│   ├── index.html                 # 主页面（含导航栏、轮播图）
│   ├── page/                      # 子页面
│   │   ├── login.html             # 登录页
│   │   ├── register.html          # 注册页
│   │   ├── repair.html            # 前台接待页
│   │   ├── access.html            # 配件查询页
│   │   ├── user.html              # 账号管理页
│   │   └── supplier.html          # 供应商管理页
│   ├── js/                        # JavaScript 逻辑
│   │   ├── indexPage.js           # 主页/前台接待逻辑
│   │   ├── repairPage.js          # 维修管理逻辑
│   │   ├── access.js              # 配件查询逻辑
│   │   ├── user.js                # 用户管理逻辑
│   │   ├── supplier.js            # 供应商管理逻辑
│   │   ├── login.js               # 登录逻辑
│   │   ├── register.js            # 注册逻辑
│   │   └── common.js              # 公共变量（API 地址）
│   ├── css/                       # 样式文件
│   └── static/                    # 静态资源
│       ├── vue2/                  # Vue 2
│       ├── elementui/             # Element UI
│       ├── axios.min.js           # Axios
│       ├── js.cookie.min.js       # Cookie 工具
│       └── img/                   # 图片资源
```

## 数据库表设计

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `yjx_user` | 用户表 | user_id, user_name, user_email, user_password_hash, role_id |
| `yjx_role` | 角色表 | role_id, role_name, role_description |
| `yjx_repair_request` | 维修申请单 | request_id, user_id, receptionist_id, phone_model, request_status |
| `yjx_repair_management` | 维修管理记录 | repair_id, repair_request_id, technician_id, repair_price, payment_status |
| `yjx_parts` | 手机配件表 | part_id, part_name, part_price, stock_quantity, supplier_id |
| `yjx_supplier_management` | 供应商管理表 | supplier_management_id, supplier_id, part_id, supply_quantity |

## 角色与权限体系

| 角色ID | 角色名称 | 权限范围 |
|--------|----------|----------|
| 1 | 管理员 | 查看全部数据，管理所有模块 |
| 2 | 普通用户 | 仅查看/操作本人相关数据 |
| 3 | 前台接待 | 查看全部数据（接待相关） |
| 4 | 维修人员 | 维修管理操作权限 |
| 5 | 供应商 | 供应商管理关联 |
| 8 | 配件管理员 | 全部配件数据查询 |

## API 接口列表

基础路径：`http://localhost:8081/hnust`

### 用户模块 `/user`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| POST | `/user/login` | 用户登录 | 公开 |
| POST | `/user/createUser` | 用户注册 | 公开 |
| GET | `/user/getAllUsers` | 查询用户列表（分页） | 按角色 |
| POST | `/user/updateUser` | 修改用户信息 | 按角色 |
| DELETE | `/user/delete/{userId}` | 删除用户 | 按角色 |

### 前台接待 `/repair`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/repair/getAllRepair` | 获取维修单列表 | 按角色（1/3 查全部） |
| GET | `/repair/getAllReceptionist` | 获取接待人员列表 | - |
| POST | `/repair/createRepair` | 创建维修单 | - |
| POST | `/repair/deleteRepair` | 删除维修单 | 密码验证 + 权限 |

### 维修管理 `/management`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/management/getAllRepairManagement` | 获取维修管理列表 | 按角色（1/3 查全部） |
| POST | `/management/createRepairManagement` | 创建维修记录 | technicianId ∈ {1,4} |
| POST | `/management/updateRepairManagement` | 更新维修记录 | password 验证 |
| POST | `/management/deleteRepairManagement` | 删除维修记录 | password 二次验证 |

### 配件管理 `/parts`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/parts/list` | 配件列表（分页） | 按角色（1/5/8 查全部） |
| POST | `/parts/addPart` | 新增配件 | - |
| POST | `/parts/updatePart` | 修改配件 | - |
| DELETE | `/parts/delete/{partId}` | 删除配件 | - |

### 供应商管理 `/supplier`

| 方法 | 路径 | 说明 | 权限 |
|------|------|------|------|
| GET | `/supplier/getAllSupplierManagement` | 供应商管理列表 | - |
| POST | `/supplier/createSupplierManagement` | 新增供应商记录 | - |
| POST | `/supplier/updateSupplierManagement` | 修改供应商记录 | - |
| POST | `/supplier/deleteSupplierManagement` | 删除供应商记录 | password 验证 |

### 角色查询 `/role`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/role/list` | 查询所有角色 |

## 统一响应格式

```json
{
  "code": 200,
  "msg": "success",
  "data": { ... },
  "timestamp": 1700000000000
}
```

- 成功：`code = 200`，`msg = "success"`
- 参数错误：`code = 400`
- 未授权/无权限：`code = 403`
- 未找到：`code = 404`
- 服务器错误：`code = 500`

## 安全特性

- **密码加密**：使用 MD5 + 盐值混合加密存储（[Md5Password.java](PhoneRepairEnd/src/main/java/com/hnust/util/Md5Password.java)）
- **删除操作二次验证**：维修单删除、维修管理删除、供应商记录删除均需输入密码确认
- **角色权限控制**：通过 SQL WHERE 条件在 Mapper 层实现基于 roleId 的数据访问隔离
- **前后端分离**：后端端口 8081，前端端口 8080，通过 CORS 配置实现跨域通信

## 运行配置

### 后端配置（application.yml）

- 服务端口：`8081`
- 上下文路径：`/hnust`
- 数据库：`jdbc:mysql://localhost:3306/phone_repair`
- 数据库用户：`root` / `123456`
- MyBatis 日志级别：`com.hnust.mapper` → `trace`

### 启动步骤

1. 创建 MySQL 数据库 `phone_repair`，导入对应的表结构
2. 修改 [application.yml](PhoneRepairEnd/src/main/resources/application.yml) 中的数据库连接信息
3. 运行 [PhoneRepairEndApplication.java](PhoneRepairEnd/src/main/java/com/hnust/PhoneRepairEndApplication.java) 启动后端
4. 使用 Live Server 或任意 HTTP 服务器启动前端页面（默认端口 8080）
