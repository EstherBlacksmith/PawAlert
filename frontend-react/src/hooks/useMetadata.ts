import { useState, useEffect, useCallback } from 'react'
import { MetadataDto, EnumType } from '../types'
import { getCachedEnumValues } from '../services/metadata.service'

// Hook to get metadata for a specific enum type (alias for useEnumValues with different return shape)
export function useMetadata(enumType: EnumType) {
  const { values, loading, error } = useEnumValues(enumType)
  return { metadata: values, loading, error }
}

// Hook to get enum values for a specific type
export function useEnumValues(enumType: EnumType) {
  const [values, setValues] = useState<MetadataDto[]>([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    let mounted = true

    const fetchValues = async () => {
      try {
        setLoading(true)
        const data = await getCachedEnumValues(enumType)
        console.log('useEnumValues - data for', enumType, ':', data)
        if (mounted) {
          setValues(data)
          setError(null)
        }
      } catch (err) {
        console.error('useEnumValues - error for', enumType, ':', err)
        if (mounted) {
          setError(err instanceof Error ? err.message : 'Failed to load enum values')
        }
      } finally {
        if (mounted) {
          setLoading(false)
        }
      }
    }

    fetchValues()

    return () => {
      mounted = false
    }
  }, [enumType])

  return { values, loading, error }
}

// Hook to get display name for a specific enum value
export function useEnumDisplayName(enumType: EnumType, value: string | undefined) {
  const { values, loading, error } = useEnumValues(enumType)

  const displayName = value 
    ? values.find(v => v.value === value)?.displayName || value 
    : ''

  return { displayName, loading, error }
}

// Helper function to map backend species format to display format
// The backend uses uppercase (e.g., "DOG", "CAT") but we might need to handle display
export function mapBackendSpeciesToDisplay(species: string): string {
  const speciesMap: Record<string, string> = {
    'DOG': 'Dog',
    'CAT': 'Cat',
    'BUNNY': 'Bunny',
    'FERRET': 'Ferret',
    'TURTLE': 'Turtle',
    'BIRD': 'Bird',
  }
  return speciesMap[species] || species
}

// Helper function to map display value to backend format
export function mapDisplayToBackend(value: string): string {
  return value.toUpperCase()
}
