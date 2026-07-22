const priceFormatter = new Intl.NumberFormat('ko-KR')

export function formatPrice(value: number | null) {
  return value !== null && Number.isFinite(value) ? `${priceFormatter.format(value)}원` : '가격 정보 없음'
}

export function formatPriceInput(value: string) {
  return value.replace(/\B(?=(\d{3})+(?!\d))/g, ',')
}
