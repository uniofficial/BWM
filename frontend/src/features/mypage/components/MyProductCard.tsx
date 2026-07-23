import { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { Badge, Button, Card } from '../../../components/ui'
import { cx } from '../../../utils/cx'
import { AuctionStatusBadge } from '../../products/components/AuctionStatusBadge'
import type { MyProductItem } from '../../../types/myProduct'
import { formatDuration, formatPoints } from '../utils/myPageFormat'
import { isAuctionImminent } from '../../products/utils/remainingTime'

interface MyProductCardProps {
  product: MyProductItem
  now: number
  isEnding: boolean
  onEndAuction: (product: MyProductItem) => void
}

function ResultBadge({ status }: { status: MyProductItem['status'] }) {
  if (status === 'SOLD') return <Badge variant="success">낙찰 완료</Badge>
  if (status === 'UNSOLD') return <Badge variant="neutral">유찰</Badge>
  if (status === 'CANCELLED') return <Badge variant="error">경매 취소</Badge>
  if (status === 'UNKNOWN') return <Badge variant="warning">낙찰 정보 확인 필요</Badge>
  return null
}

export function MyProductCard({ product, now, isEnding, onEndAuction }: MyProductCardProps) {
  const location = useLocation()
  const [imageFailed, setImageFailed] = useState(false)
  const showImage = Boolean(product.imageUrl) && !imageFailed
  const endTimestamp = product.auctionEndAt ? Date.parse(product.auctionEndAt) : Number.NaN
  const remainingSeconds = Number.isFinite(endTimestamp)
    ? Math.max(0, Math.ceil((endTimestamp - now) / 1_000))
    : product.remainingSeconds
  const canEndAuction = product.status === 'OPEN'
  const isImminent = isAuctionImminent(product.status, product.auctionEndAt, now, product.remainingSeconds)

  return (
    <Card className="flex h-full flex-col overflow-hidden p-0">
      <div className="aspect-[4/3] overflow-hidden bg-surface-secondary">
        {showImage ? (
          <img
            src={product.imageUrl || undefined}
            alt={`${product.title} 상품 이미지`}
            loading="lazy"
            className="h-full w-full object-cover"
            onError={() => setImageFailed(true)}
          />
        ) : (
          <div className="flex h-full items-center justify-center px-4 text-sm text-ink-muted" role="img" aria-label={`${product.title} 상품 이미지 없음`}>
            이미지 준비 중
          </div>
        )}
      </div>
      <div className="flex flex-1 flex-col p-4">
        <div className="flex flex-wrap items-center justify-between gap-2">
          <AuctionStatusBadge status={product.status} />
          <ResultBadge status={product.status} />
        </div>
        <h3 className="mt-3 line-clamp-2 min-h-12 text-base font-semibold leading-6 text-ink">{product.title}</h3>
        <dl className="mt-4 grid gap-2 text-sm">
          <div className="flex justify-between gap-3"><dt className="text-ink-muted">현재가</dt><dd className="text-right font-semibold text-ink">{formatPoints(product.currentPrice)}</dd></div>
          <div className="flex justify-between gap-3"><dt className="text-ink-muted">입찰 상태</dt><dd className="text-right text-ink-secondary">{product.highestBidderNickname ? '최고 입찰 있음' : '입찰 없음'}</dd></div>
          <div className="flex justify-between gap-3">
            <dt className="text-ink-muted">남은 시간</dt>
            <dd className={cx('text-right', isImminent ? 'font-semibold text-danger' : 'text-ink-secondary')}>
              {formatDuration(remainingSeconds, product.status === 'OPEN')}
            </dd>
          </div>
        </dl>
        <div className="mt-auto grid gap-2 pt-5 sm:grid-cols-2">
          <Link
            to={`/products/${product.id}`}
            state={{ from: location }}
            className="inline-flex min-h-11 items-center justify-center rounded-block border border-line bg-surface px-3 text-sm font-semibold text-ink transition-colors duration-200 hover:border-brand-border hover:bg-brand-light focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand/30"
          >
            상품 보기
          </Link>
          {canEndAuction && (
            <Button
              type="button"
              variant="danger"
              disabled={isEnding}
              isLoading={isEnding}
              loadingLabel="종료 중..."
              onClick={() => onEndAuction(product)}
            >
              경매 종료
            </Button>
          )}
        </div>
      </div>
    </Card>
  )
}
