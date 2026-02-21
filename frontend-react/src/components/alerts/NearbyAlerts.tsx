import { useState, useEffect } from 'react'
import { Box, VStack, Text, Spinner, Badge, HStack, Icon } from '@chakra-ui/react'
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
      <Box textAlign="center" py={4}>
        <Spinner size="sm" color="purple.500" />
        <Text fontSize="sm" color="gray.500" mt={2}>Looking for nearby alerts...</Text>
      </Box>
    )
  }

  if (error) {
    return (
      <Text fontSize="sm" color="gray.400" textAlign="center">{error}</Text>
    )
  }

  if (alerts.length === 0) {
    return (
      <Text fontSize="sm" color="gray.500" textAlign="center">
        No active alerts in your area ({radiusKm}km radius)
      </Text>
    )
  }

  return (
    <VStack align="stretch" gap={2}>
      <Text fontSize="sm" fontWeight="medium" color="gray.600">
        {alerts.length} active alert{alerts.length > 1 ? 's' : ''} nearby:
      </Text>
      {alerts.slice(0, 5).map((alert) => (
        <Box
          key={alert.id}
          p={3}
          bg="orange.50"
          borderRadius="md"
          borderLeft="4px solid"
          borderColor="orange.400"
        >
          <HStack justify="space-between">
            <HStack>
              <Icon as={FiAlertTriangle} color="orange.500" />
              <Text fontWeight="medium" fontSize="sm">{alert.title}</Text>
            </HStack>
            <Badge colorPalette="orange" variant="subtle">
              {alert.status}
            </Badge>
          </HStack>
          <HStack mt={1} color="gray.500">
            <Icon as={FiMapPin} boxSize={3} />
            <Text fontSize="xs">Near your location</Text>
          </HStack>
        </Box>
      ))}
      {alerts.length > 5 && (
        <Text fontSize="xs" color="gray.400" textAlign="center">
          +{alerts.length - 5} more alerts
        </Text>
      )}
    </VStack>
  )
}
