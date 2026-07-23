import type { ProductDetail } from '../../../types/product'

const pad = (value: number) => String(value).padStart(2, '0')

export function formatDetailRemainingTime(product: ProductDetail, now: number) {
  if (product.status === 'CANCELLED') return '경매 취소'
  if (product.status !== 'OPEN') return product.status === 'UNKNOWN' ? '상태 확인 필요' : '경매 종료'

  const endTime = product.auctionEndAt ? Date.parse(product.auctionEndAt) : Number.NaN
  if (!Number.isFinite(endTime)) return '마감 정보 없음'

  const totalSeconds = Math.max(0, Math.ceil((endTime - now) / 1_000))
  if (totalSeconds === 0) return '경매 종료'

  const days = Math.floor(totalSeconds / 86_400)
  const hours = Math.floor((totalSeconds % 86_400) / 3_600)
  const minutes = Math.floor((totalSeconds % 3_600) / 60)
  const seconds = totalSeconds % 60

  if (days > 0) return `${days}일 ${hours}시간 ${minutes}분 남음`
  return `${pad(hours)}:${pad(minutes)}:${pad(seconds)} 남음`
}
