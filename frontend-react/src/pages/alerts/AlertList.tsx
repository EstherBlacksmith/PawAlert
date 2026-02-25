import { useEffect, useState, useCallback } from 'react'
import { Box, Typography, Button, Grid, Card, CardContent, CircularProgress, Chip, IconButton, Stack, TextField, Paper, Collapse, FormControl, InputLabel, Select, MenuItem, FormControlLabel, Checkbox, Pagination } from '@mui/material'
import { Link, useSearchParams, useNavigate } from 'react-router-dom'
import { FaPlus, FaSearch as FaSearchIcon, FaCog, FaTimes, FaMapMarkerAlt, FaSearch, FaArrowLeft, FaChevronLeft, FaChevronRight, FaPaw } from 'react-icons/fa'
import { alertService } from '../../services/alert.service'
import { Alert, AlertStatus, AlertSearchFilters } from '../../types'
import { SubscribeButton } from '../../components/alerts/SubscribeButton'
import { useMetadata } from '../../hooks/useMetadata'
import { useLocation } from '../../hooks/useLocation'
import { useAuth } from '../../context/AuthContext'

const ITEMS_PER_PAGE = 10

const statusColors: Record<string, string> = {
  OPENED: '#b34045',
  CLOSED: '#4091d7',
  SEEN: '#fecf6d',
  SAFE: '#2d884d',
}

const statusOptions: { value: string; label: string }[] = [
  { value: '', label: 'All Statuses' },
  { value: 'OPENED', label: 'Opened' },
  { value: 'SEEN,SAFE', label: 'In Progress' },
  { value: 'SEEN', label: 'Seen' },
  { value: 'SAFE', label: 'Safe' },
  { value: 'CLOSED', label: 'Closed' },
]

