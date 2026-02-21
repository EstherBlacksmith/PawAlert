export type UserRole = 'USER' | 'ADMIN'

export interface User {
  userId: string
  username: string | null
  email: string | null
  surname?: string | null
  phoneNumber?: string | null
  telegramChatId?: string | null
  role?: UserRole | null
  emailNotificationsEnabled?: boolean
  telegramNotificationsEnabled?: boolean
}

export interface Pet {
  petId: string
  officialPetName: string
  workingPetName?: string
  chipNumber?: string
  species: string
  breed?: string
  size?: string
  color?: string
  gender?: string
  petDescription?: string
  petImage?: string
}

export interface Alert {
  id: string
  title: string
  description: string
  petId: string
  userId: string
  latitude: number
  longitude: number
  status: 'OPENED' | 'SEEN' | 'SAFE' | 'CLOSED'
  closureReason?: string
  createdAt: string
  updatedAt?: string
}

export type AlertStatus = 'OPENED' | 'SEEN' | 'SAFE' | 'CLOSED'

export interface AlertSearchParams {
  status?: AlertStatus
  petId?: string
  userId?: string
}

// Extended search filters for alert list and admin dashboard
export interface AlertSearchFilters {
  status?: AlertStatus
  title?: string
  petName?: string
  species?: string
  breed?: string
  createdFrom?: string  // ISO date string
  createdTo?: string
  updatedFrom?: string
  updatedTo?: string
  latitude?: number
  longitude?: number
  radiusKm?: number
}

export interface LoginRequest {
  email: string
  password: string
}

// Backend returns flat fields, we transform to nested user object
export interface AuthResponse {
  token: string
  tokenType: string
  expiresIn: number
  userId: string
  username: string
  email: string
  role: string
  phonenumber: string
  surname: string
}

export interface LoginResponse {
  token: string
  user: User
}

export type RegisterRequest = {
  username: string
  password: string
  email: string
  fullName?: string
  surname?: string
  phoneNumber?: string
}

export interface CreatePetRequest {
  officialPetName: string
  workingPetName?: string
  chipNumber?: string
  species: string
  breed?: string
  size?: string
  color?: string
  gender?: string
  petDescription?: string
  petImage?: string
}

export interface UpdatePetRequest extends Partial<CreatePetRequest> {}

export interface CreateAlertRequest {
  title: string
  description: string
  petId: string
  userId: string | undefined
  latitude: number
  longitude: number
}

export interface UpdateAlertStatusRequest {
  newStatus: AlertStatus
  userId: string
  latitude: number
  longitude: number
}

export interface CloseAlertRequest {
  userId: string
  latitude: number
  longitude: number
  closureReason: 'FOUNDED' | 'FALSE_ALARM' | 'OTHER_REASON'
}

// Image validation response from backend
export interface ImageValidationResponse {
  valid: boolean
  message: string
  species: string | null
  speciesConfidence: number
  breed: string | null
  breedConfidence: number
  possibleBreeds: string[]
  dominantColor: string | null
  dominantColorHex: string | null
  visualLabels: string[]
  isSafeForWork: boolean
  safetyMessage: string
}

// Metadata types for enum values from backend
export interface MetadataDto {
  value: string
  displayName: string
}

export interface MetadataListDto {
  type: string
  metadata: MetadataDto[]
}

// Enum types exposed by the backend
export type EnumType = 'Species' | 'Size' | 'Gender' | 'StatusNames' | 'EventType' | 'ClosureReason' | 'NotificationChannel' | 'Role' | 'ContentSafetyStatus'

// Error response from backend
export interface ErrorResponse {
  status: number
  error: string
  message: string
}

// Location types for GPS/IP geolocation
export interface LocationData {
  latitude: number
  longitude: number
  source?: 'gps' | 'ip'
}

export type LocationSource = 'gps' | 'ip' | null

// Alert Event types - represents the history of changes for an alert
export type EventType = 'STATUS_CHANGED' | 'TITLE_CHANGED' | 'DESCRIPTION_CHANGED'
export type ClosureReason = 'FOUNDED' | 'FALSE_ALARM' | 'OTHER_REASON'

export interface AlertEvent {
  id: string
  alertId: string
  eventType: EventType
  previousStatus: string | null
  newStatus: string | null
  oldValue: string | null
  newValue: string | null
  latitude: number | null
  longitude: number | null
  closureReason: ClosureReason | null
  changedBy: string
  changedAt: string
}

// Alert Subscription types
export interface AlertSubscription {
  id: string
  alertId: string
  userId: string
  active: boolean
  subscribedAt: string
}

export interface SubscribedResponse {
  subscribed: boolean
}

// Extended subscription with alert details for MySubscriptions page
export interface AlertSubscriptionWithDetails extends AlertSubscription {
  alert: {
    id: string
    title: string
    status: AlertStatus
    petName?: string
    petImage?: string
    createdAt: string
  }
}
