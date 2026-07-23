import { useState } from 'react'
import { Button, Card, ConfirmDialog } from '../../../components/ui'
import { useAuth } from '../../../hooks/useAuth'
import { useToast } from '../../../hooks/useToast'

export function WithdrawSection() {
  const { withdrawTemporarily } = useAuth()
  const { showToast } = useToast()
  const [open, setOpen] = useState(false)

  const handleConfirm = () => {
    // TODO: 백엔드 회원 탈퇴 API 구현 후 실제 탈퇴 요청으로 교체
    setOpen(false)
    showToast({
      message: '회원 탈퇴 API가 아직 구현되지 않아 임시 로그아웃 처리되었습니다.',
      variant: 'warning',
      duration: 5000,
    })
    withdrawTemporarily()
  }

  return (
    <>
      <Card variant="danger">
        <h3 className="text-lg font-semibold text-ink">탈퇴 전 확인사항</h3>
        <div className="mt-3 grid gap-2 text-sm leading-6 text-ink-secondary">
          <p>회원 탈퇴 시 계정과 관련된 서비스 이용이 제한될 수 있습니다.</p>
          <p>진행 중인 경매, 입찰, 잔액이 있는 경우 탈퇴가 제한될 수 있으며 실제 가능 여부는 백엔드에서 검증되어야 합니다.</p>
          <p className="font-semibold text-danger">현재 회원 탈퇴 API는 연결되어 있지 않아 실제 계정 삭제가 수행되지 않습니다.</p>
        </div>
        <Button type="button" variant="danger" className="mt-6 w-full sm:w-auto" onClick={() => setOpen(true)}>
          임시 탈퇴 처리 안내
        </Button>
      </Card>
      <ConfirmDialog
        open={open}
        onClose={() => setOpen(false)}
        onConfirm={handleConfirm}
        title="회원 탈퇴를 진행하시겠습니까?"
        description="현재는 회원 탈퇴 API가 구현되지 않아 실제 계정 삭제 없이 로그아웃만 처리됩니다."
        confirmLabel="임시 로그아웃 처리"
        danger
      />
    </>
  )
}
