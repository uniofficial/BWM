import { useState } from 'react'
import { Link, useLocation } from 'react-router-dom'
import { Card } from '../../../components/ui'
import type { ProductListItem } from '../../../types/product'
import { AuctionStatusBadge } from './AuctionStatusBadge'
import { formatPrice } from '../utils/priceFormat'
import { formatRemainingTime } from '../utils/remainingTime'

interface ProductCardProps {
  product: ProductListItem
  now: number
}

export function ProductCard({ product, now }: ProductCardProps) {
  const location = useLocation()
  const [imageFailed, setImageFailed] = useState(false)
  const showImage = Boolean(product.imageUrl) && !imageFailed

  return (
    <Link
      to={`/products/${product.id}`}
      state={{ from: location }}
      className="group block h-full rounded-content focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand/30"
      aria-label={`${product.title} 상품 상세 보기`}
    >
      <Card className="h-full overflow-hidden p-0 transition-colors duration-200 group-hover:border-brand-border">
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
            <div className="flex h-full items-center justify-center px-4 text-center text-sm text-ink-muted" role="img" aria-label={`${product.title} 상품 이미지 없음`}>
              이미지 준비 중
            </div>
          )}
        </div>

        <div className="flex min-h-48 flex-col p-4">
          <div className="flex items-start justify-between gap-3">
            <AuctionStatusBadge status={product.status} />
            <span className="min-w-0 truncate text-xs text-ink-muted">{product.category}</span>
          </div>

          <h2 className="mt-3 line-clamp-2 min-h-12 text-base font-semibold leading-6 text-ink">{product.title}</h2>

          <div className="mt-auto pt-4">
            <p className="text-xs text-ink-muted">현재가</p>
            <p className="mt-1 break-words text-xl font-bold leading-tight text-ink">{formatPrice(product.currentPrice)}</p>
            <div className="mt-3 flex items-center justify-between gap-3 border-t border-line pt-3 text-xs text-ink-secondary">
              <span className="truncate">{product.highestBidderNickname ? '최고 입찰 있음' : '첫 입찰 대기'}</span>
              <span className="shrink-0 font-medium text-brand-dark">{formatRemainingTime(product, now)}</span>
            </div>
          </div>
        </div>
      </Card>
    </Link>
  )
}
