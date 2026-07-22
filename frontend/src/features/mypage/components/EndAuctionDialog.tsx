import { ConfirmDialog } from '../../../components/ui'
import { formatPoints } from '../utils/myPageFormat'

interface EndAuctionDialogProps {
  open: boolean
  productTitle: string
  currentPrice: number | null
  hasHighestBidder: boolean
  isLoading: boolean
  errorMessage?: string | null
  onClose: () => void
  onConfirm: () => void
}

export function EndAuctionDialog({ open, productTitle, currentPrice, hasHighestBidder, isLoading, errorMessage, onClose, onConfirm }: EndAuctionDialogProps) {
  return (
    <ConfirmDialog
      open={open}
      onClose={onClose}
      onConfirm={onConfirm}
      title="경매를 종료하시겠습니까?"
      description={`'${productTitle}' 경매를 종료합니다. 현재 최고가는 ${formatPoints(currentPrice)}이며 ${hasHighestBidder ? '현재 조회 기준 최고 입찰자가 있습니다.' : '현재 조회 기준 입찰자가 없습니다.'} 최종 낙찰·유찰 결과는 서버 상태로 결정되며 종료 후에는 진행 상태로 되돌릴 수 없습니다.`}
      confirmLabel="경매 종료"
      isLoading={isLoading}
      loadingLabel="경매 종료 중..."
      errorMessage={errorMessage}
      danger
    />
  )
}
