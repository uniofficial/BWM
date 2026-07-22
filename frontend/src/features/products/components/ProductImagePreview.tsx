import { useEffect, useState } from 'react'
import { Badge } from '../../../components/ui'

interface ProductImagePreviewProps {
  file: File
  previewUrl: string
  index: number
  disabled: boolean
  onRemove: () => void
}

function formatFileSize(bytes: number) {
  if (bytes < 1024 * 1024) return `${Math.max(1, Math.round(bytes / 1024))}KB`
  return `${(bytes / (1024 * 1024)).toFixed(1)}MB`
}

export function ProductImagePreview({
  file,
  previewUrl,
  index,
  disabled,
  onRemove,
}: ProductImagePreviewProps) {
  const [loadFailed, setLoadFailed] = useState(false)

  useEffect(() => setLoadFailed(false), [previewUrl])

  return (
    <li className="min-w-0 overflow-hidden rounded-content border border-line bg-surface">
      <div className="relative aspect-square bg-surface-secondary">
        {loadFailed ? (
          <div className="grid h-full place-items-center px-3 text-center text-xs text-ink-muted">
            미리보기를 불러올 수 없습니다.
          </div>
        ) : (
          <img
            src={previewUrl}
            alt={`${file.name} 미리보기`}
            className="h-full w-full object-cover"
            onError={() => setLoadFailed(true)}
          />
        )}
        {index === 0 && (
          <span className="absolute top-2 left-2">
            <Badge variant="primary">대표 이미지</Badge>
          </span>
        )}
        <button
          type="button"
          disabled={disabled}
          onClick={onRemove}
          className="absolute top-1 right-1 inline-flex min-h-11 min-w-11 items-center justify-center rounded-compact bg-surface/90 text-xl text-ink-secondary shadow-[var(--ds-shadow-card)] transition-colors duration-200 hover:bg-surface hover:text-danger focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand/30 disabled:cursor-not-allowed disabled:opacity-60"
          aria-label={`${file.name} 이미지 제거`}
        >
          <span aria-hidden="true">×</span>
        </button>
      </div>
      <div className="px-3 py-2">
        <p className="truncate text-xs font-medium text-ink" title={file.name}>{file.name}</p>
        <p className="mt-1 text-xs text-ink-muted">{formatFileSize(file.size)}</p>
      </div>
    </li>
  )
}
