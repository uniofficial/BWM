import type { ReactNode } from 'react'

export interface EmptyStateProps {
  title: string
  description?: string
  icon?: ReactNode
  action?: ReactNode
  headingLevel?: 'h1' | 'h2' | 'h3'
}

export function EmptyState({ title, description, icon, action, headingLevel = 'h3' }: EmptyStateProps) {
  const Heading = headingLevel

  return (
    <div className="flex min-h-56 flex-col items-center justify-center px-5 py-10 text-center">
      {icon && (
        <div className="mb-4 grid min-h-12 min-w-12 place-items-center rounded-full bg-surface-secondary text-ink-muted" aria-hidden="true">
          {icon}
        </div>
      )}
      <Heading className="text-base font-semibold text-ink">{title}</Heading>
      {description && <p className="mt-2 max-w-md text-sm leading-6 text-ink-secondary">{description}</p>}
      {action && <div className="mt-5">{action}</div>}
    </div>
  )
}
