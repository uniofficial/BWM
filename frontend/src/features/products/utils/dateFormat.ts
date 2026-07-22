const dateTimeFormatter = new Intl.DateTimeFormat('ko-KR', {
  year: 'numeric',
  month: 'long',
  day: 'numeric',
  hour: '2-digit',
  minute: '2-digit',
})

export function formatDateTime(value: string | null) {
  if (!value) return '정보 없음'
  const timestamp = Date.parse(value)
  return Number.isFinite(timestamp) ? dateTimeFormatter.format(timestamp) : '정보 없음'
}
