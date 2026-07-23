import { ApiError } from '../../../api/apiError'

const pointFormatter = new Intl.NumberFormat('ko-KR')

export function formatPoints(value: number | null) {
  return value !== null && Number.isFinite(value) ? `${pointFormatter.format(value)}원` : '정보 없음'
}

export function formatDuration(seconds: number | null, active = true) {
  if (!active) return '경매 종료'
  if (seconds === null || !Number.isFinite(seconds)) return '남은 시간 확인 필요'
  if (seconds <= 0) return '경매 종료'
  const days = Math.floor(seconds / 86_400)
  const hours = Math.floor((seconds % 86_400) / 3_600)
  const minutes = Math.floor((seconds % 3_600) / 60)
  if (days > 0) return `${days}일 ${hours}시간 남음`
  if (hours > 0) return `${hours}시간 ${minutes}분 남음`
  return `${Math.max(minutes, 1)}분 남음`
}

export function getMyPageErrorMessage(error: unknown) {
  if (!(error instanceof ApiError)) return '잠시 후 다시 시도해주세요.'
  if (error.kind === 'network') return '서버에 연결할 수 없습니다. 백엔드 실행 상태를 확인해주세요.'
  if (error.status === 403) return '해당 정보를 조회할 권한이 없습니다.'
  if (error.status === 404) return '조회 API를 찾을 수 없습니다.'
  if (error.status !== null && error.status >= 500) return '서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.'
  return error.message
}
