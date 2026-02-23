import { useEffect, useMemo, useRef, useState } from 'react'
import { MapContainer, TileLayer, Marker, Popup, useMap } from 'react-leaflet'
import L from 'leaflet'
import { Box, Typography, Stack, Card, CardContent } from '@mui/material'
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
  const getColorByStatus = (status: string | null): string => {
    switch (status) {
      case 'OPENED':
        return '#b34045'
      case 'CLOSED':
        return '#9ca3af'
      case 'SAFE':
        return '#22c55e'
      case 'SEEN':
        return '#fecf6d'
      case 'FOUND':
        return '#0ea5e9'
      default:
        return '#0ea5e9'
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

const DEFAULT_CENTER: [number, number] = [40.4168, -3.7038]
const DEFAULT_ZOOM = 5

interface AlertRouteMapProps {
  events: AlertEvent[]
  height?: string
}

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

function MapSizeHandler({ containerRef }: { containerRef: React.RefObject<HTMLDivElement> }) {
  const map = useMap()
  const [hasInvalidated, setHasInvalidated] = useState(false)

  useEffect(() => {
    const checkAndInvalidate = () => {
      if (containerRef.current && !hasInvalidated) {
        const rect = containerRef.current.getBoundingClientRect()
        if (rect.width > 0 && rect.height > 0) {
          map.invalidateSize({ animate: true })
          setHasInvalidated(true)
        }
      }
    }

    checkAndInvalidate()
    const intervalId = setInterval(checkAndInvalidate, 500)
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
  
  const eventsWithLocation = useMemo(() => {
    return events
      .filter(event => event.latitude != null && event.longitude != null)
      .sort((a, b) => new Date(a.changedAt).getTime() - new Date(b.changedAt).getTime())
  }, [events])

  const routeCoordinates: [number, number][] = useMemo(() => {
    return eventsWithLocation.map(event => [event.latitude!, event.longitude!])
  }, [eventsWithLocation])

  const center: [number, number] = useMemo(() => {
    if (routeCoordinates.length === 0) {
      return DEFAULT_CENTER
    }
    
    if (routeCoordinates.length === 1) {
      return routeCoordinates[0]
    }

    const latSum = routeCoordinates.reduce((sum, [lat]) => sum + lat, 0)
    const lngSum = routeCoordinates.reduce((sum, [, lng]) => sum + lng, 0)
    return [latSum / routeCoordinates.length, lngSum / routeCoordinates.length]
  }, [routeCoordinates])

  const zoom = useMemo(() => {
    if (routeCoordinates.length === 0) return DEFAULT_ZOOM
    if (routeCoordinates.length === 1) return 15
    
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
      <Card>
        <CardContent>
          <Box textAlign="center" py={4}>
            <FaMapMarkerAlt size={32} style={{ margin: '0 auto 12px', color: '#9ca3af' }} />
            <Typography color="text.secondary">No location data available for this alert.</Typography>
          </Box>
        </CardContent>
      </Card>
    )
  }

  return (
    <Card>
      <CardContent>
        <Typography fontWeight="bold" variant="h6" color="text.primary" sx={{ mb: 2 }}>
          Alert Route Map
        </Typography>
        
        <Box
          ref={containerRef}
          sx={{
            borderRadius: 1,
            overflow: 'hidden',
            border: '1px solid',
            borderColor: 'divider',
            height: height,
          }}
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
            
            {eventsWithLocation.map((event, index) => (
              <Marker
                key={event.id}
                position={[event.latitude!, event.longitude!]}
                icon={createNumberedIcon(index + 1, event.newStatus)}
              >
                <Popup>
                  <Stack spacing={1} minWidth="180px">
                    <Typography variant="body2" fontWeight="bold">
                      Location #{index + 1}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {getEventLabel(event)}
                    </Typography>
                    <Typography variant="caption" color="text.secondary">
                      {formatDate(event.changedAt)}
                    </Typography>
                    {event.latitude && event.longitude && (
                      <Typography variant="caption" color="text.disabled">
                        {event.latitude.toFixed(6)}, {event.longitude.toFixed(6)}
                      </Typography>
                    )}
                  </Stack>
                </Popup>
              </Marker>
            ))}
          </MapContainer>
        </Box>

        {/* Legend */}
        <Box sx={{ mt: 2, p: 1.5, bgcolor: 'grey.100', borderRadius: 1 }}>
          <Typography variant="body2" fontWeight="medium" sx={{ mb: 1 }}>
            Event Markers by Status:
          </Typography>
          <Stack spacing={0.5} fontSize="0.75rem" color="text.secondary">
            <Typography variant="caption">
              <Typography component="span" fontWeight="bold" sx={{ color: '#b34045' }}>●</Typography> Red/Orange: OPEN status
            </Typography>
            <Typography variant="caption">
              <Typography component="span" fontWeight="bold" sx={{ color: '#9ca3af' }}>●</Typography> Gray: CLOSED status
            </Typography>
            <Typography variant="caption">
              <Typography component="span" fontWeight="bold" sx={{ color: '#22c55e' }}>●</Typography> Green: SAFE status
            </Typography>
            <Typography variant="caption">
              <Typography component="span" fontWeight="bold" sx={{ color: '#fecf6d' }}>●</Typography> Yellow: SEEN status
            </Typography>
            <Typography variant="caption">
              <Typography component="span" fontWeight="bold" sx={{ color: '#0ea5e9' }}>●</Typography> Blue: FOUND status or other events
            </Typography>
          </Stack>
        </Box>
      </CardContent>
    </Card>
  )
}
