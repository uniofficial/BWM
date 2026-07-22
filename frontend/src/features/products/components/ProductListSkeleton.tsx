import { Skeleton } from '../../../components/ui'
import { cx } from '../../../utils/cx'

export function ProductListSkeleton() {
  return (
    <div
      className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4"
      role="status"
      aria-busy="true"
      aria-label="상품 목록을 불러오는 중"
    >
      {Array.from({ length: 8 }, (_, index) => (
        <Skeleton
          key={index}
          variant="product-card"
          aria-hidden="true"
          className={cx(index >= 4 && index < 6 && 'hidden sm:block', index >= 6 && 'hidden lg:block')}
        />
      ))}
    </div>
  )
}
