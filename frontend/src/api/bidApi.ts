import type { CreateBidRequest, CreateBidResponse } from '../types/bid'
import { apiClient } from './axiosInstance'

export const BID_ENDPOINTS = {
  create: (productId: number | string) => `/api/items/${productId}/bids`,
} as const

export async function createBid(
  productId: number,
  request: CreateBidRequest,
): Promise<CreateBidResponse> {
  const response = await apiClient.post<CreateBidResponse>(BID_ENDPOINTS.create(productId), request)
  return response.data
}

