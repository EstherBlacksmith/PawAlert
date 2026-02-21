import { useState, useEffect } from 'react'
import { Box, Heading, Button, VStack, Field, Input, Textarea, NativeSelect, Spinner, Flex, Text, Badge, HStack, Alert } from '@chakra-ui/react'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { alertService } from '../../services/alert.service'
import { petService } from '../../services/pet.service'
import { Pet, ErrorResponse } from '../../types'
import { useAuth } from '../../context/AuthContext'
import { useLocation } from '../../hooks/useLocation'
import LocationMap from '../../components/map/LocationMap'
import { extractError } from '../../utils/errorUtils'

export default function AlertCreate() {
  const navigate = useNavigate()
  const [searchParams] = useSearchParams()
  const preselectedPetId = searchParams.get('petId')
  const { user } = useAuth()
  const [isLoading, setIsLoading] = useState(false)
  const [pets, setPets] = useState<Pet[]>([])
  const [isFetchingPets, setIsFetchingPets] = useState(true)
  const [preselectedPet, setPreselectedPet] = useState<Pet | null>(null)
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    petId: '',
    latitude: '',
    longitude: '',
  })
  const [error, setError] = useState<ErrorResponse | null>(null)
  
  const { 
    latitude: detectedLatitude, 
    longitude: detectedLongitude, 
    source: locationSource, 
    error: locationError, 
    isLoading: isLocationLoading, 
    detectLocation,
    clearLocation 
  } = useLocation()

  useEffect(() => {
    const fetchPets = async () => {
      try {
        const data = await petService.getPets()
        setPets(data)
      } catch (error) {
        console.error('Error fetching pets:', error)
      } finally {
        setIsFetchingPets(false)
      }
    }
    fetchPets()
  }, [])

  // Pre-select pet if petId is provided in URL
  useEffect(() => {
    const loadPreselectedPet = async () => {
      if (preselectedPetId && pets.length > 0) {
        // Find the pet in the already loaded pets list
        const pet = pets.find(p => p.petId === preselectedPetId)
        if (pet) {
          setPreselectedPet(pet)
          setFormData(prev => ({
            ...prev,
            petId: preselectedPetId,
            title: `Alert: ${pet.officialPetName} is missing`,
          }))
        } else {
          // Pet not found in user's pets, try to fetch it directly
          try {
            const petData = await petService.getPet(preselectedPetId)
            setPreselectedPet(petData)
            setFormData(prev => ({
              ...prev,
              petId: preselectedPetId,
              title: `Alert: ${petData.officialPetName} is missing`,
            }))
          } catch (error) {
            console.error('Error fetching preselected pet:', error)
            // Pet not found or not accessible, user will need to select manually
          }
        }
      }
    }
    loadPreselectedPet()
  }, [preselectedPetId, pets])

  // Auto-detect location when component mounts
  useEffect(() => {
    detectLocation()
  }, []) // Empty dependency array = runs once on mount

  // Update form data when location is detected
  useEffect(() => {
    if (detectedLatitude !== null && detectedLongitude !== null) {
      setFormData(prev => ({
        ...prev,
        latitude: detectedLatitude.toString(),
        longitude: detectedLongitude.toString(),
      }))
    }
  }, [detectedLatitude, detectedLongitude])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleDetectLocation = async () => {
    clearLocation()
    await detectLocation()
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    // Add validation before proceeding
    const lat = parseFloat(formData.latitude)
    const lng = parseFloat(formData.longitude)
    
    if (isNaN(lat) || isNaN(lng)) {
      setError({
        status: 400,
        error: 'Location Required',
        message: 'Please wait for location detection or click "Detect Location" button.'
      })
      return
    }
    
    setIsLoading(true)
    setError(null) // Clear previous errors
    try {
      await alertService.createAlert({
        ...formData,
        userId: user?.userId,
        latitude: lat,
        longitude: lng,
      })
      // Toast notification is handled by SSE notification system (NotificationContext)
      // to avoid duplicate toasts for new alerts
      navigate('/alerts')
    } catch (error: unknown) {
      // Use centralized error extraction for user-friendly messages
      const friendlyError = extractError(error)
      setError(friendlyError)
    } finally {
      setIsLoading(false)
    }
  }

  if (isFetchingPets) {
    return (
      <Flex justify="center" align="center" minH="300px">
        <Spinner size="xl" color="purple.500" />
      </Flex>
    )
  }

  return (
    <Box maxW="600px" mx="auto">
      <Heading size="lg" mb={6} color="gray.800" _dark={{ color: 'white' }}>
        Create Alert
      </Heading>

      {error && (
        <Alert.Root status="error" mb={6} borderRadius="md">
          <Alert.Indicator />
          <Box flex="1">
            <Alert.Title fontSize="sm" fontWeight="bold">
              {error.error || 'Error'}
            </Alert.Title>
            <Alert.Description fontSize="sm">
              {error.message}
            </Alert.Description>
          </Box>
        </Alert.Root>
      )}

      {preselectedPet && (
        <Alert.Root status="info" mb={6} borderRadius="md">
          <Alert.Indicator />
          <Box flex="1">
            <Alert.Title fontSize="sm" fontWeight="bold">
              Creating alert for: {preselectedPet.officialPetName}
            </Alert.Title>
            <Alert.Description fontSize="sm">
              {preselectedPet.species} {preselectedPet.breed && `- ${preselectedPet.breed}`}
            </Alert.Description>
          </Box>
        </Alert.Root>
      )}

      <Box as="form" onSubmit={handleSubmit}>
        <VStack gap={4}>
          <Field.Root>
            <Field.Label>Title *</Field.Label>
            <Input name="title" value={formData.title} onChange={handleChange} required placeholder="Alert title" />
          </Field.Root>

          <Field.Root>
            <Field.Label>Description *</Field.Label>
            <Textarea name="description" value={formData.description} onChange={handleChange} required placeholder="Describe the situation" />
          </Field.Root>

          <Field.Root>
            <Field.Label>Pet *</Field.Label>
            <NativeSelect.Root>
              <NativeSelect.Field name="petId" value={formData.petId} onChange={handleChange}>
                <option value="">Select a pet</option>
                {pets.map((pet) => (
                  <option key={pet.petId} value={pet.petId}>
                    {pet.officialPetName}
                  </option>
                ))}
              </NativeSelect.Field>
              <NativeSelect.Indicator />
            </NativeSelect.Root>
          </Field.Root>

          {/* Location Detection Section */}
          <Box w="full">
            <HStack mb={2} justify="space-between" align="center">
              <Text fontWeight="medium" fontSize="sm">Location</Text>
              {locationSource && (
                <Badge 
                  colorPalette={locationSource === 'gps' ? 'green' : 'orange'}
                  fontSize="xs"
                >
                  {locationSource === 'gps' ? 'GPS' : 'IP'} Location
                </Badge>
              )}
            </HStack>

            {locationError && (
              <Alert.Root status="warning" mb={3} borderRadius="md" fontSize="sm">
                <Alert.Indicator />
                <Text fontSize="sm">{locationError}</Text>
              </Alert.Root>
            )}

            {/* Interactive Map */}
            <LocationMap
              latitude={formData.latitude ? parseFloat(formData.latitude) : null}
              longitude={formData.longitude ? parseFloat(formData.longitude) : null}
              onLocationChange={(lat, lng) => setFormData(prev => ({
                ...prev,
                latitude: lat.toFixed(6),
                longitude: lng.toFixed(6),
              }))}
              onDetectLocation={handleDetectLocation}
              isDetectingLocation={isLocationLoading}
              height="300px"
            />

            {/* Manual Coordinate Input (fallback) */}
            <HStack gap={4} mt={3}>
              <Field.Root flex="1">
                <Field.Label>Latitude *</Field.Label>
                <Input 
                  name="latitude" 
                  type="number" 
                  step="any" 
                  value={formData.latitude} 
                  onChange={handleChange} 
                  required 
                  placeholder="40.416775" 
                />
              </Field.Root>

              <Field.Root flex="1">
                <Field.Label>Longitude *</Field.Label>
                <Input 
                  name="longitude" 
                  type="number" 
                  step="any" 
                  value={formData.longitude} 
                  onChange={handleChange} 
                  required 
                  placeholder="-3.703790" 
                />
              </Field.Root>
            </HStack>
          </Box>

          <Box w="full" pt={4}>
            <Button type="submit" colorPalette="red" w="full" loading={isLoading}>
              Create Alert
            </Button>
          </Box>
        </VStack>
      </Box>
    </Box>
  )
}
