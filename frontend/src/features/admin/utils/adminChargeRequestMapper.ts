import type { AdminChargeRequestItem } from '../../../types/admin'
import type { ChargeRequestStatus, WalletChargeRequestResponseDto } from '../../../types/wallet'

const CHARGE_STATUSES = new Set<ChargeRequestStatus>(['PENDING', 'APPROVED', 'REJECTED'])

export function mapAdminChargeRequests(
  responses: WalletChargeRequestResponseDto[],
): AdminChargeRequestItem[] {
  return responses.map((response) => ({
    id: response.chargeRequestId,
    userId: response.userId,
    userEmail: response.userEmail,
    amount: Number.isFinite(response.amount) ? Number(response.amount) : null,
    status: CHARGE_STATUSES.has(response.status as ChargeRequestStatus)
      ? (response.status as ChargeRequestStatus)
      : 'UNKNOWN',
    requestedAt: response.createdAt,
    processedAt: response.processedAt,
  }))
}
