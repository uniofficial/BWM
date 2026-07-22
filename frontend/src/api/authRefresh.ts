import type { AuthSession } from '../types/auth'
import { isClientLoggedOut } from '../utils/tokenStore'
import { requestAuthRefresh } from './authApi'

let refreshPromise: Promise<AuthSession> | null = null

export class AuthRefreshSuppressedError extends Error {
  constructor() {
    super('클라이언트 로그아웃 상태에서는 인증을 갱신하지 않습니다.')
    this.name = 'AuthRefreshSuppressedError'
  }
}

export function refreshAuthOnce() {
  if (isClientLoggedOut()) return Promise.reject(new AuthRefreshSuppressedError())

  if (!refreshPromise) {
    refreshPromise = requestAuthRefresh().finally(() => {
      refreshPromise = null
    })
  }

  return refreshPromise
}
