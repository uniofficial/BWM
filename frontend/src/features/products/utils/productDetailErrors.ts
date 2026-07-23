import { ApiError } from '../../../api/apiError'

export interface ProductDetailLoadError {
  status: number | null
  message: string
}

export function parseProductDetailError(error: unknown): ProductDetailLoadError {
  if (!(error instanceof ApiError)) return { status: null, message: '잠시 후 다시 시도해주세요.' }
  if (error.kind === 'network') return { status: null, message: '백엔드 서버 실행 상태를 확인해주세요.' }
  if (error.status === 404) return { status: 404, message: '삭제되었거나 존재하지 않는 상품입니다.' }
  if (error.status === 403) return { status: 403, message: '상품 정보를 조회할 권한이 없습니다.' }
  if (error.status === 401) return { status: 401, message: '상품 조회에 로그인이 필요합니다.' }
  if (error.status !== null && error.status >= 500) {
    return { status: error.status, message: '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.' }
  }
  return { status: error.status, message: error.message }
}

export function shouldRetryProductPolling(error: ProductDetailLoadError) {
  return error.status === null || (error.status !== null && error.status >= 500)
}
