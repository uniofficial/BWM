import { useEffect, useRef, useState, type FormEvent } from 'react'
import { Link, useLocation, useNavigate } from 'react-router-dom'
import { login } from '../api/authApi'
import { Button, Input } from '../components/ui'
import { AuthLayout } from '../features/auth/components/AuthLayout'
import { PasswordField } from '../features/auth/components/PasswordField'
import { parseLoginError } from '../features/auth/utils/authErrors'
import { readLoginLocationState, resolveLoginDestination } from '../features/auth/utils/authNavigation'
import {
  hasFormErrors,
  validateEmail,
  validateLogin,
  validateRequiredPassword,
  type LoginFormErrors,
  type LoginFormValues,
} from '../features/auth/utils/authValidation'
import { useAuth } from '../hooks/useAuth'

const initialValues: LoginFormValues = { email: '', password: '' }

export function LoginPage() {
  const location = useLocation()
  const navigate = useNavigate()
  const { setAuth } = useAuth()
  const [values, setValues] = useState(initialValues)
  const [errors, setErrors] = useState<LoginFormErrors>({})
  const [isSubmitting, setIsSubmitting] = useState(false)
  const mountedRef = useRef(true)
  const [showRegistrationNotice] = useState(
    () => readLoginLocationState(location.state).registrationSuccess === true,
  )

  useEffect(() => {
    mountedRef.current = true
    return () => {
      mountedRef.current = false
    }
  }, [])

  useEffect(() => {
    if (readLoginLocationState(location.state).registrationSuccess) {
      navigate(location.pathname, { replace: true, state: null })
    }
  }, [location.pathname, location.state, navigate])

  const updateValue = (field: keyof LoginFormValues, value: string) => {
    setValues((current) => ({ ...current, [field]: value }))
    setErrors((current) => ({ ...current, [field]: undefined, form: undefined }))
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (isSubmitting) return

    const nextValues = { ...values, email: values.email.trim() }
    const nextErrors = validateLogin(nextValues)
    setValues(nextValues)
    setErrors(nextErrors)
    if (hasFormErrors(nextErrors)) return

    setIsSubmitting(true)
    try {
      const session = await login(nextValues)
      if (!mountedRef.current) return
      setAuth(session.accessToken, session.user)
      navigate(resolveLoginDestination(location.state), { replace: true })
    } catch (error) {
      if (mountedRef.current) setErrors({ form: parseLoginError(error).message })
    } finally {
      if (mountedRef.current) setIsSubmitting(false)
    }
  }

  return (
    <AuthLayout
      title="로그인"
      description="계정에 로그인하고 경매를 이어가세요."
      notice={showRegistrationNotice ? '회원가입이 완료되었습니다. 로그인해주세요.' : undefined}
      footer={
        <>
          계정이 없으신가요?{' '}
          <Link className="inline-flex min-h-11 items-center font-semibold text-brand-dark hover:text-brand-hover" to="/register">
            회원가입
          </Link>
        </>
      }
    >
      <form className="space-y-5" onSubmit={handleSubmit} noValidate>
        <Input
          label="이메일"
          name="email"
          type="email"
          autoComplete="email"
          inputMode="email"
          placeholder="name@example.com"
          value={values.email}
          error={errors.email}
          disabled={isSubmitting}
          required
          onChange={(event) => updateValue('email', event.target.value)}
          onBlur={() => setErrors((current) => ({ ...current, email: validateEmail(values.email) }))}
        />
        <PasswordField
          label="비밀번호"
          name="password"
          autoComplete="current-password"
          placeholder="비밀번호를 입력해주세요"
          value={values.password}
          error={errors.password}
          disabled={isSubmitting}
          required
          onChange={(event) => updateValue('password', event.target.value)}
          onBlur={() =>
            setErrors((current) => ({ ...current, password: validateRequiredPassword(values.password) }))
          }
        />

        {errors.form && (
          <div className="rounded-card border border-danger/30 bg-[var(--ds-error-light)] px-4 py-3 text-sm leading-6 text-danger" role="alert">
            {errors.form}
          </div>
        )}

        <Button type="submit" size="lg" className="w-full" isLoading={isSubmitting} loadingLabel="로그인 중">
          로그인
        </Button>
      </form>
    </AuthLayout>
  )
}
