let accessToken: string | null = null
let clientLoggedOut = false

export function getAccessToken() {
  return accessToken
}

export function setAccessToken(token: string) {
  accessToken = token
}

export function clearAccessToken() {
  accessToken = null
}

export function isClientLoggedOut() {
  return clientLoggedOut
}

export function setClientLoggedOut(value: boolean) {
  clientLoggedOut = value
}
