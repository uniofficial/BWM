export type OperationalErrorCategory =
  | 'FRONTEND_RUNTIME'
  | 'ROUTING'
  | 'CONFIGURATION'
  | 'NETWORK'
  | 'CORS'
  | 'AUTHENTICATION'
  | 'AUTHORIZATION'
  | 'VALIDATION'
  | 'BUSINESS_CONFLICT'
  | 'SERVER'
  | 'UPLOAD'
  | 'STALE_DATA'
  | 'UNKNOWN'

export interface NormalizedAppError {
  category: OperationalErrorCategory
  status: number | null
  code: string | null
  userMessage: string
  diagnosticMessage: string | null
  requestId: string | null
  endpoint: string | null
  retryable: boolean
}

export interface OperationalErrorContext {
  feature: string
  action?: string
  route?: string
}
