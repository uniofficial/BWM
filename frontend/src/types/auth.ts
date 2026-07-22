export interface AuthUser {
  nickname?: string
  role?: string
}

export interface LoginRequest {
  email: string
  password: string
}

export interface RegisterRequest {
  email: string
  password: string
  nickname: string
}

export interface AuthSession {
  accessToken: string
  user: AuthUser | null
}
