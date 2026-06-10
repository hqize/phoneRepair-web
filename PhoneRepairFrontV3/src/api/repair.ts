import http from './request'
import type { Result } from '@/types/api'
import type { RepairRecord, Receptionist, RepairCreateParams } from '@/types/repair'

export function getRepairList(params: Record<string, unknown>) {
  return http.get<Result<{ repairRequest: RepairRecord[]; count: number }>>('/repair/getAllRepair', { params })
}

export function getReceptionists() {
  return http.get<Result<Receptionist[]>>('/repair/getAllReceptionist')
}

export function createRepair(body: RepairCreateParams) {
  return http.post<Result<null>>('/repair/createRepair', body)
}

export function deleteRepair(repairId: number, userId: number, password: string) {
  return http.post<Result<null>>('/repair/deleteRepair', null, {
    params: { repairId, userId, password },
  })
}
