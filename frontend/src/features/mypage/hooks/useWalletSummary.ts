import axios from 'axios'
import { useEffect, useRef, useState } from 'react'
import { getMyWallet } from '../../../api/walletApi'
import type { WalletSummary } from '../../../types/wallet'
import { getMyPageErrorMessage } from '../utils/myPageFormat'

export function useWalletSummary() {
  const [wallet, setWallet] = useState<WalletSummary | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)
  const [retryKey, setRetryKey] = useState(0)
  const requestIdRef = useRef(0)

  useEffect(() => {
    const controller = new AbortController()
    const requestId = ++requestIdRef.current
    setIsLoading(true)
    setError(null)
    void getMyWallet(controller.signal)
      .then((nextWallet) => {
        if (requestId === requestIdRef.current && !controller.signal.aborted) setWallet(nextWallet)
      })
      .catch((reason: unknown) => {
        if (!axios.isCancel(reason) && requestId === requestIdRef.current) {
          setError(getMyPageErrorMessage(reason))
        }
      })
      .finally(() => {
        if (!controller.signal.aborted && requestId === requestIdRef.current) setIsLoading(false)
      })
    return () => {
      controller.abort()
      if (requestId === requestIdRef.current) requestIdRef.current += 1
    }
  }, [retryKey])

  return {
    wallet,
    isLoading,
    error,
    retry: () => setRetryKey((value) => value + 1),
  }
}
