import { Spinner } from '../components/ui'

export function AuthRouteLoading() {
  return (
    <div className="grid min-h-screen place-items-center bg-surface" aria-live="polite">
      <div className="flex items-center gap-3 text-sm text-ink-secondary">
        <Spinner size="md" label="인증 상태 확인 중" />
        <span>인증 상태를 확인하고 있습니다.</span>
      </div>
    </div>
  )
}
