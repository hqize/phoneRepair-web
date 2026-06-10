<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useTable } from '@/composables/useTable'
import * as repairApi from '@/api/repair'
import type { RepairRecord, Receptionist } from '@/types/repair'

const userStore = useUserStore()
const userIdFn = computed(() => userStore.userId)

const { tableData, loading, count, pageNum, pageSize, searchKeyword, fetchTableData, handleSearch, handleSortChange, handlePageChange, handleSizeChange } =
  useTable<RepairRecord>(repairApi.getRepairList, 'repairRequest', userIdFn)

const receptionists = ref<Receptionist[]>([])
const showAddDialog = ref(false)
const newOrder = ref({ phoneModel: '', phoneIssueDescription: '', receptionistId: '' })

onMounted(() => {
  fetchTableData()
  fetchReceptionists()
})

async function fetchReceptionists() {
  try {
    const res = await repairApi.getReceptionists()
    receptionists.value = res.data.data
  } catch { /* */ }
}

function showDetail(row: RepairRecord) {
  ElMessageBox.alert(
    `<h3>订单详情：</h3>
    订单单号：${row.requestId} <br>
    顾客姓名：${row.userName} <br>
    手机型号：${row.phoneModel} <br>
    问题描述：${row.phoneIssueDescription} <br>
    维修进度：${row.requestStatus} <br>
    接待人员：${row.receptionistName} <br>
    创建时间：${row.createdAt}`,
    '订单详情',
    { dangerouslyUseHTMLString: true, confirmButtonText: '确定' },
  )
}

async function deleteOrder(row: RepairRecord) {
  try {
    const { value: password } = await ElMessageBox.prompt('请输入密码以确认删除', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'password',
    })
    if (!password) return
    if (password.length > 30 || /\s|=/.test(password)) {
      ElMessage.error('密码格式不正确！')
      return
    }
    await repairApi.deleteRepair(row.requestId, userStore.userId, password)
    ElMessage.success('订单删除成功！')
    fetchTableData()
  } catch { /* */ }
}

async function submitNewOrder() {
  if (!newOrder.value.phoneModel || !newOrder.value.phoneIssueDescription || !newOrder.value.receptionistId) {
    ElMessage.error('请填写完整信息！')
    return
  }
  try {
    await repairApi.createRepair({
      userId: userStore.userId,
      receptionistId: Number(newOrder.value.receptionistId),
      phoneModel: newOrder.value.phoneModel,
      phoneIssueDescription: newOrder.value.phoneIssueDescription,
    })
    ElMessage.success('订单创建成功！')
    showAddDialog.value = false
    fetchTableData()
  } catch { /* */ }
}
</script>

<template>
  <div>
    <div class="hengfu">
      <span>前台接待页面</span>
      <span class="hengfu-sub">心服务好每个顾客是我们的追求</span>
    </div>

    <div class="search-bar">
      <el-input v-model="searchKeyword" placeholder="搜索关键词" clearable size="mini" @keyup.enter="handleSearch" />
      <el-button class="mini-btn" size="mini" type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
      </el-button>
    </div>

    <el-table :data="tableData" border v-loading="loading" @sort-change="handleSortChange" class="data-table">
      <el-table-column prop="requestId" label="订单ID" min-width="90" sortable="custom" />
      <el-table-column prop="phoneModel" label="手机型号" min-width="140" />
      <el-table-column prop="phoneIssueDescription" label="问题描述" min-width="160" show-overflow-tooltip />
      <el-table-column prop="requestStatus" label="维修状态" min-width="110" />
      <el-table-column prop="receptionistName" label="接待人员" min-width="110" />
      <el-table-column prop="createdAt" label="创建时间" min-width="160" sortable="custom" />
      <el-table-column label="操作" min-width="180">
        <template #default="{ row }">
          <el-button size="mini" type="info" @click="showDetail(row)">详情</el-button>
          <el-button size="mini" type="danger" @click="deleteOrder(row)">删除</el-button>
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

    <el-tooltip content="添加订单" placement="top-start" effect="dark">
      <el-button class="fab-btn" type="primary" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon>
      </el-button>
    </el-tooltip>

    <el-dialog title="添加订单" v-model="showAddDialog" width="30%">
      <el-form :model="newOrder" label-width="80px">
        <el-form-item label="手机型号">
          <el-input v-model="newOrder.phoneModel" />
        </el-form-item>
        <el-form-item label="问题描述">
          <el-input v-model="newOrder.phoneIssueDescription" />
        </el-form-item>
        <el-form-item label="接待人员">
          <el-select v-model="newOrder.receptionistId" placeholder="请选择">
            <el-option v-for="r in receptionists" :key="r.userId" :label="r.userName" :value="r.userId" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取 消</el-button>
        <el-button type="primary" @click="submitNewOrder">确 定</el-button>
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
  float: right;
  width: 240px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-bottom: 5px;
}
.mini-btn { margin-left: 10px; }
.data-table { margin-top: 20px; border-radius: 8px; }
.fab-btn {
  position: fixed;
  bottom: 20px;
  right: 20px;
  padding: 8px;
  zoom: 1.5;
  border-radius: 50px;
}
</style>
