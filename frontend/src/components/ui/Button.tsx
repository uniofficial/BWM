import type { ButtonHTMLAttributes, ReactNode } from 'react'
import { cx } from '../../utils/cx'
import { Spinner } from './Spinner'

export type ButtonVariant = 'primary' | 'secondary' | 'outline' | 'danger' | 'ghost'
export type ButtonSize = 'sm' | 'md' | 'lg'

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant
  size?: ButtonSize
  isLoading?: boolean
  loadingLabel?: string
  leftIcon?: ReactNode
  rightIcon?: ReactNode
}

const variantClasses: Record<ButtonVariant, string> = {
  primary:
    'border-brand bg-brand text-white hover:border-brand-hover hover:bg-brand-hover active:border-brand-active active:bg-brand-active',
  secondary:
    'border-line bg-surface-secondary text-ink hover:border-brand-border hover:bg-brand-light active:bg-brand-border',
  outline:
    'border-line bg-surface text-ink hover:border-brand hover:text-brand-dark active:bg-brand-light',
  danger:
    'border-danger bg-danger text-white hover:brightness-90 active:brightness-80',
  ghost:
    'border-transparent bg-transparent text-ink-secondary hover:bg-surface-secondary hover:text-ink active:bg-line',
}

const sizeClasses: Record<ButtonSize, string> = {
  sm: 'min-h-11 px-3 text-sm',
  md: 'min-h-11 px-4 text-sm',
  lg: 'min-h-12 px-5 text-base',
}

export function Button({
  type = 'button',
  variant = 'primary',
  size = 'md',
  isLoading = false,
  loadingLabel = '처리 중',
  leftIcon,
  rightIcon,
  disabled,
  className,
  children,
  ...props
}: ButtonProps) {
  const isDisabled = disabled || isLoading

  return (
    <button
      type={type}
      disabled={isDisabled}
      aria-busy={isLoading || undefined}
      className={cx(
        'inline-flex items-center justify-center gap-2 rounded-block border font-semibold transition-colors duration-200',
        'focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand/30',
        'disabled:cursor-not-allowed disabled:border-line disabled:bg-line disabled:text-ink-muted disabled:opacity-100',
        variantClasses[variant],
        sizeClasses[size],
        className,
      )}
      {...props}
    >
      {isLoading ? <Spinner size="sm" label={loadingLabel} aria-hidden="true" /> : leftIcon}
      <span>{isLoading ? loadingLabel : children}</span>
      {!isLoading && rightIcon}
    </button>
  )
}
