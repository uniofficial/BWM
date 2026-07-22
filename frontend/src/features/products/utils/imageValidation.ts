import {
  MAX_IMAGE_REQUEST_SIZE_BYTES,
  MAX_IMAGE_SIZE_BYTES,
  SUPPORTED_PRODUCT_IMAGE_TYPES,
} from '../constants/productForm'

interface ImageSelectionResult {
  files: File[]
  error?: string
}

function fileIdentity(file: File) {
  return `${file.name}:${file.size}:${file.lastModified}:${file.type}`
}

export function validateSelectedImages(existing: File[], selected: File[]): ImageSelectionResult {
  const files = [...existing]
  const identities = new Set(existing.map(fileIdentity))
  let totalSize = existing.reduce((sum, file) => sum + file.size, 0)
  const rejected: string[] = []

  selected.forEach((file) => {
    const identity = fileIdentity(file)
    if (identities.has(identity)) {
      rejected.push(`${file.name}: 이미 선택한 파일입니다.`)
      return
    }
    if (file.size <= 0) {
      rejected.push(`${file.name}: 빈 파일은 선택할 수 없습니다.`)
      return
    }
    if (!SUPPORTED_PRODUCT_IMAGE_TYPES.includes(file.type as (typeof SUPPORTED_PRODUCT_IMAGE_TYPES)[number])) {
      rejected.push(`${file.name}: JPG, PNG, WebP 이미지만 선택할 수 있습니다.`)
      return
    }
    if (file.size > MAX_IMAGE_SIZE_BYTES) {
      rejected.push(`${file.name}: 파일 크기는 10MB 이하여야 합니다.`)
      return
    }
    if (totalSize + file.size > MAX_IMAGE_REQUEST_SIZE_BYTES) {
      rejected.push(`${file.name}: 전체 이미지 용량은 50MB 이하여야 합니다.`)
      return
    }

    identities.add(identity)
    totalSize += file.size
    files.push(file)
  })

  return { files, error: rejected.length > 0 ? rejected.join(' ') : undefined }
}

