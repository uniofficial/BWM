import type { ItemCreateRequest, ProductCreateFormState } from '../../../types/product'
import { buildAuctionEndAt } from './auctionSchedule'

export function mapProductFormToRequest(values: ProductCreateFormState): ItemCreateRequest {
  const description = values.description.trim()
  return {
    title: values.title.trim(),
    category: values.category.trim(),
    startPrice: Number(values.startPrice),
    auctionEndAt: buildAuctionEndAt(values),
    description: description || null,
  }
}
