import { Navigate, Route, Routes } from 'react-router-dom'
import { DesignSystemPage } from '../pages/DesignSystemPage'
import { LoginPage } from '../pages/LoginPage'
import { NotFoundPage } from '../pages/NotFoundPage'
import { ProductCreatePage } from '../pages/ProductCreatePage'
import { ProductDetailPage } from '../pages/ProductDetailPage'
import { ProductListPage } from '../pages/ProductListPage'
import { RegisterPage } from '../pages/RegisterPage'
import { ChargeHistoryPage } from '../pages/mypage/ChargeHistoryPage'
import { MyBidsPage } from '../pages/mypage/MyBidsPage'
import { MyProductsPage } from '../pages/mypage/MyProductsPage'
import { TransactionHistoryPage } from '../pages/mypage/TransactionHistoryPage'
import { WalletChargePage } from '../pages/mypage/WalletChargePage'
import { WalletPage } from '../pages/mypage/WalletPage'
import { WithdrawPage } from '../pages/mypage/WithdrawPage'
import { MyPageLayout } from '../features/mypage/components/MyPageLayout'
import { GuestRoute } from './GuestRoute'
import { ProtectedRoute } from './ProtectedRoute'

export function AppRoutes() {
  return (
    <Routes>
      <Route path="/design-system" element={<DesignSystemPage />} />
      <Route element={<GuestRoute authenticatedRedirectTo="/products" />}>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/register" element={<RegisterPage />} />
      </Route>
      <Route path="/products" element={<ProductListPage />} />
      <Route path="/products/:productId" element={<ProductDetailPage />} />
      <Route element={<ProtectedRoute />}>
        <Route path="/products/new" element={<ProductCreatePage />} />
        <Route path="/mypage" element={<MyPageLayout />}>
          <Route index element={<Navigate to="wallet" replace />} />
          <Route path="wallet" element={<WalletPage />} />
          <Route path="wallet/charge" element={<WalletChargePage />} />
          <Route path="wallet/charges" element={<ChargeHistoryPage />} />
          <Route path="wallet/transactions" element={<TransactionHistoryPage />} />
          <Route path="products" element={<MyProductsPage />} />
          <Route path="bids" element={<MyBidsPage />} />
          <Route path="withdraw" element={<WithdrawPage />} />
        </Route>
      </Route>
      <Route path="/" element={<Navigate to="/products" replace />} />
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  )
}
