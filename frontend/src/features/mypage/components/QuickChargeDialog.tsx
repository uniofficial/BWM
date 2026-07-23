import { useState, type FormEvent } from 'react'
import { Button, Dialog, Input } from '../../../components/ui'
import { useToast } from '../../../hooks/useToast'
import { useChargeRequest } from '../hooks/useChargeRequest'
import { validateChargeAmount } from '../utils/chargeRequestValidation'

interface QuickChargeDialogProps {
  open: boolean
  onClose: () => void
}

export function QuickChargeDialog({ open, onClose }: QuickChargeDialogProps) {
  const { showToast } = useToast()
  const { submit, isSubmitting } = useChargeRequest()
  const [amount, setAmount] = useState('')
  const [error, setError] = useState<string | undefined>()
  const [formError, setFormError] = useState<string | null>(null)

  const handleClose = () => {
    if (isSubmitting) return
    setAmount('')
    setError(undefined)
    setFormError(null)
    onClose()
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (isSubmitting) return
    setFormError(null)

    const validation = validateChargeAmount(amount)
    if (!validation.valid) {
      setError(validation.message)
      return
    }
    setError(undefined)

    const result = await submit(validation.amount)
    if (!result.ok && 'ignored' in result) return
    if (!result.ok) {
      if (result.error.placement === 'field') setError(result.error.message)
      else setFormError(result.error.message)
      return
    }

    setAmount('')
    showToast({ message: '충전 요청이 접수되었습니다.', variant: 'success' })
    onClose()
  }

  return (
    <Dialog
      open={open}
      onClose={handleClose}
      title="빠른 충전"
      description="충전 요청은 관리자 승인 후 잔액에 반영됩니다."
      closeOnBackdrop={!isSubmitting}
      showCloseButton={!isSubmitting}
      footer={
        <>
          <Button type="button" variant="secondary" disabled={isSubmitting} onClick={handleClose}>
            취소
          </Button>
          <Button type="submit" form="quick-charge-form" isLoading={isSubmitting} loadingLabel="충전 요청 중">
            충전 요청
          </Button>
        </>
      }
    >
      <form id="quick-charge-form" onSubmit={handleSubmit} noValidate aria-busy={isSubmitting}>
        <Input
          label="충전 금액"
          inputMode="numeric"
          autoComplete="off"
          placeholder="충전할 금액을 입력하세요"
          suffix="원"
          required
          disabled={isSubmitting}
          value={amount}
          error={error}
          onChange={(event) => {
            setAmount(event.target.value)
            if (error) setError(undefined)
            if (formError) setFormError(null)
          }}
        />
        {formError && (
          <p
            className="mt-3 rounded-inline border border-danger/30 bg-[var(--ds-error-light)] px-4 py-3 text-sm leading-6 text-danger"
            role="alert"
          >
            {formError}
          </p>
        )}
      </form>
    </Dialog>
  )
}
