import type { HTMLAttributes } from 'react'
import { cx } from '../../utils/cx'

export type SpinnerSize = 'sm' | 'md' | 'lg'

export interface SpinnerProps extends HTMLAttributes<HTMLSpanElement> {
  size?: SpinnerSize
  label?: string
}

const sizeClasses: Record<SpinnerSize, string> = {
  sm: 'h-4 w-4 border-2',
  md: 'h-6 w-6 border-2',
  lg: 'h-9 w-9 border-[3px]',
}

export function Spinner({
  size = 'md',
  label = '불러오는 중',
  className,
  'aria-hidden': ariaHidden,
  ...props
}: SpinnerProps) {
  return (
    <span
      className={cx('inline-flex items-center justify-center', className)}
      role={ariaHidden ? undefined : 'status'}
      aria-label={ariaHidden ? undefined : label}
      aria-hidden={ariaHidden}
      {...props}
    >
      <span
        aria-hidden="true"
        className={cx(
          'ds-spinner block rounded-full border-current border-r-transparent',
          sizeClasses[size],
        )}
      />
    </span>
  )
}
