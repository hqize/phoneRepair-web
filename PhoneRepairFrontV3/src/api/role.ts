import http from './request'
import type { Result } from '@/types/api'
import type { Role } from '@/types/role'

export function listAllRoles() {
  return http.get<Result<Role[]>>('/role/list')
}
