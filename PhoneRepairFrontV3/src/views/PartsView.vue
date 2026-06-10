<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useTable } from '@/composables/useTable'
import * as partsApi from '@/api/parts'
import type { PartsRecord } from '@/types/parts'

const userStore = useUserStore()
const userId = computed(() => userStore.userId)

const { tableData, loading, count, pageNum, pageSize, searchKeyword, fetchTableData, handleSearch, handleSortChange, handlePageChange, handleSizeChange } =
  useTable<PartsRecord>(partsApi.getPartsList, 'pageResult', userId)

const showAddDialog = ref(false)
const showUpdateDialog = ref(false)
const newPart = ref({ partName: '', partPrice: '', stockQuantity: '', supplierId: '' })
const currentPart = ref<Partial<PartsRecord>>({})

onMounted(() => fetchTableData())

async function addPart() {
  if (!newPart.value.partName || !newPart.value.partPrice || !newPart.value.stockQuantity || !newPart.value.supplierId) {
    ElMessage.error('请填写完整信息！')
    return
  }
  try {
    await partsApi.addPart({
      partName: newPart.value.partName,
      partPrice: Number(newPart.value.partPrice),
      stockQuantity: Number(newPart.value.stockQuantity),
      supplierId: Number(newPart.value.supplierId),
    })
    ElMessage.success('配件添加成功！')
    showAddDialog.value = false
    fetchTableData()
  } catch { /* */ }
}

function openUpdate(row: PartsRecord) {
  currentPart.value = {
    partId: row.partId,
    partName: row.partName,
    partPrice: row.partPrice,
    stockQuantity: row.stockQuantity,
    supplierId: row.supplierId,
    partDescription: row.partDescription,
  }
  showUpdateDialog.value = true
}

async function submitUpdate() {
  if (!currentPart.value.partId) {
    ElMessage.error('配件 ID 缺失，无法提交修改！')
    return
  }
  try {
    await partsApi.updatePart(currentPart.value)
    ElMessage.success('配件修改成功！')
    showUpdateDialog.value = false
    fetchTableData()
  } catch { /* */ }
}

async function deletePart(row: PartsRecord) {
  try {
    await ElMessageBox.confirm(`确认删除订单 ID ${row.partId} 吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning',
    })
    await partsApi.deletePart(row.partId)
    ElMessage.success('订单删除成功！')
    fetchTableData()
  } catch { /* */ }
}

function showDetail(row: PartsRecord) {
  ElMessageBox.alert(
    `<h3>订单详情：</h3>
    配件单号：${row.partId} <br>
    配件名称：${row.partName} <br>
    配件描述：${row.partDescription} <br>
    配件价格：${row.partPrice} <br>
    配件数量：${row.stockQuantity} <br>
    供应商id：${row.supplierId} <br>
    创建时间：${row.createdAt}`,
    '配件详情',
    { dangerouslyUseHTMLString: true, confirmButtonText: '确定' },
  )
}
</script>

<template>
  <div>
    <div class="hengfu">
      <span>配件查询页面</span>
      <span class="hengfu-sub">&emsp;用心服务好每一个顾客是我们的追求</span>
    </div>

    <div class="search-bar">
      <el-input v-model="searchKeyword" placeholder="搜索关键词" clearable size="mini" @keyup.enter="handleSearch" />
      <el-button class="mini-btn" size="mini" type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
      </el-button>
      <el-button class="mini-btn" size="mini" type="success" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon> 添加配件
      </el-button>
    </div>

    <el-table :data="tableData" border v-loading="loading" @sort-change="handleSortChange" class="data-table">
      <el-table-column prop="partId" label="配件ID" min-width="80" sortable="custom" />
      <el-table-column prop="partName" label="配件名称" min-width="120" />
      <el-table-column prop="partPrice" label="配件价格" min-width="100" />
      <el-table-column prop="partDescription" label="配件描述" min-width="160" show-overflow-tooltip />
      <el-table-column prop="stockQuantity" label="配件数量" min-width="100" />
      <el-table-column prop="supplierId" label="供应商ID" min-width="100" />
      <el-table-column prop="createdAt" label="创建时间" min-width="160" sortable="custom" />
      <el-table-column label="操作" min-width="220">
        <template #default="{ row }">
          <el-button size="mini" type="info" @click="showDetail(row)">详情</el-button>
          <el-button size="mini" type="primary" @click="openUpdate(row)">修改</el-button>
          <el-button size="mini" type="danger" @click="deletePart(row)">删除</el-button>
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
      <el-form :model="newPart" label-width="100px">
        <el-form-item label="配件名称" :rules="[{ required: true, message: '请输入配件名称', trigger: 'blur' }]">
          <el-input v-model="newPart.partName" placeholder="请输入配件名称" />
        </el-form-item>
        <el-form-item label="配件价格" :rules="[{ required: true, message: '请输入配件价格', trigger: 'blur' }]">
          <el-input v-model="newPart.partPrice" placeholder="请输入配件价格" />
        </el-form-item>
        <el-form-item label="配件库存" :rules="[{ required: true, message: '请输入配件库存', trigger: 'blur' }]">
          <el-input v-model="newPart.stockQuantity" placeholder="请输入配件库存" />
        </el-form-item>
        <el-form-item label="供应商编号" :rules="[{ required: true, message: '请输入供应商编号', trigger: 'blur' }]">
          <el-input v-model="newPart.supplierId" placeholder="请输入供应商编号" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addPart">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog title="修改配件" v-model="showUpdateDialog" width="400px">
      <el-form :model="currentPart" label-width="120px">
        <el-form-item label="配件名称">
          <el-input v-model="currentPart.partName" />
        </el-form-item>
        <el-form-item label="配件价格">
          <el-input type="number" v-model="currentPart.partPrice" />
        </el-form-item>
        <el-form-item label="配件库存">
          <el-input type="number" v-model="currentPart.stockQuantity" />
        </el-form-item>
        <el-form-item label="供应商编号">
          <el-input type="number" v-model="currentPart.supplierId" />
        </el-form-item>
        <el-form-item label="配件描述">
          <el-input type="textarea" v-model="currentPart.partDescription" />
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
