import type { ChargeRequestStatus, WalletTransactionType } from '../../../types/wallet'

export const chargeStatusPresentation: Record<
  ChargeRequestStatus,
  { label: string; variant: 'primary' | 'success' | 'error' | 'warning' }
> = {
  PENDING: { label: '승인 대기', variant: 'primary' },
  APPROVED: { label: '승인 완료', variant: 'success' },
  REJECTED: { label: '승인 거절', variant: 'error' },
  UNKNOWN: { label: '상태 확인 필요', variant: 'warning' },
}

export const transactionTypeLabels: Record<WalletTransactionType, string> = {
  CHARGE: '충전',
  BID_PAYMENT: '입찰 결제',
  REFUND: '환불',
  SALES_REVENUE: '판매 대금',
  UNKNOWN: '기타 거래',
}

const POSITIVE_TYPES = new Set<WalletTransactionType>(['CHARGE', 'REFUND', 'SALES_REVENUE'])

export function getTransactionSign(type: WalletTransactionType) {
  if (type === 'BID_PAYMENT') return '-'
  if (POSITIVE_TYPES.has(type)) return '+'
  return ''
}
