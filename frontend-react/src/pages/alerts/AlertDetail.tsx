import { useEffect, useState } from 'react'
import { Box, Heading, Button, VStack, Text, Flex, Spinner, Badge, HStack, Card, Alert, Tabs } from '@chakra-ui/react'
import { useParams, useNavigate } from 'react-router-dom'
import { FaArrowLeft, FaMapMarkerAlt, FaEdit, FaTrash, FaRoute, FaHistory } from 'react-icons/fa'
import { alertService } from '../../services/alert.service'
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
  OPENED: 'red',
  CLOSED: 'green',
  SEEN: 'yellow',
  SAFE: 'blue',
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
      <Flex justify="center" align="center" minH="300px">
        <Spinner size="xl" color="purple.500" />
      </Flex>
    )
  }

  if (!alert) {
    return (
      <Box textAlign="center" py={10}>
        <Text color="gray.500">Alert not found</Text>
        <Button mt={4} colorScheme="purple" variant="outline" onClick={() => navigate('/alerts')}>
          Back to Alerts
        </Button>
      </Box>
    )
  }

  return (
    <Box maxW="800px" mx="auto">
      <Button variant="ghost" mb={4} onClick={() => navigate('/alerts')}>
        <FaArrowLeft style={{ marginRight: '8px' }} />
        Back to Alerts
      </Button>

      {error && (
        <Alert.Root status="error" mb={6} borderRadius="md">
          <Alert.Indicator />
          <Box flex="1">
            <Alert.Title fontSize="sm" fontWeight="bold">
              {error.error || 'Error'}
            </Alert.Title>
            <Alert.Description fontSize="sm">
              {error.message}
            </Alert.Description>
          </Box>
        </Alert.Root>
      )}

      <Card.Root>
        <Card.Body>
          <Flex justify="space-between" align="start" mb={4}>
            <Box>
              <Heading size="lg" color="gray.800" _dark={{ color: 'white' }}>
                {alert.title}
              </Heading>
              <Text color="gray.500" mt={1}>
                Created for pet ID: {alert.petId}
              </Text>
            </Box>
            <Badge colorPalette={statusColors[alert.status]} fontSize="md" px={3} py={1}>
              {alert.status}
            </Badge>
          </Flex>

          <VStack align="stretch" gap={4}>
            <Box>
              <Text fontWeight="bold" mb={1} color="gray.600">
                Description
              </Text>
              <Text>{alert.description}</Text>
            </Box>

            <Box>
              <Text fontWeight="bold" mb={1} color="gray.600">
                Location
              </Text>
              <HStack color="gray.500">
                <FaMapMarkerAlt />
                <Text>
                  {alert.latitude != null ? alert.latitude.toFixed(6) : 'N/A'}, {alert.longitude != null ? alert.longitude.toFixed(6) : 'N/A'}
                </Text>
              </HStack>
            </Box>

            {alert.closureReason && (
              <Box>
                <Text fontWeight="bold" mb={1} color="gray.600">
                  Closure Reason
                </Text>
                <Text>{alert.closureReason}</Text>
              </Box>
            )}

            {/* Edit/Delete Buttons - Only visible to authorized users */}
            {canModify && (
              <Box pt={2} pb={2} borderTop="1px" borderColor="gray.200">
                <HStack gap={2}>
                  {canEdit && (
                    <Button
                      size="sm"
                      colorPalette="orange"
                      onClick={() => navigate(`/alerts/${id}/edit`)}
                    >
                      <FaEdit style={{ marginRight: '4px' }} />
                      Edit Alert
                    </Button>
                  )}
                  <Button
                    size="sm"
                    colorPalette="red"
                    onClick={handleDelete}
                    loading={isDeleting}
                  >
                    <FaTrash style={{ marginRight: '4px' }} />
                    Delete Alert
                  </Button>
                </HStack>
                {isClosedAlert && !isAdmin() && canModify && (
                  <Text fontSize="xs" color="gray.500" mt={2}>
                    Closed alerts can only be edited by administrators
                  </Text>
                )}
              </Box>
            )}

            <Box pt={4} borderTop="1px" borderColor="gray.200">
              <Text fontWeight="bold" mb={3} color="gray.600">
                Update Status
              </Text>
              <HStack gap={2} wrap="wrap">
                {/* Mark as Seen - available for OPENED or SEEN status (any authenticated user) */}
                <Button 
                  size="sm" 
                  colorPalette="yellow" 
                  onClick={() => handleOpenStatusDialog('SEEN')} 
                  disabled={alert.status !== 'OPENED' && alert.status !== 'SEEN' || isUpdatingStatus}
                  title={alert.status === 'OPENED' || alert.status === 'SEEN' ? '' : 'Only available for OPENED or SEEN status'}
                >
                  {isUpdatingStatus ? <Spinner size="sm" /> : 'Mark as Seen'}
                </Button>
                {/* Mark as Safe - available for OPENED or SEEN status (any authenticated user) */}
                <Button 
                  size="sm" 
                  colorPalette="blue" 
                  onClick={() => handleOpenStatusDialog('SAFE')} 
                  disabled={alert.status !== 'OPENED' && alert.status !== 'SEEN' || isUpdatingStatus}
                  title={alert.status === 'OPENED' || alert.status === 'SEEN' ? '' : 'Only available for OPENED or SEEN status'}
                >
                  {isUpdatingStatus ? <Spinner size="sm" /> : 'Mark as Safe'}
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
                      size="sm" 
                      colorPalette="green" 
                      onClick={() => setShowClosureDialog(true)} 
                      disabled={!canModify || !canCloseStatus || isUpdatingStatus}
                      title={closeTitle}
                    >
                      {isUpdatingStatus ? <Spinner size="sm" /> : 'Mark as Closed'}
                    </Button>
                  )
                })()}
              </HStack>
            </Box>

            {/* Subscription Section - Always visible */}
            <Box pt={4} borderTop="1px" borderColor="gray.200">
              <Text fontWeight="bold" mb={3} color="gray.600">
                Notifications
              </Text>
              <SubscribeButton
                alertId={alert.id}
                alertStatus={alert.status}
                showStatus
              />
            </Box>
          </VStack>
        </Card.Body>
      </Card.Root>

      {/* Events Section - Timeline and Route Map */}
      <Box mt={6}>
        <Tabs.Root defaultValue="history" variant="line">
          <Tabs.List bg="white" borderBottom="2px" borderColor="gray.200" mb={4}>
            <Tabs.Trigger value="history" px={4} py={2} fontWeight="medium" color="gray.600">
              <FaHistory style={{ marginRight: '8px' }} />
              History
            </Tabs.Trigger>
            <Tabs.Trigger value="route" px={4} py={2} fontWeight="medium" color="gray.600">
              <FaRoute style={{ marginRight: '8px' }} />
              Route Map
            </Tabs.Trigger>
          </Tabs.List>

          <Tabs.Content value="history">
            <AlertEventsList events={events} isLoading={isLoadingEvents} />
          </Tabs.Content>

          <Tabs.Content value="route">
            <AlertRouteMap events={events} />
          </Tabs.Content>
        </Tabs.Root>
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
    </Box>
  )
}
