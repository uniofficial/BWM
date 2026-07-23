import type { ProductListItem } from '../../../types/product'

export function formatRemainingTime(product: ProductListItem, now: number) {
  if (product.status !== 'OPEN') {
    if (product.status === 'CANCELLED') return '경매 취소'
    if (product.status === 'UNKNOWN') return '마감 정보 확인 필요'
    return '경매 종료'
  }

  const endTime = product.auctionEndAt ? Date.parse(product.auctionEndAt) : Number.NaN
  if (!Number.isFinite(endTime)) return '마감 정보 없음'

  const remainingMinutes = Math.ceil((endTime - now) / 60_000)
  if (remainingMinutes <= 0) return '경매 종료'

  const days = Math.floor(remainingMinutes / 1_440)
  const hours = Math.floor((remainingMinutes % 1_440) / 60)
  const minutes = remainingMinutes % 60

  if (days > 0) return `${days}일 ${hours}시간 남음`
  if (hours > 0) return `${hours}시간 ${minutes}분 남음`
  return `${minutes}분 남음`
}

export function isAuctionImminent(
  status: string,
  auctionEndAt: string | null | undefined,
  now: number,
  fallbackRemainingSeconds: number | null = null,
): boolean {
  if (status !== 'OPEN') return false
  const endTime = auctionEndAt ? Date.parse(auctionEndAt) : Number.NaN
  const totalSeconds = Number.isFinite(endTime)
    ? Math.max(0, Math.ceil((endTime - now) / 1_000))
    : fallbackRemainingSeconds
  return totalSeconds !== null && totalSeconds > 0 && totalSeconds <= 60
}
