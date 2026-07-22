import type { ReactNode } from 'react'
import { cx } from '../../utils/cx'

interface FieldFrameProps {
  id: string
  label?: string
  required?: boolean
  helperText?: string
  error?: string
  className?: string
  children: ReactNode
  trailingLabel?: ReactNode
}

export function FieldFrame({
  id,
  label,
  required,
  helperText,
  error,
  className,
  children,
  trailingLabel,
}: FieldFrameProps) {
  return (
    <div className={cx('grid gap-2', className)}>
      {(label || trailingLabel) && (
        <div className="flex min-h-5 items-center justify-between gap-3">
          {label ? (
            <label htmlFor={id} className="text-sm font-semibold text-ink">
              {label}
              {required && (
                <span className="ml-1 text-danger" aria-hidden="true">
                  *
                </span>
              )}
              {required && <span className="sr-only">필수 입력</span>}
            </label>
          ) : (
            <span />
          )}
          {trailingLabel}
        </div>
      )}
      {children}
      {(error || helperText) && (
        <p
          id={`${id}-${error ? 'error' : 'help'}`}
          className={cx('text-xs leading-5', error ? 'font-medium text-danger' : 'text-ink-muted')}
          role={error ? 'alert' : undefined}
        >
          {error || helperText}
        </p>
      )}
    </div>
  )
}
