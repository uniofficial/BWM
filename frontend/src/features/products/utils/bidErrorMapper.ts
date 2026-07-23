import { ApiError } from '../../../api/apiError'

export type BidErrorKind =
  | 'amount'
  | 'balance'
  | 'seller'
  | 'already-highest'
  | 'ended'
  | 'not-open'
  | 'permission'
  | 'not-found'
  | 'auth'
  | 'conflict'
  | 'server'
  | 'network'
  | 'unknown'

export interface MappedBidError {
  kind: BidErrorKind
  message: string
  placement: 'field' | 'dialog' | 'toast'
  shouldClose: boolean
  shouldRefresh: boolean
  code: string | null
  status: number | null
}

function mapped(
  error: ApiError,
  values: Omit<MappedBidError, 'code' | 'status'>,
): MappedBidError {
  return { ...values, code: error.code, status: error.status }
}

export function mapBidError(error: unknown): MappedBidError {
  if (!(error instanceof ApiError)) {
    return {
      kind: 'unknown',
      message: '입찰 요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요.',
      placement: 'dialog',
      shouldClose: false,
      shouldRefresh: false,
      code: null,
      status: null,
    }
  }

  if (error.kind === 'network') {
    return mapped(error, {
      kind: 'network',
      message: '서버에 연결할 수 없습니다. 네트워크 상태와 백엔드 서버를 확인해주세요.',
      placement: 'dialog',
      shouldClose: false,
      shouldRefresh: true,
    })
  }

  switch (error.code) {
    case 'BID_AMOUNT_TOO_LOW':
      return mapped(error, {
        kind: 'amount',
        message: '최소 입찰 가능 금액 이상을 입력해주세요.',
        placement: 'field',
        shouldClose: false,
        shouldRefresh: true,
      })
    case 'INSUFFICIENT_BALANCE':
      return mapped(error, {
        kind: 'balance',
        message: '입찰 가능한 잔액이 부족합니다.',
        placement: 'dialog',
        shouldClose: false,
        shouldRefresh: false,
      })
    case 'SELLER_CANNOT_BID':
      return mapped(error, {
        kind: 'seller',
        message: '판매자는 자신의 상품에 입찰할 수 없습니다.',
        placement: 'toast',
        shouldClose: true,
        shouldRefresh: true,
      })
    case 'ALREADY_HIGHEST_BIDDER':
      return mapped(error, {
        kind: 'already-highest',
        message: '현재 최고 입찰자는 다시 입찰할 수 없습니다.',
        placement: 'dialog',
        shouldClose: false,
        shouldRefresh: true,
      })
    case 'AUCTION_ENDED':
      return mapped(error, {
        kind: 'ended',
        message: '이미 종료된 경매입니다.',
        placement: 'toast',
        shouldClose: true,
        shouldRefresh: true,
      })
    case 'BID_NOT_OPEN':
      return mapped(error, {
        kind: 'not-open',
        message: '현재 진행 중인 경매가 아닙니다.',
        placement: 'toast',
        shouldClose: true,
        shouldRefresh: true,
      })
    case 'ITEM_NOT_FOUND':
      return mapped(error, {
        kind: 'not-found',
        message: '상품을 찾을 수 없습니다. 삭제되었거나 존재하지 않는 상품입니다.',
        placement: 'toast',
        shouldClose: true,
        shouldRefresh: false,
      })
    case 'UNAUTHORIZED':
      return mapped(error, {
        kind: 'auth',
        message: '로그인이 만료되었습니다. 다시 로그인해주세요.',
        placement: 'toast',
        shouldClose: true,
        shouldRefresh: false,
      })
    case 'FORBIDDEN':
      return mapped(error, {
        kind: 'permission',
        message: '이 경매에 입찰할 권한이 없습니다.',
        placement: 'dialog',
        shouldClose: false,
        shouldRefresh: true,
      })
    case 'BAD_REQUEST':
    case 'INVALID_AMOUNT':
      return mapped(error, {
        kind: 'amount',
        message: '입찰 금액을 다시 확인해주세요.',
        placement: 'field',
        shouldClose: false,
        shouldRefresh: false,
      })
  }

  if (error.status === 401) {
    return mapped(error, {
      kind: 'auth',
      message: '로그인이 만료되었습니다. 다시 로그인해주세요.',
      placement: 'toast',
      shouldClose: true,
      shouldRefresh: false,
    })
  }
  if (error.status === 403) {
    return mapped(error, {
      kind: 'permission',
      message: '이 경매에 입찰할 권한이 없습니다.',
      placement: 'dialog',
      shouldClose: false,
      shouldRefresh: true,
    })
  }
  if (error.status === 404) {
    return mapped(error, {
      kind: 'not-found',
      message: '상품을 찾을 수 없습니다. 삭제되었거나 존재하지 않는 상품입니다.',
      placement: 'toast',
      shouldClose: true,
      shouldRefresh: false,
    })
  }
  if (error.status === 409) {
    return mapped(error, {
      kind: 'conflict',
      message: '경매 상태가 변경되었습니다. 최신 정보를 확인한 뒤 다시 시도해주세요.',
      placement: 'dialog',
      shouldClose: false,
      shouldRefresh: true,
    })
  }
  if (error.status === 422) {
    return mapped(error, {
      kind: 'amount',
      message: '입찰 금액을 다시 확인해주세요.',
      placement: 'field',
      shouldClose: false,
      shouldRefresh: true,
    })
  }
  if (error.status !== null && error.status >= 500) {
    return mapped(error, {
      kind: 'server',
      message: '입찰 처리 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.',
      placement: 'toast',
      shouldClose: false,
      shouldRefresh: false,
    })
  }

  return mapped(error, {
    kind: 'unknown',
    message: '입찰 요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요.',
    placement: 'dialog',
    shouldClose: false,
    shouldRefresh: false,
  })
}
