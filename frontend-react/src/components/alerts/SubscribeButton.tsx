import { useState, useEffect } from 'react'
import { Button, Spinner, HStack, Badge, Text } from '@chakra-ui/react'
import { GiBell } from '../icons'
import { alertService } from '../../services/alert.service'
import { useAuth } from '../../context/AuthContext'
import { showSuccessToast, showErrorToast } from '../../utils/errorUtils'

interface SubscribeButtonProps {
  alertId: string
  alertStatus: 'OPENED' | 'SEEN' | 'CLOSED' | 'SAFE'
  onSubscriptionChange?: (isSubscribed: boolean) => void
  size?: 'sm' | 'md' | 'lg'
  showStatus?: boolean
}

export function SubscribeButton({
  alertId,
  alertStatus,
  onSubscriptionChange,
  size = 'sm',
  showStatus = false
}: SubscribeButtonProps) {
  const { user, isAuthenticated } = useAuth()
  const [isSubscribed, setIsSubscribed] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [isCheckingSubscription, setIsCheckingSubscription] = useState(true)

  // Cannot subscribe to closed alerts
  const canSubscribe = alertStatus !== 'CLOSED' && isAuthenticated

  useEffect(() => {
    const checkSubscription = async () => {
      if (!isAuthenticated || !alertId) {
        setIsCheckingSubscription(false)
        return
      }

      try {
        const subscribed = await alertService.isSubscribedToAlert(alertId)
        setIsSubscribed(subscribed)
      } catch (error) {
        console.error('Error checking subscription status:', error)
      } finally {
        setIsCheckingSubscription(false)
      }
    }

    checkSubscription()
  }, [alertId, isAuthenticated])

  const handleSubscribe = async () => {
    if (!user) return

    setIsLoading(true)
    try {
      await alertService.subscribeToAlert(alertId)
      setIsSubscribed(true)
      onSubscriptionChange?.(true)
      showSuccessToast('Subscribed!', 'You will receive notifications when this alert is updated.')
    } catch (error: unknown) {
      console.error('Error subscribing:', error)
      showErrorToast(error)
    } finally {
      setIsLoading(false)
    }
  }

  const handleUnsubscribe = async () => {
    if (!user) return

    setIsLoading(true)
    try {
      await alertService.unsubscribeFromAlert(alertId)
      setIsSubscribed(false)
      onSubscriptionChange?.(false)
      showSuccessToast('Unsubscribed', 'You will no longer receive notifications for this alert.')
    } catch (error: unknown) {
      console.error('Error unsubscribing:', error)
      showErrorToast(error)
    } finally {
      setIsLoading(false)
    }
  }

  // Show disabled button for closed alerts
  if (alertStatus === 'CLOSED') {
    return (
      <HStack gap={2}>
        {showStatus && (
          <Badge colorPalette="gray" variant="subtle">
            Closed
          </Badge>
        )}
        <Button
          size={size}
          colorPalette="gray"
          variant="outline"
          disabled
          title="Cannot subscribe to closed alerts"
        >
          <GiBell style={{ marginRight: '4px' }} />
          Subscribe
        </Button>
      </HStack>
    )
  }

  // Show disabled button for unauthenticated users
  if (!isAuthenticated) {
    return (
      <Button
        size={size}
        colorPalette="orange"
        variant="outline"
        disabled
        title="Login to subscribe to alerts"
      >
        <GiBell style={{ marginRight: '4px' }} />
        Subscribe
      </Button>
    )
  }

  if (isCheckingSubscription) {
    return <Spinner size="sm" color="purple.500" />
  }

  if (isSubscribed) {
    return (
      <HStack gap={2}>
        {showStatus && (
          <Text fontSize="sm" color="green.600">
            Subscribed
          </Text>
        )}
        <Button
          size={size}
          colorPalette="orange"
          variant="outline"
          onClick={handleUnsubscribe}
          loading={isLoading}
        >
          <GiBell style={{ marginRight: '4px' }} />
          Unsubscribe
        </Button>
      </HStack>
    )
  }

  return (
    <Button
      size={size}
      colorPalette="orange"
      variant="solid"
      onClick={handleSubscribe}
      loading={isLoading}
      disabled={!canSubscribe}
    >
      <GiBell style={{ marginRight: '4px' }} />
      Subscribe
    </Button>
  )
}
