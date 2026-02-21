import { Box, VStack, Text, Link } from '@chakra-ui/react'
import { useLocation } from 'react-router-dom'
import { FaHome, FaPaw, FaExclamationTriangle, FaUser, FaShieldAlt, FaBell } from 'react-icons/fa'
import { useAuth } from '../../context/AuthContext'
import ConnectionStatus from '../notifications/ConnectionStatus'

interface NavItemProps {
  to: string
  icon: React.ElementType
  children: React.ReactNode
}

function NavItem({ to, icon, children }: NavItemProps) {
  const location = useLocation()
  const isActive = location.pathname === to || location.pathname.startsWith(to + '/')

  return (
    <Link
      href={to}
      display="flex"
      alignItems="center"
      gap={3}
      px={4}
      py={3}
      borderRadius="lg"
      color={isActive ? 'purple.600' : 'gray.600'}
      bg={isActive ? 'purple.50' : 'transparent'}
      _dark={{ 
        color: isActive ? 'purple.300' : 'gray.300', 
        bg: isActive ? 'purple.900' : 'transparent' 
      }}
      _hover={{
        bg: 'purple.50',
        _dark: { bg: 'purple.900' },
        textDecoration: 'none',
      }}
      transition="all 0.2s"
    >
      <Box as={icon} boxSize={5} />
      <Text fontWeight={isActive ? 'semibold' : 'medium'}>{children}</Text>
    </Link>
  )
}

export default function Sidebar() {
  const { isAdmin } = useAuth()
  
  return (
    <Box
      as="aside"
      w="250px"
      bg="white"
      _dark={{ bg: 'gray.800', borderColor: 'gray.700' }}
      borderRight="1px solid"
      borderColor="gray.200"
      h="calc(100vh - 60px)"
      position="sticky"
      top="60px"
      py={6}
    >
      <VStack align="stretch" gap={1} px={3}>
        <NavItem to="/dashboard" icon={FaHome}>
          Dashboard
        </NavItem>
        <NavItem to="/pets" icon={FaPaw}>
          My Pets
        </NavItem>
        <NavItem to="/alerts" icon={FaExclamationTriangle}>
          Alerts
        </NavItem>
        <NavItem to="/subscriptions" icon={FaBell}>
          My Subscriptions
        </NavItem>
        <NavItem to="/profile" icon={FaUser}>
          Profile
        </NavItem>
        {isAdmin() && (
          <NavItem to="/admin/dashboard" icon={FaShieldAlt}>
            Admin Dashboard
          </NavItem>
        )}
      </VStack>
      
      <Box borderTopWidth={1} borderColor="gray.200" my={4} />
      
      <Box px={3}>
        <ConnectionStatus />
      </Box>
    </Box>
  )
}
