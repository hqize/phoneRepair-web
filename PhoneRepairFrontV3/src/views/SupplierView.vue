<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Plus } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'
import { useTable } from '@/composables/useTable'
import * as supplierApi from '@/api/supplier'
import type { SupplierRecord } from '@/types/supplier'

const userStore = useUserStore()
const userIdFn = computed(() => userStore.userId)

const { tableData, loading, count, pageNum, pageSize, searchKeyword, fetchTableData, handleSearch, handleSortChange, handlePageChange, handleSizeChange } =
  useTable<SupplierRecord>(supplierApi.getAllSupplierManagement, 'supplierManagementList', userIdFn)

const showAddDialog = ref(false)
const showUpdateDialog = ref(false)
const addFormRef = ref()
const updateFormRef = ref()
const newSupplier = ref({ supplierId: '', partId: '', supplyQuantity: null as number | null })
const currentSupplier = ref<SupplierRecord & { supplyQuantity: number }>({} as SupplierRecord & { supplyQuantity: number })

const rules = {
  supplierId: [{ required: true, message: '请输入供应商编号', trigger: 'blur' }],
  partId: [{ required: true, message: '请输入配件编号', trigger: 'blur' }],
  supplyQuantity: [
    { required: true, message: '请输入供应数量', trigger: 'blur' },
    {
      validator: (_: unknown, v: number, cb: (e?: Error) => void) => {
        if (v === '' || v === null) cb(new Error('请输入供应数量'))
        else if (Number(v) <= 0 || !Number.isInteger(Number(v))) cb(new Error('供应数量需为正整数'))
        else cb()
      },
      trigger: 'blur',
    },
  ],
}

onMounted(() => fetchTableData())

async function addSupplier() {
  const valid = await addFormRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    await supplierApi.createSupplierManagement({
      supplierId: Number(newSupplier.value.supplierId),
      partId: Number(newSupplier.value.partId),
      supplyQuantity: Number(newSupplier.value.supplyQuantity),
    })
    ElMessage.success('新增供应记录成功！')
    showAddDialog.value = false
    fetchTableData()
  } catch { /* */ }
}

function openUpdate(row: SupplierRecord) {
  currentSupplier.value = { ...row, supplyQuantity: Number(row.supplyQuantity) }
  showUpdateDialog.value = true
}

async function submitUpdate() {
  const valid = await updateFormRef.value?.validate().catch(() => false)
  if (!valid) return
  try {
    await supplierApi.updateSupplierManagement({ ...currentSupplier.value, supplyQuantity: Number(currentSupplier.value.supplyQuantity) })
    ElMessage.success('修改供应记录成功！')
    showUpdateDialog.value = false
    fetchTableData()
  } catch { /* */ }
}

async function deleteSupplier(row: SupplierRecord) {
  if (!row.supplierManagementId) {
    ElMessage.error('供应记录ID缺失，无法删除')
    return
  }
  try {
    const { value: password } = await ElMessageBox.prompt('请输入密码确认删除', '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      inputType: 'password',
      inputPlaceholder: '输入当前用户密码',
    })
    if (!password?.trim()) { ElMessage.error('密码不能为空'); return }
    await supplierApi.deleteSupplierManagement(row.supplierManagementId, userStore.userId, password)
    ElMessage.success('删除供应记录成功！')
    fetchTableData()
  } catch { /* */ }
}

function showDetail(row: SupplierRecord) {
  ElMessageBox.alert(
    `<h3>供应记录详情</h3>
    <p>供应商管理编号：${row.supplierManagementId}</p>
    <p>供应商名称：${row.supplierName}</p>
    <p>供应商编号：${row.supplierId}</p>
    <p>配件编号：${row.partId}</p>
    <p>配件名称：${row.partName}</p>
    <p>供应数量：${row.supplyQuantity}</p>
    <p>创建时间：${row.createdAt}</p>`,
    '详情',
    { dangerouslyUseHTMLString: true, confirmButtonText: '确定', width: '400px' },
  )
}

