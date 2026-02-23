import { useState, useEffect } from 'react'
import { Box, Typography, CircularProgress, Chip, Stack } from '@mui/material'
import { FiAlertTriangle, FiMapPin } from 'react-icons/fi'
import { alertService } from '../../services/alert.service'
import { Alert } from '../../types'

interface NearbyAlertsProps {
  latitude: number
  longitude: number
  radiusKm?: number
}

export default function NearbyAlerts({ latitude, longitude, radiusKm = 10 }: NearbyAlertsProps) {
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
        setError('Unable to load nearby alerts')
      } finally {
        setIsLoading(false)
      }
    }

    fetchNearbyAlerts()
  }, [latitude, longitude, radiusKm])

  if (isLoading) {
    return (
      <Box textAlign="center" py={2}>
        <CircularProgress size={20} />
        <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>Looking for nearby alerts...</Typography>
      </Box>
    )
  }

  if (error) {
    return (
      <Typography variant="body2" color="text.disabled" textAlign="center">{error}</Typography>
    )
  }

  if (alerts.length === 0) {
    return (
      <Typography variant="body2" color="text.secondary" textAlign="center">
        No active alerts in your area ({radiusKm}km radius)
      </Typography>
    )
  }

  return (
    <Stack spacing={1}>
      <Typography variant="body2" fontWeight="medium" color="text.secondary">
        {alerts.length} active alert{alerts.length > 1 ? 's' : ''} nearby:
      </Typography>
      {alerts.slice(0, 5).map((alert) => (
        <Box
          key={alert.id}
          sx={{
            p: 1.5,
            bgcolor: 'warning.50',
            borderRadius: 1,
            borderLeft: '4px solid',
            borderColor: 'warning.main',
          }}
        >
          <Stack direction="row" justifyContent="space-between" alignItems="center">
            <Stack direction="row" alignItems="center" spacing={1}>
              <FiAlertTriangle color="#ed8936" />
              <Typography variant="body2" fontWeight="medium">{alert.title}</Typography>
            </Stack>
            <Chip label={alert.status} size="small" color="warning" variant="outlined" />
          </Stack>
          <Stack direction="row" alignItems="center" spacing={0.5} mt={0.5} color="text.secondary">
            <FiMapPin size={12} />
            <Typography variant="caption">Near your location</Typography>
          </Stack>
        </Box>
      ))}
      {alerts.length > 5 && (
        <Typography variant="caption" color="text.disabled" textAlign="center">
          +{alerts.length - 5} more alerts
        </Typography>
      )}
    </Stack>
  )
}
