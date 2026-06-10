/** 供应商管理记录 */
export interface SupplierRecord {
  supplierManagementId: number
  supplierId: number
  partId: number
  supplyQuantity: number
  createdAt: string
  updatedAt: string
  supplierName: string
  partName: string
}
