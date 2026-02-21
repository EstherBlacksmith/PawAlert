import { useState, useEffect, useMemo } from 'react'
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet'
import L from 'leaflet'
import { Box, Text, Spinner, Badge, HStack, VStack, Icon } from '@chakra-ui/react'
import { FiAlertTriangle, FiMapPin, FiNavigation } from 'react-icons/fi'
import { alertService } from '../../services/alert.service'
import type { Alert, AlertStatus } from '../../types'
import '../map/map.css'

// Default status display names (fallback when metadata is not available)
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
    OPENED: '#E53E3E', // red - urgent
    SEEN: '#DD6B20',   // orange
    SAFE: '#38A169',   // green
    CLOSED: '#718096'  // gray
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

// User location marker icon (blue)
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
    if (alerts.length > 0) {
      const bounds: [number, number][] = alerts.map(alert => [
        alert.latitude,
        alert.longitude
      ])
      bounds.push(userLocation)
      map.fitBounds(bounds, { padding: [50, 50], maxZoom: 14 })
    }
  }, [alerts, userLocation, map])
  
  return null
}

// Get status color for badge
const getStatusColor = (status: AlertStatus): string => {
  const colors: Record<AlertStatus, string> = {
    OPENED: 'red',
    SEEN: 'orange',
    SAFE: 'green',
    CLOSED: 'gray'
  }
  return colors[status] || 'gray'
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
        const data = await alertService.getNearbyAlerts(latitude, longitude, radiusKm)
        setAlerts(data)
      } catch (err) {
        console.error('Error fetching nearby alerts:', err)
        setError('Could not load nearby alerts')
      } finally {
        setIsLoading(false)
      }
    }

    fetchNearbyAlerts()
  }, [latitude, longitude, radiusKm])

  // Get status display name (uses default fallback names)
  const getStatusDisplayName = (status: AlertStatus): string => {
    return DEFAULT_STATUS_NAMES[status] || status
  }

  // Calculate map center based on user location
  const mapCenter: [number, number] = useMemo(() => {
    return [latitude, longitude]
  }, [latitude, longitude])

  // Loading state
  if (isLoading) {
    return (
      <Box 
        textAlign="center" 
        py={6} 
        px={4}
        bg="gray.50"
        borderRadius={fullHeight ? 'none' : 'md'}
        position={fullHeight ? 'absolute' : 'relative'}
        top={fullHeight ? 0 : 'auto'}
        left={fullHeight ? 0 : 'auto'}
        right={fullHeight ? 0 : 'auto'}
        bottom={fullHeight ? 0 : 'auto'}
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
      >
        <Spinner size="md" color="purple.500" />
        <Text fontSize="sm" color="gray.500" mt={3}>
          Searching for nearby alerts...
        </Text>
      </Box>
    )
  }

  // Error state
  if (error) {
    return (
      <Box 
        textAlign="center" 
        py={4} 
        px={4}
        bg="red.50"
        borderRadius={fullHeight ? 'none' : 'md'}
        position={fullHeight ? 'absolute' : 'relative'}
        top={fullHeight ? 0 : 'auto'}
        left={fullHeight ? 0 : 'auto'}
        right={fullHeight ? 0 : 'auto'}
        bottom={fullHeight ? 0 : 'auto'}
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
      >
        <Icon as={FiAlertTriangle} color="red.400" boxSize={6} mb={2} />
        <Text fontSize="sm" color="red.500">{error}</Text>
      </Box>
    )
  }

  // No alerts state
  if (alerts.length === 0) {
    return (
      <Box 
        textAlign="center" 
        py={6} 
        px={4}
        bg="gray.50"
        borderRadius={fullHeight ? 'none' : 'md'}
        position={fullHeight ? 'absolute' : 'relative'}
        top={fullHeight ? 0 : 'auto'}
        left={fullHeight ? 0 : 'auto'}
        right={fullHeight ? 0 : 'auto'}
        bottom={fullHeight ? 0 : 'auto'}
        display="flex"
        flexDirection="column"
        alignItems="center"
        justifyContent="center"
      >
        <Icon as={FiMapPin} color="gray.400" boxSize={6} mb={2} />
        <Text fontSize="sm" color="gray.500">
          No active alerts in your area ({radiusKm}km)
        </Text>
      </Box>
    )
  }

  return (
    <VStack 
      gap={0} 
      align="stretch" 
      position={fullHeight ? 'absolute' : 'relative'}
      top={fullHeight ? 0 : 'auto'}
      left={fullHeight ? 0 : 'auto'}
      right={fullHeight ? 0 : 'auto'}
      bottom={fullHeight ? 0 : 'auto'}
      h={fullHeight ? '100%' : 'auto'}
    >
      {/* Map Container */}
      <Box
        borderRadius={fullHeight ? 'none' : 'md'}
        overflow="hidden"
        border={fullHeight ? 'none' : '1px solid'}
        borderColor="gray.200"
        _dark={{ borderColor: 'gray.600' }}
        h={fullHeight ? '100%' : { base: '200px', sm: '250px', md: '300px' }}
        className="location-map-wrapper"
        position="relative"
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
          
          {/* Fit bounds to show all markers */}
          <FitBounds alerts={alerts} userLocation={mapCenter} />
          
          {/* User location marker */}
          <Marker 
            position={mapCenter} 
            icon={userLocationIcon}
          >
            <Popup>
              <VStack align="start" gap={1} minWidth="150px">
                <HStack>
                  <Icon as={FiNavigation} color="blue.500" boxSize={4} />
                  <Text fontWeight="bold" fontSize="sm">Your location</Text>
                </HStack>
              </VStack>
            </Popup>
          </Marker>
          
          {/* Alert markers */}
          {alerts.map((alert) => (
            <Marker
              key={alert.id}
              position={[alert.latitude, alert.longitude]}
              icon={createAlertIcon(alert.status)}
            >
              <Popup>
                <VStack align="start" gap={2} minWidth="200px" p={1}>
                  <Text fontWeight="bold" fontSize="sm" color="gray.800">
                    {alert.title}
                  </Text>
                  
                  <HStack>
                    <Badge 
                      colorPalette={getStatusColor(alert.status)} 
                      variant="solid"
                      fontSize="xs"
                    >
                      {getStatusDisplayName(alert.status)}
                    </Badge>
                  </HStack>
                  
                  {alert.description && (
                    <Text fontSize="xs" color="gray.600" lineClamp={2}>
                      {alert.description}
                    </Text>
                  )}
                  
                  <Text fontSize="xs" color="gray.400">
                    {formatDate(alert.createdAt)}
                  </Text>
                </VStack>
              </Popup>
            </Marker>
          ))}
        </MapContainer>
        
        {/* Overlay Alert Counter Badge - only for fullHeight mode */}
        {fullHeight && (
          <Box
            position="absolute"
            top={4}
            left={4}
            bg="white"
            borderRadius="md"
            px={3}
            py={2}
            boxShadow="md"
            zIndex={1000}
          >
            <HStack>
              <Icon as={FiAlertTriangle} color="orange.500" boxSize={4} />
              <Text fontSize="sm" fontWeight="medium" color="gray.700">
                {alerts.length} nearby alert{alerts.length > 1 ? 's' : ''}
              </Text>
              <Badge colorPalette="purple" variant="subtle" fontSize="xs">
                {radiusKm}km
              </Badge>
            </HStack>
          </Box>
        )}
        
        {/* Overlay Legend - only for fullHeight mode */}
        {fullHeight && (
          <Box
            position="absolute"
            bottom={4}
            left={4}
            bg="white"
            borderRadius="md"
            px={3}
            py={2}
            boxShadow="md"
            zIndex={1000}
          >
            <HStack gap={3} fontSize="xs" color="gray.500">
              <HStack>
                <Box w={2} h={2} borderRadius="full" bg="red.500" />
                <Text>Open</Text>
              </HStack>
              <HStack>
                <Box w={2} h={2} borderRadius="full" bg="orange.500" />
                <Text>Seen</Text>
              </HStack>
              <HStack>
                <Box w={2} h={2} borderRadius="full" bg="green.500" />
                <Text>Safe</Text>
              </HStack>
              <HStack>
                <Box w={2} h={2} borderRadius="full" bg="blue.500" />
                <Text>You</Text>
              </HStack>
            </HStack>
          </Box>
        )}
      </Box>

      {/* Non-fullHeight mode: Counter and Legend below map */}
      {!fullHeight && (
        <>
          {/* Alert Counter Badge */}
          <HStack justify="space-between" px={1}>
            <HStack>
              <Icon as={FiAlertTriangle} color="orange.500" boxSize={4} />
              <Text fontSize="sm" fontWeight="medium" color="gray.700">
                {alerts.length} nearby alert{alerts.length > 1 ? 's' : ''}
              </Text>
            </HStack>
            <Badge colorPalette="purple" variant="subtle" fontSize="xs">
              {radiusKm}km radius
            </Badge>
          </HStack>

          {/* Legend */}
          <HStack 
            justify="center" 
            gap={4} 
            fontSize="xs" 
            color="gray.500"
            flexWrap="wrap"
          >
            <HStack>
              <Box w={3} h={3} borderRadius="full" bg="red.500" />
              <Text>Open</Text>
            </HStack>
            <HStack>
              <Box w={3} h={3} borderRadius="full" bg="orange.500" />
              <Text>Seen</Text>
            </HStack>
            <HStack>
              <Box w={3} h={3} borderRadius="full" bg="green.500" />
              <Text>Safe</Text>
            </HStack>
            <HStack>
              <Box w={3} h={3} borderRadius="full" bg="blue.500" />
              <Text>You</Text>
            </HStack>
          </HStack>
        </>
      )}
    </VStack>
  )
}
