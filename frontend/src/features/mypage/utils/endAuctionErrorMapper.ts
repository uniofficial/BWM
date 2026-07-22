import { ApiError } from '../../../api/apiError'

export interface EndAuctionMutationError {
  message: string
  shouldRefresh: boolean
  shouldCloseDialog: boolean
}

export function mapEndAuctionMutationError(error: unknown): EndAuctionMutationError {
  if (!(error instanceof ApiError)) {
    return { message: '경매 종료 요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요.', shouldRefresh: false, shouldCloseDialog: false }
  }
  if (error.kind === 'network') {
    return { message: '서버에 연결할 수 없습니다. 경매 상태를 다시 확인해주세요.', shouldRefresh: true, shouldCloseDialog: false }
  }
  if (error.code === 'AUCTION_NOT_ENDED') {
    return { message: '아직 경매 마감 시간이 되지 않았습니다.', shouldRefresh: true, shouldCloseDialog: false }
  }
  if (error.code === 'AUCTION_ALREADY_CLOSED' || error.status === 409) {
    return { message: '경매 상태가 이미 변경되었습니다. 최신 상태를 반영했습니다.', shouldRefresh: true, shouldCloseDialog: true }
  }
  if (error.code === 'AUCTION_PERMISSION_DENIED' || error.status === 403) {
    return { message: '이 상품의 경매를 종료할 권한이 없습니다.', shouldRefresh: true, shouldCloseDialog: true }
  }
  if (error.code === 'ITEM_NOT_FOUND' || error.status === 404) {
    return { message: '상품을 찾을 수 없습니다. 삭제되었거나 존재하지 않는 상품입니다.', shouldRefresh: true, shouldCloseDialog: true }
  }
  if (error.status === 400) {
    return { message: '경매 종료 요청을 처리할 수 없습니다. 상품 상태를 다시 확인해주세요.', shouldRefresh: true, shouldCloseDialog: false }
  }
  if (error.status === 422) {
    return { message: '현재 상태에서는 경매를 종료할 수 없습니다.', shouldRefresh: true, shouldCloseDialog: false }
  }
  if (error.status !== null && error.status >= 500) {
    return { message: '경매 종료 처리 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.', shouldRefresh: false, shouldCloseDialog: false }
  }
  return { message: error.status === 401 ? '로그인이 만료되었습니다. 다시 로그인해주세요.' : '경매 종료 요청을 처리하지 못했습니다.', shouldRefresh: false, shouldCloseDialog: false }
}
