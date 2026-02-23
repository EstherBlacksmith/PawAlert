import { Box, VStack, Text, Link, Flex, IconButton, Icon } from '@chakra-ui/react'
import { useLocation } from 'react-router-dom'
import { FaHome, FaPaw, FaHeart, FaUser, FaShieldAlt, FaBell, FaArrowLeft, FaArrowRight } from 'react-icons/fa'
import { useAuth } from '../../context/AuthContext'
import ConnectionStatus from '../notifications/ConnectionStatus'

interface NavItemProps {
  to: string
  icon: React.ComponentType
  children: React.ReactNode
  isCollapsed?: boolean
}

function NavItem({ to, icon: IconComponent, children, isCollapsed }: NavItemProps) {
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
      position="relative"
      color={isActive ? 'brand.600' : 'gray.600'}
      bg={isActive ? 'brand.50' : 'transparent'}
      _dark={{ 
        color: isActive ? 'brand.300' : 'gray.300', 
        bg: isActive ? 'brand.900' : 'transparent' 
      }}
      _hover={{
        bg: 'brand.50',
        _dark: { bg: 'brand.900' },
        textDecoration: 'none',
      }}
      transition="all 0.2s"
      justifyContent={isCollapsed ? 'center' : 'flex-start'}
      title={isCollapsed ? String(children) : undefined}
    >
      {isActive && (
        <Box
          position="absolute"
          left={0}
          top="50%"
          transform="translateY(-50%)"
          w="3px"
          h="60%"
          bg="brand.500"
          borderRadius="full"
        />
      )}
      <Icon as={IconComponent} boxSize={5} color={isActive ? 'brand.500' : 'inherit'} />
      {!isCollapsed && (
        <Text fontWeight={isActive ? 'semibold' : 'medium'}>
          {children}
        </Text>
      )}
    </Link>
  )
}

interface SidebarProps {
  isCollapsed: boolean
  onToggle: () => void
}

export default function Sidebar({ isCollapsed, onToggle }: SidebarProps) {
  const { isAdmin } = useAuth()
  
  return (
    <Box
      as="aside"
      w={isCollapsed ? '70px' : '250px'}
      bg="white"
      _dark={{ bg: 'gray.800', borderColor: 'gray.700' }}
      borderRight="1px solid"
      borderColor="gray.200"
      h="calc(100vh - 60px)"
      position="sticky"
      top="60px"
      py={4}
      transition="width 0.3s ease"
      overflowX="hidden"
    >
      {/* Toggle Button */}
      <Flex justify={isCollapsed ? 'center' : 'flex-end'} px={2} mb={2}>
        <IconButton
          aria-label={isCollapsed ? 'Expand sidebar' : 'Collapse sidebar'}
          size="sm"
          variant="ghost"
          onClick={onToggle}
          colorScheme="brand"
        >
          {isCollapsed ? <FaArrowRight /> : <FaArrowLeft />}
        </IconButton>
      </Flex>
      
      <VStack align="stretch" gap={1} px={isCollapsed ? 1 : 3}>
        <NavItem to="/dashboard" icon={FaHome} isCollapsed={isCollapsed}>
          Dashboard
        </NavItem>
        <NavItem to="/pets" icon={FaPaw} isCollapsed={isCollapsed}>
          My Pets
        </NavItem>
        <NavItem to="/alerts" icon={FaHeart} isCollapsed={isCollapsed}>
          Alerts
        </NavItem>
        <NavItem to="/subscriptions" icon={FaBell} isCollapsed={isCollapsed}>
          My Subscriptions
        </NavItem>
        <NavItem to="/profile" icon={FaUser} isCollapsed={isCollapsed}>
          Profile
        </NavItem>
        {isAdmin() && (
          <NavItem to="/admin/dashboard" icon={FaShieldAlt} isCollapsed={isCollapsed}>
            Admin Dashboard
          </NavItem>
        )}
      </VStack>
      
      {!isCollapsed && (
        <>
          <Box borderTopWidth={1} borderColor="gray.200" my={4} />
          
          <Box px={3}>
            <ConnectionStatus />
          </Box>
        </>
      )}
    </Box>
  )
}
