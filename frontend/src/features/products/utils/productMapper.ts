import { getAppConfig } from '../../../config/appConfig'
import type {
  ItemStatus,
  ItemSummaryResponse,
  ProductListItem,
  ProductListResult,
  SpringPage,
} from '../../../types/product'

const ITEM_STATUSES = new Set<ItemStatus>(['OPEN', 'SOLD', 'UNSOLD', 'CANCELLED'])

export function resolveProductImageUrl(value: string | null) {
  if (!value) return null
  if (value.startsWith('http://') || value.startsWith('https://')) return value
  if (!value.startsWith('/')) return null

  try {
    return new URL(value, getAppConfig().apiBaseUrl).toString()
  } catch {
    return null
  }
}

function mapItem(item: ItemSummaryResponse): ProductListItem {
  return {
    id: item.itemId,
    title: item.title,
    category: item.category,
    imageUrl: resolveProductImageUrl(item.representativeImageUrl),
    currentPrice: Number.isFinite(item.currentPrice) ? item.currentPrice : null,
    highestBidderNickname: item.highestBidderNickname,
    auctionEndAt: item.auctionEndAt,
    remainingSeconds: Number.isFinite(item.remainingSeconds) ? item.remainingSeconds : null,
    status: ITEM_STATUSES.has(item.status as ItemStatus) ? (item.status as ItemStatus) : 'UNKNOWN',
  }
}

export function mapProductPage(page: SpringPage<ItemSummaryResponse>): ProductListResult {
  return {
    items: page.content.map(mapItem),
    page: page.number,
    size: page.size,
    totalElements: page.totalElements,
    totalPages: page.totalPages,
    hasNext: !page.last,
  }
}
