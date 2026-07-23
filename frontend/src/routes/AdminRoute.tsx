import type { ReactNode } from 'react'
import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import { isAdmin } from '../utils/authRole'
import { AuthRouteLoading } from './AuthRouteLoading'

export interface AdminRouteProps {
  children?: ReactNode
  loginPath?: string
  forbiddenPath?: string
}

export function AdminRoute({ children, loginPath = '/login', forbiddenPath = '/products' }: AdminRouteProps) {
  const location = useLocation()
  const { user, isAuthenticated, isInitializing } = useAuth()

  if (isInitializing) return <AuthRouteLoading />

  if (!isAuthenticated) {
    return <Navigate to={loginPath} replace state={{ from: location }} />
  }

  if (!isAdmin(user)) {
    return <Navigate to={forbiddenPath} replace />
  }

  return children ?? <Outlet />
}
