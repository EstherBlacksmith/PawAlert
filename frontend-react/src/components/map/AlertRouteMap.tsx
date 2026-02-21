import { useEffect, useMemo } from 'react'
import { MapContainer, TileLayer, Marker, Polyline, Popup } from 'react-leaflet'
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

// Create numbered icons for the route
const createNumberedIcon = (number: number, isLast: boolean = false) => {
  const color = isLast ? 'green' : 'blue'
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

export default function AlertRouteMap({ events, height = '400px' }: AlertRouteMapProps) {
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
            <FaMapMarkerAlt size={32} color="#A0AEC0" style={{ margin: '0 auto 12px' }} />
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
            <TileLayer
              attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
              url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
            />
            
            {/* Draw route line */}
            {routeCoordinates.length > 1 && (
              <Polyline
                positions={routeCoordinates}
                color="purple"
                weight={3}
                opacity={0.7}
                dashArray="10, 10"
              />
            )}
            
            {/* Render markers */}
            {eventsWithLocation.map((event, index) => (
              <Marker
                key={event.id}
                position={[event.latitude!, event.longitude!]}
                icon={createNumberedIcon(index + 1, index === eventsWithLocation.length - 1)}
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
            Route Legend:
          </Text>
          <VStack align="start" gap={1} fontSize="xs" color="gray.600">
            <Text>
              <Text as="span" fontWeight="bold" color="blue.500">●</Text> Blue markers: Previous locations
            </Text>
            <Text>
              <Text as="span" fontWeight="bold" color="green.500">●</Text> Green marker: Latest location
            </Text>
            <Text>
              <Text as="span" color="purple">---</Text> Purple dashed line: Route path
            </Text>
          </VStack>
        </Box>
      </Card.Body>
    </Card.Root>
  )
}
