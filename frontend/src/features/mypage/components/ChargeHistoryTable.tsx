import { Badge, Table, type TableColumn } from '../../../components/ui'
import type { ChargeRequestHistoryItem } from '../../../types/wallet'
import { formatDateTime } from '../../products/utils/dateFormat'
import { formatPoints } from '../utils/myPageFormat'
import { chargeStatusPresentation } from '../utils/transactionLabel'

const columns: TableColumn<ChargeRequestHistoryItem>[] = [
  { key: 'amount', header: '요청 금액', cell: (row) => <strong className="text-ink">{formatPoints(row.amount)}</strong> },
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
]

export function ChargeHistoryTable({ rows }: { rows: ChargeRequestHistoryItem[] }) {
  return (
    <Table
      columns={columns}
      rows={rows}
      getRowKey={(row) => row.id}
      caption="포인트 충전 요청 내역"
      mobileMode="stack"
      emptyTitle="충전 요청 내역이 없습니다."
    />
  )
}
