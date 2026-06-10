<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useTable } from '@/composables/useTable'
import * as mgmtApi from '@/api/management'
import type { ManagementRecord, ManagementCreateParams, ManagementUpdateParams } from '@/types/management'

const userStore = useUserStore()
const userId = computed(() => userStore.userId)

const { tableData, loading, count, pageNum, pageSize, searchKeyword, fetchTableData, handleSearch, handleSortChange, handlePageChange, handleSizeChange } =
  useTable<ManagementRecord>(mgmtApi.getAllRepairManagement, 'repairManagementList', userId)

const showAddDialog = ref(false)
const showUpdateDialog = ref(false)
const newOrder = ref({ repairRequestId: '', repairNotes: '' })
const currentOrder = ref<ManagementUpdateParams>({ repairId: 0, technicianId: 0, repairPrice: 0, paymentStatus: '', repairNotes: '' })

onMounted(() => fetchTableData())

async function createOrder() {
  if (!newOrder.value.repairRequestId || !newOrder.value.repairNotes) {
    ElMessage.error('请填写完整信息！')
    return
  }
  try {
    const body: ManagementCreateParams = {
      repairRequestId: Number(newOrder.value.repairRequestId),
      repairNotes: newOrder.value.repairNotes,
      technicianId: userStore.userId,
    }
    await mgmtApi.createRepairManagement(body)
    ElMessage.success('订单添加成功！')
    showAddDialog.value = false
    fetchTableData()
  } catch { /* */ }
}

function openUpdate(row: ManagementRecord) {
  currentOrder.value = {
    repairId: row.repairId,
    repairPrice: row.repairPrice,
    paymentStatus: row.paymentStatus,
    repairNotes: row.repairNotes,
    technicianId: userStore.userId,
  }
  showUpdateDialog.value = true
}

async function submitUpdate() {
  if (!currentOrder.value.repairId) {
    ElMessage.error('订单 ID 缺失，无法提交修改！')
    return
  }
  try {
    await mgmtApi.updateRepairManagement(currentOrder.value)
    ElMessage.success('订单修改成功！')
    showUpdateDialog.value = false
    fetchTableData()
  } catch { /* */ }
}

async function deleteOrder(row: ManagementRecord) {
  if (!row.repairId) {
    ElMessage.error('订单信息错误，无法删除！')
    return
  }
  try {
    const { value: password } = await ElMessageBox.prompt('请输入密码以确认删除订单', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'password',
    })
    if (!password) { ElMessage.error('密码不能为空！'); return }
    await mgmtApi.deleteRepairManagement(row.repairId, userStore.userId, password)
    ElMessage.success('订单删除成功！')
    fetchTableData()
  } catch { /* */ }
}

function showDetail(row: ManagementRecord) {
  ElMessageBox.alert(
    `<h3>订单详情：</h3>
    <p>维修ID：${row.repairId}</p>
    <p>订单ID：${row.repairRequestId}</p>
    <p>手机型号：${row.phoneModel}</p>
    <p>维修描述：${row.repairNotes}</p>
    <p>维修状态：${row.statusName}</p>
    <p>订单价格：${row.repairPrice}</p>
    <p>支付状态：${row.paymentStatus}</p>
    <p>订单用户：${row.userName}</p>
    <p>创建时间：${row.createdAt}</p>`,
    '订单详情',
    { dangerouslyUseHTMLString: true, confirmButtonText: '确定' },
  )
}
</script>

<template>
  <div>
    <div class="hengfu">
      <span>维修管理页面</span>
      <span class="hengfu-sub">&emsp;用心服务好每一个顾客是我们的追求</span>
    </div>

    <div class="search-bar">
      <el-input v-model="searchKeyword" placeholder="搜索关键词" clearable size="mini" @keyup.enter="handleSearch" />
      <el-button class="mini-btn" size="mini" type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
      </el-button>
      <el-button class="mini-btn" size="mini" type="success" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon> 添加订单
      </el-button>
    </div>

    <el-table :data="tableData" border v-loading="loading" @sort-change="handleSortChange" class="data-table">
      <el-table-column prop="repairId" label="维修ID" min-width="80" sortable="custom" />
      <el-table-column prop="repairRequestId" label="订单ID" min-width="80" />
      <el-table-column prop="phoneModel" label="手机型号" min-width="140" />
      <el-table-column prop="technicianId" label="维修人员ID" min-width="110" />
      <el-table-column prop="repairNotes" label="维修描述" min-width="140" show-overflow-tooltip />
      <el-table-column prop="statusName" label="维修状态" min-width="110" />
      <el-table-column prop="repairPrice" label="订单价格" min-width="90" />
      <el-table-column prop="paymentStatus" label="支付状态" min-width="100" sortable="custom" />
      <el-table-column prop="userName" label="订单用户" min-width="100" />
      <el-table-column prop="createdAt" label="创建时间" min-width="160" sortable="custom" />
      <el-table-column label="操作" min-width="220">
        <template #default="{ row }">
          <el-button size="mini" type="info" @click="showDetail(row)">详情</el-button>
          <el-button size="mini" type="info" @click="openUpdate(row)">修改</el-button>
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

    <el-dialog title="添加订单" v-model="showAddDialog" width="400px">
      <el-form :model="newOrder" label-width="100px">
        <el-form-item label="订单号" :rules="[{ required: true, message: '请输入订单号', trigger: 'blur' }]">
          <el-input v-model="newOrder.repairRequestId" placeholder="请输入订单号" />
        </el-form-item>
        <el-form-item label="维修描述" :rules="[{ required: true, message: '请输入维修描述', trigger: 'blur' }]">
          <el-input type="textarea" v-model="newOrder.repairNotes" placeholder="请输入维修描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="createOrder">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog title="修改订单" v-model="showUpdateDialog" width="350px">
      <el-form :model="currentOrder" label-width="120px">
        <el-form-item label="订单价格">
          <el-input type="number" v-model="currentOrder.repairPrice" placeholder="请输入订单价格" />
        </el-form-item>
        <el-form-item label="支付状态">
          <el-select v-model="currentOrder.paymentStatus" placeholder="请选择支付状态">
            <el-option label="待支付" value="未支付" />
            <el-option label="支付中" value="支付中" />
            <el-option label="支付完成" value="支付完成" />
            <el-option label="支付异常" value="支付异常" />
          </el-select>
        </el-form-item>
        <el-form-item label="维修描述">
          <el-input type="textarea" v-model="currentOrder.repairNotes" placeholder="请输入维修描述" />
        </el-form-item>
        <el-form-item label="维修人ID">
          <el-input type="number" v-model="currentOrder.technicianId" placeholder="请输入维修人ID" />
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
