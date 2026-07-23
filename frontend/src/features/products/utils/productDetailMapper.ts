import type {
  ItemDetailResponse,
  ItemStatus,
  ProductBidHistoryItem,
  ProductDetail,
} from '../../../types/product'
import { resolveProductImageUrl } from './productMapper'

const ITEM_STATUSES = new Set<ItemStatus>(['OPEN', 'SOLD', 'UNSOLD', 'CANCELLED'])
export const MINIMUM_BID_INCREMENT = 100

function finiteNumber(value: number | null) {
  return value !== null && Number.isFinite(value) ? value : null
}

function mapBidHistory(response: ItemDetailResponse['bidHistory']): ProductBidHistoryItem[] {
  if (!Array.isArray(response)) return []
  return response
    .filter((bid) => Number.isInteger(bid.bidId) && Number.isFinite(bid.bidAmount))
    .map((bid) => ({
      id: bid.bidId,
      bidderNickname: bid.bidderNickname,
      amount: bid.bidAmount,
      bidAt: bid.bidAt,
    }))
}

function getMinimumBidAmount(
  response: ItemDetailResponse,
  currentPrice: number | null,
  bidHistory: ProductBidHistoryItem[],
) {
  if (currentPrice === null) return null
  const hasBid = Boolean(response.highestBidderNickname) || bidHistory.length > 0
  if (hasBid) return currentPrice + MINIMUM_BID_INCREMENT
  return finiteNumber(response.startPrice) ?? currentPrice
}

export function mapProductDetail(response: ItemDetailResponse): ProductDetail {
  const currentPrice = finiteNumber(response.currentPrice)
  const bidHistory = mapBidHistory(response.bidHistory)
  const imageUrls = Array.isArray(response.imageUrls)
    ? Array.from(
        new Set(
          response.imageUrls
            .map((url) => resolveProductImageUrl(url))
            .filter((url): url is string => Boolean(url)),
        ),
      )
    : []

  return {
    id: response.itemId,
    sellerNickname: response.sellerNickname || null,
    title: response.title,
    category: response.category,
    startPrice: finiteNumber(response.startPrice),
    currentPrice,
    minimumBidAmount: getMinimumBidAmount(response, currentPrice, bidHistory),
    auctionEndAt: response.auctionEndAt,
    status: ITEM_STATUSES.has(response.status as ItemStatus)
      ? (response.status as ItemStatus)
      : 'UNKNOWN',
    createdAt: response.createdAt,
    description: response.description || '',
    highestBidderNickname: response.highestBidderNickname || null,
    imageUrls,
    bidHistory,
    bidCount: bidHistory.length,
  }
}
