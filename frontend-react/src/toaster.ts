import { ToastType, AlertStatus } from './context/ToastContext'

// Global toast function reference - will be set by ToastProvider
let globalShowToast: ((options: {
  title: string
  description?: string
  type: ToastType
  duration?: number
  closable?: boolean
  alertStatus?: AlertStatus
  action?: {
    label: string
    onClick: () => void
  }
}) => string) | null = null

// Register the global toast function (called by ToastProvider)
export function registerToastFunction(
  fn: (options: {
    title: string
    description?: string
    type: ToastType
    duration?: number
    closable?: boolean
    alertStatus?: AlertStatus
    action?: {
      label: string
      onClick: () => void
    }
  }) => string
) {
  globalShowToast = fn
}

// Create a toast - mimics Chakra's toaster.create API for backward compatibility
export const toaster = {
  create: (options: {
    title: string
    description?: string
    type?: ToastType
    duration?: number
    closable?: boolean
    alertStatus?: AlertStatus
    action?: {
      label: string
      onClick: () => void
    }
  }): string => {
    if (!globalShowToast) {
      console.warn('Toast system not initialized. Make sure ToastProvider is mounted.')
      return ''
    }
    return globalShowToast({
      title: options.title,
      description: options.description,
      type: options.type || 'info',
      duration: options.duration,
      closable: options.closable,
      alertStatus: options.alertStatus,
      action: options.action,
    })
  },
}

// Export a toast function that matches the expected API
export const toast = (options: {
  title: string
  description?: string
  status?: 'success' | 'error' | 'warning' | 'info'
}) => {
  toaster.create({
    title: options.title,
    description: options.description,
    type: options.status || 'info',
    closable: true,
  })
}
