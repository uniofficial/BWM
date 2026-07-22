import type { HTMLAttributes } from 'react'
import { cx } from '../../utils/cx'

export type BadgeVariant = 'default' | 'primary' | 'success' | 'warning' | 'error' | 'neutral'

export interface BadgeProps extends HTMLAttributes<HTMLSpanElement> {
  variant?: BadgeVariant
}

const variantClasses: Record<BadgeVariant, string> = {
  default: 'border-line bg-surface text-ink-secondary',
  primary: 'border-brand-border bg-brand-light text-brand-dark',
  success: 'border-positive/25 bg-[var(--ds-success-light)] text-positive',
  warning: 'border-caution/30 bg-[var(--ds-warning-light)] text-ink',
  error: 'border-danger/25 bg-[var(--ds-error-light)] text-danger',
  neutral: 'border-line bg-surface-secondary text-ink-muted',
}

export function Badge({ variant = 'default', className, children, ...props }: BadgeProps) {
  return (
    <span
      className={cx(
        'inline-flex min-h-6 items-center rounded-pill border px-2.5 py-0.5 text-xs font-semibold',
        variantClasses[variant],
        className,
      )}
      {...props}
    >
      {children}
    </span>
  )
}
