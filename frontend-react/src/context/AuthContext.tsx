import { createContext, useContext, useState, useEffect, ReactNode } from 'react'
import { User, RegisterRequest } from '../types'
import { authService } from '../services/auth.service'

interface AuthContextType {
  user: User | null
  isAuthenticated: boolean
  isLoading: boolean
  login: (credentials: { email: string; password: string }) => Promise<void>
  register: (userData: RegisterRequest) => Promise<void>
  logout: () => void
  setUser: (user: User | null) => void
  isAdmin: () => boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

// Helper function to check if JWT token is expired
function isTokenExpired(token: string): boolean {
  try {
    // JWT structure: header.payload.signature
    const payload = token.split('.')[1]
    if (!payload) return true
    
    // Decode base64 payload
    const decodedPayload = JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')))
    
    // Check if token has exp claim and if it's expired
    if (decodedPayload.exp) {
      // exp is in seconds, Date.now() is in milliseconds
      return decodedPayload.exp * 1000 < Date.now()
    }
    return false // If no exp claim, assume token is valid
  } catch {
    return true // If we can't decode the token, consider it expired
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const token = localStorage.getItem('token')
    const savedUser = localStorage.getItem('user')
    
    if (token && savedUser) {
      // Check if token is expired before setting user
      if (isTokenExpired(token)) {
        // Token is expired, clear storage
        localStorage.removeItem('token')
        localStorage.removeItem('user')
      } else {
        try {
          setUser(JSON.parse(savedUser))
        } catch {
          localStorage.removeItem('token')
          localStorage.removeItem('user')
        }
      }
    }
    setIsLoading(false)
  }, [])

  const login = async (credentials: { email: string; password: string }) => {
    const response = await authService.login(credentials)
    localStorage.setItem('token', response.token)
    localStorage.setItem('user', JSON.stringify(response.user))
    setUser(response.user)
  }

  const register = async (userData: RegisterRequest) => {
    await authService.register(userData)
  }

  const logout = () => {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
    setUser(null)
  }

  const isAdmin = () => {
    return user?.role === 'ADMIN'
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        isLoading,
        login,
        register,
        logout,
        setUser,
        isAdmin,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth must be used within an AuthProvider')
  }
  return context
}
