import axios from 'axios'

const API_BASE_URL = '/api'

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
})

// Request interceptor to add auth token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor to handle errors
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // Only handle 401 (Unauthorized) - user is not authenticated
    // 403 (Forbidden) means user IS authenticated but lacks permission - should not redirect to login
    if (error.response?.status === 401) {
      // Clear all auth-related data from localStorage
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      localStorage.removeItem('userId')
      localStorage.removeItem('username')
      localStorage.removeItem('email')
      // Only redirect to login if not already on login or register page
      if (!window.location.pathname.match(/^\/(login|register)$/)) {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

export default api
