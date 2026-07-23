import { useEffect, useState } from 'react'
import { Link, useLocation, useNavigate, useParams } from 'react-router-dom'
import { AppHeader } from '../components/common/AppHeader'
import { EmptyState, ErrorState } from '../components/ui'
import { EndAuctionDialog } from '../features/mypage/components/EndAuctionDialog'
import { useEndAuction } from '../features/mypage/hooks/useEndAuction'
import { BidDialog } from '../features/products/components/BidDialog'
import { ProductDescription } from '../features/products/components/ProductDescription'
import { ProductBidHistorySection } from '../features/products/components/ProductBidHistorySection'
import { ProductDetailSkeleton } from '../features/products/components/ProductDetailSkeleton'
import { ProductImageGallery } from '../features/products/components/ProductImageGallery'
import { ProductSummaryCard } from '../features/products/components/ProductSummaryCard'
import { useBidSubmission, type BidSubmissionResult } from '../features/products/hooks/useBidSubmission'
import { useProductPolling } from '../features/products/hooks/useProductPolling'
import { useAuth } from '../hooks/useAuth'
import { useToast } from '../hooks/useToast'

interface ProductDetailLocationState {
  from?: {
    pathname?: unknown
    search?: unknown
  }
}

function readProductListReturnPath(value: unknown) {
  if (!value || typeof value !== 'object') return '/products'
  const { from } = value as ProductDetailLocationState
  const returnablePaths = new Set(['/products', '/mypage/products', '/mypage/bids'])
  if (!from || typeof from.pathname !== 'string' || !returnablePaths.has(from.pathname)) return '/products'
  const search = typeof from.search === 'string' && from.search.startsWith('?') ? from.search : ''
  return `${from.pathname}${search}`
}

const listLinkClass =
  'inline-flex min-h-11 items-center rounded-block px-3 text-sm font-semibold text-brand-dark transition-colors duration-200 hover:bg-brand-light focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand/30'

