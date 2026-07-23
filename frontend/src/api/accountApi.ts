import type { ApiResponse } from '../types/api'
import { apiClient } from './axiosInstance'
import { toApiError } from './apiError'

export const ACCOUNT_ENDPOINTS = {
  logout: '/api/auth/logout',
  withdraw: '/api/auth/withdraw',
} as const

// Both endpoints require an Authorization header, so they go through the
// authenticated apiClient (which also retries once after a token refresh)
// instead of authApi.ts's unauthenticated publicAuthClient.

export async function requestLogout(): Promise<void> {
  try {
    await apiClient.post<ApiResponse<null>>(ACCOUNT_ENDPOINTS.logout)
  } catch (error) {
    throw toApiError(error)
  }
}

export async function requestWithdraw(): Promise<void> {
  try {
    await apiClient.delete<ApiResponse<null>>(ACCOUNT_ENDPOINTS.withdraw)
  } catch (error) {
    throw toApiError(error)
  }
}
