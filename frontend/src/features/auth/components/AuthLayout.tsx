import type { ReactNode } from 'react'
import { AppHeader } from '../../../components/common/AppHeader'

interface AuthLayoutProps {
  title: string
  description: string
  children: ReactNode
  footer: ReactNode
  notice?: string
}

export function AuthLayout({ title, description, children, footer, notice }: AuthLayoutProps) {
  return (
    <div className="min-h-screen bg-surface">
      <AppHeader />
      <main className="flex min-h-[calc(100dvh-4.5rem)] items-start justify-center px-4 py-12 sm:items-center sm:px-6 sm:py-16">
        <section className="w-full max-w-[420px]" aria-labelledby="auth-heading">
          <div className="mb-9">
            <h1 id="auth-heading" className="text-4xl font-bold leading-tight text-ink">{title}</h1>
            <p className="mt-3 text-sm leading-6 text-ink-secondary">{description}</p>
          </div>

          {notice && (
            <div className="mb-5 rounded-inline border border-positive/30 bg-[var(--ds-success-light)] px-4 py-3 text-sm leading-6 text-ink" role="status">
              {notice}
            </div>
          )}

          {children}
          <div className="mt-8 flex min-h-11 flex-wrap items-center justify-center gap-x-1 border-t border-line pt-5 text-center text-sm text-ink-secondary">
            {footer}
          </div>
        </section>
      </main>
    </div>
  )
}
