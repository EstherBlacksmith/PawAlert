import { useState, useRef } from 'react'
import { Box, Heading, Button, VStack, Field, Input, Textarea, NativeSelect, Image, Text, Spinner, Alert, SimpleGrid } from '@chakra-ui/react'
import { useNavigate } from 'react-router-dom'
import { FaArrowLeft } from 'react-icons/fa'
import { petService } from '../../services/pet.service'
import { useEnumValues } from '../../hooks/useMetadata'
import { ErrorResponse } from '../../types'
import { extractError, showSuccessToast, showErrorToast } from '../../utils/errorUtils'

// Helper to convert species from Google Vision to our format
const mapSpeciesToFormat = (species: string | null): string => {
  if (!species) return ''
  const lower = species.toLowerCase()
  if (lower.includes('dog') || lower.includes('canine')) return 'DOG'
  if (lower.includes('cat') || lower.includes('feline')) return 'CAT'
  if (lower.includes('bird')) return 'BIRD'
  return 'OTHER'
}

export default function PetCreate() {
  const navigate = useNavigate()
  const fileInputRef = useRef<HTMLInputElement>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [isValidatingImage, setIsValidatingImage] = useState(false)
  const [imagePreview, setImagePreview] = useState<string | null>(null)
  const [imageError, setImageError] = useState<string | null>(null)
  const [error, setError] = useState<ErrorResponse | null>(null)
  
  // Fetch enum values from backend
  const { values: speciesValues, loading: loadingSpecies, error: errorSpecies } = useEnumValues('Species')
  const { values: sizeValues, loading: loadingSize, error: errorSize } = useEnumValues('Size')
  const { values: genderValues, loading: loadingGender, error: errorGender } = useEnumValues('Gender')
  
  const [formData, setFormData] = useState({
    officialPetName: '',
    workingPetName: '',
    chipNumber: '',
    species: '',
    breed: '',
    size: '',
    color: '',
    gender: '',
    petDescription: '',
    petImage: '',
  })

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleImageSelect = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file) return

    // Validate image with backend first
    setIsValidatingImage(true)
    setImageError(null)

    try {
      const result = await petService.validateImage(file)
      
      if (!result.valid) {
        setImageError(result.message)
        setImagePreview(null)
        // Clear the file input
        if (fileInputRef.current) {
          fileInputRef.current.value = ''
        }
        return
      }

      // Image is valid - upload to Cloudinary and get URL
      const imageUrl = await petService.uploadImage(file)
      
      // Create preview from file
      const reader = new FileReader()
      reader.onloadend = () => {
        const base64Data = reader.result as string
        setImagePreview(base64Data)
        // Store Cloudinary URL in formData
        setFormData(prev => ({
          ...prev,
          petImage: imageUrl
        }))
      }
      reader.readAsDataURL(file)

      // Auto-fill form fields ONLY if they are empty
      setFormData(prev => {
        const updated = { ...prev }
        
        // Species - only if empty
        if (!updated.species && result.species) {
          updated.species = mapSpeciesToFormat(result.species)
        }
        
        // Breed - only if empty
        if (!updated.breed && result.breed) {
          updated.breed = result.breed
        } else if (!updated.breed && result.possibleBreeds && result.possibleBreeds.length > 0) {
          // Use first possible breed if no specific breed detected
          updated.breed = result.possibleBreeds[0]
        }
        
        // Color - only if empty
        if (!updated.color && result.dominantColor) {
          updated.color = result.dominantColor
        }
        
        // Description - only if empty
        if (!updated.petDescription && result.visualLabels && result.visualLabels.length > 0) {
          // Create a description from the visual labels
          updated.petDescription = `Pet with ${result.visualLabels.slice(0, 5).join(', ')}.`
        }

        return updated
      })

    } catch (error: unknown) {
      console.error('Error validating image:', error)
      const friendlyError = extractError(error)
      setImageError(friendlyError.message)
      setImagePreview(null)
      // Clear the file input
      if (fileInputRef.current) {
        fileInputRef.current.value = ''
      }
    } finally {
      setIsValidatingImage(false)
    }
  }

  const handleRemoveImage = () => {
    setImagePreview(null)
    setImageError(null)
    setFormData({ ...formData, petImage: '' })
    if (fileInputRef.current) {
      fileInputRef.current.value = ''
    }
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setIsLoading(true)
    setError(null) // Clear previous errors
    try {
      // Convert to uppercase to match backend enums
      const dataToSend = {
        ...formData,
        species: formData.species?.toUpperCase(),
        size: formData.size?.toUpperCase(),
        gender: formData.gender?.toUpperCase(),
      }
      console.log('Submitting pet data:', dataToSend)
      await petService.createPet(dataToSend)
      showSuccessToast('Pet Created', 'Your pet has been added successfully.')
      navigate('/pets')
    } catch (error: unknown) {
      console.error('Error creating pet:', error)
      const friendlyError = extractError(error)
      setError(friendlyError)
      showErrorToast(error)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Box w="100%" mx="auto" bg="rgba(255, 255, 255, 0.85)" p={6} borderRadius="lg" boxShadow="lg">
      <Button variant="ghost" mb={4} onClick={() => navigate('/pets')}>
        <FaArrowLeft style={{ marginRight: '8px' }} />
        Back to My Pets
      </Button>

      <Heading size="lg" mb={6} color="gray.800" _dark={{ color: 'white' }}>
        Add New Pet
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

      <Box as="form" onSubmit={handleSubmit}>
        <SimpleGrid columns={{ base: 1, md: 2 }} gap={4}>
          {/* Image Upload Section - spans full width */}
          <Field.Root gridColumn={{ base: '1', md: 'span 2' }}>
            <Field.Label>Pet Photo</Field.Label>
            <Box
              border="2px dashed"
              borderColor={imageError ? 'red.400' : 'gray.300'}
              borderRadius="md"
              p={4}
              textAlign="center"
              cursor="pointer"
              _hover={{ borderColor: 'brand.400', bg: 'brand.50' }}
              onClick={() => fileInputRef.current?.click()}
              position="relative"
            >
              {isValidatingImage ? (
                <Box py={4}>
                  <Spinner size="lg" color="brand.500" />
                  <Text mt={2} color="gray.700">Validating image...</Text>
                </Box>
              ) : imagePreview ? (
                <Box position="relative">
                  <Image 
                    src={imagePreview} 
                    alt="Pet preview" 
                    maxH="150px" 
                    mx="auto" 
                    borderRadius="md"
                  />
                  <Button
                    size="sm"
                    colorPalette="red"
                    position="absolute"
                    top={2}
                    right={2}
                    onClick={(e) => {
                      e.stopPropagation()
                      handleRemoveImage()
                    }}
                  >
                    Remove
                  </Button>
                </Box>
              ) : (
                <Box py={2}>
                  <Text color="gray.700">
                    Click to upload a photo
                  </Text>
                  <Text fontSize="sm" color="gray.600" mt={1}>
                    (AI will detect species, breed, color)
                  </Text>
                </Box>
              )}
              <input
                ref={fileInputRef}
                type="file"
                accept="image/*"
                onChange={handleImageSelect}
                style={{ display: 'none' }}
              />
            </Box>
            {imageError && (
              <Text color="red.500" fontSize="sm" mt={1}>
                {imageError}
              </Text>
            )}
          </Field.Root>

          <Field.Root gridColumn={{ base: '1', md: '1' }}>
            <Field.Label>Official Name *</Field.Label>
            <Input name="officialPetName" value={formData.officialPetName} onChange={handleChange} required bg="white" color="gray.800" />
          </Field.Root>

          <Field.Root gridColumn={{ base: '1', md: '2' }}>
            <Field.Label>Working Name</Field.Label>
            <Input name="workingPetName" value={formData.workingPetName} onChange={handleChange} bg="white" color="gray.800" />
          </Field.Root>

          <Field.Root gridColumn={{ base: '1', md: '1' }}>
            <Field.Label>Chip Number</Field.Label>
            <Input name="chipNumber" value={formData.chipNumber} onChange={handleChange} bg="white" color="gray.800" />
          </Field.Root>

          <Field.Root gridColumn={{ base: '1', md: '2' }}>
            <Field.Label>Species *</Field.Label>
            <NativeSelect.Root>
              <NativeSelect.Field 
                name="species" 
                value={formData.species} 
                onChange={handleChange}
                _disabled={{ opacity: loadingSpecies ? 0.5 : 1, pointerEvents: loadingSpecies ? 'none' : 'auto' }}
              >
                <option value="">Select species</option>
                {speciesValues.map((species) => (
                  <option key={species.value} value={species.value}>
                    {species.displayName}
                  </option>
                ))}
              </NativeSelect.Field>
              <NativeSelect.Indicator />
            </NativeSelect.Root>
          </Field.Root>

          <Field.Root gridColumn={{ base: '1', md: '1' }}>
            <Field.Label>Breed</Field.Label>
            <Input name="breed" value={formData.breed} onChange={handleChange} placeholder={!formData.breed ? "Detected from image" : ""} bg="white" color="gray.800" />
          </Field.Root>

          <Field.Root gridColumn={{ base: '1', md: '2' }}>
            <Field.Label>Size</Field.Label>
            <NativeSelect.Root>
               <NativeSelect.Field name="size" value={formData.size} onChange={handleChange} bg="white" color="gray.800" _disabled={{ opacity: loadingSize ? 0.5 : 1, pointerEvents: loadingSize ? 'none' : 'auto' }}>
                <option value="">Select size</option>
                {sizeValues.map((size) => (
                  <option key={size.value} value={size.value}>
                    {size.displayName}
                  </option>
                ))}
              </NativeSelect.Field>
              <NativeSelect.Indicator />
            </NativeSelect.Root>
          </Field.Root>

          <Field.Root gridColumn={{ base: '1', md: '1' }}>
            <Field.Label>Color</Field.Label>
            <Input name="color" value={formData.color} onChange={handleChange} placeholder={!formData.color ? "Detected from image" : ""} bg="white" color="gray.800" />
          </Field.Root>

          <Field.Root gridColumn={{ base: '1', md: '2' }}>
            <Field.Label>Gender</Field.Label>
            <NativeSelect.Root>
               <NativeSelect.Field name="gender" value={formData.gender} onChange={handleChange} bg="white" color="gray.800" _disabled={{ opacity: loadingGender ? 0.5 : 1, pointerEvents: loadingGender ? 'none' : 'auto' }}>
                <option value="">Select gender</option>
                {genderValues.map((gender) => (
                  <option key={gender.value} value={gender.value}>
                    {gender.displayName}
                  </option>
                ))}
              </NativeSelect.Field>
              <NativeSelect.Indicator />
            </NativeSelect.Root>
          </Field.Root>

          <Field.Root gridColumn={{ base: '1', md: 'span 2' }}>
            <Field.Label>Description</Field.Label>
            <Textarea 
              name="petDescription" 
              value={formData.petDescription} 
              onChange={handleChange}
              placeholder={!formData.petDescription ? "Auto-generated from image" : ""}
              rows={2}
              bg="white"
              color="gray.800"
            />
          </Field.Root>

          <Box gridColumn={{ base: '1', md: 'span 2' }} pt={2}>
            <Button type="submit" colorPalette="brand" bg="brand.500" _hover={{ bg: 'brand.600' }} w="full" loading={isLoading}>
              Add Pet
            </Button>
          </Box>
        </SimpleGrid>
      </Box>
    </Box>
  )
}
