export type ChargeRequestStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'UNKNOWN'

export type WalletTransactionType =
  | 'CHARGE'
  | 'BID_PAYMENT'
  | 'REFUND'
  | 'SALES_REVENUE'
  | 'UNKNOWN'

export interface WalletResponseDto {
  balance: number | null
  lastUpdatedAt: string | null
}

export interface WalletSummary {
  balance: number | null
  updatedAt: string | null
  isValid: boolean
}

export interface WalletChargeRequestResponseDto {
  chargeRequestId: number
  userId: number
  userEmail: string
  amount: number | null
  status: string
  createdAt: string | null
  processedAt: string | null
}

export interface CreateChargeRequest {
  amount: number
}

export type CreateChargeResponse = WalletChargeRequestResponseDto

export interface ChargeRequestHistoryItem {
  id: number
  amount: number | null
  status: ChargeRequestStatus
  requestedAt: string | null
  processedAt: string | null
}

export interface WalletHistoryResponseDto {
  type: string
  itemId: number | null
  amount: number | null
  balanceAfter: number | null
  createdAt: string | null
}

export interface WalletTransaction {
  id: string
  type: WalletTransactionType
  amount: number | null
  balanceAfter: number | null
  createdAt: string | null
  productId: number | null
}
