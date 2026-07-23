import type { GenericAbortSignal } from 'axios'
import type { ApiResponse } from '../types/api'
import type { AdminChargeRequestItem } from '../types/admin'
import type { WalletChargeRequestResponseDto } from '../types/wallet'
import { mapAdminChargeRequests } from '../features/admin/utils/adminChargeRequestMapper'
import { apiClient } from './axiosInstance'

export const ADMIN_ENDPOINTS = {
  chargeRequests: '/api/admin/wallets/charge/requests',
  approveChargeRequest: (requestId: number) => `/api/admin/wallets/charge/requests/${requestId}/approve`,
  rejectChargeRequest: (requestId: number) => `/api/admin/wallets/charge/requests/${requestId}/reject`,
} as const

export async function getAdminChargeRequests(
  signal?: GenericAbortSignal,
): Promise<AdminChargeRequestItem[]> {
  const response = await apiClient.get<ApiResponse<WalletChargeRequestResponseDto[]>>(
    ADMIN_ENDPOINTS.chargeRequests,
    { signal },
  )
  return mapAdminChargeRequests(response.data.data)
}

export async function approveChargeRequest(requestId: number): Promise<void> {
  await apiClient.post<ApiResponse<null>>(ADMIN_ENDPOINTS.approveChargeRequest(requestId))
}

export async function rejectChargeRequest(requestId: number): Promise<void> {
  await apiClient.post<ApiResponse<null>>(ADMIN_ENDPOINTS.rejectChargeRequest(requestId))
}
