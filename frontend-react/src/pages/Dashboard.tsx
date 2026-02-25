import { useEffect, useState, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import { Box, Grid, Typography, Card, CardContent, Stack, CircularProgress, Paper, Button, Chip, Alert as MuiAlert, FormControl, InputLabel, Select, MenuItem, IconButton, Collapse, Divider } from '@mui/material'
import { GiPawPrint, GiHealthPotion, GiCheck, GiSword, GiBell, GiCat } from '../components/icons'
import { FiRadio, FiMapPin, FiRefreshCw, FiMap, FiList, FiNavigation } from 'react-icons/fi'
import { useAuth } from '../context/AuthContext'
import { petService } from '../services/pet.service'
import { alertService } from '../services/alert.service'
import { Alert } from '../types'
import { useLocation } from '../hooks/useLocation'
import NearbyAlerts from '../components/alerts/NearbyAlerts'
import NearbyAlertsMap from '../components/alerts/NearbyAlertsMap'
import userBgImage from '../assets/user-bg-image.jpeg'

// LocalStorage key for persisting location
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

interface StatCardProps {
  icon: React.ElementType
  label: string
  value: number
  color: string
  gradientColors?: string
  onClick?: () => void
}

function StatCard({ icon: Icon, label, value, color, onClick, gradientColors }: StatCardProps) {
  return (
    <Card 
      elevation={3}
      sx={{ 
        p: 3, 
        cursor: onClick ? "pointer" : "default",
        transition: 'all 0.3s',
        '&:hover': onClick ? { 
          boxShadow: 6, 
          transform: 'translateY(-4px)' 
        } : undefined,
        overflow: 'hidden',
        position: 'relative'
      }}
      onClick={onClick}
    >
      {/* Gradient background overlay */}
      {gradientColors && (
        <Box
          sx={{
            position: 'absolute',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            background: gradientColors,
            opacity: 0.1,
            pointerEvents: 'none'
          }}
        />
      )}
      <CardContent sx={{ position: 'relative' }}>
        <Stack direction="row" alignItems="center" gap={2}>
          <Box
            sx={{
              p: 1.5,
              borderRadius: '50%',
              background: gradientColors || `${color}.light`
            }}
          >
            <Icon size={24} />
          </Box>
          <Box>
            <Typography variant="h3" fontWeight="bold" color="text.primary">
              {value}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {label}
            </Typography>
          </Box>
        </Stack>
      </CardContent>
    </Card>
  )
}

export default function Dashboard() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [stats, setStats] = useState({
    myPets: 0,
    openAlerts: 0,
    inProgressAlerts: 0,
    resolvedAlerts: 0,
    mySubscriptions: 0,
  })
  const [isLoading, setIsLoading] = useState(true)
  
  // Location and nearby alerts state
  const { latitude, longitude, source, error: locationError, isLoading: locationLoading, detectLocation } = useLocation()
  const [radiusKm, setRadiusKm] = useState<number>(() => {
    const saved = localStorage.getItem(RADIUS_STORAGE_KEY)
    return saved ? parseInt(saved, 10) : 10
  })
  const [showMap, setShowMap] = useState(false)
  const [hasAttemptedLocation, setHasAttemptedLocation] = useState(false)
  
  // Shared nearby alerts state - single source of truth for both map and list
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
      // The saved location in localStorage is just for reference, not for skipping detection
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

  // Fetch nearby alerts when location or radius changes - SINGLE API CALL
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

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const [pets, alerts, subscriptions] = await Promise.all([
          petService.getPets(),
          alertService.getAlerts(),
          alertService.getMySubscriptions(),
        ])

        const openAlerts = alerts.filter((a: Alert) => a.status === 'OPENED').length
        const inProgressAlerts = alerts.filter((a: Alert) => a.status === 'SEEN' || a.status === 'SAFE').length
        const resolvedAlerts = alerts.filter((a: Alert) => a.status === 'CLOSED').length

        setStats({
          myPets: pets.length,
          openAlerts,
          inProgressAlerts,
          resolvedAlerts,
          mySubscriptions: subscriptions.length,
        })
      } catch (error) {
        console.error('Error fetching stats:', error)
      } finally {
        setIsLoading(false)
      }
    }

    fetchStats()
  }, [])

  if (isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '300px' }}>
        <CircularProgress color="primary" />
      </Box>
    )
  }

  return (
    <Box
      sx={{
        minHeight: '100vh',
        backgroundImage: `url(${userBgImage})`,
        backgroundAttachment: 'fixed',
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        p: 3,
        '&::before': {
          content: '""',
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0, 0, 0, 0.3)',
          pointerEvents: 'none',
          zIndex: 0
        }
      }}
    >
      <Paper sx={{ bgcolor: 'rgba(255, 255, 255, 0.95)', p: 3, borderRadius: 2, boxShadow: 3, position: 'relative', zIndex: 1 }}>
        <Stack spacing={3}>
      {/* Welcome Section */}
      <Box>
        <Typography variant="h5" color="text.primary">
          Welcome back, {user?.username}!
        </Typography>
        <Typography color="text.secondary" sx={{ mt: 0.5 }}>
          Here's your personal overview
        </Typography>
      </Box>

       {/* Stats Grid - 5 cards showing user's personal data */}
       <Grid container spacing={3}>
         <Grid item xs={12} sm={6} md={2.4}>
           <StatCard 
             icon={GiPawPrint} 
             label="My Pets" 
             value={stats.myPets} 
             color="blue" 
             gradientColors="linear-gradient(to bottom right, #4091d7, #2e5f9e)"
             onClick={() => navigate('/pets')} 
           />
         </Grid>
         <Grid item xs={12} sm={6} md={2.4}>
           <StatCard
             icon={GiHealthPotion}
             label="Open Alerts"
             value={stats.openAlerts}
             color="red"
             gradientColors="linear-gradient(to bottom right, #b34045, #8b2e32)"
             onClick={() => navigate('/alerts?status=OPENED')}
           />
         </Grid>
         <Grid item xs={12} sm={6} md={2.4}>
           <StatCard
             icon={GiSword}
             label="In Progress"
             value={stats.inProgressAlerts}
             color="orange"
             gradientColors="linear-gradient(to bottom right, #ff9800, #f57c00)"
             onClick={() => navigate('/alerts?status=SEEN,SAFE')}
           />
         </Grid>
         <Grid item xs={12} sm={6} md={2.4}>
           <StatCard
             icon={GiCheck}
             label="Resolved"
             value={stats.resolvedAlerts}
             color="green"
             gradientColors="linear-gradient(to bottom right, #2d884d, #1f5a34)"
             onClick={() => navigate('/alerts?status=CLOSED')}
           />
         </Grid>
         <Grid item xs={12} sm={6} md={2.4}>
           <StatCard
             icon={GiBell}
             label="My Subscriptions"
             value={stats.mySubscriptions}
             color="purple"
             gradientColors="linear-gradient(to bottom right, #9c27b0, #7b1fa2)"
             onClick={() => navigate('/subscriptions')}
           />
         </Grid>
       </Grid>

      {/* Quick Actions */}
      <Box>
        <Typography variant="h6" mb={2} color="text.primary">
          Quick Actions
        </Typography>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
           <Button
            variant="contained"
            size="large"
            startIcon={<GiCat />}
            onClick={() => navigate('/pets/create')}
            sx={{
              bgcolor: '#2d884d',
              '&:hover': { bgcolor: '#1f5a34' },
              px: 4,
              py: 1.5,
            }}
          >
            Add New Pet
          </Button>
          <Button
            variant="contained"
            size="large"
            startIcon={<GiHealthPotion />}
            onClick={() => navigate('/alerts/create')}
            sx={{
              bgcolor: '#b34045',
              '&:hover': { bgcolor: '#8b2e32' },
              px: 4,
              py: 1.5,
            }}
          >
            Create Alert
          </Button>
        </Stack>
      </Box>

      {/* Nearby Alerts Section */}
      <Box>
        <Stack direction="row" justifyContent="space-between" alignItems="center" mb={2}>
          <Stack direction="row" alignItems="center" spacing={1}>
            <FiRadio size={24} color="#ed8936" />
            <Typography variant="h6" color="text.primary">
              Alerts in Your Area
            </Typography>
          </Stack>
          <Stack direction="row" alignItems="center" spacing={1}>
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
            {/* Map/List Toggle */}
            <IconButton 
              onClick={() => setShowMap(!showMap)} 
              title={showMap ? 'Show list' : 'Show map'}
              sx={{ 
                bgcolor: showMap ? 'primary.main' : 'grey.100',
                color: showMap ? 'white' : 'text.secondary',
                '&:hover': { bgcolor: showMap ? 'primary.dark' : 'grey.200' }
              }}
            >
              {showMap ? <FiList /> : <FiMap />}
            </IconButton>
          </Stack>
        </Stack>

        {/* Location Status and Content */}
        {locationLoading ? (
          <Card elevation={2} sx={{ p: 3 }}>
            <Box textAlign="center">
              <CircularProgress size={24} />
              <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                Detecting your location...
              </Typography>
            </Box>
          </Card>
        ) : locationError ? (
          <Card elevation={2} sx={{ p: 3 }}>
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
          </Card>
        ) : latitude && longitude ? (
          <Card elevation={2} sx={{ p: 2, overflow: 'visible' }}>
            {/* Location source indicator */}
            <Stack direction="row" alignItems="center" spacing={1} mb={2}>
              <FiNavigation size={14} />
              <Typography variant="caption" color="text.secondary">
                Location: {latitude.toFixed(4)}, {longitude.toFixed(4)}
                {source && (
                  <Chip 
                    label={source === 'gps' ? 'GPS' : source === 'ip' ? 'IP' : 'Default'} 
                    size="small" 
                    sx={{ ml: 1 }}
                    color={source === 'gps' ? 'success' : source === 'ip' ? 'warning' : 'default'}
                  />
                )}
              </Typography>
              <IconButton 
                size="small" 
                onClick={() => detectLocation()}
                title="Refresh location"
                sx={{ ml: 'auto' }}
              >
                <FiRefreshCw size={14} />
              </IconButton>
            </Stack>
            
            <Divider sx={{ mb: 2 }} />
            
            {/* Show Map or List based on toggle - BOTH USE SAME DATA */}
            <Collapse in={showMap}>
              <NearbyAlertsMap 
                latitude={latitude} 
                longitude={longitude} 
                alerts={nearbyAlerts}
                isLoading={nearbyAlertsLoading}
                error={nearbyAlertsError}
                radiusKm={radiusKm}
              />
            </Collapse>
            <Collapse in={!showMap}>
              <NearbyAlerts 
                alerts={nearbyAlerts}
                isLoading={nearbyAlertsLoading}
                error={nearbyAlertsError}
                radiusKm={radiusKm}
              />
            </Collapse>
          </Card>
        ) : (
          <Card elevation={2} sx={{ p: 3 }}>
            <Box textAlign="center">
              <FiMapPin size={32} color="#9ca3af" />
              <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                Enable location access to see nearby alerts
              </Typography>
              <Button
                variant="outlined"
                startIcon={<FiNavigation />}
                onClick={() => detectLocation()}
                sx={{ mt: 2 }}
              >
                Enable Location
              </Button>
            </Box>
          </Card>
        )}
      </Box>
       </Stack>
       </Paper>
     </Box>
   )
}
