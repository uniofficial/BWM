import { ApiError } from '../../../api/apiError'

export function getProductListErrorMessage(error: unknown) {
  if (!(error instanceof ApiError)) return '잠시 후 다시 시도해주세요.'
  if (error.kind === 'network') return '백엔드 서버 실행 상태를 확인해주세요.'
  if (error.status === 400) return '검색어나 필터 조건을 확인해주세요.'
  if (error.status === 401) return '상품 조회에 로그인이 필요합니다.'
  if (error.status === 403) return '상품을 조회할 권한이 없습니다.'
  if (error.status === 404) return '상품 목록 API를 찾을 수 없습니다.'
  if (error.status !== null && error.status >= 500) return '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.'
  return error.message
}
