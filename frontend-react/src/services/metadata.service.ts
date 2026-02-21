import api from './api'
import { MetadataListDto, MetadataDto, EnumType } from '../types'

// Fallback values in case API is unavailable
const FALLBACK_VALUES: Record<EnumType, MetadataDto[]> = {
  Species: [
    { value: 'CAT', displayName: 'Cat' },
    { value: 'DOG', displayName: 'Dog' },
    { value: 'BUNNY', displayName: 'Bunny' },
    { value: 'FERRET', displayName: 'Ferret' },
    { value: 'TURTLE', displayName: 'Turtle' },
    { value: 'BIRD', displayName: 'Bird' },
  ],
  Size: [
    { value: 'TINY', displayName: 'Tiny' },
    { value: 'SMALL', displayName: 'Small' },
    { value: 'MEDIUM', displayName: 'Medium' },
    { value: 'LARGE', displayName: 'Large' },
    { value: 'GIANT', displayName: 'Giant' },
  ],
  Gender: [
    { value: 'FEMALE', displayName: 'Female' },
    { value: 'MALE', displayName: 'Male' },
    { value: 'UNKNOWN', displayName: 'Unknown' },
  ],
  StatusNames: [
    { value: 'OPENED', displayName: 'Opened' },
    { value: 'CLOSED', displayName: 'Closed' },
    { value: 'SEEN', displayName: 'Seen' },
    { value: 'SAFE', displayName: 'Safe' },
  ],
  EventType: [
    { value: 'STATUS_CHANGED', displayName: 'Status changed' },
    { value: 'TITLE_CHANGED', displayName: 'Title changed' },
    { value: 'DESCRIPTION_CHANGED', displayName: 'Description changed' },
  ],
  ClosureReason: [
    { value: 'FOUNDED', displayName: 'Founded' },
    { value: 'FALSE_ALARM', displayName: 'False alarm' },
    { value: 'OTHER_REASON', displayName: 'Other reason' },
  ],
  NotificationChannel: [
    { value: 'EMAIL', displayName: 'Email' },
    { value: 'PUSH', displayName: 'Push' },
    { value: 'SMS', displayName: 'Sms' },
    { value: 'WHATSAPP', displayName: 'WhatsApp' },
    { value: 'ALL', displayName: 'All' },
  ],
  Role: [
    { value: 'USER', displayName: 'User' },
    { value: 'ADMIN', displayName: 'Admin' },
  ],
  ContentSafetyStatus: [
    { value: 'SAFE', displayName: 'Safe' },
    { value: 'QUESTIONABLE', displayName: 'Questionable' },
    { value: 'UNSAFE', displayName: 'Unsafe' },
  ],
}

export const metadataService = {
  // Fetch all metadata (all enums)
  getAllMetadata: async (): Promise<MetadataListDto[]> => {
    try {
      const response = await api.get<MetadataListDto[]>('/v1/metadata/get-metadata')
      console.log('Metadata API response:', response.data)
      return response.data
    } catch (error) {
      console.error('Error fetching metadata:', error)
      throw error
    }
  },

  // Get a specific enum type by name
  getEnumValues: async (enumType: EnumType): Promise<MetadataDto[]> => {
    const allMetadata = await metadataService.getAllMetadata()
    const found = allMetadata.find(m => m.type === enumType)
    return found?.metadata || []
  },

  // Helper to get display name for a specific enum value
  getDisplayName: async (enumType: EnumType, value: string): Promise<string> => {
    const values = await metadataService.getEnumValues(enumType)
    const found = values.find(v => v.value === value)
    return found?.displayName || value
  },
}

// Cache for metadata to avoid repeated API calls
let metadataCache: MetadataListDto[] | null = null

export const getCachedMetadata: () => Promise<MetadataListDto[]> = async () => {
  if (!metadataCache) {
    metadataCache = await metadataService.getAllMetadata()
  }
  return metadataCache
}

export const getCachedEnumValues = async (enumType: EnumType): Promise<MetadataDto[]> => {
  try {
    const allMetadata = await getCachedMetadata()
    console.log('All metadata cached:', allMetadata)
    console.log('Looking for enum type:', enumType)
    const found = allMetadata.find(m => m.type === enumType)
    console.log('Found metadata:', found)
    
    if (found?.metadata && found.metadata.length > 0) {
      return found.metadata
    }
    
    console.warn('No metadata found for', enumType, '- using fallback values')
    return FALLBACK_VALUES[enumType] || []
  } catch (error) {
    console.error('Error getting cached enum values:', error)
    console.warn('Using fallback values for', enumType)
    return FALLBACK_VALUES[enumType] || []
  }
}

// Clear cache (useful for testing or when data might have changed)
export const clearMetadataCache = () => {
  metadataCache = null
}
