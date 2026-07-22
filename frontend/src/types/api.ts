export interface ApiResponse<T> {
  success: boolean
  code: string | null
  message: string | null
  data: T
}

export type ApiErrorKind = 'http' | 'network' | 'unknown'
