import { Badge, Button, Table, type TableColumn } from '../../../components/ui'
import type { AdminChargeRequestItem } from '../../../types/admin'
import { formatDateTime } from '../../products/utils/dateFormat'
import { formatPoints } from '../../mypage/utils/myPageFormat'
import { chargeStatusPresentation } from '../../mypage/utils/transactionLabel'

interface AdminChargeRequestsTableProps {
  rows: AdminChargeRequestItem[]
  processingId: number | null
  processingAction: 'approve' | 'reject' | null
  onApprove: (requestId: number) => void
  onReject: (requestId: number) => void
}

export function AdminChargeRequestsTable({
  rows,
  processingId,
  processingAction,
  onApprove,
  onReject,
}: AdminChargeRequestsTableProps) {
  const columns: TableColumn<AdminChargeRequestItem>[] = [
    { key: 'userEmail', header: '요청자', cell: (row) => row.userEmail },
    {
      key: 'amount',
      header: '요청 금액',
      cell: (row) => <strong className="text-ink">{formatPoints(row.amount)}</strong>,
    },
    {
      key: 'status',
      header: '처리 상태',
      cell: (row) => {
        const presentation = chargeStatusPresentation[row.status]
        return <Badge variant={presentation.variant}>{presentation.label}</Badge>
      },
    },
    { key: 'requestedAt', header: '요청 일시', cell: (row) => formatDateTime(row.requestedAt) },
    { key: 'processedAt', header: '처리 일시', cell: (row) => formatDateTime(row.processedAt) },
    {
      key: 'actions',
      header: '처리',
      align: 'right',
      cell: (row) => {
        if (row.status !== 'PENDING') return <span className="text-xs text-ink-muted">-</span>

        const isRowProcessing = processingId === row.id
        const isBusy = processingId !== null

        return (
          <div className="flex justify-end gap-2">
            <Button
              type="button"
              size="sm"
              variant="secondary"
              disabled={isBusy}
              isLoading={isRowProcessing && processingAction === 'approve'}
              loadingLabel="승인 중"
              onClick={() => onApprove(row.id)}
            >
              승인
            </Button>
            <Button
              type="button"
              size="sm"
              variant="danger"
              disabled={isBusy}
              isLoading={isRowProcessing && processingAction === 'reject'}
              loadingLabel="반려 중"
              onClick={() => onReject(row.id)}
            >
              반려
            </Button>
          </div>
        )
      },
    },
  ]

  return (
    <Table
      columns={columns}
      rows={rows}
      getRowKey={(row) => row.id}
      caption="관리자 충전 요청 목록"
      mobileMode="stack"
      emptyTitle="충전 요청이 없습니다."
    />
  )
}
