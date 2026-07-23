import { useEffect, useRef, useState } from 'react'
import { endAuction, getProductDetail } from '../../../api/productApi'
import type { EndAuctionResponse } from '../../../types/myProduct'
import { mapEndAuctionMutationError, type EndAuctionMutationError } from '../utils/endAuctionErrorMapper'

export type EndAuctionResult =
  | { ok: true; response: EndAuctionResponse }
  | { ok: false; error: EndAuctionMutationError }
  | { ok: false; ignored: true }

export function useEndAuction() {
  const [endingProductId, setEndingProductId] = useState<number | null>(null)
  const inFlightProductRef = useRef<number | null>(null)
  const mountedRef = useRef(true)

  useEffect(() => {
    mountedRef.current = true
    return () => {
      mountedRef.current = false
    }
  }, [])

  const submit = async (productId: number): Promise<EndAuctionResult> => {
    if (inFlightProductRef.current === productId) return { ok: false, ignored: true }
    if (inFlightProductRef.current !== null) return { ok: false, ignored: true }

    inFlightProductRef.current = productId
    if (mountedRef.current) setEndingProductId(productId)

    try {
      const latestProduct = await getProductDetail(productId)
      if (!mountedRef.current) return { ok: false, ignored: true }
      if (latestProduct.status !== 'OPEN') {
        return {
          ok: false,
          error: {
            message: '경매 상태가 이미 변경되었습니다. 최신 상태를 반영했습니다.',
            shouldRefresh: true,
            shouldCloseDialog: true,
          },
        }
      }

      const response = await endAuction(productId)
      if (!mountedRef.current) return { ok: false, ignored: true }
      return { ok: true, response }
    } catch (error) {
      if (!mountedRef.current) return { ok: false, ignored: true }
      return { ok: false, error: mapEndAuctionMutationError(error) }
    } finally {
      inFlightProductRef.current = null
      if (mountedRef.current) setEndingProductId(null)
    }
  }

  return { submit, endingProductId }
}
