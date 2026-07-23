import type { ItemStatus, ItemSummaryResponse } from './product'

export type MyProductTab = 'active' | 'ended'

export interface MyProductItem {
  id: number
  title: string
  category: string
  imageUrl: string | null
  currentPrice: number | null
  highestBidderNickname: string | null
  auctionEndAt: string | null
  remainingSeconds: number | null
  status: ItemStatus | 'UNKNOWN'
}

export type MyProductResponse = ItemSummaryResponse

export type AuctionEndResult = 'SOLD' | 'UNSOLD' | 'UNKNOWN'

export interface EndAuctionResponse {
  itemId: number
  status: string
  highestBidderNickname: string | null
  currentPrice: number | null
}
