import { Link } from 'react-router-dom'
import { Card, Skeleton } from '../../../components/ui'
import type { WalletSummary } from '../../../types/wallet'
import { formatDateTime } from '../../products/utils/dateFormat'
import { formatPoints } from '../utils/myPageFormat'

interface WalletSummaryCardProps {
  wallet: WalletSummary | null
  isLoading?: boolean
  compact?: boolean
}

const actionLinkClass =
  'inline-flex min-h-11 items-center justify-center rounded-block px-4 text-sm font-semibold transition-colors duration-200 focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand/30'

export function WalletSummaryCard({ wallet, isLoading = false, compact = false }: WalletSummaryCardProps) {
  if (isLoading) {
    return (
      <Card aria-busy="true">
        <Skeleton variant="balance" />
      </Card>
    )
  }

  return (
    <Card variant="highlight" className={`border-l-4 border-l-brand ${compact ? 'p-4' : ''}`}>
      <p className="text-sm font-semibold text-brand-dark">현재 보유 포인트</p>
      <p className="mt-2 break-words text-3xl font-bold text-ink">
        {wallet?.isValid ? formatPoints(wallet.balance) : '잔액 정보를 확인할 수 없습니다.'}
      </p>
      <p className="mt-2 text-xs text-ink-secondary">
        {wallet?.updatedAt ? `마지막 갱신 ${formatDateTime(wallet.updatedAt)}` : '마지막 갱신 시간 없음'}
      </p>
      {!compact && (
        <div className="mt-6 flex flex-wrap gap-2">
          <Link to="/mypage/wallet/charge" className={`${actionLinkClass} bg-brand text-white hover:bg-brand-hover active:bg-brand-active`}>
            충전 요청
          </Link>
          <Link to="/mypage/wallet/charges" className={`${actionLinkClass} border border-line bg-surface text-ink hover:border-brand-border hover:bg-brand-light`}>
            요청 내역
          </Link>
          <Link to="/mypage/wallet/transactions" className={`${actionLinkClass} border border-line bg-surface text-ink hover:border-brand-border hover:bg-brand-light`}>
            거래 내역
          </Link>
        </div>
      )}
    </Card>
  )
}
