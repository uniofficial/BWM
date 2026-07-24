import {
  DEFAULT_PRODUCT_SORT,
  PRODUCT_SORT_VALUES,
  PRODUCT_STATUS_VALUES,
} from '../constants/productFilters'
import type { ProductListQuery, ProductSortOption, ProductStatusFilter } from '../../../types/product'

function readPage(value: string | null) {
  const parsed = Number(value)
  return Number.isInteger(parsed) && parsed > 0 ? parsed : 1
}

export function readProductQuery(searchParams: URLSearchParams): ProductListQuery {
  const statusValue = searchParams.get('status') || 'OPEN'
  const sortValue = searchParams.get('sort') || DEFAULT_PRODUCT_SORT

  return {
    keyword: searchParams.get('keyword')?.trim() || '',
    category: searchParams.get('category')?.trim() || '',
    status: PRODUCT_STATUS_VALUES.has(statusValue as ProductStatusFilter)
      ? (statusValue as ProductStatusFilter)
      : 'ALL',
    sort: PRODUCT_SORT_VALUES.has(sortValue as ProductSortOption)
      ? (sortValue as ProductSortOption)
      : DEFAULT_PRODUCT_SORT,
    page: readPage(searchParams.get('page')),
  }
}

export function hasActiveProductFilters(query: ProductListQuery) {
  return Boolean(query.keyword || query.category || query.status !== 'OPEN')
}
