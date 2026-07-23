import type { ReactNode } from 'react'
import { Navigate, Outlet } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth'
import { AuthRouteLoading } from './AuthRouteLoading'

export interface GuestRouteProps {
  children?: ReactNode
  authenticatedRedirectTo: string
}

export function GuestRoute({ children, authenticatedRedirectTo }: GuestRouteProps) {
  const { isAuthenticated, isInitializing } = useAuth()

  if (isInitializing) return <AuthRouteLoading />
  if (isAuthenticated) return <Navigate to={authenticatedRedirectTo} replace />

  return children ?? <Outlet />
}
