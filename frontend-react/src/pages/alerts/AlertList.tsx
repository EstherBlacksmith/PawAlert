import { useEffect, useState, useCallback } from 'react'
import { Box, Heading, Button, SimpleGrid, Card, Text, Flex, Spinner, Badge, IconButton, HStack, Collapsible, Input, NativeSelect, Grid, VStack } from '@chakra-ui/react'
import { Link } from 'react-router-dom'
import { FaPlus, FaEye, FaCog, FaTimes, FaMapMarkerAlt, FaSearch } from 'react-icons/fa'
import { alertService } from '../../services/alert.service'
import { Alert, AlertStatus, AlertSearchFilters } from '../../types'
import { SubscribeButton } from '../../components/alerts/SubscribeButton'
import { useMetadata } from '../../hooks/useMetadata'
import { useLocation } from '../../hooks/useLocation'

const statusColors: Record<string, string> = {
  OPENED: 'red',
  CLOSED: 'green',
  SEEN: 'yellow',
  SAFE: 'blue',
}

const statusOptions: { value: string; label: string }[] = [
  { value: '', label: 'All Statuses' },
  { value: 'OPENED', label: 'Opened' },
  { value: 'SEEN', label: 'Seen' },
  { value: 'SAFE', label: 'Safe' },
  { value: 'CLOSED', label: 'Closed' },
]

