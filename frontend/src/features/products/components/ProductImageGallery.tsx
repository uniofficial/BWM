import { useEffect, useState } from 'react'
import { cx } from '../../../utils/cx'

interface ProductImageGalleryProps {
  imageUrls: string[]
  productTitle: string
}

export function ProductImageGallery({ imageUrls, productTitle }: ProductImageGalleryProps) {
  const [selectedIndex, setSelectedIndex] = useState(0)
  const [failedUrls, setFailedUrls] = useState<string[]>([])

  useEffect(() => {
    if (selectedIndex >= imageUrls.length) setSelectedIndex(0)
    setFailedUrls((current) => current.filter((url) => imageUrls.includes(url)))
  }, [imageUrls, selectedIndex])

  const selectedImage = imageUrls[selectedIndex]
  const selectedImageFailed = !selectedImage || failedUrls.includes(selectedImage)
  const markFailed = (url: string) => setFailedUrls((current) => (current.includes(url) ? current : [...current, url]))

  return (
    <section aria-label="상품 이미지">
      <div className="aspect-square overflow-hidden rounded-card border border-line bg-surface">
        {selectedImageFailed ? (
          <div className="flex h-full items-center justify-center px-6 text-center text-sm text-ink-muted" role="img" aria-label={`${productTitle} 상품 이미지 없음`}>
            이미지 준비 중
          </div>
        ) : (
          <img
            src={selectedImage}
            alt={`${productTitle} 상품 이미지 ${selectedIndex + 1}`}
            loading="lazy"
            className="h-full w-full object-contain"
            onError={() => markFailed(selectedImage)}
          />
        )}
      </div>

      {imageUrls.length > 1 && (
        <div className="mt-3 flex gap-2 overflow-x-auto pb-2" aria-label="상품 이미지 선택">
          {imageUrls.map((url, index) => {
            const failed = failedUrls.includes(url)
            return (
              <button
                key={url}
                type="button"
                onClick={() => setSelectedIndex(index)}
                className={cx(
                  'h-16 w-16 shrink-0 overflow-hidden rounded-card border bg-surface-secondary transition-colors duration-200',
                  'focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand/30',
                  selectedIndex === index ? 'border-brand' : 'border-line hover:border-brand-border',
                )}
                aria-label={`${index + 1}번 상품 이미지 보기`}
                aria-pressed={selectedIndex === index}
              >
                {failed ? (
                  <span className="grid h-full place-items-center text-xs text-ink-muted">없음</span>
                ) : (
                  <img
                    src={url}
                    alt=""
                    loading="lazy"
                    className="h-full w-full object-cover"
                    onError={() => markFailed(url)}
                  />
                )}
              </button>
            )
          })}
        </div>
      )}
    </section>
  )
}
