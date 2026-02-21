import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Box, Heading, VStack, HStack, Card, Table, Button, Flex, Spinner,
  Badge, Text, Input, Select, SimpleGrid, Icon
} from '@chakra-ui/react'
import { FaSearch, FaTimes, FaEye } from 'react-icons/fa'
import { useAuth } from '../../context/AuthContext'
import { alertService } from '../../services/alert.service'
import { useMetadata } from '../../hooks/useMetadata'
import { Alert, AlertStatus, MetadataDto } from '../../types'

interface FilterState {
  status: string
  species: string
  createdFrom: string
  createdTo: string
  updatedFrom: string
  updatedTo: string
}

const statusColors: Record<string, string> = {
  OPENED: 'red',
  SEEN: 'yellow',
  SAFE: 'green',
  CLOSED: 'gray',
}

export default function AdminDashboard() {
  const { isAdmin } = useAuth()
  const navigate = useNavigate()
  const [alerts, setAlerts] = useState<Alert[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const { metadata } = useMetadata('StatusNames')
  const { metadata: speciesMetadata } = useMetadata('Species')
  
  const [filters, setFilters] = useState<FilterState>({
    status: '',
    species: '',
    createdFrom: '',
    createdTo: '',
    updatedFrom: '',
    updatedTo: ''
  })

  const fetchAlerts = useCallback(async () => {
    setIsLoading(true)
    try {
      // Build filter params - only include non-empty values
      const filterParams: Record<string, string> = {}
      
      if (filters.status) filterParams.status = filters.status
      if (filters.species) filterParams.species = filters.species
      if (filters.createdFrom) filterParams.createdFrom = filters.createdFrom + 'T00:00:00'
      if (filters.createdTo) filterParams.createdTo = filters.createdTo + 'T23:59:59'
      if (filters.updatedFrom) filterParams.updatedFrom = filters.updatedFrom + 'T00:00:00'
      if (filters.updatedTo) filterParams.updatedTo = filters.updatedTo + 'T23:59:59'
      
      const data = await alertService.searchAlertsWithFilters(filterParams)
      setAlerts(data)
    } catch (error) {
      console.error('Error fetching alerts:', error)
    } finally {
      setIsLoading(false)
    }
  }, [filters])

  useEffect(() => {
    // Redirect non-admin users
    if (!isAdmin()) {
      navigate('/dashboard')
      return
    }
    
    fetchAlerts()
  }, [isAdmin, navigate, fetchAlerts])

  const handleFilterChange = (field: keyof FilterState, value: string) => {
    setFilters(prev => ({ ...prev, [field]: value }))
  }

  const clearFilters = () => {
    setFilters({
      status: '',
      species: '',
      createdFrom: '',
      createdTo: '',
      updatedFrom: '',
      updatedTo: ''
    })
  }

  const formatDate = (dateString: string) => {
    if (!dateString) return '-'
    try {
      const date = new Date(dateString)
      return date.toLocaleDateString() + ' ' + date.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })
    } catch {
      return dateString
    }
  }

  const getStatusDisplayName = (status: string): string => {
    const meta = metadata?.find((m: MetadataDto) => m.value === status)
    return meta?.displayName || status
  }

  const getSpeciesDisplayName = (species: string): string => {
    const meta = speciesMetadata?.find((m: MetadataDto) => m.value === species)
    return meta?.displayName || species
  }

  const hasActiveFilters = Object.values(filters).some(v => v !== '')

  return (
    <VStack gap={6} align="stretch">
      {/* Header */}
      <Box>
        <Heading size="lg" color="gray.800" _dark={{ color: 'white' }}>
          Admin Dashboard - Alert Management
        </Heading>
        <Text color="gray.500" mt={1}>
          View and filter all alerts in the system
        </Text>
      </Box>

      {/* Filter Panel */}
      <Card.Root p={6} boxShadow="sm">
        <VStack gap={4} align="stretch">
          <Heading size="sm" color="gray.700" _dark={{ color: 'gray.300' }}>
            Filters
          </Heading>
          
          <SimpleGrid columns={{ base: 1, md: 2, lg: 4 }} gap={4}>
            {/* Status Filter */}
            <Box>
              <Text fontSize="sm" fontWeight="medium" mb={1} color="gray.600" _dark={{ color: 'gray.400' }}>
                Status
              </Text>
              <Select
                value={filters.status}
                onChange={(e) => handleFilterChange('status', e.target.value)}
                placeholder="All statuses"
              >
                {metadata?.map((item: MetadataDto) => (
                  <option key={item.value} value={item.value}>
                    {item.displayName}
                  </option>
                ))}
              </Select>
            </Box>

            {/* Species Filter */}
            <Box>
              <Text fontSize="sm" fontWeight="medium" mb={1} color="gray.600" _dark={{ color: 'gray.400' }}>
                Species
              </Text>
              <Select
                value={filters.species}
                onChange={(e) => handleFilterChange('species', e.target.value)}
                placeholder="All species"
              >
                {speciesMetadata?.map((item: MetadataDto) => (
                  <option key={item.value} value={item.value}>
                    {item.displayName}
                  </option>
                ))}
              </Select>
            </Box>

            {/* Created Date From */}
            <Box>
              <Text fontSize="sm" fontWeight="medium" mb={1} color="gray.600" _dark={{ color: 'gray.400' }}>
                Created From
              </Text>
              <Input
                type="date"
                value={filters.createdFrom}
                onChange={(e) => handleFilterChange('createdFrom', e.target.value)}
              />
            </Box>

            {/* Created Date To */}
            <Box>
              <Text fontSize="sm" fontWeight="medium" mb={1} color="gray.600" _dark={{ color: 'gray.400' }}>
                Created To
              </Text>
              <Input
                type="date"
                value={filters.createdTo}
                onChange={(e) => handleFilterChange('createdTo', e.target.value)}
              />
            </Box>

            {/* Updated Date From */}
            <Box>
              <Text fontSize="sm" fontWeight="medium" mb={1} color="gray.600" _dark={{ color: 'gray.400' }}>
                Last Update From
              </Text>
              <Input
                type="date"
                value={filters.updatedFrom}
                onChange={(e) => handleFilterChange('updatedFrom', e.target.value)}
              />
            </Box>

            {/* Updated Date To */}
            <Box>
              <Text fontSize="sm" fontWeight="medium" mb={1} color="gray.600" _dark={{ color: 'gray.400' }}>
                Last Update To
              </Text>
              <Input
                type="date"
                value={filters.updatedTo}
                onChange={(e) => handleFilterChange('updatedTo', e.target.value)}
              />
            </Box>
          </SimpleGrid>

          {/* Action Buttons */}
          <HStack justify="flex-end" gap={3}>
            {hasActiveFilters && (
              <Button variant="outline" onClick={clearFilters}>
                <Icon as={FaTimes} mr={2} />
                Clear Filters
              </Button>
            )}
            <Button colorScheme="purple" onClick={fetchAlerts}>
              <Icon as={FaSearch} mr={2} />
              Search
            </Button>
          </HStack>
        </VStack>
      </Card.Root>

      {/* Results */}
      <Card.Root p={6} boxShadow="sm">
        <VStack gap={4} align="stretch">
          <Flex justify="space-between" align="center">
            <Heading size="sm" color="gray.700" _dark={{ color: 'gray.300' }}>
              Results ({alerts.length} alerts)
            </Heading>
          </Flex>

          {isLoading ? (
            <Flex justify="center" align="center" minH="200px">
              <Spinner size="xl" color="purple.500" />
            </Flex>
          ) : alerts.length === 0 ? (
            <Flex justify="center" align="center" minH="200px">
              <Text color="gray.500">No alerts found matching your criteria</Text>
            </Flex>
          ) : (
            <Box overflowX="auto">
              <Table.Root>
                <Table.Header>
                  <Table.Row>
                    <Table.ColumnHeader>Status</Table.ColumnHeader>
                    <Table.ColumnHeader>Title</Table.ColumnHeader>
                    <Table.ColumnHeader>Species</Table.ColumnHeader>
                    <Table.ColumnHeader>Created</Table.ColumnHeader>
                    <Table.ColumnHeader>Last Updated</Table.ColumnHeader>
                    <Table.ColumnHeader textAlign="center">Actions</Table.ColumnHeader>
                  </Table.Row>
                </Table.Header>
                <Table.Body>
                  {alerts.map((alert) => (
                    <Table.Row key={alert.id}>
                      <Table.Cell>
                        <Badge colorPalette={statusColors[alert.status] || 'gray'}>
                          {getStatusDisplayName(alert.status)}
                        </Badge>
                      </Table.Cell>
                      <Table.Cell>
                        <Text fontWeight="medium">{alert.title}</Text>
                        <Text fontSize="sm" color="gray.500" noOfLines={1}>
                          {alert.description}
                        </Text>
                      </Table.Cell>
                      <Table.Cell>
                        <Text>{alert.petId ? 'Pet' : '-'}</Text>
                      </Table.Cell>
                      <Table.Cell>
                        <Text fontSize="sm">{formatDate(alert.createdAt)}</Text>
                      </Table.Cell>
                      <Table.Cell>
                        <Text fontSize="sm">{formatDate(alert.updatedAt || alert.createdAt)}</Text>
                      </Table.Cell>
                      <Table.Cell textAlign="center">
                        <Button
                          size="sm"
                          variant="ghost"
                          colorScheme="purple"
                          onClick={() => navigate(`/alerts/${alert.id}`)}
                        >
                          <Icon as={FaEye} />
                        </Button>
                      </Table.Cell>
                    </Table.Row>
                  ))}
                </Table.Body>
              </Table.Root>
            </Box>
          )}
        </VStack>
      </Card.Root>
    </VStack>
  )
}