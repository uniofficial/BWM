export interface CreateBidRequest {
  bidAmount: number
}

export interface CreateBidResponse {
  bidId: number
  itemId: number
  bidderNickname: string
  bidAmount: number
  bidAt: string
}

