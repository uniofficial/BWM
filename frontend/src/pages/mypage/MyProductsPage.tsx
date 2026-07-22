import { useEffect, useState } from 'react'
import { Link, useSearchParams } from 'react-router-dom'
import { EmptyState, ErrorState, Tabs } from '../../components/ui'
import { EndAuctionDialog } from '../../features/mypage/components/EndAuctionDialog'
import { MyPageListSkeleton } from '../../features/mypage/components/MyPageListSkeleton'
import { MyPageSectionHeader } from '../../features/mypage/components/MyPageSectionHeader'
import { MyProductList } from '../../features/mypage/components/MyProductList'
import { filterMyProducts } from '../../features/mypage/utils/myProductMapper'
import { useMyProducts } from '../../features/mypage/hooks/useMyProducts'
import { useEndAuction } from '../../features/mypage/hooks/useEndAuction'
import { useToast } from '../../hooks/useToast'
import type { MyProductItem, MyProductTab } from '../../types/myProduct'

const tabs = [
  { value: 'active', label: '진행 중', panelId: 'my-products-panel' },
  { value: 'ended', label: '종료', panelId: 'my-products-panel' },
]

const linkClass = 'inline-flex min-h-11 items-center justify-center rounded-card bg-brand px-4 text-sm font-semibold text-white transition-colors duration-200 hover:bg-brand-hover focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand/30'

export function MyProductsPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const tab: MyProductTab = searchParams.get('status') === 'ended' ? 'ended' : 'active'
  const { products, isLoading, error, refresh } = useMyProducts()
  const { submit: submitEndAuction, endingProductId } = useEndAuction()
  const { showToast } = useToast()
  const [selectedProduct, setSelectedProduct] = useState<MyProductItem | null>(null)
  const [dialogError, setDialogError] = useState<string | null>(null)
  const [now, setNow] = useState(() => Date.now())

  useEffect(() => {
    const timer = window.setInterval(() => setNow(Date.now()), 1_000)
    return () => window.clearInterval(timer)
  }, [])

  const filtered = filterMyProducts(products || [], tab)
  const changeTab = (value: string) => {
    const next = value === 'ended' ? 'ended' : 'active'
    setSearchParams(next === 'active' ? {} : { status: next })
    setDialogError(null)
  }

  const openEndAuctionDialog = (product: MyProductItem) => {
    setDialogError(null)
    setSelectedProduct(product)
  }

  const confirmEndAuction = async () => {
    if (!selectedProduct || endingProductId !== null) return
    const result = await submitEndAuction(selectedProduct.id)
    if (!result.ok && 'ignored' in result) return

    if (!result.ok) {
      if (result.error.shouldRefresh) refresh()
      if (result.error.shouldCloseDialog) {
        setSelectedProduct(null)
        setDialogError(null)
        showToast({ message: result.error.message, variant: 'warning' })
      } else {
        setDialogError(result.error.message)
      }
      return
    }

    setSelectedProduct(null)
    setDialogError(null)
    refresh()
    const message = result.response.status === 'SOLD'
      ? '경매가 종료되고 낙찰자가 결정되었습니다.'
      : result.response.status === 'UNSOLD'
        ? '경매가 종료되었으며 유찰 처리되었습니다.'
        : '경매 종료 요청이 처리되었습니다. 최신 결과를 확인합니다.'
    showToast({ message, variant: 'success' })
  }

  return (
    <section aria-labelledby="my-products-title">
      <MyPageSectionHeader id="my-products-title" title="내 상품" description="등록한 상품의 경매 상태를 확인합니다." action={<Link to="/products/new" className={linkClass}>상품 등록하기</Link>} />
      <Tabs items={tabs} value={tab} onChange={changeTab} ariaLabel="내 상품 상태" />
      <div id="my-products-panel" role="tabpanel" className="mt-5">
        {isLoading && products === null ? <MyPageListSkeleton cards /> : error && products === null ? (
          <ErrorState title="내 상품을 불러오지 못했습니다." description={error} onRetry={refresh} />
        ) : filtered.length === 0 ? (
          <EmptyState title={tab === 'active' ? '진행 중인 상품이 없습니다.' : '종료된 상품이 없습니다.'} action={tab === 'active' ? <Link to="/products/new" className={linkClass}>상품 등록하기</Link> : undefined} />
        ) : (
          <div aria-busy={isLoading} className={isLoading ? 'opacity-60' : undefined}>
            {error && <p className="mb-3 text-sm text-danger" role="alert">{error}</p>}
            <MyProductList products={filtered} now={now} endingProductId={endingProductId} onEndAuction={openEndAuctionDialog} />
          </div>
        )}
      </div>
      <EndAuctionDialog
        open={selectedProduct !== null}
        productTitle={selectedProduct?.title || '선택한 상품'}
        currentPrice={selectedProduct?.currentPrice ?? null}
        hasHighestBidder={Boolean(selectedProduct?.highestBidderNickname)}
        isLoading={endingProductId !== null}
        errorMessage={dialogError}
        onClose={() => {
          if (endingProductId === null) {
            setSelectedProduct(null)
            setDialogError(null)
          }
        }}
        onConfirm={confirmEndAuction}
      />
    </section>
  )
}
