import { useEffect, useState, type FormEvent } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { Button, Card, ConfirmDialog, Input, Textarea } from '../../../components/ui'
import { useAuth } from '../../../hooks/useAuth'
import { useToast } from '../../../hooks/useToast'
import type { ProductCreateFormState } from '../../../types/product'
import {
  MINIMUM_BID_INCREMENT,
  PRODUCT_CATEGORY_MAX_LENGTH,
  PRODUCT_DESCRIPTION_MAX_LENGTH,
  PRODUCT_TITLE_MAX_LENGTH,
} from '../constants/productForm'
import { useProductCreation } from '../hooks/useProductCreation'
import { mapProductFormToRequest } from '../utils/productFormMapper'
import {
  hasProductFormErrors,
  INITIAL_PRODUCT_FORM,
  isProductFormDirty,
  validateProductCreateForm,
} from '../utils/productFormValidation'
import { ProductImageUploader } from './ProductImageUploader'

function toDateTimeLocalMinimum(date: Date) {
  const pad = (value: number) => String(value).padStart(2, '0')
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`
}

export function ProductForm() {
  const navigate = useNavigate()
  const location = useLocation()
  const { accessToken, isAuthenticated, isInitializing } = useAuth()
  const { showToast } = useToast()
  const [values, setValues] = useState<ProductCreateFormState>(INITIAL_PRODUCT_FORM)
  const [images, setImages] = useState<File[]>([])
  const [errors, setErrors] = useState<ReturnType<typeof validateProductCreateForm>>({})
  const [cancelDialogOpen, setCancelDialogOpen] = useState(false)
  const { submitProduct, isSubmitting } = useProductCreation(
    isAuthenticated && !isInitializing && Boolean(accessToken),
  )
  const dirty = isProductFormDirty(values, images)
  const minimumEndAt = toDateTimeLocalMinimum(new Date(Date.now() + 60_000))

  useEffect(() => {
    if (!dirty || isSubmitting) return undefined
    const handleBeforeUnload = (event: BeforeUnloadEvent) => {
      event.preventDefault()
      event.returnValue = ''
    }
    window.addEventListener('beforeunload', handleBeforeUnload)
    return () => window.removeEventListener('beforeunload', handleBeforeUnload)
  }, [dirty, isSubmitting])

  const updateField = (field: keyof ProductCreateFormState, value: string) => {
    setValues((current) => ({ ...current, [field]: value }))
    setErrors((current) => ({ ...current, [field]: undefined, form: undefined }))
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (isSubmitting) return

    const validationErrors = validateProductCreateForm(values)
    setErrors(validationErrors)
    if (hasProductFormErrors(validationErrors)) return

    const result = await submitProduct(mapProductFormToRequest(values), images)
    if (!result.ok && result.phase === 'ignored') return

    if (result.ok) {
      showToast({ message: '상품이 등록되었습니다.', variant: 'success' })
      navigate(`/products/${result.product.itemId}`, { replace: true })
      return
    }

    if (result.phase === 'images') {
      showToast({
        message: `상품은 등록되었지만 이미지를 업로드하지 못했습니다. ${result.error.message}`,
        variant: 'warning',
        duration: 6000,
      })
      navigate(`/products/${result.product.itemId}`, { replace: true })
      return
    }

    if (result.error.kind === 'auth') {
      navigate('/login', { replace: true, state: { from: location } })
      return
    }

    setErrors((current) => ({ ...current, form: result.error.message }))
  }

  const handleCancel = () => {
    if (isSubmitting) return
    if (dirty) setCancelDialogOpen(true)
    else navigate('/products')
  }

  return (
    <>
      <Card className="overflow-hidden !p-0">
        <form onSubmit={handleSubmit} noValidate aria-busy={isSubmitting || undefined}>
          <section className="p-5 sm:p-7" aria-labelledby="product-basic-heading">
            <h2 id="product-basic-heading" className="text-xl font-semibold text-ink">기본 정보</h2>
            <div className="mt-5 grid gap-5 sm:grid-cols-2">
              <Input
                containerClassName="sm:col-span-2"
                label="상품명"
                name="title"
                placeholder="상품명을 입력해주세요"
                value={values.title}
                error={errors.title}
                maxLength={PRODUCT_TITLE_MAX_LENGTH}
                disabled={isSubmitting}
                required
                onChange={(event) => updateField('title', event.target.value)}
              />
              <Input
                label="카테고리"
                name="category"
                placeholder="예: 전자기기"
                helperText="카테고리는 직접 입력합니다."
                value={values.category}
                error={errors.category}
                maxLength={PRODUCT_CATEGORY_MAX_LENGTH}
                disabled={isSubmitting}
                required
                onChange={(event) => updateField('category', event.target.value)}
              />
              <Input
                label="시작가"
                name="startPrice"
                type="text"
                inputMode="numeric"
                autoComplete="off"
                placeholder="시작가를 숫자로 입력해주세요"
                helperText={`입찰은 현재가보다 ${MINIMUM_BID_INCREMENT.toLocaleString('ko-KR')}P 높은 금액부터 가능합니다.`}
                value={values.startPrice}
                error={errors.startPrice}
                disabled={isSubmitting}
                required
                suffix="P"
                onChange={(event) => updateField('startPrice', event.target.value)}
                onBlur={() => {
                  if (/^\d+$/.test(values.startPrice)) {
                    updateField('startPrice', values.startPrice.replace(/^0+(?=\d)/, ''))
                  }
                }}
              />
              <div className="sm:col-span-2">
                <Textarea
                  label="상품 설명"
                  name="description"
                  placeholder="상품의 상태와 특징을 입력해주세요"
                  value={values.description}
                  error={errors.description}
                  maxLength={PRODUCT_DESCRIPTION_MAX_LENGTH}
                  showCount
                  disabled={isSubmitting}
                  onChange={(event) => updateField('description', event.target.value)}
                />
                <p className="mt-2 text-xs leading-5 text-ink-muted">선택 입력이며 일반 텍스트로 저장됩니다.</p>
              </div>
            </div>
          </section>

          <section className="border-t border-line p-5 sm:p-7" aria-labelledby="auction-schedule-heading">
            <h2 id="auction-schedule-heading" className="text-xl font-semibold text-ink">경매 일정</h2>
            <p className="mt-2 text-sm leading-6 text-ink-secondary">등록 즉시 경매가 시작됩니다.</p>
            <Input
              containerClassName="mt-5 max-w-md"
              label="경매 종료 시각"
              name="auctionEndAt"
              type="datetime-local"
              min={minimumEndAt}
              value={values.auctionEndAt}
              error={errors.auctionEndAt}
              helperText="현재 기기에 표시되는 현지 시각 기준으로 입력합니다."
              disabled={isSubmitting}
              required
              onChange={(event) => updateField('auctionEndAt', event.target.value)}
            />
          </section>

          <section className="border-t border-line p-5 sm:p-7" aria-labelledby="product-images-heading">
            <h2 id="product-images-heading" className="sr-only">상품 이미지</h2>
            <ProductImageUploader
              images={images}
              error={errors.images}
              disabled={isSubmitting}
              onChange={setImages}
              onError={(error) => setErrors((current) => ({ ...current, images: error, form: undefined }))}
            />
          </section>

          <div className="border-t border-line p-5 sm:p-7">
            {errors.form && (
              <div className="mb-5 rounded-card border border-danger/30 bg-[var(--ds-error-light)] px-4 py-3 text-sm leading-6 text-danger" role="alert">
                {errors.form}
              </div>
            )}
            <div className="flex flex-col-reverse gap-2 sm:flex-row sm:justify-end">
              <Button type="button" variant="secondary" className="w-full sm:w-auto" disabled={isSubmitting} onClick={handleCancel}>
                취소
              </Button>
              <Button
                type="submit"
                size="lg"
                className="w-full sm:w-auto"
                isLoading={isSubmitting}
                loadingLabel="상품 등록 중"
              >
                상품 등록
              </Button>
            </div>
          </div>
        </form>
      </Card>

      <ConfirmDialog
        open={cancelDialogOpen}
        title="상품 등록 취소"
        description="작성 중인 내용이 사라집니다. 상품 등록을 취소하시겠습니까?"
        confirmLabel="등록 취소"
        onClose={() => setCancelDialogOpen(false)}
        onConfirm={() => navigate('/products')}
      />
    </>
  )
}
