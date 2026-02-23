import { useState, useEffect } from 'react'
import { Box, Typography, Button, Stack, TextField, CircularProgress, Alert as MuiAlert, Grid, FormControl, InputLabel, Select, MenuItem, Chip, Paper } from '@mui/material'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { FaArrowLeft } from 'react-icons/fa'
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
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '300px' }}>
        <CircularProgress color="primary" />
      </Box>
    )
  }

  return (
    <Paper sx={{ width: '100%', mx: 'auto', bgcolor: 'rgba(255, 255, 255, 0.85)', p: 3, borderRadius: 2, boxShadow: 3 }}>
      <Button variant="text" sx={{ mb: 2 }} onClick={() => navigate('/alerts')} startIcon={<FaArrowLeft />}>
        Back to Alerts
      </Button>

      <Typography variant="h5" mb={3} color="text.primary" fontWeight="bold">
        Create Alert
      </Typography>

      {error && (
        <MuiAlert severity="error" sx={{ mb: 3 }}>
          <Typography variant="subtitle2" component="div">{error.error || 'Error'}</Typography>
          <Typography variant="body2">{error.message}</Typography>
        </MuiAlert>
      )}

      {preselectedPet && (
        <MuiAlert severity="info" sx={{ mb: 3 }}>
          <Typography variant="subtitle2" component="div">
            Creating alert for: {preselectedPet.officialPetName}
          </Typography>
          <Typography variant="body2">
            {preselectedPet.species} {preselectedPet.breed && `- ${preselectedPet.breed}`}
          </Typography>
        </MuiAlert>
      )}

       <Box component="form" onSubmit={handleSubmit}>
         <Grid container spacing={2}>
           <Grid item xs={12}>
             <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Title *</Typography>
             <TextField 
               name="title" 
               value={formData.title} 
               onChange={handleChange} 
               required 
               placeholder="Alert title"
               fullWidth
               size="small"
               bgcolor="white"
             />
           </Grid>

           <Grid item xs={12}>
             <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Pet *</Typography>
             <FormControl fullWidth size="small">
               <InputLabel>Select a pet</InputLabel>
               <Select
                 name="petId" 
                 value={formData.petId} 
                 onChange={handleChange}
                 label="Select a pet"
               >
                 <MenuItem value="">Select a pet</MenuItem>
                 {pets.map((pet) => (
                   <MenuItem key={pet.petId} value={pet.petId}>
                     {pet.officialPetName}
                   </MenuItem>
                 ))}
               </Select>
             </FormControl>
           </Grid>

           <Grid item xs={12}>
             <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Description *</Typography>
             <TextField 
               name="description" 
               value={formData.description} 
               onChange={handleChange} 
               required 
               placeholder="Describe the situation" 
               multiline
               rows={2}
               fullWidth
               size="small"
             />
           </Grid>

          {/* Location Detection Section */}
          <Grid item xs={12}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
              <Typography fontWeight="medium" variant="body2" color="text.primary">Location</Typography>
              {locationSource && (
                <Chip 
                  label={locationSource === 'gps' ? 'GPS Location' : 'IP Location'}
                  size="small"
                  color={locationSource === 'gps' ? 'success' : 'warning'}
                />
              )}
            </Box>

            {locationError && (
              <MuiAlert severity="warning" sx={{ mb: 2 }}>
                {locationError}
              </MuiAlert>
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
              height="200px"
            />

           {/* Manual Coordinate Input (fallback) */}
             <Grid container spacing={2} sx={{ mt: 1 }}>
               <Grid item xs={6}>
                 <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Latitude *</Typography>
                 <TextField 
                   name="latitude" 
                   type="number" 
                   value={formData.latitude} 
                   onChange={handleChange} 
                   required 
                   placeholder="40.416775"
                   fullWidth
                   size="small"
                   inputProps={{ step: 'any' }}
                 />
               </Grid>

               <Grid item xs={6}>
                 <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Longitude *</Typography>
                 <TextField 
                   name="longitude" 
                   type="number" 
                   value={formData.longitude} 
                   onChange={handleChange} 
                   required 
                   placeholder="-3.703790"
                   fullWidth
                   size="small"
                   inputProps={{ step: 'any' }}
                 />
               </Grid>
             </Grid>
          </Grid>

          <Grid item xs={12} sx={{ pt: 1 }}>
            <Button type="submit" variant="contained" color="primary" fullWidth disabled={isLoading}>
              {isLoading ? <CircularProgress size={24} color="inherit" /> : 'Create Alert'}
            </Button>
          </Grid>
        </Grid>
      </Box>
    </Paper>
  )
}
