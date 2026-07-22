import { Link, NavLink } from 'react-router-dom'
import { useAuth } from '../../hooks/useAuth'
import { cx } from '../../utils/cx'
import { Button, Spinner } from '../ui'

const navLinkClass = ({ isActive }: { isActive: boolean }) =>
  cx(
    'inline-flex min-h-11 items-center rounded-card px-2 text-sm font-semibold transition-colors duration-200 sm:px-3',
    'focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand/30',
    isActive ? 'bg-brand-light text-brand-dark' : 'text-ink-secondary hover:bg-surface-secondary hover:text-ink',
  )

export function AppHeader() {
  const { user, isAuthenticated, isInitializing, logout } = useAuth()

  return (
    <header className="border-b border-line bg-surface">
      <div className="mx-auto flex min-h-16 max-w-6xl items-center justify-between gap-3 px-4 sm:px-8 lg:px-10">
        <Link
          to="/products"
          className="inline-flex min-h-11 items-center text-xl font-bold text-brand-dark focus-visible:rounded-card"
          aria-label="BWM 홈"
        >
          BWM
        </Link>

        <nav className="flex min-w-0 items-center gap-1" aria-label="주요 메뉴">
          <NavLink to="/products" end className={navLinkClass} aria-label="상품 둘러보기">
            <span className="sm:hidden">상품</span>
            <span className="hidden sm:inline">상품 둘러보기</span>
          </NavLink>
          {isInitializing ? (
            <div className="flex min-h-11 min-w-24 items-center justify-end" aria-label="인증 상태 확인 중">
              <Spinner size="sm" label="인증 상태 확인 중" />
            </div>
          ) : isAuthenticated ? (
            <>
              <NavLink to="/products/new" className={navLinkClass} aria-label="상품 등록">
                <span className="sm:hidden">등록</span>
                <span className="hidden sm:inline">상품 등록</span>
              </NavLink>
              <NavLink to="/mypage" className={navLinkClass} aria-label="마이페이지">
                <span className="sm:hidden">마이</span>
                <span className="hidden sm:inline">마이페이지</span>
              </NavLink>
              <span className="hidden max-w-40 truncate px-2 text-sm text-ink-secondary sm:block">
                {user?.nickname || '내 계정'}
              </span>
              <Button type="button" variant="ghost" size="sm" onClick={logout}>
                로그아웃
              </Button>
            </>
          ) : (
            <>
              <NavLink to="/login" className={navLinkClass}>
                로그인
              </NavLink>
              <NavLink to="/register" className={navLinkClass}>
                회원가입
              </NavLink>
            </>
          )}
        </nav>
      </div>
    </header>
  )
}
