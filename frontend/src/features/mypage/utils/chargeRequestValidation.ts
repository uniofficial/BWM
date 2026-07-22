export type ChargeAmountValidation =
  | { valid: true; amount: number }
  | { valid: false; message: string }

const JAVA_INTEGER_MAX = 2_147_483_647

export function validateChargeAmount(value: string): ChargeAmountValidation {
  const trimmed = value.trim()
  if (!trimmed) return { valid: false, message: '충전 금액을 입력해주세요.' }
  if (!/^\d+$/.test(trimmed)) return { valid: false, message: '유효한 정수 금액을 입력해주세요.' }

  const amount = Number(trimmed)
  if (!Number.isSafeInteger(amount) || amount > JAVA_INTEGER_MAX) {
    return { valid: false, message: '충전 금액은 2,147,483,647원 이하여야 합니다.' }
  }
  if (amount <= 0) return { valid: false, message: '충전 금액은 1원 이상이어야 합니다.' }
  return { valid: true, amount }
}
