import { AppHeader } from '../../components/common/AppHeader'
import { ErrorState } from '../../components/ui'
import { AdminChargeRequestsTable } from '../../features/admin/components/AdminChargeRequestsTable'
import { useAdminChargeRequests } from '../../features/admin/hooks/useAdminChargeRequests'
import { MyPageListSkeleton } from '../../features/mypage/components/MyPageListSkeleton'
import { useToast } from '../../hooks/useToast'

export function AdminChargeRequestsPage() {
  const { rows, isLoading, error, processingId, processingAction, refresh, approve, reject } =
    useAdminChargeRequests()
  const { showToast } = useToast()

  const handleApprove = async (requestId: number) => {
    const result = await approve(requestId)
    if (result.ok) showToast({ message: '충전 요청을 승인했습니다.', variant: 'success' })
    else showToast({ message: result.message, variant: 'error' })
  }

  const handleReject = async (requestId: number) => {
    const result = await reject(requestId)
    if (result.ok) showToast({ message: '충전 요청을 반려했습니다.', variant: 'info' })
    else showToast({ message: result.message, variant: 'error' })
  }

  return (
    <div className="min-h-screen bg-surface-secondary">
      <AppHeader />
      <main>
        <header className="border-b border-line bg-surface">
          <div className="mx-auto max-w-5xl px-4 py-12 sm:px-8 sm:py-14 lg:px-10">
            <h1 className="text-4xl font-bold leading-tight text-ink">충전 요청 관리</h1>
            <p className="mt-3 max-w-xl text-base leading-7 text-ink-secondary">
              유저의 포인트 충전 요청을 승인하거나 반려합니다.
            </p>
          </div>
        </header>
        <div className="mx-auto max-w-5xl px-4 py-8 sm:px-8 sm:py-10 lg:px-10">
          {isLoading && rows === null ? (
            <MyPageListSkeleton />
          ) : error && rows === null ? (
            <ErrorState title="충전 요청 목록을 불러오지 못했습니다." description={error} onRetry={refresh} />
          ) : (
            <div aria-busy={isLoading} className={isLoading ? 'opacity-60' : undefined}>
              {error && (
                <p className="mb-3 text-sm text-danger" role="alert">
                  {error}
                </p>
              )}
              <AdminChargeRequestsTable
                rows={rows || []}
                processingId={processingId}
                processingAction={processingAction}
                onApprove={handleApprove}
                onReject={handleReject}
              />
            </div>
          )}
        </div>
      </main>
    </div>
  )
}
