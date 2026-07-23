import { ApiError } from '../../../api/apiError'

export type ProductCreationErrorKind =
  | 'validation'
  | 'auth'
  | 'permission'
  | 'conflict'
  | 'payload-too-large'
  | 'media-type'
  | 'server'
  | 'network'
  | 'unknown'

export interface ProductCreationError {
  kind: ProductCreationErrorKind
  message: string
  status: number | null
  code: string | null
}

export function mapProductCreationError(error: unknown): ProductCreationError {
  if (!(error instanceof ApiError)) {
    return {
      kind: 'unknown',
      message: '상품 등록 요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요.',
      status: null,
      code: null,
    }
  }

  if (error.kind === 'network') {
    return {
      kind: 'network',
      message: '서버에 연결할 수 없습니다. 요청 처리 여부를 상품 목록에서 확인한 뒤 다시 시도해주세요.',
      status: null,
      code: error.code,
    }
  }

  if (error.status === 400) {
    return {
      kind: 'validation',
      message: '입력한 상품 정보를 다시 확인해주세요.',
      status: error.status,
      code: error.code,
    }
  }
  if (error.status === 401) {
    return {
      kind: 'auth',
      message: '로그인이 만료되었습니다. 다시 로그인해주세요.',
      status: error.status,
      code: error.code,
    }
  }
  if (error.status === 403) {
    return {
      kind: 'permission',
      message: '상품을 등록할 권한이 없습니다.',
      status: error.status,
      code: error.code,
    }
  }
  if (error.status === 409) {
    return {
      kind: 'conflict',
      message: '상품 등록 상태가 변경되었습니다. 입력 내용을 확인한 뒤 다시 시도해주세요.',
      status: error.status,
      code: error.code,
    }
  }
  if (error.status === 413) {
    return {
      kind: 'payload-too-large',
      message: '업로드한 이미지의 전체 용량이 너무 큽니다. 이미지 개수나 파일 크기를 줄여주세요.',
      status: error.status,
      code: error.code,
    }
  }
  if (error.status === 415) {
    return {
      kind: 'media-type',
      message: '지원하지 않는 이미지 형식이 포함되어 있습니다.',
      status: error.status,
      code: error.code,
    }
  }
  if (error.status === 422) {
    return {
      kind: 'validation',
      message: '상품 등록 조건을 다시 확인해주세요.',
      status: error.status,
      code: error.code,
    }
  }
  if (error.status !== null && error.status >= 500) {
    return {
      kind: 'server',
      message: '상품 등록 중 서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.',
      status: error.status,
      code: error.code,
    }
  }

  return {
    kind: 'unknown',
    message: '상품 등록 요청을 처리하지 못했습니다. 잠시 후 다시 시도해주세요.',
    status: error.status,
    code: error.code,
  }
}

