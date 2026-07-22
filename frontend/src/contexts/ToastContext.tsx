import { useEffect, useState, type ReactNode } from 'react'
import { createPortal } from 'react-dom'
import { cx } from '../utils/cx'
import { ToastContext, type ToastOptions, type ToastVariant } from './toast-context'

interface ToastRecord extends Required<ToastOptions> {
  id: string
}

const variantStyles: Record<ToastVariant, string> = {
  success: 'border-positive/30 bg-surface text-ink',
  error: 'border-danger/30 bg-surface text-ink',
  warning: 'border-caution/35 bg-surface text-ink',
  info: 'border-brand-border bg-surface text-ink',
}

const variantLabel: Record<ToastVariant, string> = {
  success: '성공',
  error: '오류',
  warning: '주의',
  info: '안내',
}

const variantLabelStyles: Record<ToastVariant, string> = {
  success: 'bg-[var(--ds-success-light)] text-positive',
  error: 'bg-[var(--ds-error-light)] text-danger',
  warning: 'bg-[var(--ds-warning-light)] text-ink',
  info: 'bg-brand-light text-brand-dark',
}

interface ToastItemProps {
  toast: ToastRecord
  onDismiss: (id: string) => void
}

function ToastItem({ toast, onDismiss }: ToastItemProps) {
  useEffect(() => {
    if (toast.duration <= 0) return undefined
    const timer = window.setTimeout(() => onDismiss(toast.id), toast.duration)
    return () => window.clearTimeout(timer)
  }, [toast, onDismiss])

  return (
    <div
      className={cx(
        'flex w-full items-start gap-3 rounded-card border p-4 shadow-[var(--ds-shadow-card)]',
        variantStyles[toast.variant],
      )}
      role={toast.variant === 'error' ? 'alert' : 'status'}
    >
      <span className={cx('rounded-full px-2 py-1 text-xs font-semibold', variantLabelStyles[toast.variant])}>
        {variantLabel[toast.variant]}
      </span>
      <p className="min-w-0 flex-1 pt-0.5 text-sm leading-5">{toast.message}</p>
      <button
        type="button"
        onClick={() => onDismiss(toast.id)}
        className="-m-2 inline-flex min-h-11 min-w-11 items-center justify-center rounded-card text-xl text-ink-muted hover:bg-surface-secondary hover:text-ink"
        aria-label="알림 닫기"
      >
        <span aria-hidden="true">×</span>
      </button>
    </div>
  )
}

export function ToastProvider({ children }: { children: ReactNode }) {
  const [toasts, setToasts] = useState<ToastRecord[]>([])

  const dismissToast = (id: string) => {
    setToasts((current) => current.filter((toast) => toast.id !== id))
  }

  const showToast = ({ message, variant = 'info', duration = 3500 }: ToastOptions) => {
    setToasts((current) => {
      const duplicate = current.some((toast) => toast.message === message && toast.variant === variant)
      if (duplicate) return current

      const nextToast: ToastRecord = {
        id: `${Date.now()}-${Math.random().toString(36).slice(2)}`,
        message,
        variant,
        duration,
      }
      return [...current.slice(-3), nextToast]
    })
  }

  return (
    <ToastContext.Provider value={{ showToast, dismissToast }}>
      {children}
      {createPortal(
        <div
          className="pointer-events-none fixed right-4 bottom-4 z-[60] flex w-[calc(100%-2rem)] max-w-sm flex-col gap-2"
          aria-label="알림"
          aria-live="polite"
        >
          {toasts.map((toast) => (
            <div key={toast.id} className="pointer-events-auto">
              <ToastItem toast={toast} onDismiss={dismissToast} />
            </div>
          ))}
        </div>,
        document.body,
      )}
    </ToastContext.Provider>
  )
}
