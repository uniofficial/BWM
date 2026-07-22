import type { HTMLAttributes, KeyboardEvent } from 'react'
import { cx } from '../../utils/cx'

export type CardVariant = 'default' | 'clickable' | 'highlight' | 'danger'

export interface CardProps extends HTMLAttributes<HTMLElement> {
  variant?: CardVariant
}

const variantClasses: Record<CardVariant, string> = {
  default: 'border-line bg-surface',
  clickable:
    'cursor-pointer border-line bg-surface hover:border-brand-border hover:bg-brand-light/30 active:bg-brand-light',
  highlight: 'border-brand-border bg-brand-light/45',
  danger: 'border-danger/35 bg-[var(--ds-error-light)]',
}

export function Card({ variant = 'default', className, onClick, onKeyDown, children, ...props }: CardProps) {
  const isClickable = variant === 'clickable' || Boolean(onClick)

  const handleKeyDown = (event: KeyboardEvent<HTMLElement>) => {
    onKeyDown?.(event)
    if (!event.defaultPrevented && isClickable && onClick && (event.key === 'Enter' || event.key === ' ')) {
      event.preventDefault()
      event.currentTarget.click()
    }
  }

  return (
    <article
      className={cx(
        'rounded-card border p-5 shadow-[var(--ds-shadow-card)] transition-colors duration-200',
        'focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand/30',
        variantClasses[variant],
        className,
      )}
      onClick={onClick}
      onKeyDown={handleKeyDown}
      role={isClickable ? 'button' : undefined}
      tabIndex={isClickable ? 0 : undefined}
      {...props}
    >
      {children}
    </article>
  )
}
