import { defineConfig, loadEnv } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

function validateProductionApiBaseUrl(value: string | undefined) {
  const configured = value?.trim()
  if (!configured) throw new Error('Production build requires VITE_API_BASE_URL.')

  let url: URL
  try {
    url = new URL(configured)
  } catch {
    throw new Error('Production VITE_API_BASE_URL must be an absolute URL.')
  }

  const localHosts = new Set(['localhost', '127.0.0.1', '[::1]'])
  if (
    url.protocol !== 'https:' ||
    localHosts.has(url.hostname) ||
    url.username ||
    url.password ||
    url.search ||
    url.hash ||
    url.pathname !== '/'
  ) {
    throw new Error('Production VITE_API_BASE_URL must be a non-local HTTPS Origin without a path.')
  }
}

export default defineConfig(({ command, mode }) => {
  if (command === 'build' && mode === 'production') {
    const env = loadEnv(mode, process.cwd(), 'VITE_')
    validateProductionApiBaseUrl(process.env.VITE_API_BASE_URL || env.VITE_API_BASE_URL)
  }

  return {
    base: '/',
    plugins: [react(), tailwindcss()],
    build: {
      sourcemap: false,
    },
  }
})
