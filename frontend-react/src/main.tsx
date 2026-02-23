import React from 'react'
import ReactDOM from 'react-dom/client'
import { ChakraProvider } from '@chakra-ui/react'
import { system } from './theme'
import { BrowserRouter } from 'react-router-dom'
import App from './App'
import { AuthProvider } from './context/AuthContext'
import { NotificationProvider } from './context/NotificationContext'
import { ToastProvider } from './context/ToastContext'
import { ToastContainer } from './components/ui/Toast'

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ChakraProvider value={system}>
      <BrowserRouter>
        <ToastProvider>
          <AuthProvider>
            <NotificationProvider>
              <App />
              <ToastContainer />
            </NotificationProvider>
          </AuthProvider>
        </ToastProvider>
      </BrowserRouter>
    </ChakraProvider>
  </React.StrictMode>,
)
