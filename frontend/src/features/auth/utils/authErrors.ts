import { ApiError } from '../../../api/apiError'

export interface ParsedAuthError {
  message: string
  field?: 'email' | 'nickname'
}

const NETWORK_MESSAGE = '서버에 연결할 수 없습니다. 백엔드 서버 실행 상태를 확인해주세요.'

export function parseLoginError(error: unknown): ParsedAuthError {
  if (!(error instanceof ApiError)) return { message: '로그인 중 오류가 발생했습니다.' }
  if (error.kind === 'network') return { message: NETWORK_MESSAGE }
  if (error.status === 400 || error.status === 401) {
    return { message: '이메일 또는 비밀번호를 확인해주세요.' }
  }
  if (error.status === 403) return { message: error.message || '이 계정으로 로그인할 수 없습니다.' }
  if (error.status !== null && error.status >= 500) {
    return { message: '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.' }
  }
  return { message: error.message }
}

export function parseRegisterError(error: unknown): ParsedAuthError {
  if (!(error instanceof ApiError)) return { message: '회원가입 중 오류가 발생했습니다.' }
  if (error.kind === 'network') return { message: NETWORK_MESSAGE }
  if (error.status !== null && error.status >= 500) {
    return { message: '회원가입 처리 중 서버 오류가 발생했습니다.' }
  }
  if (error.message.includes('이메일') && error.message.includes('사용 중')) {
    return { message: '이미 사용 중인 이메일입니다.', field: 'email' }
  }
  if (error.message.includes('닉네임') && error.message.includes('사용 중')) {
    return { message: '이미 사용 중인 닉네임입니다.', field: 'nickname' }
  }
  if (error.status === 400) return { message: '입력값을 확인해주세요.' }
  return { message: error.message }
}
