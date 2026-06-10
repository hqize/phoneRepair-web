/** 维修单记录 */
export interface RepairRecord {
  requestId: number
  userName: string
  phoneModel: string
  phoneIssueDescription: string
  requestStatus: string
  receptionistName: string
  createdAt: string
}

/** 接待人员 */
export interface Receptionist {
  userId: number
  userName: string
}

/** 创建维修单参数 */
export interface RepairCreateParams {
  userId: number
  receptionistId: number
  phoneModel: string
  phoneIssueDescription: string
}
