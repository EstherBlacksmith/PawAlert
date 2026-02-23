import { useEffect, useState } from 'react'
import { Box, Typography, Stack, CircularProgress, Card, CardContent, Chip, Button, Paper } from '@mui/material'
import { useNavigate } from 'react-router-dom'
import { FaArrowLeft, FaArrowRight } from 'react-icons/fa'
import { GiBell, GiTrashCan, GiInfo } from '../../components/icons'
import { alertService } from '../../services/alert.service'
import type { AlertSubscriptionWithDetails, ErrorResponse } from '../../types'
import { toast } from '../../toaster'

const statusColors: Record<string, string> = {
  OPENED: '#b34045',
  SEEN: '#fecf6d',
  CLOSED: '#4091d7',
  SAFE: '#2d884d',
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
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '300px' }}>
        <CircularProgress color="primary" />
      </Box>
    )
  }

  if (error) {
    return (
      <Box sx={{ textAlign: 'center', py: 5 }}>
        <GiInfo size={48} color="#d32f2f" />
        <Typography color="error" variant="h6" sx={{ mt: 2, mb: 1 }}>
          {error.error || 'Error'}
        </Typography>
        <Typography color="text.secondary" sx={{ mb: 2 }}>
          {error.message}
        </Typography>
        <Button variant="contained" color="primary" onClick={() => window.location.reload()}>
          Retry
        </Button>
      </Box>
    )
  }

  return (
    <Paper sx={{ maxWidth: '600px', mx: 'auto', bgcolor: 'rgba(255, 255, 255, 0.85)', p: 3, borderRadius: 2, boxShadow: 3 }}>
      <Button variant="text" sx={{ mb: 2 }} onClick={() => navigate('/')} startIcon={<FaArrowLeft />}>
         Back
       </Button>

      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Typography variant="h5" color="text.primary">
          <GiBell style={{ marginRight: '12px', display: 'inline' }} />
          My Subscriptions
        </Typography>
        <Typography color="text.secondary">
          {subscriptions.length} active subscription{subscriptions.length !== 1 ? 's' : ''}
        </Typography>
      </Box>

      {subscriptions.length === 0 ? (
        <Box sx={{ textAlign: 'center', py: 5 }}>
          <GiBell size={48} color="#9e9e9e" />
          <Typography variant="h6" sx={{ mt: 2 }}>No subscriptions yet</Typography>
          <Typography color="text.secondary" sx={{ mt: 1, mb: 2 }}>
            Subscribe to alerts to receive notifications when they are updated.
          </Typography>
          <Button variant="contained" color="primary" onClick={() => navigate('/alerts')}>
            Browse Alerts
          </Button>
        </Box>
      ) : (
        <Stack spacing={2}>
           {subscriptions.map((subscription) => (
             <Card 
               key={subscription.id} 
               elevation={2}
               sx={{ 
                 transition: 'box-shadow 0.2s',
                 '&:hover': { boxShadow: 4 }
               }}
             >
               <CardContent>
                 <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: 2 }}>
                   <Box sx={{ flex: 1, cursor: 'pointer' }} onClick={() => navigate(`/alerts/${subscription.alertId}`)}>
                     <Stack direction="row" alignItems="center" spacing={1} sx={{ mb: 1 }}>
                       <Typography variant="subtitle1" color="primary.main" fontWeight="medium">
                         {subscription.alert?.title || 'Unknown Alert'}
                       </Typography>
                       <Chip 
                         label={subscription.alert?.status || 'OPENED'}
                         size="small"
                         sx={{ 
                           bgcolor: statusColors[subscription.alert?.status || 'OPENED'],
                           color: subscription.alert?.status === 'SEEN' ? '#333' : 'white',
                           fontWeight: 'bold'
                         }}
                       />
                     </Stack>
                     
                     {subscription.alert?.petName && (
                       <Typography variant="body2" color="text.secondary" sx={{ mb: 0.5 }}>
                         Pet: {subscription.alert.petName}
                       </Typography>
                     )}
                     
                     <Typography variant="caption" color="text.secondary">
                       Subscribed: {formatDate(subscription.subscribedAt)}
                     </Typography>
                   </Box>
                   
                   <Stack direction="row" spacing={1} sx={{ flexShrink: 0 }}>
                      <Button
                        size="small"
                        variant="text"
                        color="primary"
                        onClick={() => navigate(`/alerts/${subscription.alertId}`)}
                        startIcon={<FaArrowRight />}
                      >
                        View
                      </Button>
                     <Button
                       size="small"
                       variant="text"
                       color="error"
                       onClick={() => handleUnsubscribe(subscription.alertId)}
                       disabled={unsubscribingId === subscription.alertId}
                       startIcon={<GiTrashCan />}
                     >
                       Unsubscribe
                     </Button>
                   </Stack>
                 </Box>
               </CardContent>
             </Card>
           ))}
        </Stack>
      )}
    </Paper>
  )
}
