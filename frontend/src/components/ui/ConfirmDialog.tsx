import { Button } from './Button'
import { Dialog } from './Dialog'

export interface ConfirmDialogProps {
  open: boolean
  onClose: () => void
  onConfirm: () => void
  title: string
  description: string
  confirmLabel?: string
  cancelLabel?: string
  danger?: boolean
  isLoading?: boolean
  loadingLabel?: string
  errorMessage?: string | null
  closeOnBackdrop?: boolean
}

export function ConfirmDialog({
  open,
  onClose,
  onConfirm,
  title,
  description,
  confirmLabel = '확인',
  cancelLabel = '취소',
  danger = false,
  isLoading = false,
  loadingLabel = '처리 중',
  errorMessage,
  closeOnBackdrop = true,
}: ConfirmDialogProps) {
  return (
    <Dialog
      open={open}
      onClose={isLoading ? () => undefined : onClose}
      title={title}
      description={description}
      closeOnBackdrop={!isLoading && closeOnBackdrop}
      showCloseButton={!isLoading}
      footer={
        <>
          <Button variant="secondary" onClick={onClose} disabled={isLoading}>
            {cancelLabel}
          </Button>
          <Button
            variant={danger ? 'danger' : 'primary'}
            onClick={onConfirm}
            isLoading={isLoading}
            loadingLabel={loadingLabel}
          >
            {confirmLabel}
          </Button>
        </>
      }
    >
      {errorMessage && (
        <p className="rounded-card border border-danger/30 bg-[var(--ds-error-light)] px-4 py-3 text-sm leading-6 text-danger" role="alert">
          {errorMessage}
        </p>
      )}
    </Dialog>
  )
}
