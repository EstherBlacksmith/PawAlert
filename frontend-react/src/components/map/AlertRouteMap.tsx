import { useEffect, useMemo, useRef, useState } from 'react'
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet'
import L from 'leaflet'
import { Box, Text, VStack, Card } from '@chakra-ui/react'
import { FaMapMarkerAlt } from 'react-icons/fa'
import type { AlertEvent } from '../../types'
import './map.css'

// Fix Leaflet default icon issue with bundlers
delete (L.Icon.Default.prototype as any)._getIconUrl
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
})

// Create numbered icons for the route based on alert status
const createNumberedIcon = (number: number, status: string | null = null) => {
  // Map alert status to colors
  const getColorByStatus = (status: string | null): string => {
    switch (status) {
      case 'OPENED':
        return '#b34045' // Red/Orange
      case 'CLOSED':
        return '#9ca3af' // Gray
      case 'SAFE':
        return '#22c55e' // Green
      case 'SEEN':
        return '#fecf6d' // Yellow
      case 'FOUND':
        return '#0ea5e9' // Blue
      default:
        return '#0ea5e9' // Default blue
    }
  }

  const color = getColorByStatus(status)
  return new L.DivIcon({
    className: 'custom-numbered-marker',
    html: `<div style="
      background-color: ${color};
      color: white;
      border-radius: 50%;
      width: 28px;
      height: 28px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-weight: bold;
      font-size: 12px;
      border: 2px solid white;
      box-shadow: 0 2px 5px rgba(0,0,0,0.3);
    ">${number}</div>`,
    iconSize: [28, 28],
    iconAnchor: [14, 14],
    popupAnchor: [0, -14]
  })
}

// Default center: Madrid, Spain
const DEFAULT_CENTER: [number, number] = [40.4168, -3.7038]
const DEFAULT_ZOOM = 5

interface AlertRouteMapProps {
  events: AlertEvent[]
  height?: string
}

// Helper to format date for popup
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

// Get event label based on type
const getEventLabel = (event: AlertEvent): string => {
  switch (event.eventType) {
    case 'STATUS_CHANGED':
      return `Status: ${event.newStatus || 'Unknown'}`
    case 'TITLE_CHANGED':
      return `Title: ${event.newValue || 'N/A'}`
    case 'DESCRIPTION_CHANGED':
      return 'Description updated'
    default:
      return event.eventType
  }
}

// Component to handle map size invalidation when container becomes visible
function MapSizeHandler({ containerRef }: { containerRef: React.RefObject<HTMLDivElement> }) {
  const map = useMap()
  const [hasInvalidated, setHasInvalidated] = useState(false)

  useEffect(() => {
    const checkAndInvalidate = () => {
      if (containerRef.current && !hasInvalidated) {
        const rect = containerRef.current.getBoundingClientRect()
        // If container has non-zero dimensions, invalidate map size
        if (rect.width > 0 && rect.height > 0) {
          map.invalidateSize({ animate: true })
          setHasInvalidated(true)
        }
      }
    }

    // Check immediately and then periodically
    checkAndInvalidate()
    const intervalId = setInterval(checkAndInvalidate, 500)
    
    // Also check when the window resizes
    const resizeHandler = () => checkAndInvalidate()
    window.addEventListener('resize', resizeHandler)

    return () => {
      clearInterval(intervalId)
      window.removeEventListener('resize', resizeHandler)
    }
  }, [map, containerRef, hasInvalidated])

  return null
}