function mapSort({ prop, order }: { prop: string; order: string }) {
  const fieldMap: Record<string, string> = { supplierManagementId: 'supplier_management_id', supplierId: 'supplier_id', partId: 'part_id' }
  handleSortChange({ prop: fieldMap[prop] || 'created_at', order })
}
</script>

<template>
  <div>
    <div class="hengfu">
      <span>供应商管理页面</span>
      <span class="hengfu-sub">&emsp;高效管理供应商与配件供应数据</span>
    </div>

    <div class="search-bar">
      <el-input v-model="searchKeyword" placeholder="搜索关键词（支持供应商/配件编号）" clearable size="mini" @keyup.enter="handleSearch" />
      <el-button class="mini-btn" size="mini" type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
      </el-button>
      <el-button class="mini-btn" size="mini" type="success" @click="showAddDialog = true">
        <el-icon><Plus /></el-icon> 新增供应记录
      </el-button>
    </div>

    <el-table :data="tableData" border v-loading="loading" @sort-change="mapSort" class="data-table">
      <el-table-column prop="supplierManagementId" label="供应商管理编号" min-width="140" sortable="custom" />
      <el-table-column prop="supplierName" label="供应商名称" min-width="150" />
      <el-table-column prop="supplierId" label="供应商编号" min-width="110" sortable="custom" />
      <el-table-column prop="partId" label="配件编号" min-width="100" sortable="custom" />
      <el-table-column prop="partName" label="配件名称" min-width="150" />
      <el-table-column prop="supplyQuantity" label="供应数量" min-width="100" />
      <el-table-column prop="createdAt" label="创建时间" min-width="160" sortable="custom" />
      <el-table-column label="操作" min-width="210">
        <template #default="{ row }">
          <el-button size="mini" type="info" @click="showDetail(row)">详情</el-button>
          <el-button size="mini" type="primary" @click="openUpdate(row)">修改</el-button>
          <el-button size="mini" type="danger" @click="deleteSupplier(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-pagination
      v-model:current-page="pageNum" v-model:page-size="pageSize"
      :page-sizes="[10, 20, 30, 40]" :total="count"
      layout="total, sizes, prev, pager, next, jumper"
      @current-change="handlePageChange" @size-change="handleSizeChange"
      style="margin-top: 16px; text-align: right"
    />

    <el-dialog title="新增供应记录" v-model="showAddDialog" width="420px">
      <el-form ref="addFormRef" :model="newSupplier" :rules="rules" label-width="130px">
        <el-form-item label="供应商编号" prop="supplierId">
          <el-input v-model="newSupplier.supplierId" placeholder="请输入供应商编号" />
        </el-form-item>
        <el-form-item label="配件编号" prop="partId">
          <el-input v-model="newSupplier.partId" placeholder="请输入配件编号" />
        </el-form-item>
        <el-form-item label="供应数量" prop="supplyQuantity">
          <el-input type="number" min="1" v-model="newSupplier.supplyQuantity" placeholder="请输入供应数量（≥1）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showAddDialog = false">取消</el-button>
        <el-button type="primary" @click="addSupplier">提交</el-button>
      </template>
    </el-dialog>

    <el-dialog title="修改供应记录" v-model="showUpdateDialog" width="420px">
      <el-form ref="updateFormRef" :model="currentSupplier" :rules="rules" label-width="130px">
        <el-form-item label="供应商管理编号">
          <el-input v-model="currentSupplier.supplierManagementId" disabled placeholder="管理编号不可修改" />
        </el-form-item>
        <el-form-item label="供应商编号" prop="supplierId">
          <el-input v-model="currentSupplier.supplierId" placeholder="请输入供应商编号" />
        </el-form-item>
        <el-form-item label="配件编号" prop="partId">
          <el-input v-model="currentSupplier.partId" placeholder="请输入配件编号" />
        </el-form-item>
        <el-form-item label="供应数量" prop="supplyQuantity">
          <el-input type="number" min="1" v-model="currentSupplier.supplyQuantity" placeholder="请输入供应数量（≥1）" />
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
  margin-bottom: 16px;
  gap: 0;
}
.mini-btn { margin-left: 10px; }
.data-table { margin-top: 20px; border-radius: 8px; }
</style>
