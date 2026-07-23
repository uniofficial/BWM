import { useEffect, useState, type ReactNode } from 'react'
import { useNavigate } from 'react-router-dom'
import { requestLogout, requestWithdraw } from '../api/accountApi'
import { refreshAuthOnce } from '../api/authRefresh'
import { setAuthLifecycleHandlers } from '../api/axiosInstance'
import type { AuthSession, AuthUser } from '../types/auth'
import {
  clearAccessToken,
  isClientLoggedOut,
  setAccessToken,
  setClientLoggedOut,
} from '../utils/tokenStore'
import { AuthContext } from './auth-context'

export function AuthProvider({ children }: { children: ReactNode }) {
  const navigate = useNavigate()
  const [user, setUser] = useState<AuthUser | null>(null)
  const [accessToken, setAccessTokenState] = useState<string | null>(null)
  const [isInitializing, setIsInitializing] = useState(true)

  const applySession = (session: AuthSession) => {
    setAccessToken(session.accessToken)
    setAccessTokenState(session.accessToken)
    if (session.user !== null) setUser(session.user)
  }

  const clearAuth = () => {
    clearAccessToken()
    setAccessTokenState(null)
    setUser(null)
    setIsInitializing(false)
  }

  const setAuth = (nextAccessToken: string, nextUser?: AuthUser | null) => {
    setClientLoggedOut(false)
    setAccessToken(nextAccessToken)
    setAccessTokenState(nextAccessToken)
    if (nextUser !== undefined) setUser(nextUser)
    setIsInitializing(false)
  }

  const refreshAuth = async () => {
    if (isClientLoggedOut()) return false

    try {
      const session = await refreshAuthOnce()
      if (isClientLoggedOut()) return false
      applySession(session)
      return true
    } catch {
      clearAuth()
      return false
    }
  }

  const logout = async () => {
    try {
      await requestLogout()
    } catch {
      // Client-side logout must still proceed even if invalidating the
      // server-side Refresh Token fails (e.g. offline, already expired).
    }
    setClientLoggedOut(true)
    clearAuth()
    navigate('/login', { replace: true })
  }

  const withdraw = async () => {
    await requestWithdraw()
    setClientLoggedOut(true)
    clearAuth()
    navigate('/login', { replace: true })
  }

  useEffect(() => {
    return setAuthLifecycleHandlers({
      onRefresh: (session) => {
        if (!isClientLoggedOut()) {
          setAccessToken(session.accessToken)
          setAccessTokenState(session.accessToken)
          if (session.user !== null) setUser(session.user)
        }
      },
      onAuthFailure: () => {
        clearAccessToken()
        setAccessTokenState(null)
        setUser(null)
        setIsInitializing(false)
      },
    })
  }, [])

  useEffect(() => {
    let active = true

    const initializeAuth = async () => {
      try {
        const session = await refreshAuthOnce()
        if (active && !isClientLoggedOut()) {
          setAccessToken(session.accessToken)
          setAccessTokenState(session.accessToken)
          if (session.user !== null) setUser(session.user)
        }
      } catch {
        if (active) {
          clearAccessToken()
          setAccessTokenState(null)
          setUser(null)
        }
      } finally {
        if (active) setIsInitializing(false)
      }
    }

    void initializeAuth()
    return () => {
      active = false
    }
  }, [])

  const isAuthenticated = !isInitializing && accessToken !== null

  return (
    <AuthContext.Provider
      value={{
        user,
        accessToken,
        isAuthenticated,
        isInitializing,
        setAuth,
        clearAuth,
        refreshAuth,
        logout,
        withdraw,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}
