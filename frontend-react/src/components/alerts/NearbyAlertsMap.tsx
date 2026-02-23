import { useState, useEffect, useMemo } from 'react'
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet'
import L from 'leaflet'
import { Box, Typography, CircularProgress, Chip, Stack } from '@mui/material'
import { FiAlertTriangle, FiMapPin, FiNavigation } from 'react-icons/fi'
import { alertService } from '../../services/alert.service'
import type { Alert, AlertStatus } from '../../types'
import '../map/map.css'

// Default status display names
const DEFAULT_STATUS_NAMES: Record<AlertStatus, string> = {
  OPENED: 'Open',
  SEEN: 'Seen',
  SAFE: 'Safe',
  CLOSED: 'Closed'
}

// Fix Leaflet default icon issue with bundlers
delete (L.Icon.Default.prototype as any)._getIconUrl
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
})

// Create custom marker icons based on alert status
const createAlertIcon = (status: AlertStatus): L.DivIcon => {
  const colors: Record<AlertStatus, string> = {
    OPENED: '#b34045',
    SEEN: '#fecf6d',
    SAFE: '#2d884d',
    CLOSED: '#4091d7'
  }
  
  const color = colors[status] || '#718096'
  
  return new L.DivIcon({
    className: 'custom-alert-marker',
    html: `<div style="
      background-color: ${color};
      color: white;
      border-radius: 50%;
      width: 32px;
      height: 32px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: bold;
      font-size: 14px;
      border: 3px solid white;
      box-shadow: 0 2px 8px rgba(0,0,0,0.4);
    ">🐾</div>`,
    iconSize: [32, 32],
    iconAnchor: [16, 16],
    popupAnchor: [0, -16]
  })
}

// User location marker icon
const userLocationIcon = new L.DivIcon({
  className: 'custom-user-marker',
  html: `<div style="
    background-color: #3182CE;
    color: white;
    border-radius: 50%;
    width: 24px;
    height: 24px;
    display: flex;
    align-items: center;
    justify-content: center;
    border: 3px solid white;
    box-shadow: 0 2px 8px rgba(0,0,0,0.4);
  "><svg width="12" height="12" viewBox="0 0 24 24" fill="currentColor"><circle cx="12" cy="12" r="6"/></svg></div>`,
  iconSize: [24, 24],
  iconAnchor: [12, 12],
  popupAnchor: [0, -12]
})

interface NearbyAlertsMapProps {
  latitude: number
  longitude: number
  radiusKm?: number
  fullHeight?: boolean
}

// Component to fit bounds to show all markers
function FitBounds({ 
  alerts, 
  userLocation 
}: { 
  alerts: Alert[]
  userLocation: [number, number] 
}) {
  const map = useMap()
  
  useEffect(() => {
    if (alerts.length > 0 && map) {
      const bounds: [number, number][] = alerts
        .filter(alert => alert.latitude != null && alert.longitude != null)
        .map(alert => [
          alert.latitude,
          alert.longitude
        ])
      
      if (userLocation && userLocation[0] != null && userLocation[1] != null) {
        bounds.push(userLocation)
      }
      
      if (bounds.length > 0) {
        try {
          map.fitBounds(bounds, { padding: [50, 50], maxZoom: 14 })
        } catch (err) {
          console.error('Error fitting bounds to map:', err)
        }
      }
    }
  }, [alerts, userLocation, map])
  
  return null
}

// Get status color for badge
const getStatusColor = (status: AlertStatus): 'error' | 'warning' | 'success' | 'info' => {
  const colors: Record<AlertStatus, 'error' | 'warning' | 'success' | 'info'> = {
    OPENED: 'error',
    SEEN: 'warning',
    SAFE: 'success',
    CLOSED: 'info'
  }
  return colors[status] || 'info'
}

