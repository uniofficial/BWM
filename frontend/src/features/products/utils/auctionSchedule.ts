import type { ProductCreateFormState } from '../../../types/product'

type AuctionSchedule = Pick<
  ProductCreateFormState,
  'auctionDate' | 'auctionMeridiem' | 'auctionHour' | 'auctionMinute'
>

export function buildAuctionEndAt(schedule: AuctionSchedule) {
  const { auctionDate, auctionMeridiem, auctionHour, auctionMinute } = schedule
  if (!auctionDate || !auctionMeridiem || !auctionHour || !auctionMinute) return ''
  if (auctionMeridiem !== 'AM' && auctionMeridiem !== 'PM') return ''

  const hour = Number(auctionHour)
  const minute = Number(auctionMinute)
  if (!Number.isInteger(hour) || hour < 1 || hour > 12) return ''
  if (!Number.isInteger(minute) || minute < 0 || minute > 59) return ''

  const hour24 = auctionMeridiem === 'AM' ? hour % 12 : (hour % 12) + 12
  return `${auctionDate}T${String(hour24).padStart(2, '0')}:${String(minute).padStart(2, '0')}`
}
