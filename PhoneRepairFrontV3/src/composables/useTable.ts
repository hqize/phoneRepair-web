import { ref, unref, type Ref } from 'vue'

export function useTable<T>(
  fetchFn: (params: Record<string, unknown>) => Promise<{ data: { data: { count: number } & Record<string, T[]> } }>,
  listKey: string,
  userId: Ref<number>,
  sortOrderKey = 'sortOrder',
) {
  const tableData = ref<T[]>([])
  const loading = ref(false)
  const count = ref(0)
  const pageNum = ref(1)
  const pageSize = ref(10)
  const searchKeyword = ref('')
  const sortField = ref('created_at')
  const sortOrder = ref<'asc' | 'desc'>('desc')

  async function fetchTableData() {
    loading.value = true
    try {
      const params: Record<string, unknown> = {
        userId: unref(userId),
        searchKeyword: searchKeyword.value,
        pageNum: pageNum.value,
        pageSize: pageSize.value,
        sortField: sortField.value,
        [sortOrderKey]: sortOrder.value,
      }
      const res = await fetchFn(params)
      const payload = res.data.data
      tableData.value = (payload[listKey] || []) as T[]
      count.value = payload.count || 0
    } catch (e) {
      console.error('获取表格数据失败:', e)
    } finally {
      loading.value = false
    }
  }

  function handleSearch() {
    pageNum.value = 1
    fetchTableData()
  }

  function handleSortChange({ prop, order }: { prop: string; order: string }) {
    sortField.value = prop === 'createdAt' ? 'created_at' : prop
    sortOrder.value = order === 'ascending' ? 'asc' : 'desc'
    fetchTableData()
  }

  function handlePageChange(page: number) {
    pageNum.value = page
    fetchTableData()
  }

  function handleSizeChange(size: number) {
    pageSize.value = size
    fetchTableData()
  }

  return {
    tableData,
    loading,
    count,
    pageNum,
    pageSize,
    searchKeyword,
    sortField,
    sortOrder,
    fetchTableData,
    handleSearch,
    handleSortChange,
    handlePageChange,
    handleSizeChange,
  }
}
