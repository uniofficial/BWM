export type ItemStatus = 'OPEN' | 'SOLD' | 'UNSOLD' | 'CANCELLED'
export type ProductStatusFilter = 'ALL' | ItemStatus
export type ProductSortOption = 'LATEST' | 'ENDING_SOON' | 'PRICE_ASC' | 'PRICE_DESC'

export interface ItemSummaryResponse {
  itemId: number
  title: string
  category: string
  representativeImageUrl: string | null
  currentPrice: number | null
  highestBidderNickname: string | null
  auctionEndAt: string | null
  remainingSeconds: number | null
  status: string
  createdAt: string
}

export interface SpringPage<T> {
  content: T[]
  number: number
  size: number
  totalElements: number
  totalPages: number
  first: boolean
  last: boolean
  empty: boolean
  numberOfElements: number
}

export interface ProductListItem {
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

export interface ProductListQuery {
  keyword: string
  category: string
  status: ProductStatusFilter
  sort: ProductSortOption
  page: number
}

export interface ProductListResult {
  items: ProductListItem[]
  page: number
  size: number
  totalElements: number
  totalPages: number
  hasNext: boolean
}

export interface ItemBidHistoryResponse {
  bidId: number
  bidderNickname: string
  bidAmount: number
  bidAt: string
}

export interface ItemDetailResponse {
  itemId: number
  sellerNickname: string | null
  title: string
  category: string
  startPrice: number | null
  currentPrice: number | null
  auctionEndAt: string | null
  status: string
  createdAt: string | null
  description: string | null
  highestBidderNickname: string | null
  imageUrls: string[] | null
  bidHistory: ItemBidHistoryResponse[] | null
}

export interface ProductBidHistoryItem {
  id: number
  bidderNickname: string
  amount: number
  bidAt: string
}

export interface ProductDetail {
  id: number
  sellerNickname: string | null
  title: string
  category: string
  startPrice: number | null
  currentPrice: number | null
  minimumBidAmount: number | null
  auctionEndAt: string | null
  status: ItemStatus | 'UNKNOWN'
  createdAt: string | null
  description: string
  highestBidderNickname: string | null
  imageUrls: string[]
  bidHistory: ProductBidHistoryItem[]
  bidCount: number
}

export interface ItemCreateRequest {
  title: string
  category: string
  startPrice: number
  auctionEndAt: string
  description: string | null
}

export interface ItemCreateResponse {
  itemId: number
  sellerNickname: string
  title: string
  category: string
  startPrice: number
  currentPrice: number
  auctionEndAt: string
  status: ItemStatus
  createdAt: string
  description: string | null
}

export interface ItemImageUploadResponse {
  imageId: number
  itemId: number
  imageUrl: string
  isRepresentative: boolean
  createdAt: string
}

export interface ProductCreateFormState {
  title: string
  category: string
  startPrice: string
  auctionEndAt: string
  description: string
}

export interface ProductCreateFormErrors {
  title?: string
  category?: string
  startPrice?: string
  auctionEndAt?: string
  description?: string
  images?: string
  form?: string
}
