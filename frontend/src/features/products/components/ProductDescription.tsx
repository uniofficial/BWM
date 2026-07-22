export function ProductDescription({ description }: { description: string }) {
  return (
    <section className="border-t border-line py-9" aria-labelledby="product-description-heading">
      <h2 id="product-description-heading" className="text-xl font-semibold text-ink">
        상품 설명
      </h2>
      <p className="mt-4 whitespace-pre-wrap break-words text-sm leading-7 text-ink-secondary">
        {description || '상품 설명이 등록되지 않았습니다.'}
      </p>
    </section>
  )
}
