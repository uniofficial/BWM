import type { ChargeRequestStatus } from './wallet'

export interface AdminChargeRequestItem {
  id: number
  userId: number
  userEmail: string
  amount: number | null
  status: ChargeRequestStatus
  requestedAt: string | null
  processedAt: string | null
}
