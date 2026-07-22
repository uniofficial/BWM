import type { GenericAbortSignal } from 'axios'
import type {
  ItemCreateRequest,
  ItemCreateResponse,
  ItemDetailResponse,
  ItemImageUploadResponse,
  ItemSummaryResponse,
  ProductDetail,
  ProductListQuery,
  ProductListResult,
  SpringPage,
} from '../types/product'
import { PRODUCT_PAGE_SIZE, PRODUCT_SORT_PARAMS } from '../features/products/constants/productFilters'
import { mapProductPage } from '../features/products/utils/productMapper'
import { mapProductDetail } from '../features/products/utils/productDetailMapper'
import { createProductImageFormData } from '../features/products/utils/createProductFormData'
import { ApiError } from './apiError'
import { apiClient } from './axiosInstance'
import type { EndAuctionResponse } from '../types/myProduct'

export const PRODUCT_ENDPOINTS = {
  list: '/api/items',
  search: '/api/items/search',
  create: '/api/items',
  detail: (productId: number | string) => `/api/items/${productId}`,
  images: (productId: number | string) => `/api/items/${productId}/images`,
  close: (productId: number | string) => `/api/items/${productId}/close`,
} as const

export async function getProducts(query: ProductListQuery, signal?: GenericAbortSignal): Promise<ProductListResult> {
  const response = await apiClient.get<SpringPage<ItemSummaryResponse>>(PRODUCT_ENDPOINTS.search, {
    signal,
    params: {
      keyword: query.keyword || undefined,
      category: query.category || undefined,
      status: query.status === 'ALL' ? undefined : query.status,
      page: query.page - 1,
      size: PRODUCT_PAGE_SIZE,
      sort: PRODUCT_SORT_PARAMS[query.sort],
    },
  })

  return mapProductPage(response.data)
}

export async function endAuction(productId: number): Promise<EndAuctionResponse> {
  const response = await apiClient.post<EndAuctionResponse>(PRODUCT_ENDPOINTS.close(productId))
  if (
    !response.data ||
    response.data.itemId !== productId ||
    typeof response.data.status !== 'string'
  ) {
    throw new ApiError('경매 종료 응답을 확인할 수 없습니다.', { kind: 'unknown' })
  }
  return response.data
}

export async function getProductDetail(productId: number, signal?: GenericAbortSignal): Promise<ProductDetail> {
  const response = await apiClient.get<ItemDetailResponse>(PRODUCT_ENDPOINTS.detail(productId), { signal })
  return mapProductDetail(response.data)
}

export async function createProduct(request: ItemCreateRequest): Promise<ItemCreateResponse> {
  const response = await apiClient.post<ItemCreateResponse>(PRODUCT_ENDPOINTS.create, request)
  if (!Number.isInteger(response.data.itemId) || response.data.itemId <= 0) {
    throw new ApiError('상품 등록 응답에서 상품 번호를 확인할 수 없습니다.', { kind: 'unknown' })
  }
  return response.data
}

export async function uploadProductImages(
  productId: number,
  images: File[],
): Promise<ItemImageUploadResponse[]> {
  const formData = createProductImageFormData(images)
  const response = await apiClient.post<ItemImageUploadResponse[]>(
    PRODUCT_ENDPOINTS.images(productId),
    formData,
  )
  return response.data
}
