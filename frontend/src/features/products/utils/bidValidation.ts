export const MAXIMUM_BID_AMOUNT = 2_147_483_647

export interface BidValidationResult {
  amount: number | null
  error?: string
}

export function validateBidAmount(value: string, minimumBidAmount: number | null): BidValidationResult {
  const normalized = value.trim()
  if (!normalized) return { amount: null, error: '입찰 금액을 입력해주세요.' }
  if (!/^\d+$/.test(normalized)) return { amount: null, error: '숫자만 입력해주세요.' }

  const amount = Number(normalized)
  if (!Number.isSafeInteger(amount)) return { amount: null, error: '입찰 금액이 너무 큽니다.' }
  if (amount <= 0) return { amount: null, error: '입찰 금액은 0원보다 커야 합니다.' }
  if (amount > MAXIMUM_BID_AMOUNT) return { amount: null, error: '입찰 금액이 너무 큽니다.' }
  if (minimumBidAmount !== null && amount < minimumBidAmount) {
    return { amount: null, error: '최소 입찰 가능 금액 이상을 입력해주세요.' }
  }

  return { amount }
}
