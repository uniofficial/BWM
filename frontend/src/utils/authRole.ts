import type { AuthUser } from '../types/auth'

export function isAdmin(user: AuthUser | null): boolean {
  if (!user?.role) return false
  return user.role
    .split(',')
    .map((role) => role.trim())
    .includes('ADMIN')
}
