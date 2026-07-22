import { useEffect, useId, useRef, useState, type ChangeEvent } from 'react'
import { cx } from '../../../utils/cx'
import { PRODUCT_IMAGE_ACCEPT } from '../constants/productForm'
import { validateSelectedImages } from '../utils/imageValidation'
import { ProductImagePreview } from './ProductImagePreview'

interface ImagePreviewRecord {
  key: string
  url: string
  file: File
}

interface ProductImageUploaderProps {
  images: File[]
  error?: string
  disabled: boolean
  onChange: (images: File[]) => void
  onError: (error?: string) => void
}

function imageKey(file: File) {
  return `${file.name}:${file.size}:${file.lastModified}:${file.type}`
}

export function ProductImageUploader({
  images,
  error,
  disabled,
  onChange,
  onError,
}: ProductImageUploaderProps) {
  const inputId = useId()
  const fileInputRef = useRef<HTMLInputElement>(null)
  const [previews, setPreviews] = useState<ImagePreviewRecord[]>([])

  useEffect(() => {
    const nextPreviews = images.map((file) => ({
      key: imageKey(file),
      file,
      url: URL.createObjectURL(file),
    }))
    setPreviews(nextPreviews)
    return () => nextPreviews.forEach((preview) => URL.revokeObjectURL(preview.url))
  }, [images])

  const handleFiles = (event: ChangeEvent<HTMLInputElement>) => {
    const selected = Array.from(event.target.files || [])
    const result = validateSelectedImages(images, selected)
    onChange(result.files)
    onError(result.error)
    event.target.value = ''
  }

  const removeImage = (key: string) => {
    onChange(images.filter((file) => imageKey(file) !== key))
    onError(undefined)
    if (fileInputRef.current) fileInputRef.current.value = ''
  }

  const helpId = `${inputId}-help`
  const errorId = `${inputId}-error`

  return (
    <div>
      <div className="flex flex-wrap items-center justify-between gap-3">
        <div>
          <label htmlFor={inputId} className="text-sm font-semibold text-ink">상품 이미지</label>
          <p id={helpId} className="mt-1 text-xs leading-5 text-ink-muted">
            선택 사항 · JPG, PNG, WebP · 파일당 10MB 이하 · 첫 번째 이미지가 대표 이미지
          </p>
        </div>
        <label
          htmlFor={inputId}
          aria-disabled={disabled || undefined}
          className={cx(
            'inline-flex min-h-11 cursor-pointer items-center justify-center rounded-card border border-line bg-surface px-4 text-sm font-semibold text-ink transition-colors duration-200',
            'hover:border-brand hover:text-brand-dark focus-within:ring-3 focus-within:ring-brand/30',
            disabled && 'cursor-not-allowed bg-surface-secondary text-ink-muted opacity-70',
          )}
        >
          이미지 선택
          <input
            ref={fileInputRef}
            id={inputId}
            className="sr-only"
            type="file"
            accept={PRODUCT_IMAGE_ACCEPT}
            multiple
            disabled={disabled}
            aria-invalid={error ? true : undefined}
            aria-describedby={error ? `${helpId} ${errorId}` : helpId}
            onChange={handleFiles}
          />
        </label>
      </div>

      {error && <p id={errorId} className="mt-2 text-xs font-medium leading-5 text-danger" role="alert">{error}</p>}

      {previews.length > 0 ? (
        <ul className="mt-4 grid grid-cols-2 gap-3 sm:grid-cols-3 lg:grid-cols-4">
          {previews.map((preview, index) => (
            <ProductImagePreview
              key={preview.key}
              file={preview.file}
              previewUrl={preview.url}
              index={index}
              disabled={disabled}
              onRemove={() => removeImage(preview.key)}
            />
          ))}
        </ul>
      ) : (
        <div className="mt-4 grid min-h-32 place-items-center rounded-card border border-dashed border-line bg-surface-secondary px-5 text-center text-sm leading-6 text-ink-muted">
          상품을 잘 보여주는 이미지를 선택해주세요.
        </div>
      )}
    </div>
  )
}
