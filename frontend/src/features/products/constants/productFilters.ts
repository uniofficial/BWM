import type { SelectOption } from '../../../components/ui'
import type { ProductSortOption, ProductStatusFilter } from '../../../types/product'

export const PRODUCT_PAGE_SIZE = 12
export const DEFAULT_PRODUCT_SORT: ProductSortOption = 'LATEST'

export const STATUS_OPTIONS: SelectOption[] = [
  { value: 'ALL', label: '전체 상태' },
  { value: 'OPEN', label: '진행 중' },
  { value: 'SOLD', label: '낙찰' },
  { value: 'UNSOLD', label: '유찰' },
  { value: 'CANCELLED', label: '취소' },
]

export const SORT_OPTIONS: SelectOption[] = [
  { value: 'LATEST', label: '최신 등록순' },
  { value: 'ENDING_SOON', label: '마감 임박순' },
  { value: 'PRICE_ASC', label: '낮은 가격순' },
  { value: 'PRICE_DESC', label: '높은 가격순' },
]

export const PRODUCT_SORT_PARAMS: Record<ProductSortOption, string> = {
  LATEST: 'createdAt,desc',
  ENDING_SOON: 'auctionEndAt,asc',
  PRICE_ASC: 'currentPrice,asc',
  PRICE_DESC: 'currentPrice,desc',
}

export const PRODUCT_STATUS_VALUES = new Set<ProductStatusFilter>([
  'ALL',
  'OPEN',
  'SOLD',
  'UNSOLD',
  'CANCELLED',
])

export const PRODUCT_SORT_VALUES = new Set<ProductSortOption>([
  'LATEST',
  'ENDING_SOON',
  'PRICE_ASC',
  'PRICE_DESC',
])
