import { AppHeader } from '../components/common/AppHeader'
import { ProductForm } from '../features/products/components/ProductForm'

export function ProductCreatePage() {
  return (
    <div className="min-h-screen bg-surface-secondary">
      <AppHeader />
      <main>
        <header className="border-b border-line bg-surface">
          <div className="mx-auto max-w-4xl px-4 py-12 sm:px-8 sm:py-14 lg:px-10">
            <h1 className="text-4xl font-bold leading-tight text-ink">상품 등록</h1>
            <p className="mt-3 max-w-xl text-base leading-7 text-ink-secondary">
              상품 정보와 경매 마감 시각을 입력해 판매를 시작하세요.
            </p>
          </div>
        </header>
        <div className="mx-auto max-w-4xl px-4 py-8 sm:px-8 sm:py-10 lg:px-10">
          <ProductForm />
        </div>
      </main>
    </div>
  )
}
