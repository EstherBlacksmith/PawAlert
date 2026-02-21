import { Box, VStack, HStack, Text, Badge, Spinner, Card } from '@chakra-ui/react'
import { FaMapMarkerAlt, FaHistory } from 'react-icons/fa'
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
const getEventColor = (eventType: string): string => {
  switch (eventType) {
    case 'STATUS_CHANGED':
      return 'blue'
    case 'TITLE_CHANGED':
      return 'purple'
    case 'DESCRIPTION_CHANGED':
      return 'orange'
    default:
      return 'gray'
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
      <Box textAlign="center" py={8}>
        <Spinner size="lg" color="purple.500" />
        <Text mt={4} color="gray.500">Loading events...</Text>
      </Box>
    )
  }

  if (!events || events.length === 0) {
    return (
      <Card.Root>
        <Card.Body>
          <Box textAlign="center" py={6}>
            <FaHistory size={32} color="#A0AEC0" style={{ margin: '0 auto 12px' }} />
            <Text color="gray.500">No events recorded for this alert yet.</Text>
          </Box>
        </Card.Body>
      </Card.Root>
    )
  }

  // Sort events by date (most recent first)
  const sortedEvents = [...events].sort((a, b) => 
    new Date(b.changedAt).getTime() - new Date(a.changedAt).getTime()
  )

  return (
    <Card.Root>
      <Card.Body>
        <Text fontWeight="bold" mb={4} fontSize="lg" color="gray.700">
          Alert History
        </Text>
        <VStack align="stretch" gap={0}>
          {sortedEvents.map((event, index) => (
            <Box key={event.id} position="relative">
              {/* Timeline connector line */}
              {index < sortedEvents.length - 1 && (
                <Box
                  position="absolute"
                  left="15px"
                  top="40px"
                  bottom="-20px"
                  width="2px"
                  bg="gray.200"
                  zIndex={0}
                />
              )}
              
              <HStack align="start" gap={4} py={3}>
                {/* Timeline dot */}
                <Box
                  minW="32px"
                  h="32px"
                  borderRadius="full"
                  bg={`${getEventColor(event.eventType)}.100`}
                  display="flex"
                  alignItems="center"
                  justifyContent="center"
                  zIndex={1}
                >
                  <FaHistory size={14} color={`var(--chakra-colors-${getEventColor(event.eventType)}-600)`} />
                </Box>
                
                <Box flex={1}>
                  <HStack justify="space-between" align="start" mb={1}>
                    <Badge colorPalette={getEventColor(event.eventType)} fontSize="sm">
                      {getEventTypeDisplay(event.eventType)}
                    </Badge>
                    <Text fontSize="xs" color="gray.500">
                      {formatDate(event.changedAt)}
                    </Text>
                  </HStack>
                  
                  {/* Status change */}
                  {event.eventType === 'STATUS_CHANGED' && (
                    <Text fontSize="sm" color="gray.700" fontWeight="medium">
                      {formatStatusChange(event)}
                      {event.closureReason && (
                        <Text as="span" color="gray.500" fontStyle="italic">
                          {' '}(Reason: {event.closureReason})
                        </Text>
                      )}
                    </Text>
                  )}
                  
                  {/* Value change */}
                  {formatValueChange(event) && (
                    <Text fontSize="sm" color="gray.600">
                      {formatValueChange(event)}
                    </Text>
                  )}
                  
                  {/* Location */}
                  {event.latitude != null && event.longitude != null && (
                    <HStack mt={2} gap={1} color="gray.500" fontSize="xs">
                      <FaMapMarkerAlt size={10} />
                      <Text>
                        {event.latitude.toFixed(6)}, {event.longitude.toFixed(6)}
                      </Text>
                    </HStack>
                  )}
                </Box>
              </HStack>
            </Box>
          ))}
        </VStack>
      </Card.Body>
    </Card.Root>
  )
}
