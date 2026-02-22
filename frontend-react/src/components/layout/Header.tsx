import { Box, Flex, Text, IconButton, Avatar, Menu, Portal, Button } from '@chakra-ui/react'
import { FaPaw, FaBell, FaUser, FaSignOutAlt } from 'react-icons/fa'
import { useAuth } from '../../context/AuthContext'
import { useNavigate } from 'react-router-dom'

export default function Header() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <Box
      as="header"
      bg="white"
      _dark={{ bg: 'gray.800', borderColor: 'gray.700' }}
      borderBottom="1px solid"
      borderColor="gray.200"
      px={6}
      py={3}
      position="sticky"
      top={0}
      zIndex={10}
    >
      <Flex justify="space-between" align="center">
        {/* Left side - Logo/Brand */}
        <Flex align="center" gap={3}>
          <Text
            fontSize="xl"
            fontWeight="bold"
            color="brand.500"
            cursor="pointer"
            onClick={() => navigate('/dashboard')}
            _hover={{ color: 'brand.600', transform: 'scale(1.02)' }}
            transition="all 0.2s"
          >
            <FaPaw style={{ marginRight: '8px', verticalAlign: 'middle', color: 'brand.500' }} />
            PawAlert
          </Text>
        </Flex>

        {/* Right side - User menu */}
        <Flex align="center" gap={4}>
           <IconButton 
             aria-label="Notifications" 
             variant="ghost" 
             borderRadius="full"
             _hover={{ bg: 'brand.50', color: 'brand.500' }}
             transition="all 0.2s"
             onClick={() => navigate('/subscriptions')}
             cursor="pointer"
           >
             <FaBell color="gray.600" />
           </IconButton>

          <Menu.Root>
            <Menu.Trigger asChild>
              <Button variant="ghost" borderRadius="full" px={2}>
                <Flex align="center" gap={2}>
                  <Avatar.Root size="sm">
                    <Avatar.Fallback name={user?.username || 'User'} />
                  </Avatar.Root>
                  <Text display={{ base: 'none', md: 'block' }} fontSize="sm">
                    {user?.username || 'User'}
                  </Text>
                </Flex>
              </Button>
            </Menu.Trigger>
            <Portal>
              <Menu.Positioner>
                <Menu.Content>
                  <Menu.Item 
                    value="profile" 
                    onClick={() => navigate('/profile')}
                    _hover={{ bg: 'brand.50' }}
                  >
                    <FaUser style={{ marginRight: '8px', color: 'gray.600' }} />
                    Profile
                  </Menu.Item>
                  <Menu.Separator />
                  <Menu.Item 
                    value="logout" 
                    onClick={handleLogout} 
                    color="red.500"
                    _hover={{ bg: 'red.50' }}
                  >
                    <FaSignOutAlt style={{ marginRight: '8px' }} />
                    Logout
                  </Menu.Item>
                </Menu.Content>
              </Menu.Positioner>
            </Portal>
          </Menu.Root>
        </Flex>
      </Flex>
    </Box>
  )
}
