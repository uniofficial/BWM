import { useState, type FormEvent } from 'react'
import { Button, Card, Input } from '../../../components/ui'
import { useToast } from '../../../hooks/useToast'
import { useChargeRequest } from '../hooks/useChargeRequest'
import { validateChargeAmount } from '../utils/chargeRequestValidation'

interface ChargeRequestFormProps {
  onRequestCreated: () => void
  onHistoryCheckNeeded: () => void
}

export function ChargeRequestForm({ onRequestCreated, onHistoryCheckNeeded }: ChargeRequestFormProps) {
  const { showToast } = useToast()
  const { submit, isSubmitting } = useChargeRequest()
  const [amount, setAmount] = useState('')
  const [error, setError] = useState<string | undefined>()
  const [formError, setFormError] = useState<string | null>(null)

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
      if (result.error.shouldRefreshHistory) onHistoryCheckNeeded()
      return
    }

    setAmount('')
    onRequestCreated()
    showToast({ message: '충전 요청이 접수되었습니다.', variant: 'success' })
  }

  return (
    <Card>
      <form className="grid gap-5" onSubmit={handleSubmit} noValidate aria-busy={isSubmitting}>
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
        <p className="text-sm leading-6 text-ink-secondary">
          충전 요청은 관리자 승인 후 잔액에 반영됩니다. 요청 직후에는 현재 잔액과 거래 내역이 변경되지 않습니다.
        </p>
        {formError && (
          <p className="rounded-card border border-danger/30 bg-[var(--ds-error-light)] px-4 py-3 text-sm leading-6 text-danger" role="alert">
            {formError}
          </p>
        )}
        <Button type="submit" className="w-full sm:w-auto sm:justify-self-start" isLoading={isSubmitting} loadingLabel="충전 요청 중...">
          충전 요청
        </Button>
      </form>
    </Card>
  )
}
