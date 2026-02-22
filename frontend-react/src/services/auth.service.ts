import api from './api'
import { AuthResponse, LoginRequest, RegisterRequest, LoginResponse, User } from '../types'

export const authService = {
  login: async (credentials: LoginRequest): Promise<LoginResponse> => {
    const response = await api.post<AuthResponse>('/auth/login', credentials)
    // Transform backend response to match frontend expected format
    const authResponse = response.data
    return {
      token: authResponse.token,
      user: {
        userId: authResponse.userId,
        username: authResponse.username,
        email: authResponse.email,
        surname: authResponse.surname,
        phoneNumber: authResponse.phonenumber,
        telegramChatId: null,
        role: authResponse.role as User['role'],
        emailNotificationsEnabled: true,
        telegramNotificationsEnabled: true
      } as User
    }
  },

  register: async (userData: RegisterRequest): Promise<{ id: string; username: string; email: string }> => {
    const response = await api.post('/users/register', userData)
    return response.data
  },

  logout: () => {
    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    localStorage.removeItem('email')
  },

  getCurrentUser: () => {
    return {
      userId: localStorage.getItem('userId'),
      username: localStorage.getItem('username'),
      email: localStorage.getItem('email'),
    }
  },

  isAuthenticated: () => {
    return !!localStorage.getItem('token')
  },
}
