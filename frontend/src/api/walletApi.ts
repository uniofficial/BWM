import type { GenericAbortSignal } from 'axios'
import type { ApiResponse } from '../types/api'
import type {
  ChargeRequestHistoryItem,
  CreateChargeRequest,
  CreateChargeResponse,
  WalletChargeRequestResponseDto,
  WalletHistoryResponseDto,
  WalletResponseDto,
  WalletSummary,
  WalletTransaction,
} from '../types/wallet'
import { mapChargeRequests, mapWalletSummary, mapWalletTransactions } from '../features/mypage/utils/walletMapper'
import { apiClient } from './axiosInstance'
import { ApiError } from './apiError'

export const WALLET_ENDPOINTS = {
  balance: '/api/wallets/me',
  chargeRequests: '/api/wallets/charge/requests',
  transactions: '/api/wallets/me/histories',
} as const

export async function createChargeRequest(request: CreateChargeRequest): Promise<CreateChargeResponse> {
  const response = await apiClient.post<ApiResponse<CreateChargeResponse>>(
    WALLET_ENDPOINTS.chargeRequests,
    request,
  )
  const data = response.data.data
  if (!data || !Number.isInteger(data.chargeRequestId) || data.chargeRequestId <= 0 || typeof data.status !== 'string') {
    throw new ApiError('충전 요청 응답을 확인할 수 없습니다.', { kind: 'unknown' })
  }
  return data
}

export async function getMyWallet(signal?: GenericAbortSignal): Promise<WalletSummary> {
  const response = await apiClient.get<ApiResponse<WalletResponseDto>>(WALLET_ENDPOINTS.balance, { signal })
  return mapWalletSummary(response.data.data)
}

export async function getMyChargeRequests(signal?: GenericAbortSignal): Promise<ChargeRequestHistoryItem[]> {
  const response = await apiClient.get<ApiResponse<WalletChargeRequestResponseDto[]>>(
    WALLET_ENDPOINTS.chargeRequests,
    { signal },
  )
  return mapChargeRequests(response.data.data)
}

export async function getMyWalletTransactions(signal?: GenericAbortSignal): Promise<WalletTransaction[]> {
  const response = await apiClient.get<ApiResponse<WalletHistoryResponseDto[]>>(WALLET_ENDPOINTS.transactions, {
    signal,
  })
  return mapWalletTransactions(response.data.data)
}
