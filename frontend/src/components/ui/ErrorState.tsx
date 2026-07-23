import { Button } from './Button'

export interface ErrorStateProps {
  title?: string
  description: string
  onRetry?: () => void
  retryLabel?: string
}

export function ErrorState({
  title = '문제가 발생했습니다.',
  description,
  onRetry,
  retryLabel = '다시 시도',
}: ErrorStateProps) {
  return (
    <div className="flex min-h-56 flex-col items-center justify-center px-5 py-10 text-center" role="alert">
      <div className="mb-4 grid min-h-12 min-w-12 place-items-center rounded-pill bg-[var(--ds-error-light)] font-bold text-danger" aria-hidden="true">
        !
      </div>
      <h3 className="text-base font-semibold text-ink">{title}</h3>
      <p className="mt-2 max-w-md text-sm leading-6 text-ink-secondary">{description}</p>
      {onRetry && (
        <Button className="mt-5" variant="secondary" onClick={onRetry}>
          {retryLabel}
        </Button>
      )}
    </div>
  )
}
