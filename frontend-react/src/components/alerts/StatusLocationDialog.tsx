import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  Typography,
  Box,
} from '@mui/material'
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
    <Dialog open={open} onClose={handleClose} maxWidth="sm" fullWidth>
      <DialogTitle>
        <Typography variant="h6">{statusLabels[status]}</Typography>
        <Typography variant="body2" color="text.secondary">
          {alertTitle}
        </Typography>
      </DialogTitle>
      <DialogContent>
        <Typography color="text.secondary" sx={{ mb: 2 }}>
          {statusDescriptions[status]}
        </Typography>
        <Box sx={{ height: 300 }}>
          <LocationMap
            latitude={latitude}
            longitude={longitude}
            onLocationChange={handleLocationChange}
            onDetectLocation={onDetectLocation ? handleDetectLocation : undefined}
            isDetectingLocation={detecting}
            height="300px"
          />
        </Box>
      </DialogContent>
      <DialogActions>
        <Button variant="outlined" onClick={handleClose} disabled={isLoading}>
          Cancel
        </Button>
        <Button
          variant="contained"
          color={status === 'SAFE' ? 'success' : 'warning'}
          onClick={handleConfirm}
          disabled={!isValid || isLoading}
        >
          {statusLabels[status]}
        </Button>
      </DialogActions>
    </Dialog>
  )
}
