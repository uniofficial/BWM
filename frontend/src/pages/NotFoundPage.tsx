import { Link } from 'react-router-dom'
import { AppHeader } from '../components/common/AppHeader'
import { EmptyState } from '../components/ui'

const homeLinkClass =
  'inline-flex min-h-11 items-center rounded-block bg-brand px-5 text-sm font-semibold text-white transition-colors duration-200 hover:bg-brand-hover active:bg-brand-active focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand/30 focus-visible:ring-offset-2'

export function NotFoundPage() {
  return (
    <div className="min-h-screen bg-surface-secondary">
      <AppHeader />
      <main className="mx-auto max-w-6xl px-4 py-12 sm:px-8 sm:py-16 lg:px-10">
        <EmptyState
          icon={<span className="text-sm font-bold">404</span>}
          title="페이지를 찾을 수 없습니다."
          description="주소가 잘못되었거나 이동한 페이지입니다."
          headingLevel="h1"
          action={
            <Link to="/products" className={homeLinkClass}>
              상품 목록으로 이동
            </Link>
          }
        />
      </main>
    </div>
  )
}
