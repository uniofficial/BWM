import { useEffect, useRef, useState, type FormEvent } from 'react'
import { Button, Dialog, Input } from '../../../components/ui'
import type { ProductDetail } from '../../../types/product'
import type { BidSubmissionResult } from '../hooks/useBidSubmission'
import { validateBidAmount } from '../utils/bidValidation'
import { formatPrice } from '../utils/priceFormat'

interface BidDialogProps {
  open: boolean
  product: ProductDetail
  isSubmitting: boolean
  onSubmit: (amount: number) => Promise<BidSubmissionResult>
  onClose: () => void
}

function validateAuctionState(product: ProductDetail) {
  if (product.status !== 'OPEN') return '현재 진행 중인 경매가 아닙니다.'
  const endTime = product.auctionEndAt ? Date.parse(product.auctionEndAt) : Number.NaN
  if (!Number.isFinite(endTime)) return '경매 마감 정보를 확인할 수 없습니다.'
  if (endTime <= Date.now()) return '이미 종료된 경매입니다.'
  if (product.minimumBidAmount === null) return '현재 가격 정보를 확인할 수 없습니다.'
  return null
}

export function BidDialog({ open, product, isSubmitting, onSubmit, onClose }: BidDialogProps) {
  const [amount, setAmount] = useState('')
  const [submittedError, setSubmittedError] = useState<string | undefined>()
  const [commonError, setCommonError] = useState<string | null>(null)
  const initialMinimumRef = useRef<number | null>(null)
  const wasOpenRef = useRef(false)

  useEffect(() => {
    if (open && !wasOpenRef.current) {
      setAmount('')
      setSubmittedError(undefined)
      setCommonError(null)
      initialMinimumRef.current = product.minimumBidAmount
    }
    wasOpenRef.current = open
  }, [open, product.minimumBidAmount])

  const liveValidation = amount ? validateBidAmount(amount, product.minimumBidAmount) : null
  const displayedError = submittedError || liveValidation?.error
  const minimumBidChanged =
    initialMinimumRef.current !== null &&
    product.minimumBidAmount !== null &&
    initialMinimumRef.current !== product.minimumBidAmount

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (isSubmitting) return

    const auctionError = validateAuctionState(product)
    if (auctionError) {
      setCommonError(auctionError)
      return
    }

    const validation = validateBidAmount(amount, product.minimumBidAmount)
    setSubmittedError(validation.error)
    setCommonError(null)
    if (validation.error || validation.amount === null) return

    const result = await onSubmit(validation.amount)
    if (result.ok) return
    if ('ignored' in result) return
    if (result.error.placement === 'field') setSubmittedError(result.error.message)
    if (result.error.placement === 'dialog') setCommonError(result.error.message)
  }

  const handleClose = () => {
    if (!isSubmitting) onClose()
  }

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      title="입찰하기"
      description={product.title}
      closeOnBackdrop={!isSubmitting}
      showCloseButton={!isSubmitting}
      footer={
        <>
          <Button type="button" variant="secondary" disabled={isSubmitting} onClick={handleClose}>
            취소
          </Button>
          <Button
            type="submit"
            form="bid-form"
            isLoading={isSubmitting}
            loadingLabel="입찰 처리 중"
            disabled={Boolean(displayedError)}
          >
            입찰 확인
          </Button>
        </>
      }
    >
      <form id="bid-form" onSubmit={handleSubmit} noValidate aria-busy={isSubmitting || undefined}>
        <dl className="grid gap-3 rounded-inline bg-surface-secondary p-4 text-sm">
          <div className="flex items-center justify-between gap-4">
            <dt className="text-ink-secondary">{product.bidCount > 0 ? '현재 최고 입찰가' : '시작가'}</dt>
            <dd className="font-semibold text-ink">{formatPrice(product.currentPrice)}</dd>
          </div>
          <div className="flex items-center justify-between gap-4">
            <dt className="text-ink-secondary">최소 입찰 가능 금액</dt>
            <dd className="font-semibold text-brand-dark">{formatPrice(product.minimumBidAmount)}</dd>
          </div>
        </dl>

        {minimumBidChanged && (
          <div
            className="mt-4 rounded-inline border border-caution/30 bg-[var(--ds-warning-light)] px-4 py-3 text-sm leading-6 text-ink"
            role="status"
          >
            다른 사용자의 입찰로 최소 입찰 금액이 변경되었습니다. 입력한 금액을 다시 확인해주세요.
          </div>
        )}

        <Input
          containerClassName="mt-5"
          label="입찰 금액"
          name="bidAmount"
          type="text"
          inputMode="numeric"
          autoComplete="off"
          placeholder="입찰 금액을 숫자로 입력해주세요"
          value={amount}
          error={displayedError}
          disabled={isSubmitting}
          required
          suffix="원"
          onChange={(event) => {
            setAmount(event.target.value)
            setSubmittedError(undefined)
            setCommonError(null)
          }}
          onBlur={() => {
            if (/^\d+$/.test(amount)) setAmount(amount.replace(/^0+(?=\d)/, ''))
          }}
        />

        <p className="mt-3 text-xs leading-5 text-ink-muted">입찰 후에는 취소할 수 없습니다.</p>
        {commonError && (
          <div
            className="mt-4 rounded-inline border border-danger/30 bg-[var(--ds-error-light)] px-4 py-3 text-sm leading-6 text-danger"
            role="alert"
          >
            {commonError}
          </div>
        )}
      </form>
    </Dialog>
  )
}
