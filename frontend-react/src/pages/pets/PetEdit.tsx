import { useState, useEffect, useRef } from 'react'
import { Box, Typography, Button, Stack, TextField, CircularProgress, Alert as MuiAlert, Grid, FormControl, InputLabel, Select, MenuItem, Paper } from '@mui/material'
import { useNavigate, useParams, useLocation } from 'react-router-dom'
import { FaArrowLeft } from 'react-icons/fa'
import { petService } from '../../services/pet.service'
import { useEnumValues } from '../../hooks/useMetadata'
import { useAuth } from '../../context/AuthContext'
import { ErrorResponse } from '../../types'

export default function PetEdit() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const location = useLocation()
  const { user } = useAuth()
  
  // Check if we're in admin context
  const isAdminContext = location.pathname.startsWith('/admin')
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
      // Navigate based on context - admin goes back to admin pet detail, user goes to their pet list
      if (isAdminContext) {
        navigate(`/admin/pets/${id}`)
      } else {
        navigate('/pets')
      }
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
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '300px' }}>
        <CircularProgress color="primary" />
      </Box>
    )
  }

  return (
    <Paper sx={{ width: '100%', mx: 'auto', bgcolor: 'rgba(255, 255, 255, 0.85)', p: 3, borderRadius: 2, boxShadow: 3 }}>
      <Button variant="text" sx={{ mb: 2 }} onClick={() => navigate(isAdminContext ? `/admin/pets/${id}` : `/pets/${id}`)} startIcon={<FaArrowLeft />}>
        Back to Pet
      </Button>

      <Typography variant="h5" mb={3} color="text.primary">
        Edit Pet
      </Typography>

      {error && (
        <MuiAlert severity="error" sx={{ mb: 3 }}>
          <Typography variant="subtitle2" component="div">{error.error || 'Error'}</Typography>
          <Typography variant="body2">{error.message}</Typography>
        </MuiAlert>
      )}

       <Box component="form" onSubmit={handleSubmit}>
         <Grid container spacing={2}>
           {/* Image Upload Section - spans full width */}
           <Grid item xs={12}>
             <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Pet Photo</Typography>
            <Box
              sx={{
                border: '2px dashed',
                borderColor: imageError ? 'error.main' : 'grey.400',
                borderRadius: 1,
                p: 2,
                textAlign: 'center',
                cursor: 'pointer',
                '&:hover': { borderColor: 'primary.main', bgcolor: 'primary.50' },
                position: 'relative'
              }}
              onClick={() => fileInputRef.current?.click()}
            >
              {isValidatingImage ? (
                <Box sx={{ py: 2 }}>
                  <CircularProgress color="primary" />
                  <Typography sx={{ mt: 1 }} color="text.secondary">Validating image...</Typography>
                </Box>
              ) : imagePreview ? (
                <Box sx={{ position: 'relative' }}>
                  <Box
                    component="img"
                    src={imagePreview}
                    alt="Pet preview"
                    sx={{ maxHeight: '150px', mx: 'auto', borderRadius: 1 }}
                  />
                  <Button
                    size="small"
                    variant="contained"
                    color="error"
                    sx={{ position: 'absolute', top: 8, right: 8 }}
                    onClick={(e) => {
                      e.stopPropagation()
                      handleRemoveImage()
                    }}
                  >
                    Remove
                  </Button>
                </Box>
              ) : (
                <Box sx={{ py: 1 }}>
                  <Typography color="text.primary">
                    Click to upload a new photo
                  </Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
                    (AI will validate the image)
                  </Typography>
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
              <Typography color="error" variant="body2" sx={{ mt: 0.5 }}>
                {imageError}
               </Typography>
             )}
           </Grid>

           <Grid item xs={12} sm={6}>
             <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Official Name *</Typography>
             <TextField name="officialPetName" value={formData.officialPetName} onChange={handleChange} required fullWidth size="small" />
           </Grid>

           <Grid item xs={12} sm={6}>
             <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Working Name</Typography>
             <TextField name="workingPetName" value={formData.workingPetName} onChange={handleChange} fullWidth size="small" />
           </Grid>

           <Grid item xs={12} sm={6}>
             <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Chip Number</Typography>
             <TextField name="chipNumber" value={formData.chipNumber} onChange={handleChange} fullWidth size="small" />
           </Grid>

           <Grid item xs={12} sm={6}>
             <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Species *</Typography>
             <FormControl fullWidth size="small">
               <InputLabel>Select species</InputLabel>
               <Select
                 name="species" 
                 value={formData.species} 
                 onChange={handleChange}
                 disabled={speciesLoading}
                 label="Select species"
               >
                 {speciesLoading ? (
                   <MenuItem value="">Loading...</MenuItem>
                 ) : (
                   speciesValues.map((item) => (
                     <MenuItem key={item.value} value={item.value}>
                       {item.displayName}
                     </MenuItem>
                   ))
                 )}
               </Select>
             </FormControl>
           </Grid>

           <Grid item xs={12} sm={6}>
             <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Breed</Typography>
             <TextField name="breed" value={formData.breed} onChange={handleChange} fullWidth size="small" />
           </Grid>

           <Grid item xs={12} sm={6}>
             <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Size</Typography>
             <FormControl fullWidth size="small">
               <InputLabel>Select size</InputLabel>
               <Select
                 name="size" 
                 value={formData.size} 
                 onChange={handleChange}
                 disabled={sizeLoading}
                 label="Select size"
               >
                 {sizeLoading ? (
                   <MenuItem value="">Loading...</MenuItem>
                 ) : (
                   sizeValues.map((item) => (
                     <MenuItem key={item.value} value={item.value}>
                       {item.displayName}
                     </MenuItem>
                   ))
                 )}
               </Select>
             </FormControl>
           </Grid>

           <Grid item xs={12} sm={6}>
             <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Color</Typography>
             <TextField name="color" value={formData.color} onChange={handleChange} fullWidth size="small" />
           </Grid>

           <Grid item xs={12} sm={6}>
             <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Gender</Typography>
             <FormControl fullWidth size="small">
               <InputLabel>Select gender</InputLabel>
               <Select
                 name="gender" 
                 value={formData.gender} 
                 onChange={handleChange}
                 disabled={genderLoading}
                 label="Select gender"
               >
                 {genderLoading ? (
                   <MenuItem value="">Loading...</MenuItem>
                 ) : (
                   genderValues.map((item) => (
                     <MenuItem key={item.value} value={item.value}>
                       {item.displayName}
                     </MenuItem>
                   ))
                 )}
               </Select>
             </FormControl>
           </Grid>

           <Grid item xs={12}>
             <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Description</Typography>
             <TextField name="petDescription" value={formData.petDescription} onChange={handleChange} multiline rows={2} fullWidth size="small" />
           </Grid>

          <Grid item xs={12} sx={{ pt: 1 }}>
            <Button
              type="submit"
              variant="contained"
              color="primary"
              fullWidth
              disabled={isLoading || !user}
              sx={{ '&:disabled': { opacity: 0.6, cursor: 'not-allowed' } }}
            >
              {isLoading ? <CircularProgress size={24} color="inherit" /> : 'Update Pet'}
            </Button>
          </Grid>
        </Grid>
      </Box>
    </Paper>
  )
}
