import { useEffect, useState, type FormEvent } from 'react'
import { Button, Input, Select } from '../../../components/ui'
import type { ProductListQuery, ProductSortOption, ProductStatusFilter } from '../../../types/product'
import { SORT_OPTIONS, STATUS_OPTIONS } from '../constants/productFilters'

interface ProductFiltersProps {
  query: ProductListQuery
  disabled?: boolean
  onSearch: (values: { keyword: string; category: string }) => void
  onStatusChange: (status: ProductStatusFilter) => void
  onSortChange: (sort: ProductSortOption) => void
  onReset: () => void
}

export function ProductFilters({
  query,
  disabled = false,
  onSearch,
  onStatusChange,
  onSortChange,
  onReset,
}: ProductFiltersProps) {
  const [keyword, setKeyword] = useState(query.keyword)
  const [category, setCategory] = useState(query.category)

  useEffect(() => setKeyword(query.keyword), [query.keyword])
  useEffect(() => setCategory(query.category), [query.category])

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    onSearch({ keyword: keyword.trim(), category: category.trim() })
  }

  return (
    <form
      className="grid gap-4 rounded-card border border-line bg-surface p-4 sm:p-5 lg:grid-cols-[minmax(0,2fr)_minmax(180px,1fr)_160px_160px_auto] lg:items-end"
      onSubmit={handleSubmit}
    >
      <Input
        label="상품 검색"
        name="keyword"
        type="search"
        placeholder="상품명을 입력해주세요"
        value={keyword}
        disabled={disabled}
        onChange={(event) => setKeyword(event.target.value)}
      />
      <Input
        label="카테고리"
        name="category"
        placeholder="예: 전자기기"
        value={category}
        disabled={disabled}
        onChange={(event) => setCategory(event.target.value)}
      />
      <Select
        label="경매 상태"
        aria-label="경매 상태"
        options={STATUS_OPTIONS}
        value={query.status}
        disabled={disabled}
        onChange={(event) => onStatusChange(event.target.value as ProductStatusFilter)}
      />
      <Select
        label="정렬"
        aria-label="상품 정렬"
        options={SORT_OPTIONS}
        value={query.sort}
        disabled={disabled}
        onChange={(event) => onSortChange(event.target.value as ProductSortOption)}
      />
      <div className="grid grid-cols-2 gap-2 lg:flex lg:justify-end">
        <Button type="submit" disabled={disabled}>
          검색
        </Button>
        <Button type="button" variant="ghost" disabled={disabled} onClick={onReset}>
          초기화
        </Button>
      </div>
    </form>
  )
}
