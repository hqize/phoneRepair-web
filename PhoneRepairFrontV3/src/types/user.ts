/** 登录响应 */
export interface LoginUser {
  userId: number
  userName: string
  userEmail: string
  roleId: number
  userBio: string
  userPhone: string
}

/** 用户列表项 */
export interface UserRecord {
  userId: number
  userName: string
  userEmail: string
  userPasswordHash: string
  roleId: number
  userBio: string
  userPhone: string
  userGender: string
  userLastActive: string
  userCreatedAt: string
  userStatus: string
}

/** 注册参数 */
export interface RegisterParams {
  userName: string
  userEmail: string
  userPasswordHash: string
}
