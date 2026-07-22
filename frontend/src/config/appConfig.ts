export type AppEnvironment = 'development' | 'production' | 'test'

export interface AppConfig {
  apiBaseUrl: string
  appEnv: AppEnvironment
}

export class AppConfigurationError extends Error {
  constructor(message: string) {
    super(message)
    this.name = 'AppConfigurationError'
  }
}

function readRequiredEnv(name: string, value: unknown) {
  if (typeof value !== 'string' || value.trim() === '') {
    throw new AppConfigurationError(`${name} 환경 변수가 필요합니다.`)
  }
  return value.trim()
}

function readAppEnvironment(value: unknown): AppEnvironment {
  const configured = typeof value === 'string' ? value.trim() : ''
  if (!configured) {
    if (import.meta.env.MODE === 'test') return 'test'
    return import.meta.env.PROD ? 'production' : 'development'
  }
  if (configured === 'development' || configured === 'production' || configured === 'test') {
    return configured
  }
  throw new AppConfigurationError(
    'VITE_APP_ENV는 development, production, test 중 하나여야 합니다.',
  )
}

function readApiBaseUrl(value: unknown, appEnv: AppEnvironment) {
  const rawValue = readRequiredEnv('VITE_API_BASE_URL', value)
  let url: URL

  try {
    url = new URL(rawValue)
  } catch {
    throw new AppConfigurationError('VITE_API_BASE_URL은 유효한 절대 URL이어야 합니다.')
  }

  if (url.protocol !== 'http:' && url.protocol !== 'https:') {
    throw new AppConfigurationError('VITE_API_BASE_URL은 HTTP(S) URL이어야 합니다.')
  }
  if (url.username || url.password || url.search || url.hash || url.pathname !== '/') {
    throw new AppConfigurationError(
      'VITE_API_BASE_URL에는 Origin만 입력해주세요. 예: https://api.example.com',
    )
  }

  const productionBuild = import.meta.env.PROD || appEnv === 'production'
  const localHosts = new Set(['localhost', '127.0.0.1', '[::1]'])
  if (productionBuild && (url.protocol !== 'https:' || localHosts.has(url.hostname))) {
    throw new AppConfigurationError(
      'Production의 VITE_API_BASE_URL은 localhost가 아닌 HTTPS 주소여야 합니다.',
    )
  }

  return url.origin
}

let cachedConfig: AppConfig | null = null

export function getAppConfig(): AppConfig {
  if (cachedConfig) return cachedConfig

  const appEnv = readAppEnvironment(import.meta.env.VITE_APP_ENV)
  cachedConfig = {
    appEnv,
    apiBaseUrl: readApiBaseUrl(import.meta.env.VITE_API_BASE_URL, appEnv),
  }
  return cachedConfig
}
