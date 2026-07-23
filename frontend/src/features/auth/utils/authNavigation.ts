const AUTH_PATHS = new Set(['/login', '/register'])

interface RouteLocationLike {
  pathname?: unknown
  search?: unknown
  hash?: unknown
}

interface LoginLocationState {
  from?: RouteLocationLike
  registrationSuccess?: boolean
}

function isObject(value: unknown): value is Record<string, unknown> {
  return typeof value === 'object' && value !== null
}

export function readLoginLocationState(value: unknown): LoginLocationState {
  if (!isObject(value)) return {}
  return {
    from: isObject(value.from) ? value.from : undefined,
    registrationSuccess: value.registrationSuccess === true,
  }
}

export function resolveLoginDestination(value: unknown, fallback = '/') {
  const { from } = readLoginLocationState(value)
  const pathname = typeof from?.pathname === 'string' ? from.pathname : ''

  if (!pathname.startsWith('/') || pathname.startsWith('//') || AUTH_PATHS.has(pathname)) {
    return fallback
  }

  const search = typeof from?.search === 'string' && from.search.startsWith('?') ? from.search : ''
  const hash = typeof from?.hash === 'string' && from.hash.startsWith('#') ? from.hash : ''
  return `${pathname}${search}${hash}`
}