export default function AlertList() {
    const navigate = useNavigate()
    const [searchParams] = useSearchParams()
    const [alerts, setAlerts] = useState<Alert[]>([])
    const [isLoading, setIsLoading] = useState(true)
    const [isFilterOpen, setIsFilterOpen] = useState(false)
    const [currentPage, setCurrentPage] = useState(1)
    
    // Filter state - check for myAlerts query param to enable user filter by default
    const [status, setStatus] = useState<string>(searchParams.get('status') || '')
    const [species, setSpecies] = useState<string>('')
    const [petName, setPetName] = useState('')
    const [breed, setBreed] = useState('')
    const [createdFrom, setCreatedFrom] = useState('')
    const [createdTo, setCreatedTo] = useState('')
    const [radiusKm, setRadiusKm] = useState<string>('10')
    const [showMyAlertsOnly, setShowMyAlertsOnly] = useState(searchParams.get('myAlerts') === 'true')
    
    // Location state
    const [userLatitude, setUserLatitude] = useState<number | null>(null)
    const [userLongitude, setUserLongitude] = useState<number | null>(null)
    const [locationError, setLocationError] = useState<string | null>(null)
   
   // Hooks
   const { metadata: speciesOptions } = useMetadata('Species')
   const { detectLocation, isLoading: isLocationLoading } = useLocation()
   const { user } = useAuth()

  // Count active filters
  const activeFilterCount = [
    status,
    species,
    petName,
    breed,
    createdFrom,
    createdTo,
    userLatitude !== null && userLongitude !== null,
    showMyAlertsOnly
  ].filter(Boolean).length

   const fetchAlerts = useCallback(async () => {
     setIsLoading(true)
     try {
       // Parse comma-separated statuses
       const statuses = status.split(',').filter(s => s.trim())
       
       const filters: AlertSearchFilters = {}
       
       // For single status, use the API filter directly
       // For multiple statuses, we'll filter client-side
       if (statuses.length === 1) {
         filters.status = statuses[0] as AlertStatus
       }
       if (petName) filters.petName = petName
       if (species) filters.species = species
       if (breed) filters.breed = breed
       if (createdFrom) filters.createdFrom = createdFrom
       if (createdTo) filters.createdTo = createdTo
       
       // Add user filter if "My Alerts" is enabled
       if (showMyAlertsOnly && user?.userId) {
         filters.userId = user.userId
       }
       
       // Add location filters if available
       if (userLatitude !== null && userLongitude !== null && radiusKm) {
         filters.latitude = userLatitude
         filters.longitude = userLongitude
         filters.radiusKm = parseFloat(radiusKm)
       }
       
       let data = await alertService.searchAlertsWithFilters(filters)
       
       // Apply client-side filtering for multiple statuses
       if (statuses.length > 1) {
         data = data.filter(alert => statuses.includes(alert.status))
       }
       
       setAlerts(data)
     } catch (error) {
       console.error('Error fetching alerts:', error)
     } finally {
       setIsLoading(false)
     }
   }, [status, species, petName, breed, createdFrom, createdTo, userLatitude, userLongitude, radiusKm, showMyAlertsOnly, user?.userId])

  useEffect(() => {
    fetchAlerts()
  }, [fetchAlerts]) // Initial load and when filters change

  const handleApplyFilters = () => {
    fetchAlerts()
  }

  const handleClearFilters = () => {
    setStatus('')
    setSpecies('')
    setPetName('')
    setBreed('')
    setCreatedFrom('')
    setCreatedTo('')
    setRadiusKm('10')
    setShowMyAlertsOnly(false)
    setUserLatitude(null)
    setUserLongitude(null)
    setLocationError(null)
  }

  const handleGetLocation = async () => {
    setLocationError(null)
    const result = await detectLocation()
    if (result.error) {
      setLocationError(result.error)
    } else if (result.latitude && result.longitude) {
      setUserLatitude(result.latitude)
      setUserLongitude(result.longitude)
    }
  }

  const handleClearLocation = () => {
    setUserLatitude(null)
    setUserLongitude(null)
    setLocationError(null)
  }

   const handleKeyPress = (e: React.KeyboardEvent) => {
     if (e.key === 'Enter') {
       handleApplyFilters()
     }
   }

   // Pagination calculations
   const totalPages = Math.ceil(alerts.length / ITEMS_PER_PAGE)
   const startIndex = (currentPage - 1) * ITEMS_PER_PAGE
   const endIndex = startIndex + ITEMS_PER_PAGE
   const paginatedAlerts = alerts.slice(startIndex, endIndex)

   const handlePageChange = (page: number) => {
     setCurrentPage(page)
     // Scroll to top of alerts section
     window.scrollTo({ top: 0, behavior: 'smooth' })
   }

  if (isLoading && alerts.length === 0) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '300px' }}>
        <CircularProgress color="primary" />
      </Box>
    )
  }

   return (
     <Paper sx={{ bgcolor: 'rgba(255, 255, 255, 0.85)', p: 3, borderRadius: 2, boxShadow: 3 }}>
        <Button 
          variant="text"
          sx={{ mb: 2 }}
          onClick={() => navigate('/')}
          size="small"
          startIcon={<FaArrowLeft />}
        >
          Back
        </Button>

       <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h5" color="text.primary">
            Alerts
          </Typography>
          <Typography color="text.secondary" sx={{ mt: 0.5 }}>
            View and manage pet alerts
          </Typography>
        </Box>
        <Stack direction="row" spacing={1.5}>
          <Button
            variant="outlined"
            onClick={() => setIsFilterOpen(!isFilterOpen)}
            color={activeFilterCount > 0 ? 'primary' : 'inherit'}
            sx={activeFilterCount > 0 ? { bgcolor: 'primary.50', borderColor: 'primary.main' } : {}}
            startIcon={<FaCog />}
          >
            Filters
            {activeFilterCount > 0 && (
              <Chip label={activeFilterCount} size="small" color="secondary" sx={{ ml: 1 }} />
            )}
          </Button>
          <Button component={Link} to="/alerts/create" variant="contained" color="primary" startIcon={<FaPlus />}>
            Create Alert
          </Button>
        </Stack>
      </Box>

      {/* Filter Panel */}
      <Collapse in={isFilterOpen}>
        <Card sx={{ mb: 3, bgcolor: 'grey.50' }}>
            <CardContent sx={{ p: 2 }}>
              <Stack spacing={2}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <Typography fontWeight="medium" color="text.primary">
                    Filter Alerts
                  </Typography>
                  <Button
                    size="small"
                    variant="text"
                    onClick={handleClearFilters}
                    disabled={!status && !species && !petName && !breed && !createdFrom && !createdTo && userLatitude === null && !showMyAlertsOnly}
                    startIcon={<FaTimes />}
                  >
                    Clear All
                  </Button>
                </Box>

                {/* My Alerts Filter */}
                  <Box sx={{ mb: 2 }}>
                    <FormControlLabel
                      control={
                        <Checkbox
                          checked={showMyAlertsOnly}
                          onChange={(e) => setShowMyAlertsOnly(e.target.checked)}
                        />
                      }
                      label={<Typography variant="body2" color="text.primary">Show only my alerts</Typography>}
                    />
                  </Box>

                  <Grid container spacing={2}>
                  {/* Status Filter */}
                  <Grid item xs={12} sm={6} md={3}>
                    <FormControl fullWidth size="small">
                      <InputLabel>Status</InputLabel>
                      <Select
                        value={status}
                        label="Status"
                        onChange={(e) => setStatus(e.target.value)}
                      >
                        {statusOptions.map((option) => (
                          <MenuItem key={option.value} value={option.value}>
                            {option.label}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  </Grid>

                  {/* Species Filter */}
                  <Grid item xs={12} sm={6} md={3}>
                    <FormControl fullWidth size="small">
                      <InputLabel>Species</InputLabel>
                      <Select
                        value={species}
                        label="Species"
                        onChange={(e) => setSpecies(e.target.value)}
                      >
                        <MenuItem value="">All Species</MenuItem>
                        {speciesOptions?.map((option) => (
                          <MenuItem key={option.value} value={option.value}>
                            {option.displayName}
                          </MenuItem>
                        ))}
                      </Select>
                    </FormControl>
                  </Grid>

                  {/* Pet Name Filter */}
                  <Grid item xs={12} sm={6} md={3}>
                    <TextField
                      placeholder="Search by pet name..."
                      value={petName}
                      onChange={(e) => setPetName(e.target.value)}
                      onKeyPress={handleKeyPress}
                      size="small"
                      fullWidth
                      label="Pet Name"
                    />
                  </Grid>

                  {/* Breed Filter */}
                  <Grid item xs={12} sm={6} md={3}>
                    <TextField
                      placeholder="Search by breed..."
                      value={breed}
                      onChange={(e) => setBreed(e.target.value)}
                      onKeyPress={handleKeyPress}
                      size="small"
                      fullWidth
                      label="Breed"
                    />
                  </Grid>

                  {/* Date From Filter */}
                  <Grid item xs={12} sm={6} md={3}>
                    <TextField
                      type="date"
                      value={createdFrom}
                      onChange={(e) => setCreatedFrom(e.target.value)}
                      size="small"
                      fullWidth
                      label="Date From"
                      InputLabelProps={{ shrink: true }}
                    />
                  </Grid>

                  {/* Date To Filter */}
                  <Grid item xs={12} sm={6} md={3}>
                    <TextField
                      type="date"
                      value={createdTo}
                      onChange={(e) => setCreatedTo(e.target.value)}
                      size="small"
                      fullWidth
                      label="Date To"
                      InputLabelProps={{ shrink: true }}
                    />
                  </Grid>

                  {/* Distance Filter */}
                  <Grid item xs={12} sm={6} md={3}>
                    <Stack direction="row" spacing={1}>
                      <TextField
                        type="number"
                        placeholder="Radius"
                        value={radiusKm}
                        onChange={(e) => setRadiusKm(e.target.value)}
                        onKeyPress={handleKeyPress}
                        size="small"
                        label="Distance (km)"
                        inputProps={{ min: 1, max: 1000 }}
                        sx={{ flex: 1 }}
                      />
                      {userLatitude !== null && userLongitude !== null ? (
                        <IconButton
                          size="small"
                          color="error"
                          onClick={handleClearLocation}
                        >
                          <FaTimes />
                        </IconButton>
                      ) : (
                        <IconButton
                          size="small"
                          color="primary"
                          onClick={handleGetLocation}
                          disabled={isLocationLoading}
                        >
                          <FaMapMarkerAlt />
                        </IconButton>
                      )}
                    </Stack>
                  </Grid>
                </Grid>

                {/* Location Status */}
                {locationError && (
                  <Typography variant="body2" color="error">
                    {locationError}
                  </Typography>
                )}
                {userLatitude !== null && userLongitude !== null && (
                  <Typography variant="body2" color="success.main">
                    <FaMapMarkerAlt style={{ display: 'inline', marginRight: '4px' }} />
                    Location set: {userLatitude.toFixed(4)}, {userLongitude.toFixed(4)}
                  </Typography>
                )}

                {/* Apply Button */}
                <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
                  <Button
                    variant="contained"
                    color="primary"
                    onClick={handleApplyFilters}
                    disabled={isLoading}
                    size="small"
                    startIcon={<FaSearch />}
                  >
                    Apply Filters
                  </Button>
                </Box>
              </Stack>
            </CardContent>
          </Card>
      </Collapse>

       {/* Results */}
       {alerts.length === 0 ? (
         <Card sx={{ p: 4, textAlign: 'center' }}>
           <Typography color="text.secondary">No alerts found matching your criteria.</Typography>
           <Button component={Link} to="/alerts/create" sx={{ mt: 2 }} variant="outlined" color="primary">
             Create a new alert
           </Button>
         </Card>
       ) : (
         <>
           <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
             Showing {startIndex + 1} to {Math.min(endIndex, alerts.length)} of {alerts.length} alert{alerts.length !== 1 ? 's' : ''}
           </Typography>
           <Grid container spacing={3} sx={{ mb: 3 }}>
              {paginatedAlerts.map((alert) => (
                <Grid item xs={12} sm={6} lg={4} key={alert.id}>
                  <Card sx={{ display: 'flex', flexDirection: 'column', height: '100%' }}>
                    <CardContent sx={{ display: 'flex', flexDirection: 'column', flex: 1 }}>
                      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 1 }}>
                        <Typography variant="h6">{alert.title}</Typography>
                        <Chip 
                          label={alert.status}
                          size="small"
                          sx={{ 
                            bgcolor: statusColors[alert.status],
                            color: 'white',
                            fontWeight: 'bold'
                          }}
                        />
                      </Box>
                      <Typography variant="body2" color="text.secondary" sx={{ mb: 1, overflow: 'hidden', textOverflow: 'ellipsis', display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical' }}>
                        {alert.description}
                      </Typography>
                       <Typography variant="caption" color="text.disabled" sx={{ mb: 2, flex: 1 }}>
                         {alert.latitude != null && alert.longitude != null 
                           ? `Location: ${alert.latitude.toFixed(4)}, ${alert.longitude.toFixed(4)}`
                           : 'Location not available'}
                       </Typography>
                      <Box sx={{ display: 'flex', mt: 'auto', gap: 1, justifyContent: 'space-between', alignItems: 'center' }}>
                         <IconButton component={Link} to={`/alerts/${alert.id}`} size="small" color="primary">
                           <FaSearchIcon />
                         </IconButton>
                         <IconButton
                           size="small"
                           color="success"
                           onClick={() => navigate(`/pets/${alert.petId}`)}
                         >
                           <FaPaw />
                         </IconButton>
                         <SubscribeButton
                           alertId={alert.id}
                           alertStatus={alert.status}
                           size="small"
                         />
                       </Box>
                    </CardContent>
                  </Card>
                </Grid>
              ))}
           </Grid>

           {/* Pagination Controls */}
           {totalPages > 1 && (
             <Box sx={{ display: 'flex', justifyContent: 'center', mt: 3 }}>
               <Pagination
                 count={totalPages}
                 page={currentPage}
                 onChange={(_, page) => handlePageChange(page)}
                 color="primary"
                 showFirstButton
                 showLastButton
               />
             </Box>
           )}
         </>
       )}
     </Paper>
   )
 }
