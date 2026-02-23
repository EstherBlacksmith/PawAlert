import { useState, useEffect } from 'react'
import { Button, CircularProgress, Stack, Chip } from '@mui/material'
import { GiBell } from '../icons'
import { alertService } from '../../services/alert.service'
import { useAuth } from '../../context/AuthContext'
import { showSuccessToast, showErrorToast } from '../../utils/errorUtils'

interface SubscribeButtonProps {
  alertId: string
  alertStatus: 'OPENED' | 'SEEN' | 'CLOSED' | 'SAFE'
  onSubscriptionChange?: (isSubscribed: boolean) => void
  size?: 'small' | 'medium' | 'large'
  showStatus?: boolean
}

export function SubscribeButton({
  alertId,
  alertStatus,
  onSubscriptionChange,
  size = 'small',
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

  // Show disabled button for closed alerts
  if (alertStatus === 'CLOSED') {
    return (
      <Stack direction="row" spacing={1} alignItems="center">
        {showStatus && (
          <Chip label="Closed" size="small" color="default" variant="outlined" />
        )}
        <Button
          size={size}
          color="inherit"
          variant="outlined"
          disabled
          title="Cannot subscribe to closed alerts"
          startIcon={<GiBell />}
        >
          Subscribe
        </Button>
      </Stack>
    )
  }

  // Show disabled button for unauthenticated users
  if (!isAuthenticated) {
    return (
      <Button
        size={size}
        color="warning"
        variant="outlined"
        disabled
        title="Login to subscribe to alerts"
        startIcon={<GiBell />}
      >
        Subscribe
      </Button>
    )
  }

  if (isCheckingSubscription) {
    return <CircularProgress size={20} />
  }

   if (isSubscribed) {
     return (
       <Button
         size={size}
         color="inherit"
         variant="outlined"
         disabled
         title="You are already subscribed to this alert"
         startIcon={<GiBell />}
       >
         Subscribed
       </Button>
     )
   }

  return (
    <Button
      size={size}
      color="warning"
      variant="contained"
      onClick={handleSubscribe}
      disabled={!canSubscribe || isLoading}
      startIcon={<GiBell />}
    >
      Subscribe
    </Button>
  )
}
