import type { CreateBidRequest, CreateBidResponse } from '../types/bid'
import { apiClient } from './axiosInstance'

export const BID_ENDPOINTS = {
  create: (productId: number | string) => `/api/items/${productId}/bids`,
  quick: (productId: number | string) => `/api/items/${productId}/bids/quick`,
} as const

export async function createBid(
  productId: number,
  request: CreateBidRequest,
): Promise<CreateBidResponse> {
  const response = await apiClient.post<CreateBidResponse>(BID_ENDPOINTS.create(productId), request)
  return response.data
}

export async function createQuickBid(productId: number): Promise<CreateBidResponse> {
  const response = await apiClient.post<CreateBidResponse>(BID_ENDPOINTS.quick(productId))
  return response.data
}
