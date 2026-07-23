import axios, { AxiosHeaders } from 'axios'
import type { AuthSession } from '../types/auth'
import { getAppConfig } from '../config/appConfig'
import {
  clearAccessToken,
  getAccessToken,
  isClientLoggedOut,
  setAccessToken,
} from '../utils/tokenStore'
import { toApiError } from './apiError'
import { refreshAuthOnce } from './authRefresh'

interface AuthLifecycleHandlers {
  onRefresh?: (session: AuthSession) => void
  onAuthFailure?: () => void
}

let authLifecycleHandlers: AuthLifecycleHandlers = {}

export function setAuthLifecycleHandlers(handlers: AuthLifecycleHandlers) {
  authLifecycleHandlers = handlers

  return () => {
    if (authLifecycleHandlers === handlers) authLifecycleHandlers = {}
  }
}

function invalidateAuth() {
  clearAccessToken()
  authLifecycleHandlers.onAuthFailure?.()
}

export const apiClient = axios.create({
  withCredentials: true,
  headers: {
    Accept: 'application/json',
  },
})

apiClient.interceptors.request.use((config) => {
  config.baseURL = getAppConfig().apiBaseUrl
  const accessToken = getAccessToken()

  if (accessToken) {
    config.headers.set('Authorization', `Bearer ${accessToken}`)
  } else {
    config.headers.delete('Authorization')
  }

  return config
})

apiClient.interceptors.response.use(
  (response) => response,
  async (error: unknown) => {
    if (!axios.isAxiosError(error)) return Promise.reject(toApiError(error))
    if (axios.isCancel(error)) return Promise.reject(error)

    const requestConfig = error.config
    const status = error.response?.status

    if (status !== 401 || !requestConfig) return Promise.reject(toApiError(error))

    if (requestConfig._skipAuthRefresh || isClientLoggedOut()) {
      return Promise.reject(toApiError(error))
    }

    if (requestConfig._retry) {
      invalidateAuth()
      return Promise.reject(toApiError(error))
    }

    requestConfig._retry = true

    try {
      const refreshedSession = await refreshAuthOnce()

      if (isClientLoggedOut()) {
        clearAccessToken()
        return Promise.reject(toApiError(new Error('로그아웃 이후 Refresh 결과를 적용하지 않습니다.')))
      }

      setAccessToken(refreshedSession.accessToken)
      authLifecycleHandlers.onRefresh?.(refreshedSession)

      requestConfig.headers = AxiosHeaders.from(requestConfig.headers)
      requestConfig.headers.set('Authorization', `Bearer ${refreshedSession.accessToken}`)

      return apiClient.request(requestConfig)
    } catch {
      if (!isClientLoggedOut()) invalidateAuth()
      return Promise.reject(toApiError(error))
    }
  },
)
