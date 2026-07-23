import { Skeleton } from '../../../components/ui'

export function ProductDetailSkeleton() {
  return (
    <div role="status" aria-busy="true" aria-label="상품 상세 정보를 불러오는 중">
      <Skeleton variant="detail" aria-hidden="true" />
      <div className="mt-8 border-t border-line pt-8">
        <Skeleton variant="line" className="max-w-32" aria-hidden="true" />
        <Skeleton variant="list" lines={3} className="mt-5" aria-hidden="true" />
      </div>
    </div>
  )
}
