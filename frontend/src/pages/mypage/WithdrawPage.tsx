import { MyPageSectionHeader } from '../../features/mypage/components/MyPageSectionHeader'
import { WithdrawSection } from '../../features/mypage/components/WithdrawSection'

export function WithdrawPage() {
  return (
    <section aria-labelledby="withdraw-title">
      <MyPageSectionHeader id="withdraw-title" title="회원 탈퇴" description="탈퇴 전 제한사항과 현재 제공되는 임시 동작을 확인해주세요." />
      <WithdrawSection />
    </section>
  )
}
