import { createContext } from 'react'
import type { AuthUser } from '../types/auth'

export interface AuthContextValue {
  user: AuthUser | null
  accessToken: string | null
  isAuthenticated: boolean
  isInitializing: boolean
  setAuth: (accessToken: string, user?: AuthUser | null) => void
  clearAuth: () => void
  refreshAuth: () => Promise<boolean>
  logout: () => Promise<void>
  withdraw: () => Promise<void>
}

export const AuthContext = createContext<AuthContextValue | null>(null)
