import { useEffect, useState } from 'react'
import { Box, Typography, Button, Stack, CircularProgress, Chip, Card, CardContent, Alert as MuiAlert, Tabs, Tab, Paper, Divider } from '@mui/material'
import { useParams, useNavigate } from 'react-router-dom'
import { FaArrowLeft, FaMapMarkerAlt, FaEdit, FaTrash, FaCalendar, FaDirections } from 'react-icons/fa'

import { alertService } from '../../services/alert.service'
import { userService } from '../../services/user.service'
import { petService } from '../../services/pet.service'
import type { Alert as AlertType, ErrorResponse, AlertStatus, AlertEvent } from '../../types'
import { useAuth } from '../../context/AuthContext'
import { useLocation } from '../../hooks/useLocation'
import { ClosureReasonDialog, ClosureReason } from '../../components/alerts/ClosureReasonDialog'
import { StatusLocationDialog } from '../../components/alerts/StatusLocationDialog'
import { AlertEventsList } from '../../components/alerts/AlertEventsList'
import AlertRouteMap from '../../components/map/AlertRouteMap'
import { SubscribeButton } from '../../components/alerts/SubscribeButton'
import { extractError, showSuccessToast, showErrorToast } from '../../utils/errorUtils'

const statusColors: Record<string, string> = {
  OPENED: '#b34045',
  CLOSED: '#4091d7',
  SEEN: '#fecf6d',
  SAFE: '#2d884d',
}

