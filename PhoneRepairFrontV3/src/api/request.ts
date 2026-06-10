import axios from 'axios'
import { ElMessage } from 'element-plus'
import type { Result } from '@/types/api'

const http = axios.create({
  baseURL: '/hnust',
  timeout: 15000,
})

http.interceptors.response.use(
  (response) => {
    const res = response.data as Result
    if (res.code !== 200) {
      ElMessage.error(res.msg || '请求失败')
      return Promise.reject(new Error(res.msg))
    }
    return response
  },
  (error) => {
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  },
)

export default http
