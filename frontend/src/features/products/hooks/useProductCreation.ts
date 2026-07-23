import { useCallback, useEffect, useRef, useState } from 'react'
import { createProduct, uploadProductImages } from '../../../api/productApi'
import type { ItemCreateRequest, ItemCreateResponse } from '../../../types/product'
import {
  mapProductCreationError,
  type ProductCreationError,
} from '../utils/productCreationErrors'

export type ProductCreationResult =
  | { ok: true; product: ItemCreateResponse }
  | { ok: false; phase: 'product'; error: ProductCreationError }
  | { ok: false; phase: 'images'; product: ItemCreateResponse; error: ProductCreationError }
  | { ok: false; phase: 'ignored' }

const duplicateError: ProductCreationError = {
  kind: 'conflict',
  message: '이미 상품 등록 요청을 처리하고 있습니다.',
  status: null,
  code: null,
}

const authenticationError: ProductCreationError = {
  kind: 'auth',
  message: '상품을 등록하려면 로그인이 필요합니다.',
  status: 401,
  code: 'UNAUTHORIZED',
}

export function useProductCreation(canCreate: boolean) {
  const [isSubmitting, setIsSubmitting] = useState(false)
  const inFlightRef = useRef(false)
  const mountedRef = useRef(true)

  useEffect(() => {
    mountedRef.current = true
    return () => {
      mountedRef.current = false
    }
  }, [])

  const submitProduct = useCallback(async (
    request: ItemCreateRequest,
    images: File[],
  ): Promise<ProductCreationResult> => {
    if (inFlightRef.current) return { ok: false, phase: 'product', error: duplicateError }
    if (!canCreate) return { ok: false, phase: 'product', error: authenticationError }

    inFlightRef.current = true
    if (mountedRef.current) setIsSubmitting(true)
    const finishSubmission = () => {
      inFlightRef.current = false
      if (mountedRef.current) setIsSubmitting(false)
    }

    let product: ItemCreateResponse
    try {
      product = await createProduct(request)
    } catch (error) {
      finishSubmission()
      if (!mountedRef.current) return { ok: false, phase: 'ignored' }
      return { ok: false, phase: 'product', error: mapProductCreationError(error) }
    }

    if (images.length === 0) {
      finishSubmission()
      if (!mountedRef.current) return { ok: false, phase: 'ignored' }
      return { ok: true, product }
    }

    try {
      await uploadProductImages(product.itemId, images)
      if (!mountedRef.current) return { ok: false, phase: 'ignored' }
      return { ok: true, product }
    } catch (error) {
      if (!mountedRef.current) return { ok: false, phase: 'ignored' }
      return { ok: false, phase: 'images', product, error: mapProductCreationError(error) }
    } finally {
      finishSubmission()
    }
  }, [canCreate])

  return { submitProduct, isSubmitting }
}
