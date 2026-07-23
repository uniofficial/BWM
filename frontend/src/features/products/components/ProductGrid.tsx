import type { ProductListItem } from '../../../types/product'
import { ProductCard } from './ProductCard'

interface ProductGridProps {
  products: ProductListItem[]
  now: number
}

export function ProductGrid({ products, now }: ProductGridProps) {
  return (
    <div className="grid grid-cols-1 gap-5 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4">
      {products.map((product) => (
        <ProductCard key={product.id} product={product} now={now} />
      ))}
    </div>
  )
}
