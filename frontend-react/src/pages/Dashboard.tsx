import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Box, SimpleGrid, Heading, Text, Card, Icon, VStack, Flex, Spinner } from '@chakra-ui/react'
import { FaPaw, FaBell, FaCheckCircle, FaClock } from 'react-icons/fa'
import { useAuth } from '../context/AuthContext'
import { petService } from '../services/pet.service'
import { alertService } from '../services/alert.service'
import { Alert } from '../types'

interface StatCardProps {
  icon: React.ElementType
  label: string
  value: number
  color: string
  onClick?: () => void
}

function StatCard({ icon, label, value, color, onClick }: StatCardProps) {
  return (
    <Card.Root 
      p={6} 
      boxShadow="sm" 
      cursor={onClick ? "pointer" : "default"}
      _hover={onClick ? { boxShadow: 'md', transform: 'translateY(-2px)' } : undefined}
      transition="all 0.2s"
      onClick={onClick}
    >
      <Flex align="center" gap={4}>
        <Box
          p={3}
          borderRadius="full"
          bg={`${color}.100`}
          _dark={{ bg: `${color}.900` }}
        >
          <Icon as={icon} boxSize={6} color={`${color}.500`} />
        </Box>
        <Box>
          <Text fontSize="3xl" fontWeight="bold" color="gray.800" _dark={{ color: 'white' }}>
            {value}
          </Text>
          <Text fontSize="sm" color="gray.500">
            {label}
          </Text>
        </Box>
      </Flex>
    </Card.Root>
  )
}

export default function Dashboard() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [stats, setStats] = useState({
    totalPets: 0,
    activeAlerts: 0,
    foundPets: 0,
    pendingAlerts: 0,
  })
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const [pets, alerts] = await Promise.all([
          petService.getPets(),
          alertService.getAlerts(),
        ])

        const activeAlerts = alerts.filter((a: Alert) => a.status === 'OPENED').length
        const foundPets = alerts.filter((a: Alert) => a.status === 'CLOSED').length

        setStats({
          totalPets: pets.length,
          activeAlerts,
          foundPets,
          pendingAlerts: alerts.length - activeAlerts - foundPets,
        })
      } catch (error) {
        console.error('Error fetching stats:', error)
      } finally {
        setIsLoading(false)
      }
    }

    fetchStats()
  }, [])

  if (isLoading) {
    return (
      <Flex justify="center" align="center" minH="300px">
        <Spinner size="xl" color="purple.500" />
      </Flex>
    )
  }

  return (
    <VStack gap={6} align="stretch">
      {/* Welcome Section */}
      <Box>
        <Heading size="lg" color="gray.800" _dark={{ color: 'white' }}>
          Welcome back, {user?.username}!
        </Heading>
        <Text color="gray.500" mt={1}>
          Here's an overview of your pet alerts
        </Text>
      </Box>

      {/* Stats Grid */}
      <SimpleGrid columns={{ base: 1, md: 2, lg: 4 }} gap={6}>
        <StatCard icon={FaPaw} label="Total Pets" value={stats.totalPets} color="blue" onClick={() => navigate('/pets')} />
        <StatCard icon={FaBell} label="Active Alerts" value={stats.activeAlerts} color="red" onClick={() => navigate('/alerts')} />
        <StatCard icon={FaCheckCircle} label="Found Pets" value={stats.foundPets} color="green" onClick={() => navigate('/alerts')} />
        <StatCard icon={FaClock} label="Pending Alerts" value={stats.pendingAlerts} color="yellow" onClick={() => navigate('/alerts')} />
      </SimpleGrid>

      {/* Quick Actions */}
      <Box>
        <Heading size="md" mb={4} color="gray.800" _dark={{ color: 'white' }}>
          Quick Actions
        </Heading>
        <SimpleGrid columns={{ base: 1, md: 3 }} gap={4}>
          <Card.Root p={4} cursor="pointer" _hover={{ boxShadow: 'md' }} transition="all 0.2s" onClick={() => navigate('/pets/create')}>
            <VStack>
              <Text fontSize="2xl">🐕</Text>
              <Text fontWeight="medium">Add New Pet</Text>
            </VStack>
          </Card.Root>
          <Card.Root p={4} cursor="pointer" _hover={{ boxShadow: 'md' }} transition="all 0.2s" onClick={() => navigate('/alerts/create')}>
            <VStack>
              <Text fontSize="2xl">🚨</Text>
              <Text fontWeight="medium">Create Alert</Text>
            </VStack>
          </Card.Root>
          <Card.Root p={4} cursor="pointer" _hover={{ boxShadow: 'md' }} transition="all 0.2s" onClick={() => navigate('/alerts')}>
            <VStack>
              <Text fontSize="2xl">📋</Text>
              <Text fontWeight="medium">View All Alerts</Text>
            </VStack>
          </Card.Root>
        </SimpleGrid>
      </Box>
    </VStack>
  )
}
