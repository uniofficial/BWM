import axios from 'axios'
import { useEffect, useRef, useState } from 'react'
import { getMyWalletTransactions } from '../../api/walletApi'
import { ErrorState } from '../../components/ui'
import { MyPageListSkeleton } from '../../features/mypage/components/MyPageListSkeleton'
import { MyPageSectionHeader } from '../../features/mypage/components/MyPageSectionHeader'
import { TransactionHistoryTable } from '../../features/mypage/components/TransactionHistoryTable'
import { getMyPageErrorMessage } from '../../features/mypage/utils/myPageFormat'
import type { WalletTransaction } from '../../types/wallet'

export function TransactionHistoryPage() {
  const [rows, setRows] = useState<WalletTransaction[] | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [retryKey, setRetryKey] = useState(0)
  const requestIdRef = useRef(0)

  useEffect(() => {
    const controller = new AbortController()
    const requestId = ++requestIdRef.current
    setIsLoading(true)
    setError(null)
    void getMyWalletTransactions(controller.signal)
      .then((nextRows) => {
        if (requestId === requestIdRef.current && !controller.signal.aborted) setRows(nextRows)
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
    <section aria-labelledby="transaction-history-title">
      <MyPageSectionHeader id="transaction-history-title" title="거래 내역" description="포인트의 증감과 거래 후 잔액을 확인합니다." />
      {isLoading && rows === null ? <MyPageListSkeleton /> : error && rows === null ? (
        <ErrorState title="거래 내역을 불러오지 못했습니다." description={error} onRetry={() => setRetryKey((value) => value + 1)} />
      ) : (
        <div aria-busy={isLoading} className={isLoading ? 'opacity-60' : undefined}>
          {error && <p className="mb-3 text-sm text-danger" role="alert">{error}</p>}
          <TransactionHistoryTable rows={rows || []} />
        </div>
      )}
    </section>
  )
}
