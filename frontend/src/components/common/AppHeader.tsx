import { useEffect, useState } from 'react'
import { Link, NavLink } from 'react-router-dom'
import { getMyWallet } from '../../api/walletApi'
import { QuickChargeDialog } from '../../features/mypage/components/QuickChargeDialog'
import { formatPoints } from '../../features/mypage/utils/myPageFormat'
import { useAuth } from '../../hooks/useAuth'
import { cx } from '../../utils/cx'
import { isAdmin } from '../../utils/authRole'
import { Badge, Button, Spinner } from '../ui'

const navLinkClass = ({ isActive }: { isActive: boolean }) =>
  cx(
    'inline-flex min-h-11 items-center border-b-2 px-1 text-sm font-semibold transition-colors duration-200 sm:px-2',
    'focus-visible:rounded-compact focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand/30',
    isActive
      ? 'border-brand text-ink'
      : 'border-transparent text-ink-secondary hover:border-line hover:text-ink',
  )

export function AppHeader() {
  const { user, isAuthenticated, isInitializing, logout } = useAuth()
  const [walletBalance, setWalletBalance] = useState<number | null>(null)
  const [isQuickChargeOpen, setIsQuickChargeOpen] = useState(false)

  useEffect(() => {
    if (!isAuthenticated) {
      setWalletBalance(null)
      return
    }

    const controller = new AbortController()
    void getMyWallet(controller.signal)
      .then((summary) => {
        if (!controller.signal.aborted) setWalletBalance(summary.isValid ? summary.balance : null)
      })
      .catch(() => {
        if (!controller.signal.aborted) setWalletBalance(null)
      })
    return () => controller.abort()
  }, [isAuthenticated])

  return (
    <header className="border-b border-line bg-surface">
      <div className="mx-auto flex min-h-[72px] max-w-7xl items-center justify-between gap-3 px-4 sm:px-8 lg:px-10">
        <Link
          to="/products"
          className="inline-flex min-h-11 items-center border-l-4 border-brand pl-3 text-xl font-bold text-ink focus-visible:rounded-compact"
          aria-label="BWM 홈"
        >
          BWM
        </Link>

        <nav className="flex min-w-0 items-center gap-1" aria-label="주요 메뉴">
          <NavLink to="/products" end className={navLinkClass} aria-label="경매 둘러보기">
            <span className="sm:hidden">경매</span>
            <span className="hidden sm:inline">경매 둘러보기</span>
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
              {isAdmin(user) && (
                <NavLink to="/admin/charge-requests" className={navLinkClass} aria-label="충전 요청 관리">
                  <span className="sm:hidden">관리자</span>
                  <span className="hidden sm:inline">충전 요청 관리</span>
                </NavLink>
              )}
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

      {isAuthenticated && walletBalance !== null && (
        <div className="border-t border-line bg-surface-secondary/60">
          <div className="mx-auto flex max-w-7xl items-center justify-end gap-2 px-4 py-2 sm:px-8 lg:px-10">
            <Badge variant="primary" className="text-sm">
              현재 포인트 {formatPoints(walletBalance)}
            </Badge>
            <Button type="button" size="sm" variant="outline" onClick={() => setIsQuickChargeOpen(true)}>
              빠른 충전
            </Button>
          </div>
        </div>
      )}

      <QuickChargeDialog open={isQuickChargeOpen} onClose={() => setIsQuickChargeOpen(false)} />
    </header>
  )
}