export default function AlertDetail() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { user, isAdmin } = useAuth()
  const location = useLocation()
  
  const [alert, setAlert] = useState<AlertType | null>(null)
  const [events, setEvents] = useState<AlertEvent[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [isLoadingEvents, setIsLoadingEvents] = useState(false)
  const [error, setError] = useState<ErrorResponse | null>(null)
  const [isUpdatingStatus, setIsUpdatingStatus] = useState(false)
  const [isDeleting, setIsDeleting] = useState(false)
  const [showClosureDialog, setShowClosureDialog] = useState(false)
  const [statusLocationDialog, setStatusLocationDialog] = useState<{
    isOpen: boolean
    status: 'SEEN' | 'SAFE' | null
  }>({ isOpen: false, status: null })
  const [userName, setUserName] = useState<string>('Unknown')
  const [workingPetName, setWorkingPetName] = useState<string>('Unknown')
  const [tabValue, setTabValue] = useState(0)

  // Authorization check: admin or alert owner can modify
  const canModify = user && (isAdmin() || alert?.userId === user.userId)
  
  // For CLOSED alerts, only admin can edit
  const isClosedAlert = alert?.status === 'CLOSED'
  const canEdit = canModify && (!isClosedAlert || isAdmin())

  useEffect(() => {
    const fetchAlert = async () => {
      try {
        if (id) {
          const data = await alertService.getAlert(id)
          setAlert(data)
          
          // Fetch user name if userId is available
          if (data.userId) {
            try {
              const userData = await userService.getUser(data.userId)
              setUserName(userData.username || 'Unknown')
            } catch (userError) {
              console.error('Error fetching user:', userError)
              setUserName('Unknown')
            }
          }
          
          // Fetch pet name if petId is available
          if (data.petId) {
            try {
              const petData = await petService.getPet(data.petId)
              setWorkingPetName(petData.workingPetName || petData.officialPetName || 'Unknown')
            } catch (petError) {
              console.error('Error fetching pet:', petError)
              setWorkingPetName('Unknown')
            }
          }
          
          // Also fetch events
          setIsLoadingEvents(true)
          try {
            const eventsData = await alertService.getAlertEvents(id)
            setEvents(eventsData)
          } catch (eventsError) {
            console.error('Error fetching events:', eventsError)
          } finally {
            setIsLoadingEvents(false)
          }
        }
      } catch (error) {
        console.error('Error fetching alert:', error)
      } finally {
        setIsLoading(false)
      }
    }
    fetchAlert()
  }, [id])

  // Handler for opening the status location dialog
  const handleOpenStatusDialog = async (targetStatus: 'SEEN' | 'SAFE') => {
    // Auto-detect user's GPS location before opening the dialog
    if (location.latitude === null || location.longitude === null) {
      try {
        await location.detectLocation()
      } catch (e) {
        // If detection fails, dialog will still open with alert's location as fallback
        console.warn('Could not auto-detect location:', e)
      }
    }
    setStatusLocationDialog({ isOpen: true, status: targetStatus })
  }

  // Handler for confirming location in the dialog
  const handleStatusLocationConfirm = async (latitude: number, longitude: number) => {
    if (!user || !statusLocationDialog.status || !id) return
    
    setIsUpdatingStatus(true)
    setError(null)
    
    try {
      await alertService.updateAlertStatus(id, {
        newStatus: statusLocationDialog.status as AlertStatus,
        userId: user.userId,
        latitude,
        longitude
      })
      
      // Refresh alert data and events
      const updatedAlert = await alertService.getAlert(id)
      setAlert(updatedAlert)
      
      // Also refresh the events list
      const eventsData = await alertService.getAlertEvents(id)
      setEvents(eventsData)
      
      showSuccessToast('Status Updated', `Alert marked as ${statusLocationDialog.status.toLowerCase()}`)
    } catch (err: unknown) {
      console.error('Error updating status:', err)
      const friendlyError = extractError(err)
      setError(friendlyError)
      showErrorToast(err)
    } finally {
      setIsUpdatingStatus(false)
      setStatusLocationDialog({ isOpen: false, status: null })
    }
  }

  const handleCloseWithReason = async (reason: ClosureReason) => {
    if (!user || !id) return
    
    setShowClosureDialog(false)
    setIsUpdatingStatus(true)
    setError(null)
    
    try {
      // Get location - use current or detect new
      let lat = location.latitude
      let lng = location.longitude
      
      if (lat === null || lng === null) {
        // Detect location and use returned values directly
        const result = await location.detectLocation()
        lat = result.latitude
        lng = result.longitude
      }
      
      // Check if location was successfully obtained
      if (lat === null || lng === null) {
        setError({
          status: 400,
          error: 'Location Required',
          message: 'Unable to get your location. Please enable location services or enter coordinates manually.'
        })
        return
      }
      
      await alertService.closeAlert(id, {
        userId: user.userId,
        latitude: lat,
        longitude: lng,
        closureReason: reason
      })
      
      // Refresh alert data
      const updatedAlert = await alertService.getAlert(id)
      setAlert(updatedAlert)
      
      showSuccessToast('Alert Closed', `Alert has been closed with reason: ${reason}`)
    } catch (err: unknown) {
      console.error('Error closing alert:', err)
      const friendlyError = extractError(err)
      setError(friendlyError)
      showErrorToast(err)
    } finally {
      setIsUpdatingStatus(false)
    }
  }

  const handleDelete = async () => {
    if (!id) return
    
    if (!window.confirm('Are you sure you want to delete this alert? This action cannot be undone.')) {
      return
    }
    
    setIsDeleting(true)
    setError(null)
    
    try {
      await alertService.deleteAlert(id)
      
      showSuccessToast('Alert Deleted', 'The alert has been successfully deleted.')
      
      navigate('/alerts', { replace: true })
    } catch (err: unknown) {
      console.error('Error deleting alert:', err)
      const friendlyError = extractError(err)
      setError(friendlyError)
      showErrorToast(err)
    } finally {
      setIsDeleting(false)
    }
  }

  // Handler for detecting location in the dialog
  const handleDetectLocation = async () => {
    const result = await location.detectLocation()
    return { latitude: result.latitude!, longitude: result.longitude! }
  }

  // Get initial location for the status dialog (prioritize user's GPS location)
  const getStatusDialogInitialLocation = () => {
    // For status changes (SEEN/SAFE), prioritize user's current GPS location
    // since we want to record WHERE the user saw/found the pet
    if (location.latitude != null && location.longitude != null) {
      return { latitude: location.latitude, longitude: location.longitude }
    }
    // Fallback to alert's existing location if GPS is not available
    if (alert?.latitude != null && alert?.longitude != null) {
      return { latitude: alert.latitude, longitude: alert.longitude }
    }
    return null
  }

  if (isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '300px' }}>
        <CircularProgress color="primary" />
      </Box>
    )
  }

  if (!alert) {
    return (
      <Box sx={{ textAlign: 'center', py: 5 }}>
        <Typography color="text.secondary">Alert not found</Typography>
        <Button sx={{ mt: 2 }} variant="outlined" color="primary" onClick={() => navigate('/alerts')}>
          Back to Alerts
        </Button>
      </Box>
    )
  }

  return (
    <Paper sx={{ maxWidth: '100%', mx: 'auto', bgcolor: 'rgba(255, 255, 255, 0.85)', p: 3, borderRadius: 2, boxShadow: 3 }}>
      <Button variant="text" sx={{ mb: 2 }} onClick={() => navigate('/alerts')} startIcon={<FaArrowLeft />}>
        Back to Alerts
      </Button>

      {error && (
        <MuiAlert severity="error" sx={{ mb: 3 }}>
          <Typography variant="subtitle2" component="div">{error.error || 'Error'}</Typography>
          <Typography variant="body2">{error.message}</Typography>
        </MuiAlert>
      )}

      <Box sx={{ display: 'flex', gap: 3, flexDirection: { xs: 'column', lg: 'row' } }}>
        {/* Left Column - Alert Details */}
        <Box sx={{ flex: { xs: '1', lg: '0 0 45%' }, minWidth: 0 }}>
          <Card elevation={2}>
            <CardContent>
               <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', mb: 2 }}>
                  <Box>
                     <Typography variant="h5" color="text.primary">
                       {alert.title}
                     </Typography>
                     <Typography color="text.secondary" sx={{ mt: 0.5, fontSize: '0.875rem' }}>
                        Created by: <Box component="span" fontWeight={600}>{userName}</Box>
                      </Typography>
                       <Typography color="text.secondary" sx={{ fontSize: '0.875rem' }}>
                         Pet: <Box component="span" fontWeight={600} sx={{ cursor: 'pointer', '&:hover': { color: 'primary.main', textDecoration: 'underline' } }} onClick={() => {
                           if (alert.petId) {
                             navigate(`/pets/${alert.petId}`)
                           }
                         }}>{workingPetName}</Box>
                       </Typography>
                  </Box>
                 <Chip 
                   label={alert.status}
                   sx={{ 
                     bgcolor: statusColors[alert.status],
                     color: 'white',
                     fontWeight: 'bold'
                   }}
                 />
               </Box>

              <Stack spacing={2}>
                <Box>
                  <Typography fontWeight="bold" mb={0.5} color="text.secondary">
                    Description
                  </Typography>
                  <Typography>{alert.description}</Typography>
                </Box>

                <Box>
                  <Typography fontWeight="bold" mb={0.5} color="text.secondary">
                    Location
                  </Typography>
                  <Stack direction="row" spacing={1} color="text.secondary">
                    <FaMapMarkerAlt />
                    <Typography>
                      {alert.latitude != null ? alert.latitude.toFixed(6) : 'N/A'}, {alert.longitude != null ? alert.longitude.toFixed(6) : 'N/A'}
                    </Typography>
                  </Stack>
                </Box>

                {alert.closureReason && (
                  <Box>
                    <Typography fontWeight="bold" mb={0.5} color="text.secondary">
                      Closure Reason
                    </Typography>
                    <Typography>{alert.closureReason}</Typography>
                  </Box>
                )}

                {/* Edit/Delete Buttons - Only visible to authorized users */}
                {canModify && (
                  <Box sx={{ pt: 1, pb: 1, borderTop: '1px solid', borderColor: 'divider' }}>
                    <Stack direction="row" spacing={1}>
                      {canEdit && (
                        <Button
                          size="small"
                          variant="contained"
                          sx={{ bgcolor: '#4682B4', '&:hover': { bgcolor: '#3a6d96' } }}
                          onClick={() => navigate(`/alerts/${id}/edit`)}
                          startIcon={<FaEdit />}
                        >
                          Edit Alert
                        </Button>
                      )}
                      <Button
                        size="small"
                        variant="contained"
                        color="error"
                        onClick={handleDelete}
                        disabled={isDeleting}
                        startIcon={<FaTrash />}
                      >
                        Delete Alert
                      </Button>
                    </Stack>
                    {isClosedAlert && !isAdmin() && canModify && (
                      <Typography variant="caption" color="text.secondary" sx={{ mt: 1 }}>
                        Closed alerts can only be edited by administrators
                      </Typography>
                    )}
                  </Box>
                )}

                <Box sx={{ pt: 2, borderTop: '1px solid', borderColor: 'divider' }}>
                  <Typography fontWeight="bold" mb={1.5} color="text.secondary">
                    Update Status
                  </Typography>
                  <Stack direction="row" spacing={1} flexWrap="wrap" useFlexGap>
                    {/* Mark as Seen - available for OPENED or SEEN status (any authenticated user) */}
                    <Button
                      size="small"
                      variant="contained"
                      sx={{ bgcolor: '#fecf6d', color: 'text.primary', '&:hover': { bgcolor: '#e6b85c' } }}
                      onClick={() => handleOpenStatusDialog('SEEN')}
                      disabled={alert.status !== 'OPENED' && alert.status !== 'SEEN' || isUpdatingStatus}
                      title={alert.status === 'OPENED' || alert.status === 'SEEN' ? '' : 'Only available for OPENED or SEEN status'}
                    >
                      {isUpdatingStatus ? <CircularProgress size={16} color="inherit" /> : 'Mark as Seen'}
                    </Button>
                    {/* Mark as Safe - available for OPENED or SEEN status (any authenticated user) */}
                    <Button
                      size="small"
                      variant="contained"
                      sx={{ bgcolor: '#2d884d', color: 'white', '&:hover': { bgcolor: '#246b3e' } }}
                      onClick={() => handleOpenStatusDialog('SAFE')}
                      disabled={alert.status !== 'OPENED' && alert.status !== 'SEEN' || isUpdatingStatus}
                      title={alert.status === 'OPENED' || alert.status === 'SEEN' ? '' : 'Only available for OPENED or SEEN status'}
                    >
                      {isUpdatingStatus ? <CircularProgress size={16} color="inherit" /> : 'Mark as Safe'}
                    </Button>
                    {/* Mark as Closed - available for OPENED, SEEN, or SAFE status (owner/admin only) */}
                    {(() => {
                      const canCloseStatus = alert.status === 'OPENED' || alert.status === 'SEEN' || alert.status === 'SAFE'
                      const closeTitle = !canModify
                        ? 'Only alert owner or admin can close'
                        : !canCloseStatus
                          ? 'Only available for OPENED, SEEN, or SAFE status'
                          : ''
                      return (
                        <Button
                          size="small"
                          variant="contained"
                          sx={{ bgcolor: '#4091d7', color: 'white', '&:hover': { bgcolor: '#3578b3' } }}
                          onClick={() => setShowClosureDialog(true)}
                          disabled={!canModify || !canCloseStatus || isUpdatingStatus}
                          title={closeTitle}
                        >
                          {isUpdatingStatus ? <CircularProgress size={16} color="inherit" /> : 'Mark as Closed'}
                        </Button>
                      )
                    })()}
                  </Stack>
                </Box>

                {/* Subscription Section - Always visible */}
                <Box sx={{ pt: 2, borderTop: '1px solid', borderColor: 'divider' }}>
                  <Typography fontWeight="bold" mb={1.5} color="text.secondary">
                    Notifications
                  </Typography>
                  <SubscribeButton
                    alertId={alert.id}
                    alertStatus={alert.status}
                    showStatus
                  />
                </Box>
              </Stack>
            </CardContent>
          </Card>
        </Box>

        {/* Right Column - Events Section */}
        <Box sx={{ flex: { xs: '1', lg: '0 0 55%' }, minWidth: 0 }}>
          <Paper elevation={2}>
            <Tabs value={tabValue} onChange={(_, newValue) => setTabValue(newValue)}>
              <Tab icon={<FaDirections />} iconPosition="start" label="Route Map" />
              <Tab icon={<FaCalendar />} iconPosition="start" label="History" />
            </Tabs>

            <Box sx={{ p: 0 }}>
              {tabValue === 0 && (
                <AlertRouteMap events={events} />
              )}
              {tabValue === 1 && (
                <AlertEventsList events={events} isLoading={isLoadingEvents} />
              )}
            </Box>
          </Paper>
        </Box>
      </Box>

      <ClosureReasonDialog
        open={showClosureDialog}
        onOpenChange={setShowClosureDialog}
        onConfirm={handleCloseWithReason}
        isLoading={isUpdatingStatus}
      />

      <StatusLocationDialog
        open={statusLocationDialog.isOpen}
        onOpenChange={(open) => setStatusLocationDialog({ isOpen: open, status: open ? statusLocationDialog.status : null })}
        onConfirm={handleStatusLocationConfirm}
        status={statusLocationDialog.status}
        alertTitle={alert?.title || ''}
        initialLocation={getStatusDialogInitialLocation()}
        isDetectingLocation={location.isLoading}
        onDetectLocation={handleDetectLocation}
        isLoading={isUpdatingStatus}
      />
    </Paper>
  )
}
