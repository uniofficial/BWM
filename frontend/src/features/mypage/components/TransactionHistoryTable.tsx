import { Link } from 'react-router-dom'
import { Table, type TableColumn } from '../../../components/ui'
import type { WalletTransaction } from '../../../types/wallet'
import { cx } from '../../../utils/cx'
import { formatDateTime } from '../../products/utils/dateFormat'
import { formatPoints } from '../utils/myPageFormat'
import { getTransactionSign, transactionTypeLabels } from '../utils/transactionLabel'

function TransactionAmount({ row }: { row: WalletTransaction }) {
  if (row.amount === null || !Number.isFinite(row.amount)) return <span>정보 없음</span>
  const sign = getTransactionSign(row.type)
  return (
    <span className={cx('font-semibold', sign === '+' && 'text-positive', sign === '-' && 'text-danger')}>
      <span className="sr-only">{sign === '+' ? '증가 ' : sign === '-' ? '감소 ' : ''}</span>
      {sign}{formatPoints(row.amount)}
    </span>
  )
}

const columns: TableColumn<WalletTransaction>[] = [
  { key: 'type', header: '거래 유형', cell: (row) => transactionTypeLabels[row.type] },
  { key: 'amount', header: '거래 금액', cell: (row) => <TransactionAmount row={row} /> },
  { key: 'createdAt', header: '거래 일시', cell: (row) => formatDateTime(row.createdAt) },
  { key: 'balanceAfter', header: '거래 후 잔액', cell: (row) => formatPoints(row.balanceAfter) },
  {
    key: 'product',
    header: '관련 상품',
    cell: (row) => row.productId ? <Link className="font-semibold text-brand-dark hover:underline" to={`/products/${row.productId}`}>상품 보기</Link> : '-',
  },
]

export function TransactionHistoryTable({ rows }: { rows: WalletTransaction[] }) {
  return (
    <Table
      columns={columns}
      rows={rows}
      getRowKey={(row) => row.id}
      caption="지갑 거래 내역"
      mobileMode="stack"
      emptyTitle="지갑 거래 내역이 없습니다."
    />
  )
}
