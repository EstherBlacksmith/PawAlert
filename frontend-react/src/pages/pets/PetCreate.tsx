import { useState, useRef } from 'react'
import { Box, Typography, Button, TextField, CircularProgress, Alert as MuiAlert, Grid, FormControl, InputLabel, Select, MenuItem, Paper, Divider } from '@mui/material'
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
  const { values: speciesValues, loading: loadingSpecies } = useEnumValues('Species')
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

  const isFormValid = 
    formData.officialPetName.trim().length >= 3 &&
    formData.species !== '' &&
    formData.size !== '' &&
    formData.gender !== ''

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
    <Paper sx={{ width: '100%', mx: 'auto', bgcolor: 'rgba(255, 255, 255, 0.85)', p: 3, borderRadius: 2, boxShadow: 3 }}>
      <Button variant="text" sx={{ mb: 2 }} onClick={() => navigate('/pets')} startIcon={<FaArrowLeft />}>
        Back to My Pets
      </Button>

      <Typography variant="h5" mb={3} color="text.primary">
        Add New Pet
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
                    Click to upload a photo
                  </Typography>
                  <Typography variant="body2" color="text.secondary" sx={{ mt: 0.5 }}>
                    (AI will detect species, breed, color)
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

           {/* Identification Section */}
           <Grid item xs={12}>
             <Divider sx={{ my: 1 }} />
             <Typography variant="subtitle2" color="primary" sx={{ mb: 1, mt: 1 }}>
               IDENTIFICATION
             </Typography>
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

           {/* Physical Characteristics Section */}
           <Grid item xs={12}>
             <Divider sx={{ my: 1 }} />
             <Typography variant="subtitle2" color="primary" sx={{ mb: 1, mt: 1 }}>
               PHYSICAL CHARACTERISTICS
             </Typography>
           </Grid>

           <Grid item xs={12} sm={6}>
             <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Species *</Typography>
             <FormControl fullWidth size="small">
               <InputLabel>Select species</InputLabel>
               <Select
                 name="species" 
                 value={formData.species} 
                 onChange={handleChange}
                 disabled={loadingSpecies}
                 label="Select species"
               >
                 <MenuItem value="">Select species</MenuItem>
                 {speciesValues.map((species) => (
                   <MenuItem key={species.value} value={species.value}>
                     {species.displayName}
                   </MenuItem>
                 ))}
               </Select>
             </FormControl>
           </Grid>

           <Grid item xs={12} sm={6}>
             <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Breed</Typography>
             <TextField name="breed" value={formData.breed} onChange={handleChange} placeholder={!formData.breed ? "Detected from image" : ""} fullWidth size="small" />
           </Grid>

            <Grid item xs={12} sm={6}>
              <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Size *</Typography>
              <FormControl fullWidth size="small">
                <InputLabel>Select size</InputLabel>
                <Select name="size" value={formData.size} onChange={handleChange} disabled={sizeLoading} label="Select size">
                  <MenuItem value="">Select size</MenuItem>
                  {sizeValues.map((size) => (
                    <MenuItem key={size.value} value={size.value}>
                      {size.displayName}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>

            <Grid item xs={12} sm={6}>
              <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Color</Typography>
              <TextField name="color" value={formData.color} onChange={handleChange} placeholder={!formData.color ? "Detected from image" : ""} fullWidth size="small" />
            </Grid>

            <Grid item xs={12} sm={6}>
              <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Gender *</Typography>
              <FormControl fullWidth size="small">
                <InputLabel>Select gender</InputLabel>
                <Select name="gender" value={formData.gender} onChange={handleChange} disabled={genderLoading} label="Select gender">
                  <MenuItem value="">Select gender</MenuItem>
                  {genderValues.map((gender) => (
                    <MenuItem key={gender.value} value={gender.value}>
                      {gender.displayName}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>

           {/* Additional Info Section */}
           <Grid item xs={12}>
             <Divider sx={{ my: 1 }} />
             <Typography variant="subtitle2" color="primary" sx={{ mb: 1, mt: 1 }}>
               ADDITIONAL INFO
             </Typography>
           </Grid>

           <Grid item xs={12}>
             <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Description</Typography>
             <TextField 
               name="petDescription" 
               value={formData.petDescription} 
               onChange={handleChange}
               placeholder={!formData.petDescription ? "Auto-generated from image" : ""}
               multiline
               rows={2}
               fullWidth
               size="small"
             />
           </Grid>

           <Grid item xs={12} sx={{ pt: 1 }}>
             <Button type="submit" variant="contained" color="primary" fullWidth disabled={isLoading || !isFormValid}>
               {isLoading ? <CircularProgress size={24} color="inherit" /> : 'Add Pet'}
             </Button>
           </Grid>
        </Grid>
      </Box>
    </Paper>
  )
}
