import { AppHeader } from '../components/common/AppHeader'
import { ProductForm } from '../features/products/components/ProductForm'

export function ProductCreatePage() {
  return (
    <div className="min-h-screen bg-surface-secondary">
      <AppHeader />
      <main className="mx-auto max-w-4xl px-4 py-8 sm:px-8 sm:py-10 lg:px-10">
        <div className="mb-7">
          <p className="text-sm font-semibold text-brand-dark">판매 시작</p>
          <h1 className="mt-2 text-3xl font-bold text-ink">상품 등록</h1>
          <p className="mt-2 text-sm leading-6 text-ink-secondary">경매에 등록할 상품 정보를 입력해주세요.</p>
        </div>
        <ProductForm />
      </main>
    </div>
  )
}
