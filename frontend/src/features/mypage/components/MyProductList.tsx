import type { MyProductItem } from '../../../types/myProduct'
import { MyProductCard } from './MyProductCard'

interface MyProductListProps {
  products: MyProductItem[]
  now: number
  endingProductId: number | null
  onEndAuction: (product: MyProductItem) => void
}

export function MyProductList({ products, now, endingProductId, onEndAuction }: MyProductListProps) {
  return (
    <div className="grid gap-4 sm:grid-cols-2">
      {products.map((product) => (
        <MyProductCard
          key={product.id}
          product={product}
          now={now}
          isEnding={endingProductId === product.id}
          onEndAuction={onEndAuction}
        />
      ))}
    </div>
  )
}
