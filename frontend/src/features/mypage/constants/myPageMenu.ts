export interface MyPageMenuItem {
  label: string
  to: string
  group?: string
}

export const MY_PAGE_MENU: MyPageMenuItem[] = [
  { label: '현재 잔액', to: '/mypage/wallet', group: '내 지갑' },
  { label: '충전 요청', to: '/mypage/wallet/charge', group: '내 지갑' },
  { label: '요청 내역', to: '/mypage/wallet/charges', group: '내 지갑' },
  { label: '거래 내역', to: '/mypage/wallet/transactions', group: '내 지갑' },
  { label: '내 상품', to: '/mypage/products' },
  { label: '내 입찰', to: '/mypage/bids' },
  { label: '회원 탈퇴', to: '/mypage/withdraw' },
]
