import type { GenericAbortSignal } from 'axios'
import { mapMyProductPage } from '../features/mypage/utils/myProductMapper'
import type { MyProductItem, MyProductResponse } from '../types/myProduct'
import type { SpringPage } from '../types/product'
import { apiClient } from './axiosInstance'

export const MY_PRODUCT_ENDPOINTS = {
  list: '/api/users/me/items',
} as const

export async function getMyProducts(signal?: GenericAbortSignal): Promise<MyProductItem[]> {
  const response = await apiClient.get<SpringPage<MyProductResponse>>(MY_PRODUCT_ENDPOINTS.list, {
    signal,
    params: { page: 0, size: 100, sort: 'createdAt,desc' },
  })
  return mapMyProductPage(response.data)
}
