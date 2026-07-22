import { forwardRef, useId, type TextareaHTMLAttributes } from 'react'
import { cx } from '../../utils/cx'
import { FieldFrame } from './FieldFrame'

export interface TextareaProps extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string
  error?: string
  showCount?: boolean
  containerClassName?: string
}

export const Textarea = forwardRef<HTMLTextAreaElement, TextareaProps>(function Textarea(
  {
    id: providedId,
    label,
    error,
    showCount = false,
    value,
    defaultValue,
    maxLength,
    required,
    disabled,
    className,
    containerClassName,
    ...props
  },
  ref,
) {
  const generatedId = useId()
  const id = providedId || generatedId
  const currentLength = String(value ?? defaultValue ?? '').length

  return (
    <FieldFrame
      id={id}
      label={label}
      error={error}
      required={required}
      className={containerClassName}
      trailingLabel={
        showCount ? (
          <span className="text-xs tabular-nums text-ink-muted" aria-live="polite">
            {currentLength}{maxLength ? ` / ${maxLength}` : ''}
          </span>
        ) : undefined
      }
    >
      <textarea
        ref={ref}
        id={id}
        value={value}
        defaultValue={defaultValue}
        maxLength={maxLength}
        required={required}
        disabled={disabled}
        aria-invalid={error ? true : undefined}
        aria-describedby={error ? `${id}-error` : undefined}
        className={cx(
          'min-h-28 w-full resize-y rounded-compact border bg-surface px-3 py-3 text-sm leading-6 text-ink outline-none',
          'placeholder:text-ink-muted transition-colors duration-200',
          'focus:border-brand focus:ring-3 focus:ring-brand/15',
          'disabled:cursor-not-allowed disabled:bg-surface-secondary disabled:opacity-70',
          error ? 'border-danger' : 'border-line',
          className,
        )}
        {...props}
      />
    </FieldFrame>
  )
})
