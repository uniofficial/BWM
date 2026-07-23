import { useState } from 'react'
import { Button, Card, ConfirmDialog } from '../../../components/ui'
import { useAuth } from '../../../hooks/useAuth'
import { useToast } from '../../../hooks/useToast'

export function WithdrawSection() {
  const { withdraw } = useAuth()
  const { showToast } = useToast()
  const [open, setOpen] = useState(false)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [errorMessage, setErrorMessage] = useState<string | null>(null)

  const handleClose = () => {
    if (isSubmitting) return
    setOpen(false)
    setErrorMessage(null)
  }

  const handleConfirm = async () => {
    setIsSubmitting(true)
    setErrorMessage(null)
    try {
      await withdraw()
      setOpen(false)
      showToast({ message: '회원 탈퇴가 완료되었습니다.', variant: 'success' })
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : '회원 탈퇴에 실패했습니다.')
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <>
      <Card variant="danger">
        <h3 className="text-lg font-semibold text-ink">탈퇴 전 확인사항</h3>
        <div className="mt-3 grid gap-2 text-sm leading-6 text-ink-secondary">
          <p>회원 탈퇴 시 계정과 관련된 서비스 이용이 즉시 제한됩니다.</p>
          <p>진행 중인 경매의 판매자이거나 최고 입찰자인 경우 탈퇴가 제한됩니다.</p>
          <p>탈퇴 후에는 계정을 복구할 수 없습니다.</p>
        </div>
        <Button type="button" variant="danger" className="mt-6 w-full sm:w-auto" onClick={() => setOpen(true)}>
          회원 탈퇴
        </Button>
      </Card>
      <ConfirmDialog
        open={open}
        onClose={handleClose}
        onConfirm={handleConfirm}
        title="회원 탈퇴를 진행하시겠습니까?"
        description="탈퇴 후에는 계정을 복구할 수 없습니다."
        confirmLabel="탈퇴하기"
        isLoading={isSubmitting}
        loadingLabel="탈퇴 처리 중"
        errorMessage={errorMessage}
        danger
      />
    </>
  )
}
