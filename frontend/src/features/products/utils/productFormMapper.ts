import type { ItemCreateRequest, ProductCreateFormState } from '../../../types/product'

export function mapProductFormToRequest(values: ProductCreateFormState): ItemCreateRequest {
  const description = values.description.trim()
  return {
    title: values.title.trim(),
    category: values.category.trim(),
    startPrice: Number(values.startPrice),
    auctionEndAt: values.auctionEndAt,
    description: description || null,
  }
}

