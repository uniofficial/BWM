import { Button, Card, Spinner } from '../../../components/ui'
import type { ProductDetail } from '../../../types/product'
import { AuctionStatusBadge } from './AuctionStatusBadge'
import { getAuctionAvailability } from '../utils/auctionAvailability'
import { formatDateTime } from '../utils/dateFormat'
import { formatDetailRemainingTime } from '../utils/detailTime'
import { formatPrice } from '../utils/priceFormat'

interface ProductSummaryCardProps {
  product: ProductDetail
  now: number
  isAuthenticated: boolean
  isRefreshing: boolean
  bidRestriction?: string | null
  onBidAction: (action: 'bid' | 'login' | 'disabled') => void
}

export function ProductSummaryCard({
  product,
  now,
  isAuthenticated,
  isRefreshing,
  bidRestriction,
  onBidAction,
}: ProductSummaryCardProps) {
  const availability = bidRestriction
    ? {
        action: 'disabled' as const,
        canOpenBidDialog: false,
        buttonLabel: '입찰 불가',
        message: bidRestriction,
      }
    : getAuctionAvailability(product, isAuthenticated, now)
  const hasBids = product.bidCount > 0
  const displayPrice = hasBids ? product.currentPrice : product.startPrice ?? product.currentPrice

  return (
    <Card className="flex h-full flex-col p-6 sm:p-7">
      <div className="flex flex-wrap items-center justify-between gap-3">
        <AuctionStatusBadge status={product.status} />
        {isRefreshing && (
          <span className="inline-flex items-center gap-2 text-xs text-ink-muted" role="status">
            <Spinner size="sm" label="상품 정보 갱신 중" />
            갱신 중
          </span>
        )}
      </div>

      <p className="mt-4 text-sm font-medium text-brand-dark">{product.category}</p>
      <h1 className="mt-2 break-words text-3xl font-bold leading-tight text-ink">{product.title}</h1>

      <div className="mt-7 border-y border-line py-5">
        <p className="text-sm text-ink-secondary">{hasBids ? '현재 최고 입찰가' : '현재 입찰 없음 · 시작가'}</p>
        <p className="mt-2 break-words text-3xl font-bold text-ink">{formatPrice(displayPrice)}</p>
        <p className="mt-3 text-base font-semibold text-brand-dark">{formatDetailRemainingTime(product, now)}</p>
      </div>

      <dl className="grid gap-3 py-5 text-sm">
        <div className="flex items-start justify-between gap-4">
          <dt className="text-ink-muted">입찰 수</dt>
          <dd className="font-medium text-ink">{product.bidCount.toLocaleString('ko-KR')}회</dd>
        </div>
        <div className="flex items-start justify-between gap-4">
          <dt className="text-ink-muted">판매자</dt>
          <dd className="break-words text-right font-medium text-ink">{product.sellerNickname || '판매자 정보 없음'}</dd>
        </div>
        <div className="flex items-start justify-between gap-4">
          <dt className="text-ink-muted">현재 최고 입찰자</dt>
          <dd className="break-words text-right font-medium text-ink">{product.highestBidderNickname || '아직 없음'}</dd>
        </div>
        <div className="flex items-start justify-between gap-4">
          <dt className="text-ink-muted">마감 일시</dt>
          <dd className="text-right font-medium text-ink">{formatDateTime(product.auctionEndAt)}</dd>
        </div>
      </dl>

      <div className="mt-auto border-t border-line pt-5">
        <Button
          type="button"
          size="lg"
          className="w-full"
          disabled={availability.action === 'disabled'}
          onClick={() => onBidAction(availability.action)}
        >
          {availability.buttonLabel}
        </Button>
        <p className="mt-3 text-center text-xs leading-5 text-ink-muted">{availability.message}</p>
      </div>
    </Card>
  )
}
