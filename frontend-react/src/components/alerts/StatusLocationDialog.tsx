import {
  Box,
  Button,
  Dialog,
  Flex,
  Heading,
  Text,
  VStack,
} from '@chakra-ui/react'
import { useState, useEffect } from 'react'
import LocationMap from '../map/LocationMap'

interface StatusLocationDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  onConfirm: (latitude: number, longitude: number) => void
  status: 'SEEN' | 'SAFE' | null
  alertTitle: string
  initialLocation?: { latitude: number; longitude: number } | null
  isDetectingLocation?: boolean
  onDetectLocation?: () => Promise<{ latitude: number; longitude: number }>
  isLoading?: boolean
}

const statusLabels: Record<'SEEN' | 'SAFE', string> = {
  SEEN: 'Mark as Seen',
  SAFE: 'Mark as Safe',
}

const statusDescriptions: Record<'SEEN' | 'SAFE', string> = {
  SEEN: 'Select the location where the pet was seen:',
  SAFE: 'Select the location where the pet was found safe:',
}

export function StatusLocationDialog({
  open,
  onOpenChange,
  onConfirm,
  status,
  alertTitle,
  initialLocation,
  isDetectingLocation = false,
  onDetectLocation,
  isLoading = false,
}: StatusLocationDialogProps) {
  const [latitude, setLatitude] = useState<number | null>(initialLocation?.latitude ?? null)
  const [longitude, setLongitude] = useState<number | null>(initialLocation?.longitude ?? null)
  const [isInternalDetecting, setIsInternalDetecting] = useState(false)

  // Reset location when dialog opens with initial location
  useEffect(() => {
    if (open) {
      setLatitude(initialLocation?.latitude ?? null)
      setLongitude(initialLocation?.longitude ?? null)
    }
  }, [open, initialLocation])

  const handleLocationChange = (lat: number, lng: number) => {
    setLatitude(lat)
    setLongitude(lng)
  }

  const handleDetectLocation = async () => {
    if (onDetectLocation) {
      setIsInternalDetecting(true)
      try {
        const result = await onDetectLocation()
        setLatitude(result.latitude)
        setLongitude(result.longitude)
      } finally {
        setIsInternalDetecting(false)
      }
    }
  }

  const handleConfirm = () => {
    if (latitude !== null && longitude !== null) {
      onConfirm(latitude, longitude)
    }
  }

  const handleClose = () => {
    setLatitude(null)
    setLongitude(null)
    onOpenChange(false)
  }

  const isValid = latitude !== null && longitude !== null
  const detecting = isDetectingLocation || isInternalDetecting

  if (!status) return null

  return (
    <Dialog.Root open={open} onOpenChange={(details) => !details.open && handleClose()}>
      <Dialog.Backdrop />
      <Dialog.Positioner>
        <Dialog.Content maxW="600px">
          <Dialog.Header>
            <Heading size="md">{statusLabels[status]}</Heading>
            <Text fontSize="sm" color="gray.500" mt={1}>
              {alertTitle}
            </Text>
          </Dialog.Header>
          <Dialog.Body>
          <VStack align="stretch" gap={4}>
            <Text color="gray.600">
              {statusDescriptions[status]}
            </Text>
            
            <Box>
              <LocationMap
                latitude={latitude}
                longitude={longitude}
                onLocationChange={handleLocationChange}
                onDetectLocation={onDetectLocation ? handleDetectLocation : undefined}
                isDetectingLocation={detecting}
                height="300px"
              />
            </Box>
          </VStack>
        </Dialog.Body>
        <Dialog.Footer>
          <Flex gap={2} justify="flex-end">
            <Button variant="outline" onClick={handleClose} disabled={isLoading}>
              Cancel
            </Button>
            <Button
              colorPalette={status === 'SAFE' ? 'green' : 'yellow'}
              onClick={handleConfirm}
              disabled={!isValid || isLoading}
              loading={isLoading}
            >
              {statusLabels[status]}
            </Button>
          </Flex>
        </Dialog.Footer>
        </Dialog.Content>
      </Dialog.Positioner>
    </Dialog.Root>
  )
}
