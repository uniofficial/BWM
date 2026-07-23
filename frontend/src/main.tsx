import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { AppErrorBoundary } from './components/common/AppErrorBoundary.tsx'
import { installGlobalErrorDiagnostics } from './utils/operationalDiagnostics.ts'
import './index.css'
import App from './App.tsx'

installGlobalErrorDiagnostics()

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <AppErrorBoundary>
      <App />
    </AppErrorBoundary>
  </StrictMode>,
)
