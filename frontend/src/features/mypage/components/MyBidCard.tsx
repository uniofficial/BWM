import { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { Badge, Card } from '../../../components/ui'
import type { MyBidItem } from '../../../types/myBid'
import { AuctionStatusBadge } from '../../products/components/AuctionStatusBadge'
import { formatDateTime } from '../../products/utils/dateFormat'
import { formatDuration, formatPoints } from '../utils/myPageFormat'

function BidResultBadge({ bid }: { bid: MyBidItem }) {
  if (bid.auctionStatus === 'OPEN') {
    return bid.isHighestBidder
      ? <Badge variant="primary">최고 입찰 중</Badge>
      : <Badge variant="neutral">다른 입찰자가 앞서고 있습니다</Badge>
  }
  if (bid.auctionStatus === 'SOLD') {
    return bid.isHighestBidder
      ? <Badge variant="success">낙찰</Badge>
      : <Badge variant="neutral">미낙찰</Badge>
  }
  if (bid.auctionStatus === 'UNSOLD') return <Badge variant="neutral">유찰</Badge>
  if (bid.auctionStatus === 'CANCELLED') return <Badge variant="error">경매 취소</Badge>
  return <Badge variant="warning">결과 확인 필요</Badge>
}

export function MyBidCard({ bid }: { bid: MyBidItem }) {
  const location = useLocation()
  const [imageFailed, setImageFailed] = useState(false)
  const showImage = Boolean(bid.imageUrl) && !imageFailed

  return (
    <Link
      to={`/products/${bid.productId}`}
      state={{ from: location }}
      className="group block h-full rounded-content focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand/30"
      aria-label={`${bid.productName} 상품 상세 보기`}
    >
      <Card className="flex h-full flex-col overflow-hidden p-0 transition-colors duration-200 group-hover:border-brand-border">
        <div className="aspect-[4/3] overflow-hidden bg-surface-secondary">
          {showImage ? (
            <img src={bid.imageUrl || undefined} alt={`${bid.productName} 상품 이미지`} loading="lazy" className="h-full w-full object-cover" onError={() => setImageFailed(true)} />
          ) : (
            <div className="flex h-full items-center justify-center px-4 text-sm text-ink-muted" role="img" aria-label={`${bid.productName} 상품 이미지 없음`}>이미지 준비 중</div>
          )}
        </div>
        <div className="flex flex-1 flex-col p-4">
          <div className="flex flex-wrap items-center justify-between gap-2">
            <AuctionStatusBadge status={bid.auctionStatus} />
            <BidResultBadge bid={bid} />
          </div>
          <h3 className="mt-3 line-clamp-2 min-h-12 text-base font-semibold leading-6 text-ink">{bid.productName}</h3>
          <dl className="mt-4 grid gap-2 text-sm">
            <div className="flex justify-between gap-3"><dt className="text-ink-muted">내 입찰가</dt><dd className="text-right font-bold text-brand-dark">{formatPoints(bid.myBidAmount)}</dd></div>
            <div className="flex justify-between gap-3"><dt className="text-ink-muted">현재 최고가</dt><dd className="text-right font-semibold text-ink">{formatPoints(bid.currentHighestBidAmount)}</dd></div>
            <div className="flex justify-between gap-3"><dt className="text-ink-muted">남은 시간</dt><dd className="text-right text-ink-secondary">{formatDuration(bid.remainingSeconds, bid.auctionStatus === 'OPEN')}</dd></div>
            <div className="flex justify-between gap-3"><dt className="text-ink-muted">최근 입찰</dt><dd className="text-right text-ink-secondary">{formatDateTime(bid.bidAt)}</dd></div>
          </dl>
          <span className="mt-5 inline-flex min-h-11 items-center justify-center rounded-block border border-line bg-surface text-sm font-semibold text-ink group-hover:bg-brand-light">
            상품 보기
          </span>
        </div>
      </Card>
    </Link>
  )
}
