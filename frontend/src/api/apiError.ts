import axios from 'axios'
import type { ApiErrorKind, ApiResponse } from '../types/api'
import type { NormalizedAppError, OperationalErrorCategory } from '../types/operationalError'

export class ApiError extends Error implements NormalizedAppError {
  readonly kind: ApiErrorKind
  readonly status: number | null
  readonly code: string | null
  readonly category: OperationalErrorCategory
  readonly userMessage: string
  readonly diagnosticMessage: string | null
  readonly requestId: string | null
  readonly endpoint: string | null
  readonly retryable: boolean
  readonly cause: unknown

  constructor(
    message: string,
    options: {
      kind: ApiErrorKind
      status?: number | null
      code?: string | null
      category?: OperationalErrorCategory
      diagnosticMessage?: string | null
      requestId?: string | null
      endpoint?: string | null
      retryable?: boolean
      cause?: unknown
    },
  ) {
    super(message)
    this.name = 'ApiError'
    this.kind = options.kind
    this.status = options.status ?? null
    this.code = options.code ?? null
    this.category = options.category ?? 'UNKNOWN'
    this.userMessage = message
    this.diagnosticMessage = options.diagnosticMessage ?? null
    this.requestId = options.requestId ?? null
    this.endpoint = options.endpoint ?? null
    this.retryable = options.retryable ?? false
    this.cause = options.cause
  }
}

const defaultStatusMessages: Record<number, string> = {
  400: '요청 내용을 확인해주세요.',
  401: '인증이 필요합니다.',
  403: '이 작업을 수행할 권한이 없습니다.',
  404: '요청한 정보를 찾을 수 없습니다.',
  409: '현재 상태에서는 요청을 처리할 수 없습니다.',
  413: '업로드할 파일의 크기를 확인해주세요.',
  415: '지원하지 않는 파일 형식입니다.',
  422: '입력한 내용을 다시 확인해주세요.',
  429: '요청이 많습니다. 잠시 후 다시 시도해주세요.',
}

function readErrorPayload(data: unknown) {
  if (!data || typeof data !== 'object') {
    return { code: null, message: null, requestId: null }
  }
  const payload = data as Partial<ApiResponse<unknown>> & {
    requestId?: unknown
    traceId?: unknown
  }
  return {
    code: typeof payload.code === 'string' ? payload.code : null,
    message: typeof payload.message === 'string' ? payload.message : null,
    requestId:
      typeof payload.requestId === 'string'
        ? payload.requestId
        : typeof payload.traceId === 'string'
          ? payload.traceId
          : null,
  }
}

function classifyStatus(status: number): OperationalErrorCategory {
  if (status === 401) return 'AUTHENTICATION'
  if (status === 403) return 'AUTHORIZATION'
  if (status === 404) return 'ROUTING'
  if (status === 409) return 'BUSINESS_CONFLICT'
  if (status === 413 || status === 415) return 'UPLOAD'
  if (status === 400 || status === 422) return 'VALIDATION'
  if (status >= 500) return 'SERVER'
  return 'UNKNOWN'
}

function readRequestId(headers: unknown, payloadRequestId: string | null) {
  if (payloadRequestId) return payloadRequestId
  if (!headers || typeof headers !== 'object') return null

  const candidateHeaders = headers as {
    get?: (name: string) => unknown
    [key: string]: unknown
  }
  const values = [
    candidateHeaders.get?.('x-request-id'),
    candidateHeaders.get?.('x-correlation-id'),
    candidateHeaders['x-request-id'],
    candidateHeaders['x-correlation-id'],
  ]
  const requestId = values.find((value) => typeof value === 'string' && value.trim() !== '')
  return typeof requestId === 'string' ? requestId : null
}

function readEndpoint(url: string | undefined) {
  if (!url) return null
  try {
    return new URL(url, 'http://local.invalid').pathname
  } catch {
    return url.split('?')[0] || null
  }
}

function isRetryable(method: string | undefined, status: number | null) {
  const safeMethod = !method || ['get', 'head', 'options'].includes(method.toLowerCase())
  if (!safeMethod) return false
  return status === null || status === 408 || status === 429 || status >= 500
}

function safeDiagnosticMessage(
  method: string | undefined,
  endpoint: string | null,
  status: number | null,
) {
  const requestMethod = method?.toUpperCase() || 'REQUEST'
  const target = endpoint || 'unknown-endpoint'
  return status === null
    ? `${requestMethod} ${target} failed without an HTTP response`
    : `${requestMethod} ${target} returned HTTP ${status}`
}

export function toApiError(error: unknown) {
  if (error instanceof ApiError) return error

  if (axios.isAxiosError(error)) {
    const endpoint = readEndpoint(error.config?.url)
    const method = error.config?.method

    if (!error.response) {
      return new ApiError('서버에 연결할 수 없습니다. 네트워크 상태를 확인해주세요.', {
        kind: 'network',
        category: 'NETWORK',
        diagnosticMessage: safeDiagnosticMessage(method, endpoint, null),
        endpoint,
        retryable: isRetryable(method, null),
        cause: error,
      })
    }

    const status = error.response.status
    const payload = readErrorPayload(error.response.data)
    const fallbackMessage =
      defaultStatusMessages[status] ||
      (status >= 500 ? '서버에서 요청을 처리하지 못했습니다.' : '요청을 처리하지 못했습니다.')
    const requestId = readRequestId(error.response.headers, payload.requestId)

    const userMessage = status < 500 && payload.message ? payload.message : fallbackMessage

    return new ApiError(userMessage, {
      kind: 'http',
      status,
      code: payload.code,
      category: classifyStatus(status),
      diagnosticMessage: safeDiagnosticMessage(method, endpoint, status),
      requestId,
      endpoint,
      retryable: isRetryable(method, status),
      cause: error,
    })
  }

  return new ApiError('알 수 없는 오류가 발생했습니다.', {
    kind: 'unknown',
    category: 'UNKNOWN',
    diagnosticMessage: error instanceof Error ? error.name : 'Non-Error rejection',
    cause: error,
  })
}
