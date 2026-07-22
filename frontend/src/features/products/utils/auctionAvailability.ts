import type { ProductDetail } from '../../../types/product'

export interface AuctionAvailability {
  action: 'bid' | 'login' | 'disabled'
  canOpenBidDialog: boolean
  buttonLabel: string
  message: string
}

export function getAuctionAvailability(
  product: ProductDetail,
  isAuthenticated: boolean,
  now: number,
): AuctionAvailability {
  if (!isAuthenticated) {
    return { action: 'login', canOpenBidDialog: false, buttonLabel: '로그인 후 입찰', message: '로그인 후 입찰할 수 있습니다.' }
  }
  if (product.status === 'CANCELLED') {
    return { action: 'disabled', canOpenBidDialog: false, buttonLabel: '경매 취소', message: '취소된 경매입니다.' }
  }
  if (product.status === 'SOLD' || product.status === 'UNSOLD') {
    return { action: 'disabled', canOpenBidDialog: false, buttonLabel: '경매 종료', message: '종료된 경매입니다.' }
  }
  if (product.status !== 'OPEN') {
    return { action: 'disabled', canOpenBidDialog: false, buttonLabel: '입찰 불가', message: '현재 입찰할 수 없는 상품입니다.' }
  }

  const endTime = product.auctionEndAt ? Date.parse(product.auctionEndAt) : Number.NaN
  if (!Number.isFinite(endTime)) {
    return { action: 'disabled', canOpenBidDialog: false, buttonLabel: '입찰 불가', message: '경매 마감 정보를 확인할 수 없습니다.' }
  }
  if (endTime <= now) {
    return { action: 'disabled', canOpenBidDialog: false, buttonLabel: '경매 종료', message: '마감 시간이 지나 서버 상태를 확인하고 있습니다.' }
  }
  if (product.minimumBidAmount === null) {
    return { action: 'disabled', canOpenBidDialog: false, buttonLabel: '입찰 불가', message: '현재 가격 정보를 확인할 수 없습니다.' }
  }

  return {
    action: 'bid',
    canOpenBidDialog: true,
    buttonLabel: '입찰하기',
    message: '판매자 여부와 최종 입찰 가능 여부는 서버에서 확인됩니다.',
  }
}
