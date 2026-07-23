import { ApiError, toApiError } from '../api/apiError'
import { AppConfigurationError } from '../config/appConfig'
import type {
  NormalizedAppError,
  OperationalErrorContext,
} from '../types/operationalError'

interface OperationalDiagnosticRecord
  extends Omit<NormalizedAppError, 'userMessage'>,
    OperationalErrorContext {
  occurredAt: string
  appVersion: string | null
  commitSha: string | null
}

let globalDiagnosticsInstalled = false

function optionalBuildValue(value: unknown) {
  return typeof value === 'string' && value.trim() ? value.trim() : null
}

function normalizeOperationalError(error: unknown): NormalizedAppError {
  if (error instanceof ApiError) return error
  if (error instanceof AppConfigurationError) {
    return {
      category: 'CONFIGURATION',
      status: null,
      code: null,
      userMessage: '앱 설정을 확인해주세요.',
      diagnosticMessage: error.name,
      requestId: null,
      endpoint: null,
      retryable: false,
    }
  }

  const normalized = toApiError(error)
  if (normalized.kind !== 'unknown') return normalized

  return {
    category: 'FRONTEND_RUNTIME',
    status: normalized.status,
    code: normalized.code,
    userMessage: '페이지를 표시하는 중 문제가 발생했습니다.',
    diagnosticMessage: normalized.diagnosticMessage,
    requestId: normalized.requestId,
    endpoint: normalized.endpoint,
    retryable: true,
  }
}

export function reportOperationalError(error: unknown, context: OperationalErrorContext) {
  const normalized = normalizeOperationalError(error)
  const record: OperationalDiagnosticRecord = {
    category: normalized.category,
    status: normalized.status,
    code: normalized.code,
    diagnosticMessage: normalized.diagnosticMessage,
    requestId: normalized.requestId,
    endpoint: normalized.endpoint,
    retryable: normalized.retryable,
    feature: context.feature,
    action: context.action,
    route: context.route,
    occurredAt: new Date().toISOString(),
    appVersion: optionalBuildValue(import.meta.env.VITE_APP_VERSION),
    commitSha: optionalBuildValue(import.meta.env.VITE_COMMIT_SHA),
  }

  // Exclude request/response bodies, headers, credentials and stacks from this record.
  console.error('[BWM operational error]', record)
  return normalized
}

export function installGlobalErrorDiagnostics() {
  if (globalDiagnosticsInstalled || typeof window === 'undefined') return
  globalDiagnosticsInstalled = true

  window.addEventListener('error', (event) => {
    reportOperationalError(event.error ?? new Error(event.message), {
      feature: 'application',
      action: 'unhandled-window-error',
      route: window.location.pathname,
    })
  })

  window.addEventListener('unhandledrejection', (event) => {
    reportOperationalError(event.reason, {
      feature: 'application',
      action: 'unhandled-promise-rejection',
      route: window.location.pathname,
    })
  })
}
