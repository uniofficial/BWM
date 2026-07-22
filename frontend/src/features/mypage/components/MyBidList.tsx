import type { MyBidItem } from '../../../types/myBid'
import { MyBidCard } from './MyBidCard'

export function MyBidList({ bids }: { bids: MyBidItem[] }) {
  return <div className="grid gap-4 sm:grid-cols-2">{bids.map((bid) => <MyBidCard key={bid.productId} bid={bid} />)}</div>
}
