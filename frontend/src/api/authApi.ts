import axios, { type AxiosResponse } from 'axios'
import type { ApiResponse } from '../types/api'
import type { AuthSession, AuthUser, LoginRequest, RegisterRequest } from '../types/auth'
import { getAppConfig } from '../config/appConfig'
import { toApiError } from './apiError'

export const AUTH_ENDPOINTS = {
  login: '/api/auth/login',
  register: '/api/auth/signup',
  refresh: '/api/auth/reissue',
} as const

const publicAuthClient = axios.create({
  withCredentials: true,
  headers: {
    Accept: 'application/json',
  },
})

publicAuthClient.interceptors.request.use((config) => {
  config.baseURL = getAppConfig().apiBaseUrl
  return config
})

export class MissingAccessTokenError extends Error {
  constructor() {
    super('Access Token이 Refresh 응답에 없습니다.')
    this.name = 'MissingAccessTokenError'
  }
}

export function extractAccessToken(response: AxiosResponse<unknown>) {
  const authorization = response.headers['authorization']
  if (typeof authorization !== 'string') throw new MissingAccessTokenError()

  const [scheme, token] = authorization.trim().split(/\s+/, 2)
  if (scheme.toLowerCase() !== 'bearer' || !token) throw new MissingAccessTokenError()
  return token
}

export async function login(request: LoginRequest): Promise<AuthSession> {
  try {
    const response = await publicAuthClient.post<ApiResponse<AuthUser>>(
      AUTH_ENDPOINTS.login,
      request,
      { _skipAuthRefresh: true },
    )

    return {
      accessToken: extractAccessToken(response),
      user: response.data.data,
    }
  } catch (error) {
    throw toApiError(error)
  }
}

export async function register(request: RegisterRequest): Promise<void> {
  try {
    await publicAuthClient.post<ApiResponse<null>>(AUTH_ENDPOINTS.register, request, {
      _skipAuthRefresh: true,
    })
  } catch (error) {
    throw toApiError(error)
  }
}

export async function requestAuthRefresh(): Promise<AuthSession> {
  try {
    const response = await publicAuthClient.post<ApiResponse<AuthUser | null>>(
      AUTH_ENDPOINTS.refresh,
      undefined,
      { _skipAuthRefresh: true },
    )

    return {
      accessToken: extractAccessToken(response),
      user: response.data.data ?? null,
    }
  } catch (error) {
    throw toApiError(error)
  }
}
