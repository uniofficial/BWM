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
      <header className="border-b border-line bg-surface">
        <div className="mx-auto max-w-7xl px-4 py-10 sm:px-8 sm:py-12 lg:px-10">
          <h1 className="text-4xl font-bold leading-tight text-ink">마이페이지</h1>
          <p className="mt-3 text-sm leading-6 text-ink-secondary">{displayName}님의 경매와 지갑 정보를 관리합니다.</p>
        </div>
      </header>
      <main className="mx-auto max-w-7xl px-4 py-7 sm:px-8 sm:py-9 lg:px-10">
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
