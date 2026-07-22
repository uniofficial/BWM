import { useState } from 'react'
import { Input, type InputProps } from '../../../components/ui'

type PasswordFieldProps = Omit<InputProps, 'type' | 'suffix'>

export function PasswordField(props: PasswordFieldProps) {
  const [isVisible, setIsVisible] = useState(false)
  const label = isVisible ? '비밀번호 숨기기' : '비밀번호 보기'

  return (
    <Input
      {...props}
      type={isVisible ? 'text' : 'password'}
      suffix={
        <button
          type="button"
          onClick={() => setIsVisible((current) => !current)}
          className="inline-flex min-h-11 min-w-11 items-center justify-center rounded-card text-ink-muted transition-colors duration-200 hover:bg-surface-secondary hover:text-ink focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-brand/30"
          aria-label={label}
          aria-pressed={isVisible}
          title={label}
        >
          <svg aria-hidden="true" viewBox="0 0 24 24" width="20" height="20" fill="none" stroke="currentColor" strokeWidth="1.8">
            {isVisible ? (
              <>
                <path d="M3 3l18 18" />
                <path d="M10.6 10.7a2 2 0 002.7 2.7" />
                <path d="M9.9 4.2A10.9 10.9 0 0112 4c5.5 0 9 5 9 5a15.6 15.6 0 01-2.1 2.5M6.6 6.6C4.3 8.1 3 10 3 10s3.5 5 9 5c1 0 2-.2 2.9-.5" />
              </>
            ) : (
              <>
                <path d="M3 12s3.5-5 9-5 9 5 9 5-3.5 5-9 5-9-5-9-5z" />
                <circle cx="12" cy="12" r="2.5" />
              </>
            )}
          </svg>
        </button>
      }
    />
  )
}
