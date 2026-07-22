import type { ReactNode } from 'react'

interface MyPageSectionHeaderProps {
  id?: string
  title: string
  description?: string
  action?: ReactNode
}

export function MyPageSectionHeader({ id, title, description, action }: MyPageSectionHeaderProps) {
  return (
    <header className="mb-6 flex flex-wrap items-start justify-between gap-4">
      <div>
        <h2 id={id} className="text-2xl font-semibold text-ink">{title}</h2>
        {description && <p className="mt-2 text-sm leading-6 text-ink-secondary">{description}</p>}
      </div>
      {action}
    </header>
  )
}
