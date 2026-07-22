import { forwardRef, useId, type SelectHTMLAttributes } from 'react'
import { cx } from '../../utils/cx'
import { FieldFrame } from './FieldFrame'

export interface SelectOption {
  label: string
  value: string
  disabled?: boolean
}

export interface SelectProps extends SelectHTMLAttributes<HTMLSelectElement> {
  label?: string
  placeholder?: string
  options: SelectOption[]
  error?: string
  containerClassName?: string
}

export const Select = forwardRef<HTMLSelectElement, SelectProps>(function Select(
  {
    id: providedId,
    label,
    placeholder = '선택해주세요',
    options,
    error,
    required,
    disabled,
    value,
    defaultValue,
    className,
    containerClassName,
    ...props
  },
  ref,
) {
  const generatedId = useId()
  const id = providedId || generatedId

  return (
    <FieldFrame
      id={id}
      label={label}
      error={error}
      required={required}
      className={containerClassName}
    >
      <select
        ref={ref}
        id={id}
        required={required}
        disabled={disabled}
        aria-invalid={error ? true : undefined}
        aria-describedby={error ? `${id}-error` : undefined}
        value={value}
        defaultValue={value === undefined ? defaultValue ?? '' : undefined}
        className={cx(
          'h-11 w-full appearance-none rounded-compact border bg-surface px-3 pr-10 text-sm text-ink outline-none focus-visible:outline-none',
          'transition-colors duration-200 focus:border-brand focus:ring-3 focus:ring-brand/15',
          'disabled:cursor-not-allowed disabled:bg-surface-secondary disabled:opacity-70',
          error ? 'border-danger' : 'border-line',
          className,
        )}
        style={{
          backgroundImage:
            'linear-gradient(45deg, transparent 50%, var(--ds-text-muted) 50%), linear-gradient(135deg, var(--ds-text-muted) 50%, transparent 50%)',
          backgroundPosition: 'calc(100% - 16px) 19px, calc(100% - 11px) 19px',
          backgroundSize: '5px 5px, 5px 5px',
          backgroundRepeat: 'no-repeat',
        }}
        {...props}
      >
        <option value="" disabled>
          {placeholder}
        </option>
        {options.map((option) => (
          <option key={option.value} value={option.value} disabled={option.disabled}>
            {option.label}
          </option>
        ))}
      </select>
    </FieldFrame>
  )
})
