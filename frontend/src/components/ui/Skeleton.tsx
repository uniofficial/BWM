import type { HTMLAttributes } from 'react'
import { cx } from '../../utils/cx'

export type SkeletonVariant = 'line' | 'product-card' | 'list' | 'balance' | 'detail'

export interface SkeletonProps extends HTMLAttributes<HTMLDivElement> {
  variant?: SkeletonVariant
  lines?: number
}

function Block({ className }: { className: string }) {
  return <span className={cx('ds-skeleton block rounded-block', className)} aria-hidden="true" />
}

export function Skeleton({ variant = 'line', lines = 3, className, ...props }: SkeletonProps) {
  const content = {
    line: <Block className="h-4 w-full" />,
    'product-card': (
      <>
        <Block className="aspect-[4/3] w-full" />
        <div className="grid gap-3 p-4">
          <Block className="h-5 w-2/3" />
          <Block className="h-4 w-1/2" />
          <Block className="h-6 w-1/3" />
        </div>
      </>
    ),
    list: (
      <div className="grid gap-4">
        {Array.from({ length: lines }, (_, index) => (
          <div key={index} className="flex items-center gap-3">
            <Block className="h-12 w-12 shrink-0" />
            <div className="grid flex-1 gap-2">
              <Block className="h-4 w-2/3" />
              <Block className="h-3 w-1/3" />
            </div>
          </div>
        ))}
      </div>
    ),
    balance: (
      <div className="grid gap-4">
        <Block className="h-4 w-28" />
        <Block className="h-8 w-48" />
        <Block className="h-3 w-36" />
      </div>
    ),
    detail: (
      <div className="grid gap-4 md:grid-cols-2">
        <Block className="aspect-square w-full" />
        <div className="grid content-start gap-4">
          <Block className="h-7 w-3/4" />
          <Block className="h-5 w-1/2" />
          <Block className="h-24 w-full" />
        </div>
      </div>
    ),
  }[variant]

  return (
    <div className={cx(variant === 'product-card' && 'overflow-hidden rounded-content border border-line', className)} {...props}>
      <span className="sr-only">콘텐츠를 불러오는 중입니다.</span>
      {content}
    </div>
  )
}
