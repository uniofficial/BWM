import axios from 'axios'
import { useEffect, useRef, useState } from 'react'
import { approveChargeRequest, getAdminChargeRequests, rejectChargeRequest } from '../../../api/adminApi'
import type { AdminChargeRequestItem } from '../../../types/admin'
import { getMyPageErrorMessage } from '../../mypage/utils/myPageFormat'

type ChargeAction = 'approve' | 'reject'
type ProcessResult = { ok: true } | { ok: false; message: string }

export function useAdminChargeRequests() {
  const [rows, setRows] = useState<AdminChargeRequestItem[] | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [processingId, setProcessingId] = useState<number | null>(null)
  const [processingAction, setProcessingAction] = useState<ChargeAction | null>(null)
  const [retryKey, setRetryKey] = useState(0)
  const requestIdRef = useRef(0)

  useEffect(() => {
    const controller = new AbortController()
    const requestId = requestIdRef.current + 1
    requestIdRef.current = requestId
    setIsLoading(true)
    setError(null)
    void getAdminChargeRequests(controller.signal)
      .then((nextRows) => {
        if (requestIdRef.current === requestId && !controller.signal.aborted) setRows(nextRows)
      })
      .catch((reason: unknown) => {
        if (requestIdRef.current === requestId && !axios.isCancel(reason)) {
          setError(getMyPageErrorMessage(reason))
        }
      })
      .finally(() => {
        if (requestIdRef.current === requestId && !controller.signal.aborted) setIsLoading(false)
      })
    return () => controller.abort()
  }, [retryKey])

  const refresh = () => {
    requestIdRef.current += 1
    setRetryKey((value) => value + 1)
  }

  const process = async (requestId: number, action: ChargeAction): Promise<ProcessResult> => {
    setProcessingId(requestId)
    setProcessingAction(action)
    try {
      if (action === 'approve') await approveChargeRequest(requestId)
      else await rejectChargeRequest(requestId)
      refresh()
      return { ok: true }
    } catch (reason) {
      return { ok: false, message: getMyPageErrorMessage(reason) }
    } finally {
      setProcessingId(null)
      setProcessingAction(null)
    }
  }

  return {
    rows,
    isLoading,
    error,
    processingId,
    processingAction,
    refresh,
    approve: (requestId: number) => process(requestId, 'approve'),
    reject: (requestId: number) => process(requestId, 'reject'),
  }
}
