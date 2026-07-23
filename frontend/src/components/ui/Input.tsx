import { forwardRef, useId, type InputHTMLAttributes, type ReactNode } from 'react'
import { cx } from '../../utils/cx'
import { FieldFrame } from './FieldFrame'

export interface InputProps extends Omit<InputHTMLAttributes<HTMLInputElement>, 'prefix'> {
  label?: string
  helperText?: string
  error?: string
  prefix?: ReactNode
  suffix?: ReactNode
  suffixAttached?: boolean
  containerClassName?: string
}

export const Input = forwardRef<HTMLInputElement, InputProps>(function Input(
  {
    id: providedId,
    label,
    helperText,
    error,
    prefix,
    suffix,
    suffixAttached = false,
    required,
    disabled,
    className,
    containerClassName,
    'aria-describedby': ariaDescribedBy,
    ...props
  },
  ref,
) {
  const generatedId = useId()
  const id = providedId || generatedId
  const describedBy = error
    ? `${id}-error`
    : helperText
      ? `${id}-help`
      : ariaDescribedBy

  return (
    <FieldFrame
      id={id}
      label={label}
      helperText={helperText}
      error={error}
      required={required}
      className={containerClassName}
    >
      <div
        className={cx(
          'flex min-h-11 items-center rounded-compact border bg-surface transition-colors duration-200',
          error ? 'border-danger' : 'border-line focus-within:border-brand',
          'focus-within:ring-3 focus-within:ring-brand/15',
          disabled && 'cursor-not-allowed bg-surface-secondary opacity-70',
        )}
      >
        {prefix && <span className="pl-3 text-sm text-ink-muted">{prefix}</span>}
        <input
          ref={ref}
          id={id}
          required={required}
          disabled={disabled}
          aria-invalid={error ? true : undefined}
          aria-describedby={describedBy}
          className={cx(
            'h-11 min-w-0 flex-1 border-0 bg-transparent px-3 text-sm text-ink outline-none focus-visible:outline-none',
            'placeholder:text-ink-muted disabled:cursor-not-allowed',
            Boolean(prefix) && 'pl-2',
            Boolean(suffix) && (suffixAttached ? 'pr-0' : 'pr-2'),
            className,
          )}
          {...props}
        />
        {suffix && <span className="shrink-0 pr-3 text-sm text-ink-muted">{suffix}</span>}
      </div>
    </FieldFrame>
  )
})
