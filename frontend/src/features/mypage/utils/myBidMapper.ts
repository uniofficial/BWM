import type { MyBidHistoryResponse, MyBidItem } from '../../../types/myBid'
import type { ItemStatus } from '../../../types/product'
import { resolveProductImageUrl } from '../../products/utils/productMapper'

const ITEM_STATUSES = new Set<ItemStatus>(['OPEN', 'SOLD', 'UNSOLD', 'CANCELLED'])

export function mapMyBids(responses: MyBidHistoryResponse[]): MyBidItem[] {
  const latestByProduct = new Map<number, MyBidHistoryResponse>()

  responses.forEach((response) => {
    const current = latestByProduct.get(response.itemId)
    const currentTime = current?.bidAt ? Date.parse(current.bidAt) : Number.NEGATIVE_INFINITY
    const nextTime = response.bidAt ? Date.parse(response.bidAt) : Number.NEGATIVE_INFINITY
    if (!current || nextTime > currentTime || (nextTime === currentTime && response.bidId > current.bidId)) {
      latestByProduct.set(response.itemId, response)
    }
  })

  return Array.from(latestByProduct.values()).map((response) => ({
    bidId: response.bidId,
    productId: response.itemId,
    productName: response.itemTitle,
    imageUrl: resolveProductImageUrl(response.representativeImageUrl),
    myBidAmount: Number.isFinite(response.bidAmount) ? response.bidAmount : null,
    currentHighestBidAmount: Number.isFinite(response.currentPrice) ? response.currentPrice : null,
    auctionStatus: ITEM_STATUSES.has(response.itemStatus as ItemStatus)
      ? (response.itemStatus as ItemStatus)
      : 'UNKNOWN',
    remainingSeconds: Number.isFinite(response.remainingSeconds) ? response.remainingSeconds : null,
    isHighestBidder: response.highestBid === true,
    bidAt: response.bidAt,
  }))
}