export function ProductDetailPage() {
  const { productId } = useParams()
  const location = useLocation()
  const navigate = useNavigate()
  const { user, accessToken, isAuthenticated, isInitializing } = useAuth()
  const { showToast } = useToast()
  const parsedId = Number(productId)
  const validProductId = Number.isInteger(parsedId) && parsedId > 0 ? parsedId : null
  const [retryKey, setRetryKey] = useState(0)
  const [now, setNow] = useState(() => Date.now())
  const [bidDialogOpen, setBidDialogOpen] = useState(false)
  const [bidNotFound, setBidNotFound] = useState(false)
  const [bidRestriction, setBidRestriction] = useState<string | null>(null)
  const [endAuctionDialogOpen, setEndAuctionDialogOpen] = useState(false)
  const [endAuctionError, setEndAuctionError] = useState<string | null>(null)
  const { submit: submitEndAuction, endingProductId } = useEndAuction()
  const {
    product,
    isInitialLoading,
    isRefreshing,
    initialError,
    refreshError,
    refreshProduct,
    pausePolling,
    resumePolling,
  } = useProductPolling(validProductId, retryKey)
  const { submitBid, submitQuickBid, isSubmitting: isBidSubmitting } = useBidSubmission(
    validProductId,
    isAuthenticated && !isInitializing && Boolean(accessToken),
    pausePolling,
  )
  const listPath = readProductListReturnPath(location.state)
  const productStatus = product?.status
  const isSeller = Boolean(
    isAuthenticated &&
    user?.nickname &&
    product?.sellerNickname &&
    user.nickname === product.sellerNickname,
  )

  useEffect(() => {
    setNow(Date.now())
    if (productStatus !== 'OPEN') return undefined
    const timer = window.setInterval(() => setNow(Date.now()), 1_000)
    return () => window.clearInterval(timer)
  }, [productStatus])

  useEffect(() => {
    setBidNotFound(false)
    setBidDialogOpen(false)
    setBidRestriction(null)
    setEndAuctionDialogOpen(false)
    setEndAuctionError(null)
  }, [validProductId])

  const handleBidAction = (action: 'bid' | 'login' | 'disabled') => {
    if (action === 'login') {
      navigate('/login', { state: { from: location } })
      return
    }
    if (action === 'bid') setBidDialogOpen(true)
  }

  const handleEndAuctionClick = () => {
    setEndAuctionError(null)
    setEndAuctionDialogOpen(true)
  }

  const confirmEndAuction = async () => {
    if (!product || endingProductId !== null) return
    const result = await submitEndAuction(product.id)
    if (!result.ok && 'ignored' in result) return

    if (!result.ok) {
      if (result.error.shouldRefresh) await refreshProduct()
      if (result.error.shouldCloseDialog) {
        setEndAuctionDialogOpen(false)
        setEndAuctionError(null)
        showToast({ message: result.error.message, variant: 'warning' })
      } else {
        setEndAuctionError(result.error.message)
      }
      return
    }

    setEndAuctionDialogOpen(false)
    setEndAuctionError(null)
    await refreshProduct()
    const message =
      result.response.status === 'SOLD'
        ? '경매가 종료되고 낙찰자가 결정되었습니다.'
        : result.response.status === 'UNSOLD'
          ? '경매가 종료되었으며 유찰 처리되었습니다.'
          : '경매 종료 요청이 처리되었습니다.'
    showToast({ message, variant: 'success' })
  }

  const handleBidSubmit = async (amount?: number): Promise<BidSubmissionResult> => {
    const result = amount === undefined ? await submitQuickBid() : await submitBid(amount)

    if (!result.ok && 'ignored' in result) return result

    if (result.ok) {
      setBidDialogOpen(false)
      resumePolling()
      const refreshed = await refreshProduct()
      showToast({ message: '입찰이 정상적으로 접수되었습니다.', variant: 'success' })
      if (!refreshed) {
        showToast({
          message: '입찰은 접수되었지만 최신 정보를 불러오지 못했습니다. 잠시 후 자동으로 다시 확인합니다.',
          variant: 'warning',
        })
      }
      return result
    }

    const { error } = result
    if (error.shouldClose) setBidDialogOpen(false)
    if (error.kind === 'seller' || error.kind === 'ended' || error.kind === 'not-open') {
      setBidRestriction(error.message)
    }

    if (error.kind === 'not-found') {
      pausePolling()
      setBidNotFound(true)
    } else {
      resumePolling()
      if (error.shouldRefresh) await refreshProduct()
    }

    if (error.placement === 'toast') {
      showToast({
        message: error.message,
        variant: error.kind === 'server' ? 'error' : 'warning',
      })
    }

    if (error.kind === 'auth') {
      navigate('/login', { replace: true, state: { from: location } })
    }

    return result
  }

  return (
    <div className="min-h-screen bg-surface">
      <AppHeader />
      <main className="mx-auto max-w-7xl px-4 py-7 sm:px-8 sm:py-10 lg:px-10">
        <Link to={listPath} className={listLinkClass}>
          <span aria-hidden="true">←</span>
          <span className="ml-2">상품 목록으로</span>
        </Link>

        <div className="mt-6">
          {validProductId === null ? (
            <EmptyState
              title="잘못된 접근입니다."
              description="올바른 상품 번호가 아닙니다."
              action={<Link to="/products" className={listLinkClass}>상품 목록으로 이동</Link>}
            />
          ) : bidNotFound ? (
            <EmptyState
              title="상품을 찾을 수 없습니다."
              description="삭제되었거나 존재하지 않는 상품입니다."
              action={<Link to="/products" className={listLinkClass}>상품 목록으로 이동</Link>}
            />
          ) : isInitialLoading ? (
            <ProductDetailSkeleton />
          ) : initialError?.status === 404 ? (
            <EmptyState
              title="상품을 찾을 수 없습니다."
              description={initialError.message}
              action={<Link to="/products" className={listLinkClass}>상품 목록으로 이동</Link>}
            />
          ) : initialError ? (
            <div>
              <ErrorState
                title="상품 정보를 불러오지 못했습니다."
                description={initialError.message}
                onRetry={() => setRetryKey((current) => current + 1)}
              />
              <div className="text-center">
                <Link to="/products" className={listLinkClass}>상품 목록으로 이동</Link>
              </div>
            </div>
          ) : product ? (
            <>
              {refreshError && (
                <div className="mb-4 rounded-inline border border-caution/30 bg-[var(--ds-warning-light)] px-4 py-3 text-sm leading-6 text-ink" role="status">
                  최신 정보를 갱신하지 못했습니다. 기존 정보를 표시합니다. {refreshError}
                </div>
              )}

              <div className="grid gap-6 lg:grid-cols-[minmax(0,1.1fr)_minmax(360px,0.9fr)]">
                <ProductImageGallery imageUrls={product.imageUrls} productTitle={product.title} />
                <ProductSummaryCard
                  product={product}
                  now={now}
                  isAuthenticated={isAuthenticated}
                  isRefreshing={isRefreshing}
                  bidRestriction={bidRestriction}
                  isSeller={isSeller}
                  isEndingAuction={endingProductId === product.id}
                  onBidAction={handleBidAction}
                  onEndAuction={handleEndAuctionClick}
                />
              </div>

              <ProductDescription description={product.description} />
              <ProductBidHistorySection product={product} />
              <BidDialog
                open={bidDialogOpen}
                product={product}
                isSubmitting={isBidSubmitting}
                onSubmit={handleBidSubmit}
                onQuickSubmit={() => handleBidSubmit()}
                onClose={() => setBidDialogOpen(false)}
              />
              <EndAuctionDialog
                open={endAuctionDialogOpen}
                productTitle={product.title}
                currentPrice={product.currentPrice}
                hasHighestBidder={Boolean(product.highestBidderNickname)}
                isLoading={endingProductId === product.id}
                errorMessage={endAuctionError}
                onClose={() => {
                  if (endingProductId === null) {
                    setEndAuctionDialogOpen(false)
                    setEndAuctionError(null)
                  }
                }}
                onConfirm={confirmEndAuction}
              />
            </>
          ) : null}
        </div>
      </main>
    </div>
  )
}
