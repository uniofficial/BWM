import type { ReactNode } from 'react'
import { cx } from '../../utils/cx'
import { Spinner } from './Spinner'

export interface TableColumn<T> {
  key: string
  header: ReactNode
  cell: (row: T) => ReactNode
  align?: 'left' | 'center' | 'right'
  className?: string
}

export interface TableProps<T> {
  columns: TableColumn<T>[]
  rows: T[]
  getRowKey: (row: T) => string | number
  caption?: string
  isLoading?: boolean
  loadingLabel?: string
  emptyTitle?: string
  emptyDescription?: string
  mobileMode?: 'scroll' | 'stack'
  className?: string
}

const alignClasses = {
  left: 'text-left',
  center: 'text-center',
  right: 'text-right',
}

export function Table<T>({
  columns,
  rows,
  getRowKey,
  caption,
  isLoading = false,
  loadingLabel = '목록을 불러오는 중입니다.',
  emptyTitle = '표시할 항목이 없습니다.',
  emptyDescription,
  mobileMode = 'scroll',
  className,
}: TableProps<T>) {
  return (
    <div
      className={cx(
        mobileMode === 'scroll' && 'overflow-x-auto',
        'rounded-content border border-line bg-surface',
        className,
      )}
    >
      <table
        className={cx(
          'w-full border-collapse text-sm',
          mobileMode === 'scroll' ? 'min-w-160' : 'ds-table-stack',
        )}
      >
        {caption && <caption className="sr-only">{caption}</caption>}
        <thead className="bg-surface-secondary text-ink-secondary">
          <tr>
            {columns.map((column) => (
              <th
                key={column.key}
                scope="col"
                className={cx(
                  'h-11 border-b border-line px-4 text-xs font-semibold',
                  alignClasses[column.align || 'left'],
                  column.className,
                )}
              >
                {column.header}
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {isLoading ? (
            <tr>
              <td colSpan={columns.length} className="h-36 text-center">
                <span className="inline-flex items-center gap-3 text-sm text-ink-muted">
                  <Spinner size="sm" label={loadingLabel} />
                  {loadingLabel}
                </span>
              </td>
            </tr>
          ) : rows.length === 0 ? (
            <tr>
              <td colSpan={columns.length} className="h-36 px-4 text-center">
                <strong className="block text-sm font-semibold text-ink">{emptyTitle}</strong>
                {emptyDescription && (
                  <span className="mt-1 block text-xs text-ink-muted">{emptyDescription}</span>
                )}
              </td>
            </tr>
          ) : (
            rows.map((row) => (
              <tr key={getRowKey(row)} className="border-b border-line last:border-b-0 hover:bg-surface-secondary/60">
                {columns.map((column) => (
                  <td
                    key={column.key}
                    data-label={typeof column.header === 'string' ? column.header : column.key}
                    className={cx(
                      'h-13 px-4 text-ink-secondary',
                      alignClasses[column.align || 'left'],
                      column.className,
                    )}
                  >
                    {column.cell(row)}
                  </td>
                ))}
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  )
}
