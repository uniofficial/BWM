import { useId, useRef, type KeyboardEvent } from 'react'
import { cx } from '../../utils/cx'

export interface TabItem {
  value: string
  label: string
  disabled?: boolean
  panelId?: string
}

export interface TabsProps {
  items: TabItem[]
  value: string
  onChange: (value: string) => void
  ariaLabel: string
  className?: string
}

export function Tabs({ items, value, onChange, ariaLabel, className }: TabsProps) {
  const groupId = useId()
  const tabRefs = useRef<Array<HTMLButtonElement | null>>([])

  const moveFocus = (currentIndex: number, direction: 1 | -1) => {
    let nextIndex = currentIndex
    for (let count = 0; count < items.length; count += 1) {
      nextIndex = (nextIndex + direction + items.length) % items.length
      if (!items[nextIndex].disabled) {
        tabRefs.current[nextIndex]?.focus()
        return
      }
    }
  }

  const handleKeyDown = (event: KeyboardEvent<HTMLButtonElement>, index: number) => {
    if (event.key === 'ArrowRight') {
      event.preventDefault()
      moveFocus(index, 1)
    } else if (event.key === 'ArrowLeft') {
      event.preventDefault()
      moveFocus(index, -1)
    } else if (event.key === 'Home' || event.key === 'End') {
      event.preventDefault()
      const enabledItems = items
        .map((item, itemIndex) => ({ item, itemIndex }))
        .filter(({ item }) => !item.disabled)
      const target = event.key === 'Home' ? enabledItems[0] : enabledItems.at(-1)
      if (target) tabRefs.current[target.itemIndex]?.focus()
    } else if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault()
      if (!items[index].disabled) onChange(items[index].value)
    }
  }

  return (
    <div className={cx('overflow-x-auto border-b border-line', className)}>
      <div className="flex min-w-max gap-1" role="tablist" aria-label={ariaLabel}>
        {items.map((item, index) => {
          const selected = item.value === value
          return (
            <button
              key={item.value}
              ref={(element) => {
                tabRefs.current[index] = element
              }}
              id={`${groupId}-tab-${item.value}`}
              type="button"
              role="tab"
              aria-selected={selected}
              aria-controls={item.panelId}
              tabIndex={selected ? 0 : -1}
              disabled={item.disabled}
              onClick={() => onChange(item.value)}
              onKeyDown={(event) => handleKeyDown(event, index)}
              className={cx(
                'relative min-h-11 px-4 text-sm font-semibold whitespace-nowrap transition-colors duration-200',
                'focus-visible:z-10 focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand/30',
                selected ? 'text-brand-dark' : 'text-ink-muted hover:text-ink',
                'disabled:cursor-not-allowed disabled:text-line',
                selected &&
                  'after:absolute after:right-2 after:bottom-0 after:left-2 after:h-0.5 after:rounded-full after:bg-brand',
              )}
            >
              {item.label}
            </button>
          )
        })}
      </div>
    </div>
  )
}
