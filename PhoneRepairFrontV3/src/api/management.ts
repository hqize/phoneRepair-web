import http from './request'
import type { Result } from '@/types/api'
import type { ManagementRecord, ManagementCreateParams, ManagementUpdateParams } from '@/types/management'

export function getAllRepairManagement(params: Record<string, unknown>) {
  return http.get<Result<{ repairManagementList: ManagementRecord[]; count: number }>>(
    '/management/getAllRepairManagement',
    { params },
  )
}

export function createRepairManagement(body: ManagementCreateParams) {
  return http.post<Result<null>>('/management/createRepairManagement', body)
}

export function updateRepairManagement(body: ManagementUpdateParams) {
  return http.post<Result<null>>('/management/updateRepairManagement', body)
}

export function deleteRepairManagement(repairId: number, userId: number, userPassword: string) {
  return http.post<Result<null>>('/management/deleteRepairManagement', null, {
    params: { repairId, userId, userPassword },
  })
}
