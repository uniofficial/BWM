import { ErrorState } from '../../components/ui'
import { MyPageSectionHeader } from '../../features/mypage/components/MyPageSectionHeader'
import { WalletSummaryCard } from '../../features/mypage/components/WalletSummaryCard'
import { useWalletSummary } from '../../features/mypage/hooks/useWalletSummary'

export function WalletPage() {
  const { wallet, isLoading, error, retry } = useWalletSummary()

  return (
    <section aria-labelledby="wallet-page-title">
      <MyPageSectionHeader id="wallet-page-title" title="내 지갑" description="보유 포인트와 지갑 관련 내역을 확인합니다." />
      {error && !wallet ? (
        <ErrorState title="지갑 정보를 불러오지 못했습니다." description={error} onRetry={retry} />
      ) : (
        <>
          {error && <p className="mb-3 rounded-inline border border-caution/30 bg-[var(--ds-warning-light)] px-4 py-3 text-sm text-ink" role="status">최신 잔액을 갱신하지 못했습니다. 기존 정보를 표시합니다. {error}</p>}
          <WalletSummaryCard wallet={wallet} isLoading={isLoading && !wallet} />
        </>
      )}
    </section>
  )
}
