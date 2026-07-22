import { useEffect, useRef, useState, type FormEvent } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { register } from '../api/authApi'
import { Button, Input } from '../components/ui'
import { AuthLayout } from '../features/auth/components/AuthLayout'
import { PasswordField } from '../features/auth/components/PasswordField'
import { parseRegisterError } from '../features/auth/utils/authErrors'
import {
  hasFormErrors,
  validateEmail,
  validateRegister,
  validateRequiredPassword,
  type RegisterFormErrors,
  type RegisterFormValues,
} from '../features/auth/utils/authValidation'

const initialValues: RegisterFormValues = {
  email: '',
  nickname: '',
  password: '',
  passwordConfirmation: '',
}

export function RegisterPage() {
  const navigate = useNavigate()
  const [values, setValues] = useState(initialValues)
  const [errors, setErrors] = useState<RegisterFormErrors>({})
  const [isSubmitting, setIsSubmitting] = useState(false)
  const mountedRef = useRef(true)

  useEffect(() => {
    mountedRef.current = true
    return () => {
      mountedRef.current = false
    }
  }, [])

  const updateValue = (field: keyof RegisterFormValues, value: string) => {
    setValues((current) => ({ ...current, [field]: value }))
    setErrors((current) => ({ ...current, [field]: undefined, form: undefined }))
  }

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    if (isSubmitting) return

    const nextValues = {
      ...values,
      email: values.email.trim(),
      nickname: values.nickname.trim(),
    }
    const nextErrors = validateRegister(nextValues)
    setValues(nextValues)
    setErrors(nextErrors)
    if (hasFormErrors(nextErrors)) return

    setIsSubmitting(true)
    try {
      await register({
        email: nextValues.email,
        nickname: nextValues.nickname,
        password: nextValues.password,
      })
      if (!mountedRef.current) return
      navigate('/login', { replace: true, state: { registrationSuccess: true } })
    } catch (error) {
      if (!mountedRef.current) return
      const parsed = parseRegisterError(error)
      setErrors(parsed.field ? { [parsed.field]: parsed.message } : { form: parsed.message })
    } finally {
      if (mountedRef.current) setIsSubmitting(false)
    }
  }

  return (
    <AuthLayout
      title="회원가입"
      description="경매 서비스를 이용할 계정을 만들어주세요."
      footer={
        <>
          이미 계정이 있으신가요?{' '}
          <Link className="inline-flex min-h-11 items-center font-semibold text-brand-dark hover:text-brand-hover" to="/login">
            로그인
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
        <Input
          label="닉네임"
          name="nickname"
          autoComplete="nickname"
          placeholder="사용할 닉네임을 입력해주세요"
          value={values.nickname}
          error={errors.nickname}
          disabled={isSubmitting}
          required
          onChange={(event) => updateValue('nickname', event.target.value)}
          onBlur={() =>
            setErrors((current) => ({
              ...current,
              nickname: values.nickname.trim() ? undefined : '닉네임을 입력해주세요.',
            }))
          }
        />
        <PasswordField
          label="비밀번호"
          name="password"
          autoComplete="new-password"
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
        <PasswordField
          label="비밀번호 확인"
          name="passwordConfirmation"
          autoComplete="new-password"
          placeholder="비밀번호를 다시 입력해주세요"
          value={values.passwordConfirmation}
          error={errors.passwordConfirmation}
          disabled={isSubmitting}
          required
          onChange={(event) => updateValue('passwordConfirmation', event.target.value)}
          onBlur={() =>
            setErrors((current) => ({
              ...current,
              passwordConfirmation: !values.passwordConfirmation
                ? '비밀번호를 한 번 더 입력해주세요.'
                : values.password !== values.passwordConfirmation
                  ? '비밀번호가 일치하지 않습니다.'
                  : undefined,
            }))
          }
        />

        {errors.form && (
          <div className="rounded-inline border border-danger/30 bg-[var(--ds-error-light)] px-4 py-3 text-sm leading-6 text-danger" role="alert">
            {errors.form}
          </div>
        )}

        <Button type="submit" size="lg" className="w-full" isLoading={isSubmitting} loadingLabel="가입 처리 중">
          회원가입
        </Button>
      </form>
    </AuthLayout>
  )
}
