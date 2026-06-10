import http from './request'
import type { Result } from '@/types/api'
import type { SupplierRecord } from '@/types/supplier'

export function getAllSupplierManagement(params: Record<string, unknown>) {
  return http.get<Result<{ supplierManagementList: SupplierRecord[]; count: number }>>(
    '/supplier/getAllSupplierManagement',
    { params },
  )
}

export function createSupplierManagement(body: Partial<SupplierRecord>) {
  return http.post<Result<null>>('/supplier/createSupplierManagement', body)
}

export function updateSupplierManagement(body: Partial<SupplierRecord>) {
  return http.post<Result<null>>('/supplier/updateSupplierManagement', body)
}

export function deleteSupplierManagement(supplierManagementId: number, userId: number, userPasswd: string) {
  return http.post<Result<null>>('/supplier/deleteSupplierManagement', null, {
    params: { supplierManagementId, userId, userPasswd },
  })
}
