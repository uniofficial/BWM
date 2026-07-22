import { NavLink, useLocation } from 'react-router-dom'
import { MY_PAGE_MENU } from '../constants/myPageMenu'
import { cx } from '../../../utils/cx'

const linkClass = ({ isActive }: { isActive: boolean }) =>
  cx(
    'inline-flex min-h-11 items-center rounded-card px-3 text-sm font-semibold whitespace-nowrap transition-colors duration-200',
    'focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand/30',
    isActive
      ? 'bg-brand-light text-brand-dark'
      : 'text-ink-secondary hover:bg-surface-secondary hover:text-ink',
  )

function NavigationLinks({ mobile = false }: { mobile?: boolean }) {
  const location = useLocation()
  const walletGroupActive = location.pathname.startsWith('/mypage/wallet')
  let previousGroup: string | undefined

  return MY_PAGE_MENU.map((item) => {
    const showGroup = !mobile && item.group && item.group !== previousGroup
    previousGroup = item.group
    return (
      <div key={item.to} className={mobile ? undefined : 'grid gap-1'}>
        {showGroup && (
          <p className={cx('mt-3 px-3 text-xs font-semibold first:mt-0', walletGroupActive ? 'text-brand-dark' : 'text-ink-muted')}>
            {item.group}
          </p>
        )}
        <NavLink to={item.to} end className={linkClass}>
          {item.label}
        </NavLink>
      </div>
    )
  })
}

export function MyPageNavigation({ variant }: { variant: 'desktop' | 'mobile' }) {
  if (variant === 'desktop') {
    return (
      <nav className="grid content-start gap-1" aria-label="마이페이지 메뉴">
        <NavigationLinks />
      </nav>
    )
  }

  return (
    <nav className="-mx-4 overflow-x-auto border-y border-line bg-surface px-4 sm:-mx-8 sm:px-8" aria-label="마이페이지 메뉴">
      <div className="flex min-w-max gap-1 py-2">
        <NavigationLinks mobile />
      </div>
    </nav>
  )
}
