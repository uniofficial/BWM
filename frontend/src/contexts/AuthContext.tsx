import { useEffect, useState, type ReactNode } from 'react'
import { useNavigate } from 'react-router-dom'
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

  const logout = () => {
    // TODO: 백엔드 로그아웃 API 구현 후
    // Refresh Token HttpOnly Cookie 만료 요청을 추가한다.
    // 그 전까지는 브라우저 새로고침 시 남아 있는 Cookie로 재인증될 수 있다.
    setClientLoggedOut(true)
    clearAuth()
    navigate('/login', { replace: true })
  }

  const withdrawTemporarily = () => {
    // TODO: 백엔드 회원 탈퇴 API 구현 후
    // 실제 회원 탈퇴 요청으로 교체한다.
    // 현재는 Cookie를 만료하지 않으므로 새로고침 시 재인증될 수 있다.
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
        withdrawTemporarily,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}
