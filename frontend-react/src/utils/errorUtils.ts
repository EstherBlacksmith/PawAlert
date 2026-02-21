import { ErrorResponse } from '../types'
import { toaster } from '../toaster'

/**
 * User-friendly default error messages for HTTP status codes
 */
const DEFAULT_ERROR_MESSAGES: Record<number, string> = {
  400: 'Invalid request. Please check your input and try again.',
  401: 'Your session has expired. Please log in again.',
  403: 'You do not have permission to perform this action.',
  404: 'The requested resource was not found.',
  409: 'A conflict occurred. The resource may already exist.',
  422: 'The provided data is invalid. Please check your input.',
  500: 'A server error occurred. Please try again later.',
  502: 'Server is temporarily unavailable. Please try again.',
  503: 'Service temporarily unavailable. Please try again later.',
  504: 'Server is taking too long to respond. Please try again.',
}

/**
 * Network error message when server is unreachable
 */
const NETWORK_ERROR_MESSAGE = 'Unable to connect to the server. Please check your internet connection.'

/**
 * Generic unknown error message
 */
const UNKNOWN_ERROR_MESSAGE = 'An unexpected error occurred. Please try again.'

/**
 * Axios error response structure
 */
interface AxiosErrorResponse {
  status: number
  data?: {
    status?: number
    error?: string
    message?: string
  }
  statusText?: string
}

/**
 * Axios error structure
 */
interface AxiosError {
  response?: AxiosErrorResponse
  request?: unknown
  message?: string
  code?: string
}

/**
 * Type guard to check if an error is an Axios error
 */
function isAxiosError(error: unknown): error is AxiosError {
  return (
    typeof error === 'object' &&
    error !== null &&
    ('response' in error || 'request' in error)
  )
}

/**
 * Extracts a user-friendly ErrorResponse from any error type
 * 
 * @param error - The caught error (can be Axios error, Error, or unknown)
 * @returns ErrorResponse with user-friendly message
 */
export function extractError(error: unknown): ErrorResponse {
  // Log the original error for debugging
  console.error('Error occurred:', error)

  // Handle Axios errors
  if (isAxiosError(error)) {
    const axiosError = error

    // Server responded with an error
    if (axiosError.response) {
      const { response } = axiosError
      
      // Backend already returns structured error response
      if (response.data?.message) {
        return {
          status: response.data.status || response.status,
          error: response.data.error || 'Error',
          message: response.data.message,
        }
      }

      // Use status text if available
      if (response.statusText) {
        return {
          status: response.status,
          error: response.statusText,
          message: DEFAULT_ERROR_MESSAGES[response.status] || response.statusText,
        }
      }

      // Use default message for status code
      return {
        status: response.status,
        error: 'Error',
        message: DEFAULT_ERROR_MESSAGES[response.status] || UNKNOWN_ERROR_MESSAGE,
      }
    }

    // Request was made but no response received (network error)
    if (axiosError.request) {
      return {
        status: 0,
        error: 'Network Error',
        message: NETWORK_ERROR_MESSAGE,
      }
    }

    // Something happened in setting up the request
    if (axiosError.message) {
      return {
        status: 0,
        error: 'Request Error',
        message: axiosError.message,
      }
    }
  }

  // Handle standard Error objects
  if (error instanceof Error) {
    return {
      status: 0,
      error: 'Error',
      message: error.message || UNKNOWN_ERROR_MESSAGE,
    }
  }

  // Handle string errors
  if (typeof error === 'string') {
    return {
      status: 0,
      error: 'Error',
      message: error,
    }
  }

  // Unknown error type
  return {
    status: 0,
    error: 'Unknown Error',
    message: UNKNOWN_ERROR_MESSAGE,
  }
}

/**
 * Shows an error toast notification
 * 
 * @param error - The error to display
 * @param title - Optional custom title (defaults to error type)
 */
export function showErrorToast(error: unknown, title?: string): void {
  const errorResponse = extractError(error)
  
  toaster.create({
    title: title || errorResponse.error,
    description: errorResponse.message,
    type: 'error',
    closable: true,
  })
}

/**
 * Shows a success toast notification
 * 
 * @param title - The title for the toast
 * @param description - Optional description
 */
export function showSuccessToast(title: string, description?: string): void {
  toaster.create({
    title,
    description,
    type: 'success',
    closable: true,
  })
}

/**
 * Shows an info toast notification
 * 
 * @param title - The title for the toast
 * @param description - Optional description
 */
export function showInfoToast(title: string, description?: string): void {
  toaster.create({
    title,
    description,
    type: 'info',
    closable: true,
  })
}

/**
 * Shows a warning toast notification
 * 
 * @param title - The title for the toast
 * @param description - Optional description
 */
export function showWarningToast(title: string, description?: string): void {
  toaster.create({
    title,
    description,
    type: 'warning',
    closable: true,
  })
}

/**
 * Gets a user-friendly message for a specific HTTP status code
 * 
 * @param statusCode - HTTP status code
 * @returns User-friendly error message
 */
export function getStatusMessage(statusCode: number): string {
  return DEFAULT_ERROR_MESSAGES[statusCode] || UNKNOWN_ERROR_MESSAGE
}
