import api from './api'
import { User } from '../types'

// Helper function to extract value from backend value objects
// Backend returns { value: "actualValue" } for value objects like PhoneNumber, Email, etc.
function extractValue(obj: unknown): string | null {
  if (obj === null || obj === undefined) return null
  if (typeof obj === 'string') return obj
  if (typeof obj === 'object' && obj !== null && 'value' in obj) {
    return (obj as { value: string }).value
  }
  return null
}

// Helper function to extract boolean from backend response
// Returns true as default for notification preferences when backend returns falsy values
function extractBoolean(obj: unknown): boolean {
  if (typeof obj === 'boolean') return obj
  if (typeof obj === 'string') return obj === 'true'
  // Default to true for notification preferences (null/undefined from backend)
  return true
}

// Transform backend user response to frontend User format
function transformUserResponse(data: Record<string, unknown>): User {
  return {
    userId: data.id as string,
    username: extractValue(data.username),
    email: extractValue(data.email),
    surname: extractValue(data.surname),
    phoneNumber: extractValue(data.phoneNumber),
    telegramChatId: extractValue(data.telegramChatId),
    role: extractValue(data.role) as User['role'],
    emailNotificationsEnabled: extractBoolean(data.emailNotificationsEnabled),
    telegramNotificationsEnabled: extractBoolean(data.telegramNotificationsEnabled),
  }
}

export const userService = {
  getUser: async (userId: string): Promise<User> => {
    const response = await api.get<Record<string, unknown>>(`/users/${userId}`)
    return transformUserResponse(response.data)
  },

  updateUser: async (userId: string, formData: Partial<User>, originalData: Partial<User>): Promise<User> => {
    // Call individual update endpoints only for fields that have actually changed
    let updatedUser: User | null = null
    
    // Helper to check if value changed (handles null/empty string equivalence)
    const hasChanged = (newVal: string | null | undefined, oldVal: string | null | undefined): boolean => {
      const normalizedNew = newVal?.trim() || ''
      const normalizedOld = oldVal?.trim() || ''
      return normalizedNew !== normalizedOld && normalizedNew !== ''
    }
    
    // Helper to check if boolean value changed
    const hasBooleanChanged = (newVal: boolean | undefined, oldVal: boolean | undefined): boolean => {
      return newVal !== undefined && newVal !== oldVal
    }
    
    // Only update if the value has changed and is not empty
    if (hasChanged(formData.username, originalData.username)) {
      updatedUser = await userService.updateUsername(userId, formData.username!.trim())
    }
    
    if (hasChanged(formData.email, originalData.email)) {
      updatedUser = await userService.updateEmail(userId, formData.email!.trim())
    }
    
    // Only update phone number if it changed AND matches the required pattern
    // Backend pattern: ^\+?[0-9\-\s()]{7,20}$ and length 7-20
    if (hasChanged(formData.phoneNumber, originalData.phoneNumber)) {
      const phone = formData.phoneNumber!.trim()
      // Validate phone format before sending to backend
      const phonePattern = /^\+?[0-9\-\s()]{7,20}$/
      if (phonePattern.test(phone)) {
        updatedUser = await userService.updatePhoneNumber(userId, phone)
      } else {
        console.warn('Phone number format invalid, skipping update. Format: +?[0-9-() ]{7-20 characters}')
      }
    }
    
    if (hasChanged(formData.surname, originalData.surname)) {
      updatedUser = await userService.updateSurname(userId, formData.surname!.trim())
    }
    
    // Update telegramChatId if it has changed
    if (hasChanged(formData.telegramChatId, originalData.telegramChatId)) {
      updatedUser = await userService.updateTelegramChatId(userId, formData.telegramChatId!.trim())
    }
    
    // Update notification preferences if they have changed
    if (hasBooleanChanged(formData.emailNotificationsEnabled, originalData.emailNotificationsEnabled) ||
        hasBooleanChanged(formData.telegramNotificationsEnabled, originalData.telegramNotificationsEnabled)) {
      updatedUser = await userService.updateNotificationPreferences(
        userId, 
        formData.emailNotificationsEnabled ?? originalData.emailNotificationsEnabled ?? false, 
        formData.telegramNotificationsEnabled ?? originalData.telegramNotificationsEnabled ?? false
      )
    }
    
    // If no fields were updated, just return the original user data
    if (!updatedUser) {
      return { ...originalData, userId } as User
    }
    
    return updatedUser
  },

  updateUsername: async (userId: string, newUsername: string): Promise<User> => {
    const response = await api.put<Record<string, unknown>>(`/users/${userId}/change-username`, { newUsername })
    return transformUserResponse(response.data)
  },

  updateEmail: async (userId: string, newEmail: string): Promise<User> => {
    const response = await api.put<Record<string, unknown>>(`/users/${userId}/change-email`, { newEmail })
    return transformUserResponse(response.data)
  },

  updatePhoneNumber: async (userId: string, newPhoneNumber: string): Promise<User> => {
    const response = await api.put<Record<string, unknown>>(`/users/${userId}/change-phonenumber`, { newPhonenumber: newPhoneNumber })
    return transformUserResponse(response.data)
  },

  updateSurname: async (userId: string, newSurname: string): Promise<User> => {
    const response = await api.put<Record<string, unknown>>(`/users/${userId}/change-surname`, { newSurname })
    return transformUserResponse(response.data)
  },

  updateTelegramChatId: async (userId: string, newTelegramChatId: string): Promise<User> => {
    const response = await api.put<Record<string, unknown>>(`/users/${userId}/telegram-chat-id`, { telegramChatId: newTelegramChatId })
    return transformUserResponse(response.data)
  },

  updateNotificationPreferences: async (userId: string, emailNotificationsEnabled: boolean, telegramNotificationsEnabled: boolean): Promise<User> => {
    // Call separate endpoints for email and telegram notifications
    // Email notifications
    await api.put<Record<string, unknown>>(`/users/${userId}/email-notifications`, { 
      emailNotificationsEnabled, 
    })
    
    // Telegram notifications
    await api.put<Record<string, unknown>>(`/users/${userId}/telegram-notifications`, { 
      telegramNotificationsEnabled 
    })
    
    // Fetch updated user
    const response = await api.get<Record<string, unknown>>(`/users/${userId}`)
    return transformUserResponse(response.data)
  },

  // ========== ADMIN METHODS ==========

  // Admin: Get all users
  getAllUsers: async (): Promise<User[]> => {
    const response = await api.get<Record<string, unknown>[]>('/users/admin/all')
    return response.data.map(transformUserResponse)
  },

  // Admin: Delete user by ID
  deleteUser: async (userId: string): Promise<void> => {
    await api.delete(`/users/admin/${userId}`)
  },

  // Admin: Update user by ID
  adminUpdateUser: async (userId: string, data: Partial<User>): Promise<User> => {
    console.log('adminUpdateUser called with data:', data)
    const response = await api.put<Record<string, unknown>>(`/users/admin/${userId}`, {
      newUsername: data.username,
      newEmail: data.email,
      newSurname: data.surname,
      newPhonenumber: data.phoneNumber,
      telegramChatId: data.telegramChatId,
      emailNotificationsEnabled: data.emailNotificationsEnabled,
      telegramNotificationsEnabled: data.telegramNotificationsEnabled,
      newRole: data.role
    })
    return transformUserResponse(response.data)
  },
}
