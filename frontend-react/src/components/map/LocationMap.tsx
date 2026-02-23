import { useEffect, useRef, useCallback } from 'react'
import { MapContainer, TileLayer, Marker, useMap, useMapEvents } from 'react-leaflet'
import L from 'leaflet'
import { Box, Text, HStack, VStack, Button, Icon } from '@chakra-ui/react'
import { FiNavigation } from 'react-icons/fi'
import './map.css'

// Fix Leaflet default icon issue with bundlers
delete (L.Icon.Default.prototype as any)._getIconUrl
L.Icon.Default.mergeOptions({
  iconRetinaUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon-2x.png',
  iconUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-icon.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
})

// Custom pet marker icon
const petIcon = new L.Icon({
  iconUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
  shadowUrl: 'https://unpkg.com/leaflet@1.9.4/dist/images/marker-shadow.png',
  iconSize: [25, 41],
  iconAnchor: [12, 41],
  popupAnchor: [1, -34],
  shadowSize: [41, 41],
  iconRetinaUrl: 'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-red.png',
})

interface LocationMapProps {
  latitude: number | null
  longitude: number | null
  onLocationChange: (lat: number, lng: number) => void
  onDetectLocation?: () => void
  isDetectingLocation?: boolean
  height?: string
  disabled?: boolean
}

// Default center: Madrid, Spain
const DEFAULT_CENTER: [number, number] = [40.4168, -3.7038]
const DEFAULT_ZOOM = 5
const FLY_ZOOM = 15

// Component to handle map events
function MapEventHandler({ 
  onLocationChange, 
  disabled 
}: { 
  onLocationChange: (lat: number, lng: number) => void
  disabled?: boolean 
}) {
  useMapEvents({
    click(e) {
      if (!disabled) {
        onLocationChange(e.latlng.lat, e.latlng.lng)
      }
    },
  })
  return null
}

// Component to handle map center updates
function MapCenterUpdater({ 
  position, 
  shouldFly 
}: { 
  position: [number, number] | null
  shouldFly: boolean 
}) {
  const map = useMap()
  
  useEffect(() => {
    if (position) {
      if (shouldFly) {
        map.flyTo(position, FLY_ZOOM, { duration: 1.5 })
      } else {
        map.setView(position, map.getZoom())
      }
    }
  }, [position, shouldFly, map])
  
  return null
}

// Draggable marker component
function DraggableMarker({ 
  position, 
  onDragEnd, 
  disabled 
}: { 
  position: [number, number]
  onDragEnd: (lat: number, lng: number) => void
  disabled?: boolean 
}) {
  const markerRef = useRef<L.Marker>(null)

  const eventHandlers = useCallback(
    (_e: L.DragEndEvent) => {
      const marker = markerRef.current
      if (marker != null && !disabled) {
        const latlng = marker.getLatLng()
        onDragEnd(latlng.lat, latlng.lng)
      }
    },
    [onDragEnd, disabled]
  )

  return (
    <Marker
      draggable={!disabled}
      eventHandlers={{ dragend: eventHandlers }}
      position={position}
      ref={markerRef}
      icon={petIcon}
    />
  )
}

export default function LocationMap({
  latitude,
  longitude,
  onLocationChange,
  onDetectLocation,
  isDetectingLocation = false,
  height = '300px',
  disabled = false,
}: LocationMapProps) {
  const hasPosition = latitude !== null && longitude !== null
  const position: [number, number] | null = hasPosition ? [latitude!, longitude!] : null
  const prevPositionRef = useRef<[number, number] | null>(null)
  
  // Determine if we should fly to the new position (only when position changes from null or from detect location)
  const shouldFly = hasPosition && 
    (prevPositionRef.current === null || 
     (prevPositionRef.current[0] !== latitude || prevPositionRef.current[1] !== longitude))
  
  useEffect(() => {
    if (hasPosition) {
      prevPositionRef.current = [latitude!, longitude!]
    }
  }, [latitude, longitude, hasPosition])

  const handleMarkerDrag = useCallback((lat: number, lng: number) => {
    onLocationChange(lat, lng)
  }, [onLocationChange])

  return (
    <VStack gap={2} align="stretch">
      {/* Detect Location Button */}
      {onDetectLocation && (
        <Button
          type="button"
          variant="outline"
          colorPalette="accent"
          size="sm"
          onClick={onDetectLocation}
          loading={isDetectingLocation}
          disabled={disabled}
          mb={2}
        >
          <Icon as={FiNavigation} mr={2} />
          {isDetectingLocation ? 'Detecting...' : 'Use My Location'}
        </Button>
      )}

      {/* Map Container */}
      <Box
        borderRadius="md"
        overflow="hidden"
        border="1px solid"
        borderColor="gray.200"
        _dark={{ borderColor: 'gray.600' }}
        height={height}
        className="location-map-wrapper"
        opacity={disabled ? 0.6 : 1}
        pointerEvents={disabled ? 'none' : 'auto'}
      >
        <MapContainer
          center={position || DEFAULT_CENTER}
          zoom={hasPosition ? FLY_ZOOM : DEFAULT_ZOOM}
          style={{ height: '100%', width: '100%' }}
          scrollWheelZoom={!disabled}
        >
          <TileLayer
            attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
            url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
          />
          
          <MapEventHandler onLocationChange={onLocationChange} disabled={disabled} />
          
          {position && (
            <>
              <MapCenterUpdater position={position} shouldFly={shouldFly} />
              <DraggableMarker 
                position={position} 
                onDragEnd={handleMarkerDrag} 
                disabled={disabled} 
              />
            </>
          )}
        </MapContainer>
      </Box>

      {/* Coordinates Display */}
      <HStack justify="space-between" fontSize="sm" color="gray.800" _dark={{ color: 'gray.300' }}>
        <Text>
          {hasPosition ? (
            <>
              <Text as="span" fontWeight="medium">Lat:</Text> {latitude?.toFixed(6)}, 
              <Text as="span" fontWeight="medium" ml={2}>Lng:</Text> {longitude?.toFixed(6)}
            </>
          ) : (
            'Click on the map to select a location'
          )}
        </Text>
      </HStack>

      {/* Tip */}
      <Text fontSize="xs" color="gray.700" _dark={{ color: 'gray.300' }}>
        Tip: Click on the map or drag the marker to refine the exact location.
      </Text>
    </VStack>
  )
}