// Format date for display
const formatDate = (dateString: string): string => {
  try {
    const date = new Date(dateString)
    return date.toLocaleString('en-US', {
      day: '2-digit',
      month: 'short',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch {
    return dateString
  }
}

export default function NearbyAlertsMap({ 
  latitude, 
  longitude, 
  radiusKm = 10,
  fullHeight = false
}: NearbyAlertsMapProps) {
  const [alerts, setAlerts] = useState<Alert[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchNearbyAlerts = async () => {
      try {
        setIsLoading(true)
        setError(null)
        const data = await alertService.getNearbyAlerts(latitude, longitude, radiusKm)
        const validAlerts = data.filter(alert => {
          if (!alert.latitude || !alert.longitude) {
            return false
          }
          return true
        })
        setAlerts(validAlerts)
      } catch (err) {
        console.error('Error fetching nearby alerts:', err)
        setError('Could not load nearby alerts')
      } finally {
        setIsLoading(false)
      }
    }

    fetchNearbyAlerts()
  }, [latitude, longitude, radiusKm])

  const getStatusDisplayName = (status: AlertStatus): string => {
    return DEFAULT_STATUS_NAMES[status] || status
  }

  const mapCenter: [number, number] = useMemo(() => {
    return [latitude, longitude]
  }, [latitude, longitude])

  // Show loading overlay while fetching
  const loadingOverlay = isLoading && (
    <Box 
      sx={{
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        bgcolor: 'rgba(255, 255, 255, 0.9)',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 1000,
      }}
    >
      <CircularProgress />
      <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
        Searching for nearby alerts...
      </Typography>
    </Box>
  )

  // Show error overlay
  const errorOverlay = error && (
    <Box 
      sx={{
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        bgcolor: 'rgba(255, 255, 255, 0.9)',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 1000,
      }}
    >
      <FiAlertTriangle color="#ef4444" size={24} />
      <Typography variant="body2" color="error" sx={{ mt: 1 }}>{error}</Typography>
    </Box>
  )

  // Show no alerts overlay
  const noAlertsOverlay = !isLoading && !error && alerts.length === 0 && (
    <Box 
      sx={{
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        bgcolor: 'rgba(255, 255, 255, 0.85)',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 1000,
      }}
    >
      <FiMapPin color="#9ca3af" size={32} />
      <Typography variant="body1" color="text.secondary" sx={{ mt: 1 }} fontWeight="medium">
        No active alerts in your area
      </Typography>
      <Typography variant="caption" color="text.disabled">
        Within {radiusKm}km radius
      </Typography>
    </Box>
  )

  return (
    <Stack 
      spacing={0} 
      sx={{
        position: fullHeight ? 'absolute' : 'relative',
        top: fullHeight ? 0 : 'auto',
        left: fullHeight ? 0 : 'auto',
        right: fullHeight ? 0 : 'auto',
        bottom: fullHeight ? 0 : 'auto',
        height: fullHeight ? '100%' : 'auto',
      }}
    >
      <Box
        sx={{
          borderRadius: fullHeight ? 0 : 1,
          overflow: 'hidden',
          border: fullHeight ? 'none' : '1px solid',
          borderColor: 'divider',
          height: fullHeight ? '100%' : { xs: '200px', sm: '250px', md: '300px' },
          position: 'relative',
        }}
        className="location-map-wrapper"
      >
        <MapContainer
          center={mapCenter}
          zoom={13}
          style={{ height: '100%', width: '100%' }}
          scrollWheelZoom={true}
          zoomControl={true}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a>'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          
          <FitBounds alerts={alerts} userLocation={mapCenter} />
          
          <Marker 
            position={mapCenter} 
            icon={userLocationIcon}
          >
            <Popup>
              <Stack spacing={1} minWidth="150px">
                <Stack direction="row" alignItems="center" spacing={1}>
                  <FiNavigation color="#3182ce" size={16} />
                  <Typography variant="body2" fontWeight="bold">Your location</Typography>
                </Stack>
              </Stack>
            </Popup>
          </Marker>
          
          {alerts
            .filter(alert => alert.latitude != null && alert.longitude != null)
            .map((alert) => (
            <Marker
              key={alert.id}
              position={[alert.latitude, alert.longitude]}
              icon={createAlertIcon(alert.status)}
            >
              <Popup>
                <Stack spacing={1} minWidth="200px" p={1}>
                  <Typography variant="body2" fontWeight="bold" color="text.primary">
                    {alert.title}
                  </Typography>
                  
                  <Chip 
                    label={getStatusDisplayName(alert.status)}
                    size="small"
                    color={getStatusColor(alert.status)}
                  />
                  
                  {alert.description && (
                    <Typography variant="caption" color="text.secondary" sx={{
                      overflow: 'hidden',
                      textOverflow: 'ellipsis',
                      display: '-webkit-box',
                      WebkitLineClamp: 2,
                      WebkitBoxOrient: 'vertical',
                    }}>
                      {alert.description}
                    </Typography>
                  )}
                  
                  <Typography variant="caption" color="text.disabled">
                    {formatDate(alert.createdAt)}
                  </Typography>
                </Stack>
              </Popup>
            </Marker>
          ))}
        </MapContainer>
        
        {loadingOverlay}
        {errorOverlay}
        {noAlertsOverlay}
        
        {fullHeight && (
          <Box
            sx={{
              position: 'absolute',
              top: 2,
              right: 2,
              bgcolor: 'background.paper',
              borderRadius: 1,
              px: 1.5,
              py: 1,
              boxShadow: 2,
              zIndex: 1000,
            }}
          >
            <Stack direction="row" alignItems="center" spacing={1}>
              <FiAlertTriangle color="#ed8936" size={16} />
              <Typography variant="body2" fontWeight="medium" color="text.primary">
                {alerts.length} nearby alert{alerts.length > 1 ? 's' : ''}
              </Typography>
              <Chip label={`${radiusKm}km`} size="small" />
            </Stack>
          </Box>
        )}
        
        {fullHeight && (
          <Box
            sx={{
              position: 'absolute',
              bottom: 2,
              left: 2,
              bgcolor: 'background.paper',
              borderRadius: 1,
              px: 1.5,
              py: 1,
              boxShadow: 2,
              zIndex: 1000,
            }}
          >
            <Stack direction="row" spacing={1.5} fontSize="xs" color="text.secondary">
              <Stack direction="row" alignItems="center" spacing={0.5}>
                <Box sx={{ width: 8, height: 8, borderRadius: '50%', bgcolor: 'error.main' }} />
                <Typography variant="caption">Open</Typography>
              </Stack>
              <Stack direction="row" alignItems="center" spacing={0.5}>
                <Box sx={{ width: 8, height: 8, borderRadius: '50%', bgcolor: 'warning.main' }} />
                <Typography variant="caption">Seen</Typography>
              </Stack>
              <Stack direction="row" alignItems="center" spacing={0.5}>
                <Box sx={{ width: 8, height: 8, borderRadius: '50%', bgcolor: 'success.main' }} />
                <Typography variant="caption">Safe</Typography>
              </Stack>
              <Stack direction="row" alignItems="center" spacing={0.5}>
                <Box sx={{ width: 8, height: 8, borderRadius: '50%', bgcolor: 'info.main' }} />
                <Typography variant="caption">You</Typography>
              </Stack>
            </Stack>
          </Box>
        )}
      </Box>

      {!fullHeight && (
        <>
          <Stack direction="row" justifyContent="space-between" alignItems="center" px={1}>
            <Stack direction="row" alignItems="center" spacing={1}>
              <FiAlertTriangle color="#ed8936" size={16} />
              <Typography variant="body2" fontWeight="medium" color="text.primary">
                {alerts.length} nearby alert{alerts.length > 1 ? 's' : ''}
              </Typography>
            </Stack>
            <Chip label={`${radiusKm} radius`} size="small" />
          </Stack>

          <Stack 
            direction="row" 
            justifyContent="center" 
            spacing={2} 
            fontSize="xs" 
            color="text.secondary"
            flexWrap="wrap"
          >
            <Stack direction="row" alignItems="center" spacing={0.5}>
              <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: 'error.main' }} />
              <Typography variant="caption">Open</Typography>
            </Stack>
            <Stack direction="row" alignItems="center" spacing={0.5}>
              <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: 'warning.main' }} />
              <Typography variant="caption">Seen</Typography>
            </Stack>
            <Stack direction="row" alignItems="center" spacing={0.5}>
              <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: 'success.main' }} />
              <Typography variant="caption">Safe</Typography>
            </Stack>
            <Stack direction="row" alignItems="center" spacing={0.5}>
              <Box sx={{ width: 12, height: 12, borderRadius: '50%', bgcolor: 'info.main' }} />
              <Typography variant="caption">You</Typography>
            </Stack>
          </Stack>
        </>
      )}
    </Stack>
  )
}
