import { resolveProductImageUrl } from '../../products/utils/productMapper'
import type { MyProductItem, MyProductResponse, MyProductTab } from '../../../types/myProduct'
import type { ItemStatus, SpringPage } from '../../../types/product'

const ITEM_STATUSES = new Set<ItemStatus>(['OPEN', 'SOLD', 'UNSOLD', 'CANCELLED'])

export function mapMyProductPage(page: SpringPage<MyProductResponse>): MyProductItem[] {
  return page.content.map((item) => ({
    id: item.itemId,
    title: item.title,
    category: item.category,
    imageUrl: resolveProductImageUrl(item.representativeImageUrl),
    currentPrice: Number.isFinite(item.currentPrice) ? item.currentPrice : null,
    highestBidderNickname: item.highestBidderNickname,
    auctionEndAt: item.auctionEndAt,
    remainingSeconds: Number.isFinite(item.remainingSeconds) ? item.remainingSeconds : null,
    status: ITEM_STATUSES.has(item.status as ItemStatus) ? (item.status as ItemStatus) : 'UNKNOWN',
  }))
}

export function filterMyProducts(items: MyProductItem[], tab: MyProductTab) {
  return items.filter((item) => (tab === 'active' ? item.status === 'OPEN' : item.status !== 'OPEN'))
}
