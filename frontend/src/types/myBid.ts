import type { ItemStatus } from './product'

export interface MyBidHistoryResponse {
  bidId: number
  itemId: number
  itemTitle: string
  representativeImageUrl: string | null
  bidAmount: number | null
  bidAt: string | null
  currentPrice: number | null
  itemStatus: string
  remainingSeconds: number | null
  highestBid: boolean
}

export interface MyBidItem {
  bidId: number
  productId: number
  productName: string
  imageUrl: string | null
  myBidAmount: number | null
  currentHighestBidAmount: number | null
  auctionStatus: ItemStatus | 'UNKNOWN'
  remainingSeconds: number | null
  isHighestBidder: boolean
  bidAt: string | null
}
