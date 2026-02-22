import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Box, Heading, VStack, HStack, Card, Flex, Spinner, Text, SimpleGrid, Stat, Tabs
} from '@chakra-ui/react'
import { FaUsers, FaPaw, FaBell, FaHeart, FaShieldAlt } from 'react-icons/fa'
import { useAuth } from '../../context/AuthContext'
import { userService } from '../../services/user.service'
import { petService } from '../../services/pet.service'
import { alertService } from '../../services/alert.service'
import { User, Pet, Alert } from '../../types'
import UsersTab from './components/UsersTab'
import PetsTab from './components/PetsTab'
import AlertsTab from './components/AlertsTab'
import bgImage from '../../assets/bg-image.jpg'

interface Stats {
  totalUsers: number
  totalPets: number
  totalAlerts: number
  openAlerts: number
}

export default function AdminDashboard() {
  const { isAdmin } = useAuth()
  const navigate = useNavigate()
  
  const [users, setUsers] = useState<User[]>([])
  const [pets, setPets] = useState<Pet[]>([])
  const [alerts, setAlerts] = useState<Alert[]>([])
  const [stats, setStats] = useState<Stats>({
    totalUsers: 0,
    totalPets: 0,
    totalAlerts: 0,
    openAlerts: 0
  })
  
  const [isLoading, setIsLoading] = useState(true)
  const [activeTab, setActiveTab] = useState('alerts')

  const fetchData = useCallback(async () => {
    setIsLoading(true)
    try {
      const [usersData, petsData, alertsData] = await Promise.all([
        userService.getAllUsers().catch(() => [] as User[]),
        petService.getAllPets().catch(() => [] as Pet[]),
        alertService.searchAlertsWithFilters({}).catch(() => [] as Alert[])
      ])
      
      setUsers(usersData)
      setPets(petsData)
      setAlerts(alertsData)
      
      setStats({
        totalUsers: usersData.length,
        totalPets: petsData.length,
        totalAlerts: alertsData.length,
        openAlerts: alertsData.filter(a => a.status === 'OPENED').length
      })
    } catch (error) {
      console.error('Error fetching admin data:', error)
    } finally {
      setIsLoading(false)
    }
  }, [])

  useEffect(() => {
    // Redirect non-admin users
    if (!isAdmin()) {
      navigate('/dashboard')
      return
    }
    
    fetchData()
  }, [isAdmin, navigate, fetchData])

  const refreshData = () => {
    fetchData()
  }

  if (isLoading) {
    return (
      <Flex justify="center" align="center" minH="400px">
        <Spinner size="xl" color="brand.500" />
      </Flex>
    )
  }

  return (
    <Box
      minH="100vh"
      backgroundImage={`url(${bgImage})`}
      backgroundSize="cover"
      backgroundPosition="center"
      backgroundAttachment="fixed"
      position="relative"
      _before={{
        content: '""',
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        backgroundColor: 'rgba(0, 0, 0, 0.55)',
        backdropFilter: 'blur(2px)',
        zIndex: 1,
      }}
    >
      <VStack gap={6} align="stretch" position="relative" zIndex={2} p={6}>
        {/* Header */}
        <Box>
          <Heading size="lg" color="white">
            Admin Dashboard
          </Heading>
          <Text color="gray.100" mt={1}>
            Manage users, pets, and alerts across the platform
          </Text>
        </Box>

        {/* Statistics Cards */}
        <SimpleGrid columns={{ base: 1, md: 2, lg: 4 }} gap={4}>
          <Card.Root p={4} bg="rgba(255, 255, 255, 0.95)" backdropFilter="blur(10px)">
            <Flex align="center" gap={4}>
              <Box
                p={3}
                borderRadius="lg"
                bg="brand.100"
                _dark={{ bg: 'brand.900' }}
              >
                <FaUsers size={24} color="accent.500" />
              </Box>
              <Box>
                <Text fontSize="sm" color="gray.500">Total Users</Text>
                <Text fontSize="2xl" fontWeight="bold">{stats.totalUsers}</Text>
              </Box>
            </Flex>
          </Card.Root>

          <Card.Root p={4} bg="rgba(255, 255, 255, 0.95)" backdropFilter="blur(10px)">
            <Flex align="center" gap={4}>
              <Box
                p={3}
                borderRadius="lg"
                bg="accent.100"
                _dark={{ bg: 'accent.900' }}
              >
                <FaPaw size={24} color="brand.500" />
              </Box>
              <Box>
                <Text fontSize="sm" color="gray.500">Total Pets</Text>
                <Text fontSize="2xl" fontWeight="bold">{stats.totalPets}</Text>
              </Box>
            </Flex>
          </Card.Root>

          <Card.Root p={4} bg="rgba(255, 255, 255, 0.95)" backdropFilter="blur(10px)">
            <Flex align="center" gap={4}>
              <Box
                p={3}
                borderRadius="lg"
                bg="green.100"
                _dark={{ bg: 'green.900' }}
              >
                <FaBell size={24} color="green.500" />
              </Box>
              <Box>
                <Text fontSize="sm" color="gray.500">Total Alerts</Text>
                <Text fontSize="2xl" fontWeight="bold">{stats.totalAlerts}</Text>
              </Box>
            </Flex>
          </Card.Root>

          <Card.Root p={4} bg="rgba(255, 255, 255, 0.95)" backdropFilter="blur(10px)">
            <Flex align="center" gap={4}>
              <Box
                p={3}
                borderRadius="lg"
                bg="red.100"
                _dark={{ bg: 'red.900' }}
              >
                <FaHeart size={24} color="red.500" />
              </Box>
              <Box>
                <Text fontSize="sm" color="gray.500">Open Alerts</Text>
                <Text fontSize="2xl" fontWeight="bold">{stats.openAlerts}</Text>
              </Box>
            </Flex>
          </Card.Root>
        </SimpleGrid>

        {/* Tabs */}
        <Card.Root p={6} boxShadow="lg" bg="rgba(255, 255, 255, 0.97)" backdropFilter="blur(10px)">
          <Tabs.Root value={activeTab} onValueChange={(e) => setActiveTab(e.value)}>
            <Tabs.List mb={6}>
              <Tabs.Trigger value="alerts">
                <HStack gap={2}>
                  <FaBell color="gray.600" />
                  <Text>Alerts</Text>
                  <Box
                    px={2}
                    py={0.5}
                    borderRadius="full"
                    bg="gray.100"
                    _dark={{ bg: 'gray.700' }}
                    fontSize="xs"
                  >
                    {stats.totalAlerts}
                  </Box>
                </HStack>
              </Tabs.Trigger>
              <Tabs.Trigger value="users">
                <HStack gap={2}>
                  <FaUsers color="gray.600" />
                  <Text>Users</Text>
                  <Box
                    px={2}
                    py={0.5}
                    borderRadius="full"
                    bg="gray.100"
                    _dark={{ bg: 'gray.700' }}
                    fontSize="xs"
                  >
                    {stats.totalUsers}
                  </Box>
                </HStack>
              </Tabs.Trigger>
              <Tabs.Trigger value="pets">
                <HStack gap={2}>
                  <FaPaw color="gray.600" />
                  <Text>Pets</Text>
                  <Box
                    px={2}
                    py={0.5}
                    borderRadius="full"
                    bg="gray.100"
                    _dark={{ bg: 'gray.700' }}
                    fontSize="xs"
                  >
                    {stats.totalPets}
                  </Box>
                </HStack>
              </Tabs.Trigger>
            </Tabs.List>

            <Tabs.Content value="alerts">
              <AlertsTab />
            </Tabs.Content>

            <Tabs.Content value="users">
              <UsersTab users={users} onRefresh={refreshData} />
            </Tabs.Content>

            <Tabs.Content value="pets">
              <PetsTab pets={pets} onRefresh={refreshData} />
            </Tabs.Content>
          </Tabs.Root>
        </Card.Root>
      </VStack>
    </Box>
  )
}
