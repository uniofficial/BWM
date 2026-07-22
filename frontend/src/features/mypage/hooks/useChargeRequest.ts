import { useEffect, useRef, useState } from 'react'
import { createChargeRequest } from '../../../api/walletApi'
import type { CreateChargeResponse } from '../../../types/wallet'
import { mapChargeMutationError, type ChargeMutationError } from '../utils/chargeErrorMapper'

export type ChargeRequestResult =
  | { ok: true; response: CreateChargeResponse }
  | { ok: false; error: ChargeMutationError }
  | { ok: false; ignored: true }

export function useChargeRequest() {
  const [isSubmitting, setIsSubmitting] = useState(false)
  const inFlightRef = useRef(false)
  const mountedRef = useRef(true)

  useEffect(() => {
    mountedRef.current = true
    return () => {
      mountedRef.current = false
    }
  }, [])

  const submit = async (amount: number): Promise<ChargeRequestResult> => {
    if (inFlightRef.current) return { ok: false, ignored: true }
    inFlightRef.current = true
    if (mountedRef.current) setIsSubmitting(true)

    try {
      const response = await createChargeRequest({ amount })
      if (!mountedRef.current) return { ok: false, ignored: true }
      return { ok: true, response }
    } catch (error) {
      if (!mountedRef.current) return { ok: false, ignored: true }
      return { ok: false, error: mapChargeMutationError(error) }
    } finally {
      inFlightRef.current = false
      if (mountedRef.current) setIsSubmitting(false)
    }
  }

  return { submit, isSubmitting }
}
