import { ErrorState } from '../../components/ui'
import { ChargeRequestForm } from '../../features/mypage/components/ChargeRequestForm'
import { ChargeHistoryTable } from '../../features/mypage/components/ChargeHistoryTable'
import { MyPageListSkeleton } from '../../features/mypage/components/MyPageListSkeleton'
import { MyPageSectionHeader } from '../../features/mypage/components/MyPageSectionHeader'
import { WalletSummaryCard } from '../../features/mypage/components/WalletSummaryCard'
import { useWalletSummary } from '../../features/mypage/hooks/useWalletSummary'
import { useChargeHistory } from '../../features/mypage/hooks/useChargeHistory'

export function WalletChargePage() {
  const { wallet, isLoading, error, retry } = useWalletSummary()
  const history = useChargeHistory()

  return (
    <section aria-labelledby="wallet-charge-title">
      <MyPageSectionHeader id="wallet-charge-title" title="충전 요청" description="충전 요청을 접수하고 승인 상태를 확인합니다." />
      <div className="grid gap-5">
        {error && !wallet ? (
          <ErrorState title="현재 잔액을 불러오지 못했습니다." description={error} onRetry={retry} />
        ) : (
          <WalletSummaryCard wallet={wallet} isLoading={isLoading && !wallet} compact />
        )}
        <ChargeRequestForm onRequestCreated={history.refresh} onHistoryCheckNeeded={history.refresh} />
        <section aria-labelledby="recent-charge-title">
          <h3 id="recent-charge-title" className="mb-3 text-lg font-semibold text-ink">최근 충전 요청</h3>
          {history.isLoading && history.rows === null ? <MyPageListSkeleton /> : history.error && history.rows === null ? (
            <ErrorState title="충전 요청 내역을 불러오지 못했습니다." description={history.error} onRetry={history.refresh} />
          ) : (
            <div aria-busy={history.isLoading} className={history.isLoading ? 'opacity-60' : undefined}>
              {history.error && <p className="mb-3 text-sm text-danger" role="alert">{history.error}</p>}
              <ChargeHistoryTable rows={history.rows || []} />
            </div>
          )}
        </section>
      </div>
    </section>
  )
}
