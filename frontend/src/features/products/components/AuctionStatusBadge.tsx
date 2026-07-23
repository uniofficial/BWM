import { Badge, type BadgeVariant } from '../../../components/ui'
import type { ProductListItem } from '../../../types/product'

const statusPresentation: Record<ProductListItem['status'], { label: string; variant: BadgeVariant }> = {
  OPEN: { label: '진행 중', variant: 'primary' },
  SOLD: { label: '낙찰', variant: 'success' },
  UNSOLD: { label: '유찰', variant: 'neutral' },
  CANCELLED: { label: '취소', variant: 'error' },
  UNKNOWN: { label: '상태 확인 필요', variant: 'warning' },
}

export function AuctionStatusBadge({ status }: { status: ProductListItem['status'] }) {
  const presentation = statusPresentation[status]
  return <Badge variant={presentation.variant}>{presentation.label}</Badge>
}
