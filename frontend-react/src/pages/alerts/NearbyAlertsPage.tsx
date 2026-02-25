import { useState, useEffect, useCallback } from 'react'
import { Box, Typography, CircularProgress, Paper, Button, FormControl, InputLabel, Select, MenuItem, IconButton, Stack, Chip, Alert as MuiAlert } from '@mui/material'
import { FiMapPin, FiRefreshCw, FiNavigation } from 'react-icons/fi'
import { useLocation } from '../../hooks/useLocation'
import NearbyAlertsMap from '../../components/alerts/NearbyAlertsMap'
import { alertService } from '../../services/alert.service'
import { Alert } from '../../types'

// LocalStorage keys for persisting preferences
const LOCATION_STORAGE_KEY = 'pawalert_last_location'
const RADIUS_STORAGE_KEY = 'pawalert_nearby_radius'

interface StoredLocation {
  latitude: number
  longitude: number
  timestamp: number
}

// Radius options in kilometers
const RADIUS_OPTIONS = [
  { value: 5, label: '5 km' },
  { value: 10, label: '10 km' },
  { value: 25, label: '25 km' },
  { value: 50, label: '50 km' },
]

export default function NearbyAlertsPage() {
  // Location and nearby alerts state
  const { latitude, longitude, source, error: locationError, isLoading: locationLoading, detectLocation } = useLocation()
  const [radiusKm, setRadiusKm] = useState<number>(() => {
    const saved = localStorage.getItem(RADIUS_STORAGE_KEY)
    return saved ? parseInt(saved, 10) : 10
  })
  const [hasAttemptedLocation, setHasAttemptedLocation] = useState(false)
  
  // Nearby alerts state
  const [nearbyAlerts, setNearbyAlerts] = useState<Alert[]>([])
  const [nearbyAlertsLoading, setNearbyAlertsLoading] = useState(false)
  const [nearbyAlertsError, setNearbyAlertsError] = useState<string | null>(null)

  // Save location to localStorage
  const saveLocation = useCallback((lat: number, lon: number) => {
    try {
      const toStore: StoredLocation = {
        latitude: lat,
        longitude: lon,
        timestamp: Date.now()
      }
      localStorage.setItem(LOCATION_STORAGE_KEY, JSON.stringify(toStore))
    } catch (e) {
      console.error('Error saving location:', e)
    }
  }, [])

  // Save radius preference
  useEffect(() => {
    localStorage.setItem(RADIUS_STORAGE_KEY, String(radiusKm))
  }, [radiusKm])

  // Auto-detect location on mount
  useEffect(() => {
    const initLocation = async () => {
      if (hasAttemptedLocation) return
      
      // Mark as attempted to prevent multiple calls
      setHasAttemptedLocation(true)
      
      // Always call detectLocation - it will use GPS, fallback to IP, or use dev default
      await detectLocation()
    }
    
    initLocation()
  }, [detectLocation, hasAttemptedLocation])

  // Save location when we get it
  useEffect(() => {
    if (latitude && longitude && source === 'gps') {
      saveLocation(latitude, longitude)
    }
  }, [latitude, longitude, source, saveLocation])

  // Fetch nearby alerts when location or radius changes
  useEffect(() => {
    const fetchNearbyAlerts = async () => {
      if (!latitude || !longitude) return
      
      try {
        setNearbyAlertsLoading(true)
        setNearbyAlertsError(null)
        const data = await alertService.getNearbyAlerts(latitude, longitude, radiusKm)
        setNearbyAlerts(data)
      } catch (err) {
        console.error('Error fetching nearby alerts:', err)
        setNearbyAlertsError('Unable to load nearby alerts')
      } finally {
        setNearbyAlertsLoading(false)
      }
    }

    fetchNearbyAlerts()
  }, [latitude, longitude, radiusKm])

  // Loading state - waiting for location
  if (locationLoading) {
    return (
      <Box sx={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', minHeight: '400px' }}>
        <CircularProgress color="primary" />
        <Typography variant="body1" color="text.secondary" sx={{ mt: 2 }}>
          Detecting your location...
        </Typography>
      </Box>
    )
  }

  // Error state - location error
  if (locationError && !latitude) {
    return (
      <Box sx={{ p: 3 }}>
        <Paper sx={{ p: 3 }}>
          <MuiAlert 
            severity="warning" 
            icon={<FiMapPin />}
            action={
              <Button 
                color="inherit" 
                size="small" 
                startIcon={<FiRefreshCw />}
                onClick={() => detectLocation()}
              >
                Retry
              </Button>
            }
          >
            <Typography variant="body2">{locationError}</Typography>
          </MuiAlert>
        </Paper>
      </Box>
    )
  }

  return (
    <Box sx={{ height: 'calc(100vh - 120px)', display: 'flex', flexDirection: 'column' }}>
      {/* Header with controls */}
      <Paper sx={{ p: 2, mb: 2 }}>
        <Stack direction={{ xs: 'column', sm: 'row' }} justifyContent="space-between" alignItems="center" spacing={2}>
          <Stack direction="row" alignItems="center" spacing={1}>
            <FiMapPin size={24} color="#ed8936" />
            <Typography variant="h6" color="text.primary">
              Nearby Alerts
            </Typography>
            {latitude && longitude && (
              <Chip 
                label={`${nearbyAlerts.length} alert${nearbyAlerts.length !== 1 ? 's' : ''}`}
                size="small"
                color="primary"
                variant="outlined"
              />
            )}
          </Stack>
          
          <Stack direction="row" alignItems="center" spacing={2}>
            {/* Location info */}
            {latitude && longitude && (
              <Stack direction="row" alignItems="center" spacing={0.5}>
                <FiNavigation size={14} />
                <Typography variant="caption" color="text.secondary">
                  {latitude.toFixed(4)}, {longitude.toFixed(4)}
                </Typography>
                {source && (
                  <Chip 
                    label={source === 'gps' ? 'GPS' : source === 'ip' ? 'IP' : 'Default'} 
                    size="small" 
                    sx={{ ml: 0.5 }}
                    color={source === 'gps' ? 'success' : source === 'ip' ? 'warning' : 'default'}
                  />
                )}
              </Stack>
            )}
            
            {/* Radius Selector */}
            <FormControl size="small" sx={{ minWidth: 100 }}>
              <InputLabel id="radius-select-label">Radius</InputLabel>
              <Select
                labelId="radius-select-label"
                value={radiusKm}
                label="Radius"
                onChange={(e) => setRadiusKm(Number(e.target.value))}
              >
                {RADIUS_OPTIONS.map((option) => (
                  <MenuItem key={option.value} value={option.value}>
                    {option.label}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
            
            {/* Refresh location button */}
            <IconButton 
              onClick={() => detectLocation()} 
              title="Refresh location"
              disabled={locationLoading}
            >
              <FiRefreshCw size={18} />
            </IconButton>
          </Stack>
        </Stack>
      </Paper>

      {/* Map Container */}
      <Paper sx={{ flex: 1, overflow: 'hidden', position: 'relative' }}>
        {latitude && longitude ? (
          <NearbyAlertsMap 
            latitude={latitude} 
            longitude={longitude} 
            alerts={nearbyAlerts}
            isLoading={nearbyAlertsLoading}
            error={nearbyAlertsError}
            radiusKm={radiusKm}
            fullHeight={true}
          />
        ) : (
          <Box sx={{ display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
            <FiMapPin size={48} color="#9ca3af" />
            <Typography variant="body1" color="text.secondary" sx={{ mt: 2 }}>
              Enable location access to see nearby alerts
            </Typography>
            <Button
              variant="contained"
              startIcon={<FiNavigation />}
              onClick={() => detectLocation()}
              sx={{ mt: 2 }}
            >
              Enable Location
            </Button>
          </Box>
        )}
      </Paper>
    </Box>
  )
}
