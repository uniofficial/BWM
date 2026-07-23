import type {
  ChargeRequestHistoryItem,
  ChargeRequestStatus,
  WalletChargeRequestResponseDto,
  WalletHistoryResponseDto,
  WalletResponseDto,
  WalletSummary,
  WalletTransaction,
  WalletTransactionType,
} from '../../../types/wallet'

const CHARGE_STATUSES = new Set<ChargeRequestStatus>(['PENDING', 'APPROVED', 'REJECTED'])
const TRANSACTION_TYPES = new Set<WalletTransactionType>([
  'CHARGE',
  'BID_PAYMENT',
  'REFUND',
  'SALES_REVENUE',
])

export function mapWalletSummary(response: WalletResponseDto): WalletSummary {
  const validBalance = Number.isFinite(response.balance) && Number(response.balance) >= 0
  return {
    balance: validBalance ? Number(response.balance) : null,
    updatedAt: response.lastUpdatedAt,
    isValid: validBalance,
  }
}

export function mapChargeRequests(responses: WalletChargeRequestResponseDto[]): ChargeRequestHistoryItem[] {
  return responses.map((response) => ({
    id: response.chargeRequestId,
    amount: Number.isFinite(response.amount) ? Number(response.amount) : null,
    status: CHARGE_STATUSES.has(response.status as ChargeRequestStatus)
      ? (response.status as ChargeRequestStatus)
      : 'UNKNOWN',
    requestedAt: response.createdAt,
    processedAt: response.processedAt,
  }))
}

export function mapWalletTransactions(responses: WalletHistoryResponseDto[]): WalletTransaction[] {
  return responses.map((response, index) => ({
    id: `${response.type}-${response.itemId ?? 'none'}-${response.createdAt ?? index}-${index}`,
    type: TRANSACTION_TYPES.has(response.type as WalletTransactionType)
      ? (response.type as WalletTransactionType)
      : 'UNKNOWN',
    amount: Number.isFinite(response.amount) ? Number(response.amount) : null,
    balanceAfter: Number.isFinite(response.balanceAfter) ? Number(response.balanceAfter) : null,
    createdAt: response.createdAt,
    productId: Number.isInteger(response.itemId) ? response.itemId : null,
  }))
}
