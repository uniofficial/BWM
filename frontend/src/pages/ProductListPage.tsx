import axios from 'axios'
import { useEffect, useRef, useState } from 'react'
import { useSearchParams } from 'react-router-dom'
import { getProducts } from '../api/productApi'
import { AppHeader } from '../components/common/AppHeader'
import { Button, EmptyState, ErrorState, Spinner } from '../components/ui'
import { ProductFilters } from '../features/products/components/ProductFilters'
import { ProductGrid } from '../features/products/components/ProductGrid'
import { ProductListSkeleton } from '../features/products/components/ProductListSkeleton'
import { ProductPagination } from '../features/products/components/ProductPagination'
import { getProductListErrorMessage } from '../features/products/utils/productErrors'
import { hasActiveProductFilters, readProductQuery } from '../features/products/utils/productQuery'
import type { ProductListResult, ProductSortOption, ProductStatusFilter } from '../types/product'

export function ProductListPage() {
  const [searchParams, setSearchParams] = useSearchParams()
  const query = readProductQuery(searchParams)
  const [result, setResult] = useState<ProductListResult | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [errorMessage, setErrorMessage] = useState<string | null>(null)
  const [retryKey, setRetryKey] = useState(0)
  const [now, setNow] = useState(() => Date.now())
  const latestRequestIdRef = useRef(0)
  const { keyword, category, status, sort, page } = query

  useEffect(() => {
    const timer = window.setInterval(() => setNow(Date.now()), 30_000)
    return () => window.clearInterval(timer)
  }, [])

  useEffect(() => {
    const controller = new AbortController()
    const requestId = ++latestRequestIdRef.current
    setIsLoading(true)
    setErrorMessage(null)

    void getProducts({ keyword, category, status, sort, page }, controller.signal)
      .then((nextResult) => {
        if (requestId === latestRequestIdRef.current) setResult(nextResult)
      })
      .catch((error: unknown) => {
        if (!axios.isCancel(error) && requestId === latestRequestIdRef.current) {
          setErrorMessage(getProductListErrorMessage(error))
        }
      })
      .finally(() => {
        if (!controller.signal.aborted && requestId === latestRequestIdRef.current) setIsLoading(false)
      })

    return () => {
      controller.abort()
      if (requestId === latestRequestIdRef.current) latestRequestIdRef.current += 1
    }
  }, [category, keyword, page, sort, status, retryKey])

  const updateSearchParams = (updates: Record<string, string | undefined>) => {
    const next = new URLSearchParams(searchParams)
    Object.entries(updates).forEach(([key, value]) => {
      if (value) next.set(key, value)
      else next.delete(key)
    })
    setSearchParams(next)
  }

  const handleSearch = ({ keyword, category }: { keyword: string; category: string }) => {
    updateSearchParams({ keyword: keyword || undefined, category: category || undefined, page: undefined })
  }

  const handleStatusChange = (status: ProductStatusFilter) => {
    updateSearchParams({ status: status === 'ALL' ? undefined : status, page: undefined })
  }

  const handleSortChange = (sort: ProductSortOption) => {
    updateSearchParams({ sort: sort === 'LATEST' ? undefined : sort, page: undefined })
  }

  const handlePageChange = (page: number) => {
    updateSearchParams({ page: page <= 1 ? undefined : String(page) })
    document.getElementById('products-heading')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
  }

  const handleReset = () => setSearchParams({})
  const showInitialLoading = isLoading && result === null
  const isEmpty = result !== null && result.items.length === 0

  return (
    <div className="min-h-screen bg-surface-secondary">
      <AppHeader />
      <main className="mx-auto max-w-6xl px-4 py-8 sm:px-8 sm:py-10 lg:px-10">
        <section aria-labelledby="products-heading">
          <div className="mb-7">
            <p className="text-sm font-semibold text-brand-dark">BWM 경매</p>
            <h1 id="products-heading" className="mt-2 scroll-mt-4 text-3xl font-bold text-ink">
              상품 둘러보기
            </h1>
            <p className="mt-2 text-sm leading-6 text-ink-secondary">원하는 상품을 찾아 경매에 참여해보세요.</p>
          </div>

          <ProductFilters
            query={query}
            disabled={showInitialLoading}
            onSearch={handleSearch}
            onStatusChange={handleStatusChange}
            onSortChange={handleSortChange}
            onReset={handleReset}
          />

          <div className="flex min-h-12 items-center justify-between gap-3 py-4" aria-live="polite">
            <p className="text-sm text-ink-secondary">
              {result ? `${result.totalElements.toLocaleString('ko-KR')}개의 상품` : '상품을 확인하고 있습니다.'}
            </p>
            {isLoading && result && (
              <span className="inline-flex items-center gap-2 text-xs text-ink-muted">
                <Spinner size="sm" label="상품 목록 갱신 중" />
                갱신 중
              </span>
            )}
          </div>

          {showInitialLoading ? (
            <ProductListSkeleton />
          ) : errorMessage ? (
            <ErrorState
              title="상품을 불러오지 못했습니다."
              description={errorMessage}
              onRetry={() => setRetryKey((current) => current + 1)}
            />
          ) : isEmpty ? (
            <EmptyState
              title={hasActiveProductFilters(query) ? '조건에 맞는 상품이 없습니다.' : '등록된 상품이 없습니다.'}
              description={hasActiveProductFilters(query) ? '검색어나 필터를 변경해보세요.' : undefined}
              action={
                hasActiveProductFilters(query) ? (
                  <Button type="button" variant="secondary" onClick={handleReset}>
                    조건 초기화
                  </Button>
                ) : undefined
              }
            />
          ) : result ? (
            <div className={isLoading ? 'opacity-60' : undefined} aria-busy={isLoading}>
              <ProductGrid products={result.items} now={now} />
              <ProductPagination
                currentPage={result.page + 1}
                totalPages={result.totalPages}
                disabled={isLoading}
                onPageChange={handlePageChange}
              />
            </div>
          ) : null}
        </section>
      </main>
    </div>
  )
}
