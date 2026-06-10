import http from './request'
import type { Result } from '@/types/api'
import type { PartsRecord } from '@/types/parts'

export function getPartsList(params: Record<string, unknown>) {
  return http.get<Result<{ pageResult: PartsRecord[]; count: number }>>('/parts/list', { params })
}

export function addPart(body: Partial<PartsRecord>) {
  return http.post<Result<string>>('/parts/addPart', body)
}

export function updatePart(body: Partial<PartsRecord>) {
  return http.post<Result<string>>('/parts/updatePart', body)
}

export function deletePart(partId: number) {
  return http.delete<Result<null>>(`/parts/delete/${partId}`)
}
