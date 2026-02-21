import api from './api'
import { Alert, CreateAlertRequest, UpdateAlertStatusRequest, AlertSearchParams, AlertStatus, CloseAlertRequest, AlertEvent, AlertSearchFilters, AlertSubscription, AlertSubscriptionWithDetails, SubscribedResponse } from '../types'

export const alertService = {
  getAlerts: async (params?: AlertSearchParams): Promise<Alert[]> => {
    const response = await api.get<Alert[]>('/alerts/search', { params })
    return response.data
  },

  getAllAlerts: async (): Promise<Alert[]> => {
    const response = await api.get<Alert[]>('/alerts')
    return response.data
  },

  getAlert: async (alertId: string): Promise<Alert> => {
    const response = await api.get<Alert>(`/alerts/${alertId}`)
    return response.data
  },

  createAlert: async (alertData: CreateAlertRequest): Promise<Alert> => {
    const response = await api.post<Alert>('/alerts', alertData)
    return response.data
  },

  closeAlert: async (alertId: string, closeData: CloseAlertRequest): Promise<Alert> => {
    const response = await api.post<Alert>(`/alerts/${alertId}/close`, {
      userId: closeData.userId,
      latitude: closeData.latitude,
      longitude: closeData.longitude,
      closureReason: closeData.closureReason
    })
    return response.data
  },

  updateAlertStatus: async (alertId: string, statusData: UpdateAlertStatusRequest): Promise<Alert> => {
    const response = await api.patch<Alert>(`/alerts/${alertId}/status`, {
      newStatus: statusData.newStatus,
      userId: statusData.userId,
      latitude: statusData.latitude,
      longitude: statusData.longitude
    })
    return response.data
  },

  updateAlertTitle: async (alertId: string, userId: string, title: string): Promise<Alert> => {
    const response = await api.put<Alert>(`/alerts/${alertId}/title`, { userId, title })
    return response.data
  },

  updateAlertDescription: async (alertId: string, userId: string, description: string): Promise<Alert> => {
    const response = await api.put<Alert>(`/alerts/${alertId}/description`, { userId, description })
    return response.data
  },

  deleteAlert: async (alertId: string): Promise<void> => {
    await api.delete(`/alerts/${alertId}`)
  },

  updateAlert: async (alertId: string, userId: string, data: { title?: string; description?: string }): Promise<Alert> => {
    // Update title if provided
    if (data.title !== undefined) {
      await api.put<Alert>(`/alerts/${alertId}/title`, { userId, title: data.title })
    }
    // Update description if provided
    if (data.description !== undefined) {
      await api.put<Alert>(`/alerts/${alertId}/description`, { userId, description: data.description })
    }
    // Return the updated alert
    const response = await api.get<Alert>(`/alerts/${alertId}`)
    return response.data
  },

  getAlertsByStatus: async (status: AlertStatus): Promise<Alert[]> => {
    const response = await api.get<Alert[]>('/alerts/search', { params: { status } })
    return response.data
  },

  getAlertEvents: async (alertId: string): Promise<AlertEvent[]> => {
    const response = await api.get<AlertEvent[]>(`/alerts/${alertId}/events`)
    return response.data
  },

  getActiveAlertByPetId: async (petId: string): Promise<Alert | null> => {
    try {
      const response = await api.get<Alert>(`/alerts/pets/${petId}/active`)
      return response.data
    } catch (error) {
      // Return null if no active alert found (404)
      return null
    }
  },

  // Extended search with all filters for alert list and admin dashboard
  searchAlertsWithFilters: async (filters: AlertSearchFilters): Promise<Alert[]> => {
    // Helper to convert date string (YYYY-MM-DD) to ISO datetime format
    const dateToIsoDateTime = (dateStr: string, isEndDate: boolean = false): string => {
      if (!dateStr) return ''
      // For start dates, use 00:00:00; for end dates, use 23:59:59
      const timeSuffix = isEndDate ? 'T23:59:59' : 'T00:00:00'
      return dateStr + timeSuffix
    }

    // If distance filtering is requested, use the nearby endpoint
    if (filters.latitude !== undefined && filters.longitude !== undefined && filters.radiusKm !== undefined) {
      const nearbyAlerts = await api.get<Alert[]>('/alerts/public/nearby', {
        params: {
          latitude: filters.latitude,
          longitude: filters.longitude,
          radiusKm: filters.radiusKm
        }
      })
      
      // Apply additional filters client-side for nearby results
      let filtered = nearbyAlerts.data
      
      if (filters.status) {
        filtered = filtered.filter(a => a.status === filters.status)
      }
      if (filters.petName) {
        filtered = filtered.filter(a => a.title.toLowerCase().includes(filters.petName!.toLowerCase()))
      }
      if (filters.species) {
        // Note: species filter would need pet data joined - backend should handle this
      }
      if (filters.breed) {
        // Note: breed filter would need pet data joined - backend should handle this
      }
      if (filters.createdFrom) {
        const fromDateTime = new Date(dateToIsoDateTime(filters.createdFrom, false))
        filtered = filtered.filter(a => new Date(a.createdAt) >= fromDateTime)
      }
      if (filters.createdTo) {
        const toDateTime = new Date(dateToIsoDateTime(filters.createdTo, true))
        filtered = filtered.filter(a => new Date(a.createdAt) <= toDateTime)
      }
      
      return filtered
    }
    
    // Standard search endpoint for non-location-based filtering
    const params: Record<string, string> = {}
    
    if (filters.status) params.status = filters.status
    if (filters.title) params.title = filters.title
    if (filters.petName) params.petName = filters.petName
    if (filters.species) params.species = filters.species
    if (filters.breed) params.breed = filters.breed
    // Convert dates to ISO datetime format for backend
    if (filters.createdFrom) params.createdFrom = dateToIsoDateTime(filters.createdFrom, false)
    if (filters.createdTo) params.createdTo = dateToIsoDateTime(filters.createdTo, true)
    if (filters.updatedFrom) params.updatedFrom = dateToIsoDateTime(filters.updatedFrom, false)
    if (filters.updatedTo) params.updatedTo = dateToIsoDateTime(filters.updatedTo, true)
    
    const response = await api.get<Alert[]>('/alerts/search', { params })
    return response.data
  },

  // Get all alerts for admin
  getAllAlertsForAdmin: async (): Promise<Alert[]> => {
    const response = await api.get<Alert[]>('/alerts/admin/all')
    return response.data
  },

  // Public endpoint - no authentication required
  getNearbyAlerts: async (latitude: number, longitude: number, radiusKm: number = 10): Promise<Alert[]> => {
    const response = await api.get<Alert[]>('/alerts/public/nearby', {
      params: { latitude, longitude, radiusKm }
    })
    return response.data
  },

  // Subscription methods
  subscribeToAlert: async (alertId: string): Promise<AlertSubscription> => {
    const response = await api.post<AlertSubscription>(`/alerts/${alertId}/subscribe`)
    return response.data
  },

  unsubscribeFromAlert: async (alertId: string): Promise<void> => {
    await api.delete(`/alerts/${alertId}/subscribe`)
  },

  isSubscribedToAlert: async (alertId: string): Promise<boolean> => {
    const response = await api.get<SubscribedResponse>(`/alerts/${alertId}/subscribed`)
    return response.data.subscribed
  },

  getMySubscriptions: async (): Promise<AlertSubscriptionWithDetails[]> => {
    const response = await api.get<AlertSubscriptionWithDetails[]>('/alerts/subscriptions/me')
    return response.data
  },
}
