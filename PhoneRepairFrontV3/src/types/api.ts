/** 统一响应格式 {@link com.hnust.util.Result} */
export interface Result<T = unknown> {
  code: number
  msg: string
  data: T
  timestamp: number
}

/** 分页查询通用参数 */
export interface PageQuery {
  userId?: number
  searchKeyword?: string
  pageNum: number
  pageSize: number
  sortField: string
  sortOrder: 'asc' | 'desc'
}
