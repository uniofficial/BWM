import { Component, type ErrorInfo, type ReactNode } from 'react'
import { AppConfigurationError } from '../../config/appConfig'

interface AppErrorBoundaryProps {
  children: ReactNode
}

interface AppErrorBoundaryState {
  error: Error | null
}

export class AppErrorBoundary extends Component<AppErrorBoundaryProps, AppErrorBoundaryState> {
  state: AppErrorBoundaryState = { error: null }

  static getDerivedStateFromError(error: Error): AppErrorBoundaryState {
    return { error }
  }

  componentDidCatch(error: Error, info: ErrorInfo) {
    if (import.meta.env.DEV) console.error('Application render error', error, info)
  }

  private reload = () => {
    window.location.reload()
  }

  render() {
    const { error } = this.state
    if (!error) return this.props.children

    const configurationError = error instanceof AppConfigurationError
    return (
      <main className="grid min-h-screen place-items-center bg-surface-secondary px-4 py-10">
        <section
          className="w-full max-w-lg rounded-card border border-line bg-surface p-6 text-center shadow-[var(--ds-shadow-card)] sm:p-8"
          aria-labelledby="app-error-title"
          role="alert"
        >
          <div
            className="mx-auto grid min-h-12 min-w-12 max-w-12 place-items-center rounded-full bg-[var(--ds-error-light)] font-bold text-danger"
            aria-hidden="true"
          >
            !
          </div>
          <h1 id="app-error-title" className="mt-4 text-xl font-bold text-ink">
            {configurationError ? '앱 설정을 확인해주세요.' : '페이지를 표시하는 중 문제가 발생했습니다.'}
          </h1>
          <p className="mt-3 text-sm leading-6 text-ink-secondary">
            {configurationError
              ? error.message
              : '잠시 후 다시 시도하거나 상품 목록으로 이동해주세요.'}
          </p>
          <div className="mt-6 flex flex-col gap-2 sm:flex-row sm:justify-center">
            <button
              type="button"
              className="inline-flex min-h-11 items-center justify-center rounded-card border border-line bg-surface-secondary px-4 text-sm font-semibold text-ink transition-colors duration-200 hover:bg-brand-light focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand/30"
              onClick={this.reload}
            >
              다시 시도
            </button>
            <a
              href="/products"
              className="inline-flex min-h-11 items-center justify-center rounded-card bg-brand px-4 text-sm font-semibold text-white transition-colors duration-200 hover:bg-brand-hover active:bg-brand-active focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand/30"
            >
              상품 목록으로
            </a>
          </div>
        </section>
      </main>
    )
  }
}
