import axios from 'axios'
import type { ApiErrorKind, ApiResponse } from '../types/api'

export class ApiError extends Error {
  readonly kind: ApiErrorKind
  readonly status: number | null
  readonly code: string | null
  readonly cause: unknown

  constructor(
    message: string,
    options: {
      kind: ApiErrorKind
      status?: number | null
      code?: string | null
      cause?: unknown
    },
  ) {
    super(message)
    this.name = 'ApiError'
    this.kind = options.kind
    this.status = options.status ?? null
    this.code = options.code ?? null
    this.cause = options.cause
  }
}

const defaultStatusMessages: Record<number, string> = {
  400: '요청 내용을 확인해주세요.',
  401: '인증이 필요합니다.',
  403: '이 작업을 수행할 권한이 없습니다.',
  404: '요청한 정보를 찾을 수 없습니다.',
  409: '현재 상태에서는 요청을 처리할 수 없습니다.',
}

function readErrorPayload(data: unknown) {
  if (!data || typeof data !== 'object') return { code: null, message: null }
  const payload = data as Partial<ApiResponse<unknown>>
  return {
    code: typeof payload.code === 'string' ? payload.code : null,
    message: typeof payload.message === 'string' ? payload.message : null,
  }
}

export function toApiError(error: unknown) {
  if (error instanceof ApiError) return error

  if (axios.isAxiosError(error)) {
    if (!error.response) {
      return new ApiError('서버에 연결할 수 없습니다. 네트워크 상태를 확인해주세요.', {
        kind: 'network',
        cause: error,
      })
    }

    const status = error.response.status
    const payload = readErrorPayload(error.response.data)
    const fallbackMessage =
      defaultStatusMessages[status] ||
      (status >= 500 ? '서버에서 요청을 처리하지 못했습니다.' : '요청을 처리하지 못했습니다.')

    return new ApiError(payload.message || fallbackMessage, {
      kind: 'http',
      status,
      code: payload.code,
      cause: error,
    })
  }

  return new ApiError(error instanceof Error ? error.message : '알 수 없는 오류가 발생했습니다.', {
    kind: 'unknown',
    cause: error,
  })
}
