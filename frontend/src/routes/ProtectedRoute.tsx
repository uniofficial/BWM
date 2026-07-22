import type { ReactNode } from 'react'
import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import { AuthRouteLoading } from './AuthRouteLoading'

export interface ProtectedRouteProps {
  children?: ReactNode
  loginPath?: string
}

export function ProtectedRoute({ children, loginPath = '/login' }: ProtectedRouteProps) {
  const location = useLocation()
  const { isAuthenticated, isInitializing } = useAuth()

  if (isInitializing) return <AuthRouteLoading />

  if (!isAuthenticated) {
    return <Navigate to={loginPath} replace state={{ from: location }} />
  }

  return children ?? <Outlet />
}
