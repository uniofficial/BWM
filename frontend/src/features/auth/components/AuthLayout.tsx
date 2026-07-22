import type { ReactNode } from 'react'
import { AppHeader } from '../../../components/common/AppHeader'
import { Card } from '../../../components/ui'

interface AuthLayoutProps {
  title: string
  description: string
  children: ReactNode
  footer: ReactNode
  notice?: string
}

export function AuthLayout({ title, description, children, footer, notice }: AuthLayoutProps) {
  return (
    <div className="min-h-screen bg-surface-secondary">
      <AppHeader />
      <main className="flex min-h-[calc(100dvh-4rem)] items-start justify-center px-4 py-10 sm:items-center sm:px-6 sm:py-14">
        <Card className="w-full max-w-[460px] p-6 sm:p-8">
          <div className="mb-7">
            <p className="text-sm font-semibold text-brand-dark">BWM 계정</p>
            <h1 className="mt-2 text-3xl font-bold text-ink">{title}</h1>
            <p className="mt-2 text-sm leading-6 text-ink-secondary">{description}</p>
          </div>

          {notice && (
            <div className="mb-5 rounded-card border border-positive/30 bg-[var(--ds-success-light)] px-4 py-3 text-sm leading-6 text-ink" role="status">
              {notice}
            </div>
          )}

          {children}
          <div className="mt-7 border-t border-line pt-5 text-center text-sm text-ink-secondary">{footer}</div>
        </Card>
      </main>
    </div>
  )
}
