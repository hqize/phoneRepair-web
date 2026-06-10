/** 维修管理记录 */
export interface ManagementRecord {
  repairId: number
  repairRequestId: number
  technicianId: number
  repairPrice: number
  paymentStatus: string
  repairNotes: string
  createdAt: string
  phoneModel: string
  statusName: string
  userName: string
}

/** 创建维修管理参数 */
export interface ManagementCreateParams {
  repairRequestId: number
  repairNotes: string
  technicianId: number
}

/** 更新维修管理参数 */
export interface ManagementUpdateParams {
  repairId: number
  repairPrice?: number
  paymentStatus?: string
  repairNotes?: string
  technicianId: number
}
