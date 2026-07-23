import type { GenericAbortSignal } from 'axios'
import { mapMyBids } from '../features/mypage/utils/myBidMapper'
import type { MyBidHistoryResponse, MyBidItem } from '../types/myBid'
import { apiClient } from './axiosInstance'

export const MY_BID_ENDPOINTS = {
  list: '/api/users/me/bids',
} as const

export async function getMyBids(signal?: GenericAbortSignal): Promise<MyBidItem[]> {
  const response = await apiClient.get<MyBidHistoryResponse[]>(MY_BID_ENDPOINTS.list, { signal })
  return mapMyBids(response.data)
}