export default function AlertList() {
  const [alerts, setAlerts] = useState<Alert[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [isFilterOpen, setIsFilterOpen] = useState(false)
  
  // Filter state
  const [status, setStatus] = useState<string>('')
  const [species, setSpecies] = useState<string>('')
  const [petName, setPetName] = useState('')
  const [breed, setBreed] = useState('')
  const [createdFrom, setCreatedFrom] = useState('')
  const [createdTo, setCreatedTo] = useState('')
  const [radiusKm, setRadiusKm] = useState<string>('10')
  
  // Location state
  const [userLatitude, setUserLatitude] = useState<number | null>(null)
  const [userLongitude, setUserLongitude] = useState<number | null>(null)
  const [locationError, setLocationError] = useState<string | null>(null)
  
  // Hooks
  const { metadata: speciesOptions } = useMetadata('Species')
  const { detectLocation, isLoading: isLocationLoading } = useLocation()

  // Count active filters
  const activeFilterCount = [
    status,
    species,
    petName,
    breed,
    createdFrom,
    createdTo,
    userLatitude !== null && userLongitude !== null
  ].filter(Boolean).length

  const fetchAlerts = useCallback(async () => {
    setIsLoading(true)
    try {
      const filters: AlertSearchFilters = {}
      
      if (status) filters.status = status as AlertStatus
      if (petName) filters.petName = petName
      if (species) filters.species = species
      if (breed) filters.breed = breed
      if (createdFrom) filters.createdFrom = createdFrom
      if (createdTo) filters.createdTo = createdTo
      
      // Add location filters if available
      if (userLatitude !== null && userLongitude !== null && radiusKm) {
        filters.latitude = userLatitude
        filters.longitude = userLongitude
        filters.radiusKm = parseFloat(radiusKm)
      }
      
      const data = await alertService.searchAlertsWithFilters(filters)
      setAlerts(data)
    } catch (error) {
      console.error('Error fetching alerts:', error)
    } finally {
      setIsLoading(false)
    }
  }, [status, species, petName, breed, createdFrom, createdTo, userLatitude, userLongitude, radiusKm])

  useEffect(() => {
    fetchAlerts()
  }, []) // Initial load

  const handleApplyFilters = () => {
    fetchAlerts()
  }

  const handleClearFilters = () => {
    setStatus('')
    setSpecies('')
    setPetName('')
    setBreed('')
    setCreatedFrom('')
    setCreatedTo('')
    setRadiusKm('10')
    setUserLatitude(null)
    setUserLongitude(null)
    setLocationError(null)
  }

  const handleGetLocation = async () => {
    setLocationError(null)
    const result = await detectLocation()
    if (result.error) {
      setLocationError(result.error)
    } else if (result.latitude && result.longitude) {
      setUserLatitude(result.latitude)
      setUserLongitude(result.longitude)
    }
  }

  const handleClearLocation = () => {
    setUserLatitude(null)
    setUserLongitude(null)
    setLocationError(null)
  }

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === 'Enter') {
      handleApplyFilters()
    }
  }

  if (isLoading && alerts.length === 0) {
    return (
      <Flex justify="center" align="center" minH="300px">
        <Spinner size="xl" color="brand.500" />
      </Flex>
    )
  }

  return (
    <Box
      bg="rgba(255, 255, 255, 0.85)"
      p={6}
      borderRadius="lg"
      boxShadow="lg"
    >
      <Flex justify="space-between" align="center" mb={6}>
        <Box>
          <Heading size="lg" color="gray.800">
            Alerts
          </Heading>
          <Text color="gray.600" mt={1}>
            View and manage pet alerts
          </Text>
        </Box>
        <HStack gap={3}>
          <Button
            variant="outline"
            onClick={() => setIsFilterOpen(!isFilterOpen)}
            colorPalette={activeFilterCount > 0 ? 'brand' : 'gray'}
            bg={activeFilterCount > 0 ? 'brand.50' : undefined}
            borderColor={activeFilterCount > 0 ? 'brand.500' : undefined}
          >
            <FaCog style={{ marginRight: '8px' }} />
            Filters
            {activeFilterCount > 0 && (
              <Badge ml={2} colorPalette="accent" borderRadius="full">
                {activeFilterCount}
              </Badge>
            )}
          </Button>
          <Link to="/alerts/create">
            <Button colorPalette="brand" bg="brand.500" _hover={{ bg: 'brand.600' }}>
              <FaPlus style={{ marginRight: '8px' }} />
              Create Alert
            </Button>
          </Link>
        </HStack>
      </Flex>

      {/* Filter Panel */}
      <Collapsible.Root open={isFilterOpen} onOpenChange={(e) => setIsFilterOpen(e.open)}>
        <Collapsible.Content>
          <Card.Root mb={6} bg="gray.50" _dark={{ bg: 'gray.800' }}>
            <Card.Body p={4}>
              <VStack align="stretch" gap={4}>
                <Flex justify="space-between" align="center">
                  <Text fontWeight="medium" color="gray.700" _dark={{ color: 'gray.200' }}>
                    Filter Alerts
                  </Text>
                  <Button
                    size="sm"
                    variant="ghost"
                    onClick={handleClearFilters}
                    disabled={!status && !species && !petName && !breed && !createdFrom && !createdTo && userLatitude === null}
                  >
                    <FaTimes style={{ marginRight: '4px' }} />
                    Clear All
                  </Button>
                </Flex>

                <Grid templateColumns={{ base: '1fr', md: 'repeat(3, 1fr)', lg: 'repeat(4, 1fr)' }} gap={4}>
                  {/* Status Filter */}
                  <Box>
                    <Text fontSize="sm" mb={1} color="gray.600" _dark={{ color: 'gray.400' }}>
                      Status
                    </Text>
                    <NativeSelect.Root size="sm">
                      <NativeSelect.Field
                        value={status}
                        onChange={(e) => setStatus(e.target.value)}
                        onKeyPress={handleKeyPress}
                      >
                        {statusOptions.map((option) => (
                          <option key={option.value} value={option.value}>
                            {option.label}
                          </option>
                        ))}
                      </NativeSelect.Field>
                    </NativeSelect.Root>
                  </Box>

                  {/* Species Filter */}
                  <Box>
                    <Text fontSize="sm" mb={1} color="gray.600" _dark={{ color: 'gray.400' }}>
                      Species
                    </Text>
                    <NativeSelect.Root size="sm">
                      <NativeSelect.Field
                        value={species}
                        onChange={(e) => setSpecies(e.target.value)}
                        onKeyPress={handleKeyPress}
                        placeholder="All Species"
                      >
                        <option value="">All Species</option>
                        {speciesOptions?.map((option) => (
                          <option key={option.value} value={option.value}>
                            {option.displayName}
                          </option>
                        ))}
                      </NativeSelect.Field>
                    </NativeSelect.Root>
                  </Box>

                  {/* Pet Name Filter */}
                  <Box>
                    <Text fontSize="sm" mb={1} color="gray.600" _dark={{ color: 'gray.400' }}>
                      Pet Name
                    </Text>
                    <Input
                      placeholder="Search by pet name..."
                      value={petName}
                      onChange={(e) => setPetName(e.target.value)}
                      onKeyPress={handleKeyPress}
                      size="sm"
                    />
                  </Box>

                  {/* Breed Filter */}
                  <Box>
                    <Text fontSize="sm" mb={1} color="gray.600" _dark={{ color: 'gray.400' }}>
                      Breed
                    </Text>
                    <Input
                      placeholder="Search by breed..."
                      value={breed}
                      onChange={(e) => setBreed(e.target.value)}
                      onKeyPress={handleKeyPress}
                      size="sm"
                    />
                  </Box>

                  {/* Date From Filter */}
                  <Box>
                    <Text fontSize="sm" mb={1} color="gray.600" _dark={{ color: 'gray.400' }}>
                      Date From
                    </Text>
                    <Input
                      type="date"
                      value={createdFrom}
                      onChange={(e) => setCreatedFrom(e.target.value)}
                      size="sm"
                    />
                  </Box>

                  {/* Date To Filter */}
                  <Box>
                    <Text fontSize="sm" mb={1} color="gray.600" _dark={{ color: 'gray.400' }}>
                      Date To
                    </Text>
                    <Input
                      type="date"
                      value={createdTo}
                      onChange={(e) => setCreatedTo(e.target.value)}
                      size="sm"
                    />
                  </Box>

                  {/* Distance Filter */}
                  <Box>
                    <Text fontSize="sm" mb={1} color="gray.600" _dark={{ color: 'gray.400' }}>
                      Distance (km)
                    </Text>
                    <Flex gap={2}>
                      <Input
                        type="number"
                        placeholder="Radius"
                        value={radiusKm}
                        onChange={(e) => setRadiusKm(e.target.value)}
                        onKeyPress={handleKeyPress}
                        size="sm"
                        min="1"
                        max="1000"
                        flex="1"
                      />
                      {userLatitude !== null && userLongitude !== null ? (
                        <IconButton
                          aria-label="Clear location"
                          size="sm"
                          variant="outline"
                          colorPalette="red"
                          onClick={handleClearLocation}
                        >
                          <FaTimes />
                        </IconButton>
                      ) : (
                        <IconButton
                          aria-label="Use my location"
                          size="sm"
                          variant="outline"
                          colorPalette="brand"
                          bg="brand.50"
                          borderColor="brand.500"
                          _hover={{ bg: 'brand.100' }}
                          onClick={handleGetLocation}
                          loading={isLocationLoading}
                        >
                          <FaMapMarkerAlt />
                        </IconButton>
                      )}
                    </Flex>
                  </Box>
                </Grid>

                {/* Location Status */}
                {locationError && (
                  <Text fontSize="sm" color="red.500">
                    {locationError}
                  </Text>
                )}
                {userLatitude !== null && userLongitude !== null && (
                  <Text fontSize="sm" color="green.500">
                    <FaMapMarkerAlt style={{ display: 'inline', marginRight: '4px' }} />
                    Location set: {userLatitude.toFixed(4)}, {userLongitude.toFixed(4)}
                  </Text>
                )}

                {/* Apply Button */}
                <Flex justify="flex-end">
                  <Button
                    colorPalette="brand"
                    bg="brand.500"
                    _hover={{ bg: 'brand.600' }}
                    onClick={handleApplyFilters}
                    loading={isLoading}
                    size="sm"
                  >
                    <FaSearch style={{ marginRight: '8px' }} />
                    Apply Filters
                  </Button>
                </Flex>
              </VStack>
            </Card.Body>
          </Card.Root>
        </Collapsible.Content>
      </Collapsible.Root>

      {/* Results */}
      {alerts.length === 0 ? (
        <Card.Root p={8} textAlign="center">
          <Text color="gray.500">No alerts found matching your criteria.</Text>
          <Link to="/alerts/create">
            <Button mt={4} colorPalette="brand" variant="outline" bg="brand.50" _hover={{ bg: 'brand.100' }}>
              Create a new alert
            </Button>
          </Link>
        </Card.Root>
      ) : (
        <>
          <Text fontSize="sm" color="gray.500" mb={4}>
            Showing {alerts.length} alert{alerts.length !== 1 ? 's' : ''}
          </Text>
          <SimpleGrid columns={{ base: 1, md: 2, lg: 3 }} gap={6}>
            {alerts.map((alert) => (
              <Card.Root key={alert.id}>
                <Card.Body>
                  <Flex justify="space-between" align="start" mb={2}>
                    <Heading size="md">{alert.title}</Heading>
                    <Badge colorPalette={statusColors[alert.status]}>{alert.status}</Badge>
                  </Flex>
                  <Text fontSize="sm" color="gray.500" mb={2} lineClamp={2}>
                    {alert.description}
                  </Text>
                  <Text fontSize="xs" color="gray.400">
                    Location: {alert.latitude != null ? alert.latitude.toFixed(4) : 'N/A'}, {alert.longitude != null ? alert.longitude.toFixed(4) : 'N/A'}
                  </Text>
                  <Flex mt={4} gap={2} justify="space-between" align="center">
                    <Link to={`/alerts/${alert.id}`}>
                      <IconButton aria-label="View" variant="ghost" size="sm" color="brand.500">
                        <FaEye />
                      </IconButton>
                    </Link>
                    <SubscribeButton
                      alertId={alert.id}
                      alertStatus={alert.status}
                      size="sm"
                    />
                  </Flex>
                </Card.Body>
              </Card.Root>
            ))}
          </SimpleGrid>
        </>
      )}
    </Box>
  )
}
