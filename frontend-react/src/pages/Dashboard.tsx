import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Box, SimpleGrid, Heading, Text, Card, Icon, VStack, Flex, Spinner } from '@chakra-ui/react'
import { GiPawPrint, GiHealthPotion, GiCheck, GiSword, GiDog, GiBell, GiList, GiCat } from '../components/icons'
import { useAuth } from '../context/AuthContext'
import { petService } from '../services/pet.service'
import { alertService } from '../services/alert.service'
import { Alert } from '../types'

interface StatCardProps {
  icon: React.ElementType
  label: string
  value: number
  color: string
  gradientColors?: string
  onClick?: () => void
}

function StatCard({ icon, label, value, color, onClick, gradientColors }: StatCardProps) {
  return (
    <Card.Root 
      p={6} 
      boxShadow="lg" 
      cursor={onClick ? "pointer" : "default"}
      _hover={onClick ? { boxShadow: 'xl', transform: 'translateY(-4px)' } : undefined}
      transition="all 0.3s"
      onClick={onClick}
      overflow="hidden"
      position="relative"
    >
      {/* Gradient background overlay */}
      {gradientColors && (
        <Box
          position="absolute"
          top={0}
          left={0}
          right={0}
          bottom={0}
          bgGradient={`to-br ${gradientColors}`}
          opacity={0.1}
          pointerEvents="none"
        />
      )}
      <Flex align="center" gap={4} position="relative">
        <Box
          p={3}
          borderRadius="full"
          bgGradient={`to-br ${gradientColors || `${color}.100`, `${color}.200`}`}
          _dark={{ bg: `${color}.900` }}
        >
          <Icon as={icon} boxSize={6} color={`${color}.500`} />
        </Box>
        <Box>
          <Text fontSize="3xl" fontWeight="bold" color="gray.800" _dark={{ color: 'white' }}>
            {value}
          </Text>
          <Text fontSize="sm" color="gray.700">
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
    <Box bg="rgba(255, 255, 255, 0.85)" p={6} borderRadius="lg" boxShadow="lg">
      <VStack gap={6} align="stretch">
      {/* Welcome Section */}
      <Box>
        <Heading size="lg" color="gray.800" _dark={{ color: 'white' }}>
          Welcome back, {user?.username}!
        </Heading>
        <Text color="gray.700" mt={1}>
          Here's an overview of your pet alerts
        </Text>
      </Box>

      {/* Stats Grid */}
      <SimpleGrid columns={{ base: 1, md: 2, lg: 4 }} gap={6}>
        <StatCard 
          icon={GiPawPrint} 
          label="Total Pets" 
          value={stats.totalPets} 
          color="blue" 
          gradientColors="linear(to-br, blue.400, blue.500)"
          onClick={() => navigate('/pets')} 
        />
        <StatCard 
          icon={GiHealthPotion} 
          label="Active Alerts" 
          value={stats.activeAlerts} 
          color="red" 
          gradientColors="linear(to-br, red.400, red.500)"
          onClick={() => navigate('/alerts')} 
        />
        <StatCard 
          icon={GiCheck} 
          label="Found Pets" 
          value={stats.foundPets} 
          color="green" 
          gradientColors="linear(to-br, green.400, green.500)"
          onClick={() => navigate('/alerts')} 
        />
        <StatCard 
          icon={GiSword} 
          label="Pending Alerts" 
          value={stats.pendingAlerts} 
          color="yellow" 
          gradientColors="linear(to-br, yellow.400, orange.400)"
          onClick={() => navigate('/alerts')} 
        />
      </SimpleGrid>

      {/* Quick Actions */}
      <Box>
        <Heading size="md" mb={4} color="gray.800" _dark={{ color: 'white' }}>
          Quick Actions
        </Heading>
        <SimpleGrid columns={{ base: 1, md: 3 }} gap={4}>
        <Card.Root p={4} cursor="pointer" _hover={{ boxShadow: 'lg', transform: 'translateY(-2px)' }} transition="all 0.2s" onClick={() => navigate('/pets/create')}>
            <VStack>
              <GiCat size={32} color="#F1B42F" />
              <Text fontWeight="medium">Add New Pet</Text>
            </VStack>
          </Card.Root>
          <Card.Root p={4} cursor="pointer" _hover={{ boxShadow: 'lg', transform: 'translateY(-2px)' }} transition="all 0.2s" onClick={() => navigate('/alerts/create')}>
            <VStack>
              <GiBell size={32} color="accent.500" />
              <Text fontWeight="medium">Create Alert</Text>
            </VStack>
          </Card.Root>
          <Card.Root p={4} cursor="pointer" _hover={{ boxShadow: 'lg', transform: 'translateY(-2px)' }} transition="all 0.2s" onClick={() => navigate('/alerts')}>
            <VStack>
              <GiList size={32} color="accent.500" />
              <Text fontWeight="medium">View All Alerts</Text>
            </VStack>
          </Card.Root>
        </SimpleGrid>
      </Box>
      </VStack>
    </Box>
  )
}
