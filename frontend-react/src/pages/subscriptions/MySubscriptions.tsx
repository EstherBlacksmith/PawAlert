import { useEffect, useState } from 'react'
import { Box, Heading, VStack, Text, Flex, Spinner, Card, Badge, HStack, Button, EmptyState, Icon } from '@chakra-ui/react'
import { useNavigate } from 'react-router-dom'
import { GiBell, GiDirectionSigns, GiTrashCan, GiInfo, GiEye, GiArrowRight } from '../../components/icons'
import { alertService } from '../../services/alert.service'
import type { AlertSubscriptionWithDetails, ErrorResponse } from '../../types'
import { toast } from '../../toaster'

const statusColors: Record<string, string> = {
  OPENED: 'red',
  SEEN: 'yellow',
  CLOSED: 'green',
  SAFE: 'blue',
}

export default function MySubscriptions() {
  const navigate = useNavigate()
  const [subscriptions, setSubscriptions] = useState<AlertSubscriptionWithDetails[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<ErrorResponse | null>(null)
  const [unsubscribingId, setUnsubscribingId] = useState<string | null>(null)

  useEffect(() => {
    const fetchSubscriptions = async () => {
      try {
        const data = await alertService.getMySubscriptions()
        setSubscriptions(data)
      } catch (err: any) {
        console.error('Error fetching subscriptions:', err)
        if (err.response?.data) {
          setError(err.response.data)
        } else {
          setError({
            status: err.response?.status || 500,
            error: err.response?.statusText || 'Error',
            message: 'Failed to load subscriptions. Please try again later.'
          })
        }
      } finally {
        setIsLoading(false)
      }
    }

    fetchSubscriptions()
  }, [])

  const handleUnsubscribe = async (alertId: string) => {
    setUnsubscribingId(alertId)
    try {
      await alertService.unsubscribeFromAlert(alertId)
      setSubscriptions(prev => prev.filter(sub => sub.alertId !== alertId))
      toast({
        title: 'Unsubscribed',
        description: 'You will no longer receive notifications for this alert.',
        status: 'info',
      })
    } catch (err: any) {
      console.error('Error unsubscribing:', err)
      toast({
        title: 'Error',
        description: err.response?.data?.message || 'Failed to unsubscribe',
        status: 'error',
      })
    } finally {
      setUnsubscribingId(null)
    }
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  if (isLoading) {
    return (
      <Flex justify="center" align="center" minH="300px">
        <Spinner size="xl" color="accent.500" />
      </Flex>
    )
  }

  if (error) {
    return (
      <Box textAlign="center" py={10}>
        <Icon color="red.500" mb={4}>
          <GiInfo size={48} />
        </Icon>
        <Text color="red.500" fontSize="lg" mb={2}>
          {error.error || 'Error'}
        </Text>
        <Text color="gray.700" mb={4}>
          {error.message}
        </Text>
        <Button colorPalette="brand" onClick={() => window.location.reload()}>
          Retry
        </Button>
      </Box>
    )
  }

  return (
    <Box maxW="600px" mx="auto" bg="rgba(255, 255, 255, 0.85)" p={6} borderRadius="lg" boxShadow="lg">
      <Flex justify="space-between" align="center" mb={6}>
        <Heading size="lg" color="gray.800" _dark={{ color: 'white' }}>
          <GiBell style={{ marginRight: '12px', display: 'inline' }} />
          My Subscriptions
        </Heading>
        <Text color="gray.700">
          {subscriptions.length} active subscription{subscriptions.length !== 1 ? 's' : ''}
        </Text>
      </Flex>

      {subscriptions.length === 0 ? (
        <EmptyState.Root>
          <EmptyState.Content>
            <EmptyState.Indicator>
              <GiBell />
            </EmptyState.Indicator>
            <EmptyState.Title>No subscriptions yet</EmptyState.Title>
            <EmptyState.Description>
              Subscribe to alerts to receive notifications when they are updated.
            </EmptyState.Description>
            <Button colorPalette="brand" mt={4} onClick={() => navigate('/alerts')}>
              Browse Alerts
            </Button>
          </EmptyState.Content>
        </EmptyState.Root>
      ) : (
        <VStack align="stretch" gap={4}>
          {subscriptions.map((subscription) => (
            <Card.Root key={subscription.id} _hover={{ shadow: 'md' }} transition="shadow 0.2s">
              <Card.Body>
                <Flex justify="space-between" align="start">
                  <Box flex="1" cursor="pointer" onClick={() => navigate(`/alerts/${subscription.alertId}`)}>
                    <HStack mb={2}>
                      <Heading size="sm" color="brand.600">
                        {subscription.alert?.title || 'Unknown Alert'}
                      </Heading>
                      <Badge colorPalette={statusColors[subscription.alert?.status || 'OPENED']}>
                        {subscription.alert?.status || 'OPENED'}
                      </Badge>
                    </HStack>
                    
                    {subscription.alert?.petName && (
                      <Text fontSize="sm" color="gray.600" mb={1}>
                        Pet: {subscription.alert.petName}
                      </Text>
                    )}
                    
                    <Text fontSize="xs" color="gray.700">
                      Subscribed: {formatDate(subscription.subscribedAt)}
                    </Text>
                  </Box>
                  
                  <HStack gap={2}>
                    <Button
                      size="sm"
                      colorPalette="brand"
                      variant="ghost"
                      onClick={() => navigate(`/alerts/${subscription.alertId}`)}
                    >
                      <GiArrowRight style={{ marginRight: '4px' }} />
                      View
                    </Button>
                    <Button
                      size="sm"
                      colorPalette="red"
                      variant="ghost"
                      onClick={() => handleUnsubscribe(subscription.alertId)}
                      loading={unsubscribingId === subscription.alertId}
                    >
                      <GiTrashCan style={{ marginRight: '4px' }} />
                      Unsubscribe
                    </Button>
                  </HStack>
                </Flex>
              </Card.Body>
            </Card.Root>
          ))}
        </VStack>
      )}
    </Box>
  )
}
