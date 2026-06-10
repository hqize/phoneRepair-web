import http from './request'
import type { Result } from '@/types/api'
import type { LoginUser, UserRecord, RegisterParams } from '@/types/user'

export function login(usernameOrEmail: string, password: string) {
  return http.post<Result<LoginUser>>('/user/login', null, {
    params: { usernameOrEmail, password },
  })
}

export function register(params: RegisterParams) {
  return http.post<Result<string>>('/user/createUser', params)
}

export function getAllUsers(params: Record<string, unknown>) {
  return http.get<Result<{ userList: UserRecord[]; count: number }>>('/user/getAllUsers', { params })
}

export function updateUser(user: Partial<UserRecord>) {
  return http.post<Result<string>>('/user/updateUser', user)
}

export function deleteUser(userId: number) {
  return http.delete<Result<null>>(`/user/delete/${userId}`)
}
