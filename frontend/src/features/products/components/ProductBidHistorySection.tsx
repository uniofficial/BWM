import { Badge } from '../../../components/ui'
import type { ProductDetail } from '../../../types/product'
import { formatDateTime } from '../utils/dateFormat'
import { formatPrice } from '../utils/priceFormat'

interface ProductBidHistorySectionProps {
  product: ProductDetail
}

export function ProductBidHistorySection({ product }: ProductBidHistorySectionProps) {
  const bidHistory = product.bidHistory || []
  const uniqueBiddersCount = new Set(bidHistory.map((item) => item.bidderNickname)).size
  const totalBidsCount = product.bidCount ?? bidHistory.length

  const sortedHistory = [...bidHistory].sort((a, b) => {
    if (b.amount !== a.amount) return b.amount - a.amount
    return Date.parse(b.bidAt) - Date.parse(a.bidAt)
  })

  return (
    <section className="border-t border-line py-9" aria-labelledby="bid-history-heading">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h2 id="bid-history-heading" className="text-xl font-semibold text-ink">
            입찰 내역
          </h2>
          <p className="mt-1 text-sm text-ink-muted">
            현재 <span className="font-semibold text-brand-dark">{uniqueBiddersCount}명</span>의 유저가 입찰에 관심·참여 중입니다.
          </p>
        </div>
        <div className="flex flex-wrap gap-2 text-xs">
          <span className="inline-flex items-center gap-1.5 rounded-pill bg-surface-secondary px-3 py-1.5 font-medium text-ink-secondary">
            총 입찰 건수: <strong className="font-semibold text-ink">{totalBidsCount}회</strong>
          </span>
          <span className="inline-flex items-center gap-1.5 rounded-pill bg-brand-light px-3 py-1.5 font-medium text-brand-dark">
            참여 유저: <strong className="font-semibold text-brand">{uniqueBiddersCount}명</strong>
          </span>
        </div>
      </div>

      {sortedHistory.length === 0 ? (
        <div className="mt-6 rounded-content border border-line bg-surface p-8 text-center">
          <p className="text-sm font-semibold text-ink">아직 등록된 입찰 내역이 없습니다.</p>
          <p className="mt-1 text-xs text-ink-muted">첫 번째 입찰에 참여하여 경매를 시작해보세요!</p>
        </div>
      ) : (
        <div className="mt-6 overflow-x-auto rounded-content border border-line bg-surface">
          <table className="w-full border-collapse text-left text-sm">
            <thead className="bg-surface-secondary text-xs text-ink-muted">
              <tr>
                <th scope="col" className="h-11 border-b border-line px-4 font-semibold">순위</th>
                <th scope="col" className="h-11 border-b border-line px-4 font-semibold">입찰자</th>
                <th scope="col" className="h-11 border-b border-line px-4 text-right font-semibold">입찰 금액</th>
                <th scope="col" className="h-11 border-b border-line px-4 text-right font-semibold">입찰 일시</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-line">
              {sortedHistory.map((item, index) => {
                const isHighest = index === 0
                return (
                  <tr key={item.id || `${item.bidderNickname}-${item.bidAt}-${index}`} className="hover:bg-surface-secondary/50">
                    <td className="px-4 py-3.5 text-xs font-semibold text-ink-muted">
                      {isHighest ? (
                        <span className="inline-flex h-6 w-6 items-center justify-center rounded-pill bg-brand text-xs font-bold text-white">
                          1
                        </span>
                      ) : (
                        <span className="ml-2">{index + 1}</span>
                      )}
                    </td>
                    <td className="px-4 py-3.5 font-medium text-ink">
                      <div className="flex items-center gap-2">
                        <span>{item.bidderNickname}</span>
                        {isHighest && <Badge variant="primary">최고 입찰</Badge>}
                      </div>
                    </td>
                    <td className="px-4 py-3.5 text-right font-semibold text-ink">
                      {formatPrice(item.amount)}
                    </td>
                    <td className="px-4 py-3.5 text-right text-xs text-ink-muted">
                      {formatDateTime(item.bidAt)}
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      )}
    </section>
  )
}
