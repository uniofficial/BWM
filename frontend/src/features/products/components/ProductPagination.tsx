import { Button } from '../../../components/ui'

interface ProductPaginationProps {
  currentPage: number
  totalPages: number
  disabled?: boolean
  onPageChange: (page: number) => void
}

function getVisiblePages(currentPage: number, totalPages: number) {
  if (totalPages <= 7) return Array.from({ length: totalPages }, (_, index) => index + 1)

  const pages = new Set([1, totalPages])
  for (let page = currentPage - 2; page <= currentPage + 2; page += 1) {
    if (page > 1 && page < totalPages) pages.add(page)
  }

  const sorted = Array.from(pages).sort((a, b) => a - b)
  const visible: Array<number | string> = []
  sorted.forEach((page, index) => {
    const previous = sorted[index - 1]
    if (previous && page - previous > 1) visible.push(`ellipsis-${previous}`)
    visible.push(page)
  })
  return visible
}

export function ProductPagination({
  currentPage,
  totalPages,
  disabled = false,
  onPageChange,
}: ProductPaginationProps) {
  if (totalPages <= 1) return null
  const visiblePages = getVisiblePages(currentPage, totalPages)

  return (
    <nav className="mt-10 flex items-center justify-center gap-2" aria-label="상품 목록 페이지">
      <Button
        type="button"
        size="sm"
        variant="secondary"
        disabled={disabled || currentPage <= 1}
        onClick={() => onPageChange(currentPage - 1)}
        aria-label="이전 페이지"
      >
        이전
      </Button>

      <span className="min-w-20 text-center text-sm text-ink-secondary sm:hidden">
        {currentPage} / {totalPages}
      </span>

      <div className="hidden items-center gap-1 sm:flex">
        {visiblePages.map((page) =>
          typeof page === 'number' ? (
            <Button
              key={page}
              type="button"
              size="sm"
              variant={page === currentPage ? 'primary' : 'ghost'}
              className="min-w-11 px-2"
              disabled={disabled}
              onClick={() => onPageChange(page)}
              aria-label={`${page}페이지`}
              aria-current={page === currentPage ? 'page' : undefined}
            >
              {page}
            </Button>
          ) : (
            <span key={page} className="grid min-h-11 min-w-8 place-items-center text-ink-muted" aria-hidden="true">
              ...
            </span>
          ),
        )}
      </div>

      <Button
        type="button"
        size="sm"
        variant="secondary"
        disabled={disabled || currentPage >= totalPages}
        onClick={() => onPageChange(currentPage + 1)}
        aria-label="다음 페이지"
      >
        다음
      </Button>
    </nav>
  )
}
