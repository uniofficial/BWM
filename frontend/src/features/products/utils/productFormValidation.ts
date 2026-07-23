import type { ProductCreateFormErrors, ProductCreateFormState } from '../../../types/product'
import {
  MAX_PRODUCT_PRICE,
  PRODUCT_CATEGORY_MAX_LENGTH,
  PRODUCT_DESCRIPTION_MAX_LENGTH,
  PRODUCT_TITLE_MAX_LENGTH,
} from '../constants/productForm'
import { buildAuctionEndAt } from './auctionSchedule'

export const INITIAL_PRODUCT_FORM: ProductCreateFormState = {
  title: '',
  category: '',
  startPrice: '',
  auctionDate: '',
  auctionMeridiem: '',
  auctionHour: '',
  auctionMinute: '',
  description: '',
}

export function validateProductCreateForm(
  values: ProductCreateFormState,
  now = Date.now(),
): ProductCreateFormErrors {
  const errors: ProductCreateFormErrors = {}
  const title = values.title.trim()
  const category = values.category.trim()
  const description = values.description.trim()

  if (!title) errors.title = '상품명을 입력해주세요.'
  else if (title.length > PRODUCT_TITLE_MAX_LENGTH) {
    errors.title = `상품명은 ${PRODUCT_TITLE_MAX_LENGTH}자 이하로 입력해주세요.`
  }

  if (!category) errors.category = '카테고리를 입력해주세요.'
  else if (category.length > PRODUCT_CATEGORY_MAX_LENGTH) {
    errors.category = `카테고리는 ${PRODUCT_CATEGORY_MAX_LENGTH}자 이하로 입력해주세요.`
  }

  const normalizedPrice = values.startPrice.trim()
  if (!normalizedPrice) errors.startPrice = '시작가를 입력해주세요.'
  else if (!/^\d+$/.test(normalizedPrice)) errors.startPrice = '시작가는 숫자만 입력해주세요.'
  else {
    const price = Number(normalizedPrice)
    if (!Number.isSafeInteger(price) || price > MAX_PRODUCT_PRICE) {
      errors.startPrice = '유효한 시작가를 입력해주세요.'
    } else if (price <= 0) {
      errors.startPrice = '시작가는 0원보다 커야 합니다.'
    }
  }

  const auctionEndAt = buildAuctionEndAt(values)
  if (!auctionEndAt) errors.auctionEndAt = '날짜와 오전·오후, 시, 분을 모두 선택해주세요.'
  else {
    const endTime = Date.parse(auctionEndAt)
    if (!Number.isFinite(endTime)) errors.auctionEndAt = '유효한 종료 시각을 선택해주세요.'
    else if (endTime <= now) errors.auctionEndAt = '경매 종료 시각은 현재보다 이후여야 합니다.'
  }

  if (description.length > PRODUCT_DESCRIPTION_MAX_LENGTH) {
    errors.description = `상품 설명은 ${PRODUCT_DESCRIPTION_MAX_LENGTH}자 이하로 입력해주세요.`
  }

  return errors
}

export function hasProductFormErrors(errors: ProductCreateFormErrors) {
  return Object.values(errors).some(Boolean)
}

export function isProductFormDirty(values: ProductCreateFormState, images: File[]) {
  return Object.values(values).some((value) => value.length > 0) || images.length > 0
}
