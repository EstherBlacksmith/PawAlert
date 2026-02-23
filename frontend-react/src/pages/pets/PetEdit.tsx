import { useState, useEffect, useRef } from 'react'
import { Box, Heading, Button, VStack, Input, Textarea, Image, Text, Spinner, Flex, Alert, SimpleGrid, Select } from '@chakra-ui/react'
import { useNavigate, useParams } from 'react-router-dom'
import { FaArrowLeft } from 'react-icons/fa'
import { petService } from '../../services/pet.service'
import { useEnumValues } from '../../hooks/useMetadata'
import { useAuth } from '../../context/AuthContext'
import { ErrorResponse } from '../../types'

export default function PetEdit() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { user } = useAuth()
  const fileInputRef = useRef<HTMLInputElement>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [isFetching, setIsFetching] = useState(true)
  const [isValidatingImage, setIsValidatingImage] = useState(false)
  const [imagePreview, setImagePreview] = useState<string | null>(null)
  const [imageError, setImageError] = useState<string | null>(null)
  const [error, setError] = useState<ErrorResponse | null>(null)
  const [petOwnerId, setPetOwnerId] = useState<string | null>(null)
  
  // Enum values from backend
  const { values: speciesValues, loading: speciesLoading } = useEnumValues('Species')
  const { values: sizeValues, loading: sizeLoading } = useEnumValues('Size')
  const { values: genderValues, loading: genderLoading } = useEnumValues('Gender')
  
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

  useEffect(() => {
    const fetchPet = async () => {
      try {
        if (id) {
          const pet = await petService.getPet(id)
          setPetOwnerId(pet.userId)
          
          // Check if user has permission to edit
          console.log('[DEBUG] user object:', user)
          console.log('[DEBUG] user?.userId:', user?.userId)
          console.log('[DEBUG] pet.userId:', pet.userId)
          console.log('[DEBUG] user?.role:', user?.role)
          console.log('[DEBUG] userId comparison:', user?.userId, '===', pet.userId, '?', user?.userId === pet.userId)
          
          // Don't check permissions on frontend - let backend handle it
          // The backend will return 403 if user doesn't have permission
          
          setFormData({
            officialPetName: pet.officialPetName,
            workingPetName: pet.workingPetName || '',
            chipNumber: pet.chipNumber || '',
            species: pet.species,
            breed: pet.breed || '',
            size: pet.size || '',
            color: pet.color || '',
            gender: pet.gender || '',
            petDescription: pet.petDescription || '',
            petImage: pet.petImage || '',
          })
          // Set the existing image as preview
          if (pet.petImage) {
            setImagePreview(pet.petImage)
          }
        }
      } catch (error) {
        console.error('Error fetching pet:', error)
      } finally {
        setIsFetching(false)
      }
    }
    fetchPet()
  }, [id, user])

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
        // Keep the old image if validation fails
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

    } catch (error: any) {
      console.error('Error validating image:', error)
      setImageError(error.response?.data?.message || 'Error validating image')
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
      if (id) {
        // Convert empty strings to null and uppercase for backend enums
        const dataToSend = {
          ...formData,
          species: formData.species?.trim() || undefined,
          size: formData.size?.trim() || undefined,
          gender: formData.gender?.trim() || undefined,
          breed: formData.breed?.trim() || undefined,
          color: formData.color?.trim() || undefined,
          workingPetName: formData.workingPetName?.trim() || undefined,
          chipNumber: formData.chipNumber?.trim() || undefined,
          petDescription: formData.petDescription?.trim() || undefined,
          petImage: formData.petImage?.trim() || undefined,
          officialPetName: formData.officialPetName?.trim() || undefined,
        }
        await petService.updatePet(id, dataToSend)
      }
      navigate('/pets')
    } catch (error: any) {
      console.error('Error updating pet:', error)
      // Handle backend error response
      if (error.response?.data) {
        setError(error.response.data)
      } else {
        setError({
          status: error.response?.status || 500,
          error: error.response?.statusText || 'Error',
          message: 'An unexpected error occurred. Please try again.'
        })
      }
    } finally {
      setIsLoading(false)
    }
  }

  if (isFetching) {
    return (
      <Flex justify="center" align="center" minH="300px">
        <Spinner size="xl" color="brand.500" />
      </Flex>
    )
  }

  return (
    <Box w="100%" mx="auto" bg="rgba(255, 255, 255, 0.85)" p={6} borderRadius="lg" boxShadow="lg">
      <Button variant="ghost" mb={4} onClick={() => navigate(`/pets/${id}`)}>
        <FaArrowLeft style={{ marginRight: '8px' }} />
        Back to Pet
      </Button>

      <Heading size="lg" mb={6} color="gray.800" _dark={{ color: 'white' }}>
        Edit Pet
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
           <Box gridColumn={{ base: '1', md: 'span 2' }}>
             <Text as="label" display="block" fontWeight="medium" mb={2}>Pet Photo</Text>
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
                    Click to upload a new photo
                  </Text>
                  <Text fontSize="sm" color="gray.600" mt={1}>
                    (AI will validate the image)
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
           </Box>

           <Box gridColumn={{ base: '1', md: '1' }}>
             <Text as="label" display="block" fontWeight="medium" mb={2}>Official Name *</Text>
             <Input name="officialPetName" value={formData.officialPetName} onChange={handleChange} required />
           </Box>

           <Box gridColumn={{ base: '1', md: '2' }}>
             <Text as="label" display="block" fontWeight="medium" mb={2}>Working Name</Text>
             <Input name="workingPetName" value={formData.workingPetName} onChange={handleChange} />
           </Box>

           <Box gridColumn={{ base: '1', md: '1' }}>
             <Text as="label" display="block" fontWeight="medium" mb={2}>Chip Number</Text>
             <Input name="chipNumber" value={formData.chipNumber} onChange={handleChange} />
           </Box>

           <Box gridColumn={{ base: '1', md: '2' }}>
             <Text as="label" display="block" fontWeight="medium" mb={2}>Species *</Text>
             <Select 
               name="species" 
               value={formData.species} 
               onChange={handleChange}
               disabled={speciesLoading}
             >
               {speciesLoading ? (
                 <option value="">Loading...</option>
               ) : (
                 speciesValues.map((item) => (
                   <option key={item.value} value={item.value}>
                     {item.displayName}
                   </option>
                 ))
               )}
             </Select>
           </Box>

           <Box gridColumn={{ base: '1', md: '1' }}>
             <Text as="label" display="block" fontWeight="medium" mb={2}>Breed</Text>
             <Input name="breed" value={formData.breed} onChange={handleChange} />
           </Box>

           <Box gridColumn={{ base: '1', md: '2' }}>
             <Text as="label" display="block" fontWeight="medium" mb={2}>Size</Text>
             <Select 
               name="size" 
               value={formData.size} 
               onChange={handleChange}
               disabled={sizeLoading}
             >
               {sizeLoading ? (
                 <option value="">Loading...</option>
               ) : (
                 sizeValues.map((item) => (
                   <option key={item.value} value={item.value}>
                     {item.displayName}
                   </option>
                 ))
               )}
             </Select>
           </Box>

           <Box gridColumn={{ base: '1', md: '1' }}>
             <Text as="label" display="block" fontWeight="medium" mb={2}>Color</Text>
             <Input name="color" value={formData.color} onChange={handleChange} />
           </Box>

           <Box gridColumn={{ base: '1', md: '2' }}>
             <Text as="label" display="block" fontWeight="medium" mb={2}>Gender</Text>
             <Select 
               name="gender" 
               value={formData.gender} 
               onChange={handleChange}
               disabled={genderLoading}
             >
               {genderLoading ? (
                 <option value="">Loading...</option>
               ) : (
                 genderValues.map((item) => (
                   <option key={item.value} value={item.value}>
                     {item.displayName}
                   </option>
                 ))
               )}
             </Select>
           </Box>

           <Box gridColumn={{ base: '1', md: 'span 2' }}>
             <Text as="label" display="block" fontWeight="medium" mb={2}>Description</Text>
             <Textarea name="petDescription" value={formData.petDescription} onChange={handleChange} rows={2} />
           </Box>

          <Box gridColumn={{ base: '1', md: 'span 2' }} pt={2}>
            <Button type="submit" colorPalette="brand" bg="brand.500" _hover={{ bg: 'brand.600' }} w="full" loading={isLoading}>
              Update Pet
            </Button>
          </Box>
        </SimpleGrid>
      </Box>
    </Box>
  )
}
