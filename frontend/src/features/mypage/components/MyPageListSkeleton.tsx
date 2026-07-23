import { Skeleton } from '../../../components/ui'

export function MyPageListSkeleton({ cards = false }: { cards?: boolean }) {
  if (cards) {
    return (
      <div className="grid gap-4 sm:grid-cols-2" aria-busy="true" aria-label="목록을 불러오는 중입니다.">
        {Array.from({ length: 4 }, (_, index) => <Skeleton key={index} variant="product-card" />)}
      </div>
    )
  }

  return (
    <div className="rounded-content border border-line bg-surface p-5" aria-busy="true">
      <Skeleton variant="list" lines={5} />
    </div>
  )
}
