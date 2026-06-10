<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useTable } from '@/composables/useTable'
import * as userApi from '@/api/user'
import * as roleApi from '@/api/role'
import type { UserRecord } from '@/types/user'
import type { Role } from '@/types/role'

const userStore = useUserStore()
const userId = computed(() => userStore.userId)

const { tableData, loading, count, pageNum, pageSize, searchKeyword, fetchTableData, handleSearch, handleSortChange, handlePageChange, handleSizeChange } =
  useTable<UserRecord>(userApi.getAllUsers, 'userList', userId, 'sortPart')

const roles = ref<Role[]>([])
const showAddDialog = ref(false)
const showUpdateDialog = ref(false)
const newUser = ref({ userName: '', userPwd: '', userEmail: '' })
const currentUser = ref<Partial<UserRecord>>({})

onMounted(async () => {
  await fetchAllRoles()
  fetchTableData()
})

async function fetchAllRoles() {
  try {
    const res = await roleApi.listAllRoles()
    roles.value = res.data.data || []
  } catch { roles.value = [] }
}

function getRoleName(roleId: number) {
  return roles.value.find(r => r.roleId === roleId)?.roleName || '未知角色'
}

async function addUser() {
  if (!newUser.value.userName || !newUser.value.userEmail) {
    ElMessage.error('请填写完整信息！')
    return
  }
  try {
    await userApi.register({
      userName: newUser.value.userName,
      userEmail: newUser.value.userEmail,
      userPasswordHash: newUser.value.userPwd,
    })
    ElMessage.success('用户添加成功！')
    showAddDialog.value = false
    fetchTableData()
  } catch { /* */ }
}

function openUpdate(row: UserRecord) {
  currentUser.value = {
    userId: row.userId,
    userName: row.userName,
    userEmail: row.userEmail,
    roleId: row.roleId,
    userPhone: row.userPhone,
    userBio: row.userBio,
  }
  showUpdateDialog.value = true
}

async function submitUpdate() {
  if (!currentUser.value.userId) {
    ElMessage.error('用户 ID 缺失，无法提交修改！')
    return
  }
  try {
    await userApi.updateUser(currentUser.value)
    ElMessage.success('用户修改成功！')
    showUpdateDialog.value = false
    fetchTableData()
  } catch { /* */ }
}

async function deleteUser(row: UserRecord) {
  try {
    await ElMessageBox.confirm(`确认删除用户 ID ${row.userId} 吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await userApi.deleteUser(row.userId)
    ElMessage.success('订单删除成功！')
    fetchTableData()
  } catch { /* */ }
}

function showDetail(row: UserRecord) {
  ElMessageBox.alert(
    `<h3>用户详情：</h3>
    用户ID：${row.userId} <br>
    用户名称：${row.userName} <br>
    用户邮箱：${row.userEmail} <br>
    用户简介：${row.userBio} <br>
    用户角色：${row.roleId} <br>
    用户手机号：${row.userPhone} <br>
    创建时间：${row.userCreatedAt}`,
    '用户详情',
    { dangerouslyUseHTMLString: true, confirmButtonText: '确定' },
  )
}
</script>

<template>
  <div>
    <div class="hengfu">
      <span>用户账户管理</span>
      <span class="hengfu-sub">&emsp;用心服务好每一个顾客是我们的追求</span>
    </div>

    <div class="search-bar">
      <el-input v-model="searchKeyword" placeholder="搜索关键词" clearable size="mini" @keyup.enter="handleSearch" />
      <el-button class="mini-btn" size="mini" type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
      </el-button>
      <el-button class="mini-btn" size="mini" type="success" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon> 添加用户
      </el-button>
    </div>

    <el-table :data="tableData" border v-loading="loading" @sort-change="handleSortChange" class="data-table">
      <el-table-column prop="userId" label="用户ID" min-width="80" sortable="custom" />
      <el-table-column prop="userName" label="用户名称" min-width="120" />
      <el-table-column prop="userEmail" label="用户邮箱" min-width="160" show-overflow-tooltip />
      <el-table-column prop="userBio" label="用户简介" min-width="140" show-overflow-tooltip />
      <el-table-column label="用户角色" min-width="110">
        <template #default="{ row }">{{ getRoleName(row.roleId) }}</template>
      </el-table-column>
      <el-table-column prop="userPhone" label="用户手机号" min-width="130" />
      <el-table-column prop="userCreatedAt" label="创建时间" min-width="160" sortable="custom" />
      <el-table-column label="操作" min-width="220">
        <template #default="{ row }">
          <el-button size="mini" type="info" @click="showDetail(row)">详情</el-button>
          <el-button size="mini" type="primary" @click="openUpdate(row)">修改</el-button>
          <el-button size="mini" type="danger" @click="deleteUser(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pageNum" v-model:page-size="pageSize"
      :page-sizes="[10, 20, 30, 40]" :total="count"
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="handlePageChange" @size-change="handleSizeChange"
      style="margin-top: 16px"
    />

    <el-dialog title="添加用户" v-model="showAddDialog" width="400px">
      <el-form :model="newUser" label-width="100px">
        <el-form-item label="用户姓名" :rules="[{ required: true, message: '请输入用户姓名', trigger: 'blur' }]">
          <el-input v-model="newUser.userName" placeholder="请输入用户姓名" />
        </el-form-item>
        <el-form-item label="用户密码" :rules="[{ required: true, message: '请输入用户密码', trigger: 'blur' }]">
          <el-input type="password" v-model="newUser.userPwd" placeholder="请输入用户密码" />
        </el-form-item>
        <el-form-item label="用户邮箱" :rules="[{ required: true, message: '请输入用户邮箱', trigger: 'blur' }]">
          <el-input type="textarea" v-model="newUser.userEmail" placeholder="请输入用户邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addUser">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog title="修改用户" v-model="showUpdateDialog" width="400px">
      <el-form :model="currentUser" label-width="120px">
        <el-form-item label="用户名称">
          <el-input v-model="currentUser.userName" />
        </el-form-item>
        <el-form-item label="用户邮箱">
          <el-input type="textarea" v-model="currentUser.userEmail" />
        </el-form-item>
        <el-form-item label="用户角色">
          <el-select v-model="currentUser.roleId" placeholder="请选择角色">
            <el-option v-for="r in roles" :key="r.roleId" :label="r.roleName" :value="r.roleId" />
          </el-select>
        </el-form-item>
        <el-form-item label="用户手机号">
          <el-input v-model="currentUser.userPhone" />
        </el-form-item>
        <el-form-item label="用户简介">
          <el-input type="textarea" v-model="currentUser.userBio" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showUpdateDialog = false">取消</el-button>
        <el-button type="primary" @click="submitUpdate">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<style scoped>
.hengfu {
  background-color: #545c64;
  color: white;
  padding: 15px 20px;
  border-radius: 8px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.hengfu span:first-child { font-size: 20px; font-weight: bold; }
.hengfu-sub { font-size: 13px; font-weight: 500; color: rgb(164, 164, 164); }
.search-bar {
  display: flex;
  align-items: center;
  margin-bottom: 5px;
  gap: 0;
}
.search-bar .el-input { width: 200px; }
.mini-btn { margin-left: 10px; }
.data-table { margin-top: 20px; border-radius: 8px; }
</style>
