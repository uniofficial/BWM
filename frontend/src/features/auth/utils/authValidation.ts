export interface LoginFormValues {
  email: string
  password: string
}

export interface RegisterFormValues extends LoginFormValues {
  nickname: string
  passwordConfirmation: string
}

export interface LoginFormErrors {
  email?: string
  password?: string
  form?: string
}

export interface RegisterFormErrors extends LoginFormErrors {
  nickname?: string
  passwordConfirmation?: string
}

const EMAIL_PATTERN = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

export function validateEmail(value: string) {
  const email = value.trim()
  if (!email) return '이메일을 입력해주세요.'
  if (!EMAIL_PATTERN.test(email)) return '올바른 이메일 형식을 입력해주세요.'
  return undefined
}

export function validateRequiredPassword(value: string) {
  return value ? undefined : '비밀번호를 입력해주세요.'
}

export function validateLogin(values: LoginFormValues): LoginFormErrors {
  return {
    email: validateEmail(values.email),
    password: validateRequiredPassword(values.password),
  }
}

export function validateRegister(values: RegisterFormValues): RegisterFormErrors {
  const passwordError = validateRequiredPassword(values.password)

  return {
    email: validateEmail(values.email),
    nickname: values.nickname.trim() ? undefined : '닉네임을 입력해주세요.',
    password: passwordError,
    passwordConfirmation: !values.passwordConfirmation
      ? '비밀번호를 한 번 더 입력해주세요.'
      : !passwordError && values.password !== values.passwordConfirmation
        ? '비밀번호가 일치하지 않습니다.'
        : undefined,
  }
}

export function hasFormErrors(errors: LoginFormErrors | RegisterFormErrors) {
  return Object.values(errors).some(Boolean)
}
