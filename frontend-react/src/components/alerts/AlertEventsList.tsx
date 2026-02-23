import { Box, Typography, Chip, CircularProgress, Card, CardContent, Stack } from '@mui/material'
import { GiMapMarker, GiCalendar } from '../icons'
import type { AlertEvent } from '../../types'

interface AlertEventsListProps {
  events: AlertEvent[]
  isLoading?: boolean
}

// Helper to get event type display text
const getEventTypeDisplay = (eventType: string): string => {
  switch (eventType) {
    case 'STATUS_CHANGED':
      return 'Status Changed'
    case 'TITLE_CHANGED':
      return 'Title Changed'
    case 'DESCRIPTION_CHANGED':
      return 'Description Changed'
    default:
      return eventType
  }
}

// Helper to get event color based on type
const getEventColor = (eventType: string): 'default' | 'primary' | 'secondary' | 'error' | 'info' | 'success' | 'warning' => {
  switch (eventType) {
    case 'STATUS_CHANGED':
      return 'info'
    case 'TITLE_CHANGED':
      return 'primary'
    case 'DESCRIPTION_CHANGED':
      return 'warning'
    default:
      return 'default'
  }
}

// Helper to get pastel background color for timeline dot based on new status
const getStatusPastelColor = (newStatus: string | null): string => {
  switch (newStatus) {
    case 'OPENED':
      return '#fecaca' // light red/pink pastel
    case 'SEEN':
      return '#fef08a' // light yellow pastel
    case 'SAFE':
      return '#bbf7d0' // light green pastel
    case 'CLOSED':
      return '#bfdbfe' // light blue pastel
    default:
      return '#f3f4f6' // light gray pastel
  }
}

// Helper to format status change text
const formatStatusChange = (event: AlertEvent): string => {
  if (event.eventType === 'STATUS_CHANGED') {
    if (event.previousStatus && event.newStatus) {
      return `${event.previousStatus} → ${event.newStatus}`
    }
    if (event.newStatus) {
      return `Changed to ${event.newStatus}`
    }
  }
  return ''
}

// Helper to format value change
const formatValueChange = (event: AlertEvent): string | null => {
  if (event.eventType === 'TITLE_CHANGED' || event.eventType === 'DESCRIPTION_CHANGED') {
    if (event.oldValue && event.newValue) {
      return `"${event.oldValue}" → "${event.newValue}"`
    }
    if (event.newValue) {
      return `Changed to: "${event.newValue}"`
    }
  }
  return null
}

// Helper to format date
const formatDate = (dateString: string): string => {
  try {
    const date = new Date(dateString)
    return date.toLocaleString('en-US', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  } catch {
    return dateString
  }
}

export function AlertEventsList({ events, isLoading }: AlertEventsListProps) {
  if (isLoading) {
    return (
      <Box textAlign="center" py={4}>
        <CircularProgress />
        <Typography color="text.secondary" sx={{ mt: 2 }}>Loading events...</Typography>
      </Box>
    )
  }

  if (!events || events.length === 0) {
    return (
      <Card>
        <CardContent>
          <Box textAlign="center" py={3}>
            <GiCalendar size={32} style={{ margin: '0 auto 12px', color: '#9ca3af' }} />
            <Typography color="text.secondary">No events recorded for this alert yet.</Typography>
          </Box>
        </CardContent>
      </Card>
    )
  }

  // Sort events by date (most recent first)
  const sortedEvents = [...events].sort((a, b) => 
    new Date(b.changedAt).getTime() - new Date(a.changedAt).getTime()
  )

  return (
    <Card>
      <CardContent>
        <Typography fontWeight="bold" variant="h6" color="text.primary" sx={{ mb: 2 }}>
          Alert History
        </Typography>
        <Stack spacing={0}>
          {sortedEvents.map((event, index) => (
            <Box key={event.id} position="relative">
              {/* Timeline connector line */}
              {index < sortedEvents.length - 1 && (
                <Box
                  sx={{
                    position: 'absolute',
                    left: '15px',
                    top: '40px',
                    bottom: '-20px',
                    width: '2px',
                    bgcolor: 'divider',
                    zIndex: 0,
                  }}
                />
              )}
              
              <Stack direction="row" alignItems="flex-start" spacing={2} py={1.5}>
                {/* Timeline dot */}
                <Box
                  sx={{
                    minWidth: '32px',
                    height: '32px',
                    borderRadius: '50%',
                    bgcolor: event.eventType === 'STATUS_CHANGED' ? getStatusPastelColor(event.newStatus) : 'action.hover',
                    display: 'flex',
                    alignItems: 'center',
                    justifyContent: 'center',
                    zIndex: 1,
                  }}
                >
                  <GiCalendar size={14} style={{ color: '#9ca3af' }} />
                </Box>
                
                <Box flex={1}>
                  <Stack direction="row" justifyContent="space-between" alignItems="flex-start" mb={0.5}>
                    <Chip
                      label={getEventTypeDisplay(event.eventType)}
                      color={getEventColor(event.eventType)}
                      size="small"
                      sx={{
                        bgcolor: event.eventType === 'STATUS_CHANGED' ? getStatusPastelColor(event.newStatus) : undefined,
                        color: event.eventType === 'STATUS_CHANGED' ? '#6b7280' : undefined,
                      }}
                    />
                    <Typography variant="caption" color="text.secondary">
                      {formatDate(event.changedAt)}
                    </Typography>
                  </Stack>
                  
                  {/* Status change */}
                  {event.eventType === 'STATUS_CHANGED' && (
                    <Typography variant="body2" color="text.primary" fontWeight="medium">
                      {formatStatusChange(event)}
                      {event.closureReason && (
                        <Typography component="span" variant="body2" color="text.secondary" fontStyle="italic">
                          {' '}(Reason: {event.closureReason})
                        </Typography>
                      )}
                    </Typography>
                  )}
                  
                  {/* Value change */}
                  {formatValueChange(event) && (
                    <Typography variant="body2" color="text.secondary">
                      {formatValueChange(event)}
                    </Typography>
                  )}
                  
                  {/* Location */}
                  {event.latitude != null && event.longitude != null && (
                    <Stack direction="row" spacing={0.5} alignItems="center" mt={1} color="text.secondary">
                      <GiMapMarker size={10} />
                      <Typography variant="caption">
                        {event.latitude.toFixed(6)}, {event.longitude.toFixed(6)}
                      </Typography>
                    </Stack>
                  )}
                </Box>
              </Stack>
            </Box>
          ))}
        </Stack>
      </CardContent>
    </Card>
  )
}