export default function AlertRouteMap({ events, height = '400px' }: AlertRouteMapProps) {
  const containerRef = useRef<HTMLDivElement>(null)
  // Filter events that have valid locations and sort by date
  const eventsWithLocation = useMemo(() => {
    return events
      .filter(event => event.latitude != null && event.longitude != null)
      .sort((a, b) => new Date(a.changedAt).getTime() - new Date(b.changedAt).getTime())
  }, [events])

  // Get route coordinates
  const routeCoordinates: [number, number][] = useMemo(() => {
    return eventsWithLocation.map(event => [event.latitude!, event.longitude!])
  }, [eventsWithLocation])

  // Calculate center based on all markers
  const center: [number, number] = useMemo(() => {
    if (routeCoordinates.length === 0) {
      return DEFAULT_CENTER
    }
    
    if (routeCoordinates.length === 1) {
      return routeCoordinates[0]
    }

    // Calculate the centroid of all points
    const latSum = routeCoordinates.reduce((sum, [lat]) => sum + lat, 0)
    const lngSum = routeCoordinates.reduce((sum, [, lng]) => sum + lng, 0)
    return [latSum / routeCoordinates.length, lngSum / routeCoordinates.length]
  }, [routeCoordinates])

  // Calculate appropriate zoom level based on markers spread
  const zoom = useMemo(() => {
    if (routeCoordinates.length === 0) return DEFAULT_ZOOM
    if (routeCoordinates.length === 1) return 15
    
    // Calculate bounds and determine appropriate zoom
    const minLat = Math.min(...routeCoordinates.map(([lat]) => lat))
    const maxLat = Math.max(...routeCoordinates.map(([lat]) => lat))
    const minLng = Math.min(...routeCoordinates.map(([, lng]) => lng))
    const maxLng = Math.max(...routeCoordinates.map(([, lng]) => lng))
    
    const latDiff = maxLat - minLat
    const lngDiff = maxLng - minLng
    const maxDiff = Math.max(latDiff, lngDiff)
    
    if (maxDiff < 0.01) return 15
    if (maxDiff < 0.1) return 12
    if (maxDiff < 1) return 10
    return 8
  }, [routeCoordinates])

  if (!eventsWithLocation || eventsWithLocation.length === 0) {
    return (
      <Card.Root>
        <Card.Body>
          <Box textAlign="center" py={8}>
            <FaMapMarkerAlt size={32} color="gray.400" style={{ margin: '0 auto 12px' }} />
            <Text color="gray.500">No location data available for this alert.</Text>
          </Box>
        </Card.Body>
      </Card.Root>
    )
  }

  return (
    <Card.Root>
      <Card.Body>
        <Text fontWeight="bold" mb={4} fontSize="lg" color="gray.700">
          Alert Route Map
        </Text>
        
        <Box
          ref={containerRef}
          borderRadius="md"
          overflow="hidden"
          border="1px solid"
          borderColor="gray.200"
          _dark={{ borderColor: 'gray.600' }}
          height={height}
          className="location-map-wrapper"
        >
          <MapContainer
            center={center}
            zoom={zoom}
            style={{ height: '100%', width: '100%' }}
            scrollWheelZoom={true}
          >
            <MapSizeHandler containerRef={containerRef} />
            <TileLayer
              attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            
            {/* Render markers */}
            {eventsWithLocation.map((event, index) => (
              <Marker
                key={event.id}
                position={[event.latitude!, event.longitude!]}
                icon={createNumberedIcon(index + 1, event.newStatus)}
              >
                <Popup>
                  <VStack align="start" gap={1} minWidth="180px">
                    <Text fontWeight="bold" fontSize="sm">
                      Location #{index + 1}
                    </Text>
                    <Text fontSize="xs" color="gray.600">
                      {getEventLabel(event)}
                    </Text>
                    <Text fontSize="xs" color="gray.500">
                      {formatDate(event.changedAt)}
                    </Text>
                    {event.latitude && event.longitude && (
                      <Text fontSize="xs" color="gray.400">
                        {event.latitude.toFixed(6)}, {event.longitude.toFixed(6)}
                      </Text>
                    )}
                  </VStack>
                </Popup>
              </Marker>
            ))}
          </MapContainer>
        </Box>

        {/* Legend */}
        <Box mt={3} p={3} bg="gray.50" borderRadius="md">
          <Text fontSize="sm" fontWeight="medium" mb={2}>
            Event Markers by Status:
          </Text>
          <VStack align="start" gap={1} fontSize="xs" color="gray.600">
            <Text>
              <Text as="span" fontWeight="bold" style={{ color: '#b34045' }}>●</Text> Red/Orange: OPEN status
            </Text>
            <Text>
              <Text as="span" fontWeight="bold" style={{ color: '#9ca3af' }}>●</Text> Gray: CLOSED status
            </Text>
            <Text>
              <Text as="span" fontWeight="bold" style={{ color: '#22c55e' }}>●</Text> Green: SAFE status
            </Text>
            <Text>
              <Text as="span" fontWeight="bold" style={{ color: '#fecf6d' }}>●</Text> Yellow: SEEN status
            </Text>
            <Text>
              <Text as="span" fontWeight="bold" style={{ color: '#0ea5e9' }}>●</Text> Blue: FOUND status or other events
            </Text>
          </VStack>
        </Box>
      </Card.Body>
    </Card.Root>
  )
}
