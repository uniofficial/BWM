import axios from 'axios'
import { useEffect, useRef, useState } from 'react'
import { Link } from 'react-router-dom'
import { getMyBids } from '../../api/myBidApi'
import { EmptyState, ErrorState } from '../../components/ui'
import { MyBidList } from '../../features/mypage/components/MyBidList'
import { MyPageListSkeleton } from '../../features/mypage/components/MyPageListSkeleton'
import { MyPageSectionHeader } from '../../features/mypage/components/MyPageSectionHeader'
import { getMyPageErrorMessage } from '../../features/mypage/utils/myPageFormat'
import type { MyBidItem } from '../../types/myBid'

const linkClass = 'inline-flex min-h-11 items-center justify-center rounded-card bg-brand px-4 text-sm font-semibold text-white transition-colors duration-200 hover:bg-brand-hover focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand/30'

export function MyBidsPage() {
  const [bids, setBids] = useState<MyBidItem[] | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [retryKey, setRetryKey] = useState(0)
  const requestIdRef = useRef(0)

  useEffect(() => {
    const controller = new AbortController()
    const requestId = ++requestIdRef.current
    setIsLoading(true)
    setError(null)
    void getMyBids(controller.signal)
      .then((nextBids) => {
        if (requestId === requestIdRef.current && !controller.signal.aborted) setBids(nextBids)
      })
      .catch((reason: unknown) => {
        if (!axios.isCancel(reason) && requestId === requestIdRef.current) {
          setError(getMyPageErrorMessage(reason))
        }
      })
      .finally(() => {
        if (!controller.signal.aborted && requestId === requestIdRef.current) setIsLoading(false)
      })
    return () => {
      controller.abort()
      if (requestId === requestIdRef.current) requestIdRef.current += 1
    }
  }, [retryKey])

  return (
    <section aria-labelledby="my-bids-title">
      <MyPageSectionHeader id="my-bids-title" title="내 입찰" description="상품별 최근 입찰과 현재 최고 입찰 여부를 확인합니다." />
      {isLoading && bids === null ? <MyPageListSkeleton cards /> : error && bids === null ? (
        <ErrorState title="내 입찰 내역을 불러오지 못했습니다." description={error} onRetry={() => setRetryKey((value) => value + 1)} />
      ) : (bids || []).length === 0 ? (
        <EmptyState title="입찰한 상품이 없습니다." action={<Link to="/products" className={linkClass}>상품 둘러보기</Link>} />
      ) : (
        <div aria-busy={isLoading} className={isLoading ? 'opacity-60' : undefined}>
          {error && <p className="mb-3 text-sm text-danger" role="alert">{error}</p>}
          <MyBidList bids={bids || []} />
        </div>
      )}
    </section>
  )
}
