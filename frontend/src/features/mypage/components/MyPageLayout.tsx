import { Outlet } from 'react-router-dom'
import { AppHeader } from '../../../components/common/AppHeader'
import { useAuth } from '../../../hooks/useAuth'
import { MyPageNavigation } from './MyPageNavigation'

export function MyPageLayout() {
  const { user } = useAuth()
  const displayName = user?.nickname || '내 계정'

  return (
    <div className="min-h-screen bg-surface-secondary">
      <AppHeader />
      <main className="mx-auto max-w-6xl px-4 py-7 sm:px-8 sm:py-9 lg:px-10">
        <header className="mb-6">
          <p className="text-sm font-semibold text-brand-dark">{displayName}</p>
          <h1 className="mt-2 text-3xl font-bold text-ink">마이페이지</h1>
        </header>

        <div className="lg:hidden">
          <MyPageNavigation variant="mobile" />
        </div>

        <div className="mt-6 grid min-w-0 gap-8 lg:mt-0 lg:grid-cols-[220px_minmax(0,1fr)]">
          <aside className="hidden border-r border-line pr-5 lg:block">
            <MyPageNavigation variant="desktop" />
          </aside>
          <div className="min-w-0">
            <Outlet />
          </div>
        </div>
      </main>
    </div>
  )
}
