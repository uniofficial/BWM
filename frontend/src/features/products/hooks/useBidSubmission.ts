import { useCallback, useEffect, useRef, useState } from 'react'
import { createBid } from '../../../api/bidApi'
import type { CreateBidResponse } from '../../../types/bid'
import { mapBidError, type MappedBidError } from '../utils/bidErrorMapper'

export type BidSubmissionResult =
  | { ok: true; response: CreateBidResponse }
  | { ok: false; error: MappedBidError }
  | { ok: false; ignored: true }

const missingAuthError: MappedBidError = {
  kind: 'auth',
  message: '로그인이 필요합니다.',
  placement: 'toast',
  shouldClose: true,
  shouldRefresh: false,
  code: 'UNAUTHORIZED',
  status: 401,
}

const duplicateSubmissionError: MappedBidError = {
  kind: 'conflict',
  message: '진행 중인 입찰 요청이 있습니다.',
  placement: 'dialog',
  shouldClose: false,
  shouldRefresh: false,
  code: null,
  status: null,
}

export function useBidSubmission(
  productId: number | null,
  canSubmit: boolean,
  onRequestStart: () => void,
) {
  const [isSubmitting, setIsSubmitting] = useState(false)
  const inFlightRef = useRef(false)
  const mountedRef = useRef(true)

  useEffect(() => {
    mountedRef.current = true
    return () => {
      mountedRef.current = false
    }
  }, [])

  const submitBid = useCallback(async (bidAmount: number): Promise<BidSubmissionResult> => {
    if (inFlightRef.current) return { ok: false, error: duplicateSubmissionError }
    if (!canSubmit || productId === null) return { ok: false, error: missingAuthError }

    inFlightRef.current = true
    onRequestStart()
    if (mountedRef.current) setIsSubmitting(true)

    try {
      const response = await createBid(productId, { bidAmount })
      if (!mountedRef.current) return { ok: false, ignored: true }
      return { ok: true, response }
    } catch (error) {
      if (!mountedRef.current) return { ok: false, ignored: true }
      return { ok: false, error: mapBidError(error) }
    } finally {
      inFlightRef.current = false
      if (mountedRef.current) setIsSubmitting(false)
    }
  }, [canSubmit, onRequestStart, productId])

  return { submitBid, isSubmitting }
}
