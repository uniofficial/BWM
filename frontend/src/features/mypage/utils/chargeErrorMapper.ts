import { ApiError } from '../../../api/apiError'

export interface ChargeMutationError {
  message: string
  placement: 'field' | 'form'
  shouldRefreshHistory: boolean
}

export function mapChargeMutationError(error: unknown): ChargeMutationError {
  if (!(error instanceof ApiError)) {
    return { message: '충전 요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요.', placement: 'form', shouldRefreshHistory: false }
  }
  if (error.kind === 'network') {
    return { message: '서버에 연결할 수 없습니다. 요청 내역을 확인한 뒤 다시 시도해주세요.', placement: 'form', shouldRefreshHistory: true }
  }
  if (error.status === 400 || error.status === 422 || error.code === 'INVALID_AMOUNT') {
    return { message: '충전 금액을 다시 확인해주세요.', placement: 'field', shouldRefreshHistory: false }
  }
  if (error.status === 403) {
    return { message: '충전 요청 권한이 없습니다.', placement: 'form', shouldRefreshHistory: false }
  }
  if (error.status === 409) {
    return { message: '이미 처리 중인 충전 요청이 있거나 요청 상태가 변경되었습니다. 요청 내역을 확인해주세요.', placement: 'form', shouldRefreshHistory: true }
  }
  if (error.status !== null && error.status >= 500) {
    return { message: '충전 요청 처리 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.', placement: 'form', shouldRefreshHistory: false }
  }
  return { message: error.status === 401 ? '로그인이 만료되었습니다. 다시 로그인해주세요.' : '충전 요청을 처리하지 못했습니다.', placement: 'form', shouldRefreshHistory: false }
}
