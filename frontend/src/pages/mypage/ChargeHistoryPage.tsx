import { ErrorState } from '../../components/ui'
import { ChargeHistoryTable } from '../../features/mypage/components/ChargeHistoryTable'
import { MyPageListSkeleton } from '../../features/mypage/components/MyPageListSkeleton'
import { MyPageSectionHeader } from '../../features/mypage/components/MyPageSectionHeader'
import { useChargeHistory } from '../../features/mypage/hooks/useChargeHistory'

export function ChargeHistoryPage() {
  const { rows, isLoading, error, refresh } = useChargeHistory()

  return (
    <section aria-labelledby="charge-history-title">
      <MyPageSectionHeader id="charge-history-title" title="충전 요청 내역" description="포인트 충전 요청의 처리 상태를 확인합니다." />
      {isLoading && rows === null ? <MyPageListSkeleton /> : error && rows === null ? (
        <ErrorState title="충전 요청 내역을 불러오지 못했습니다." description={error} onRetry={refresh} />
      ) : (
        <div aria-busy={isLoading} className={isLoading ? 'opacity-60' : undefined}>
          {error && <p className="mb-3 text-sm text-danger" role="alert">{error}</p>}
          <ChargeHistoryTable rows={rows || []} />
        </div>
      )}
    </section>
  )
}
