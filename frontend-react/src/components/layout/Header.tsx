import { Box, Flex, Text, Input, IconButton, Avatar, Menu, Portal, Button } from '@chakra-ui/react'
import { FaBell, FaUser, FaSignOutAlt } from 'react-icons/fa'
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
        <Text
          fontSize="xl"
          fontWeight="bold"
          color="teal.500"
          cursor="pointer"
          onClick={() => navigate('/dashboard')}
        >
          PawAlert
        </Text>

        {/* Right side - User menu */}
        <Flex align="center" gap={4}>
          <IconButton aria-label="Notifications" variant="ghost" borderRadius="full">
            <FaBell />
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
                  <Menu.Item value="profile" onClick={() => navigate('/profile')}>
                    <FaUser style={{ marginRight: '8px' }} />
                    Profile
                  </Menu.Item>
                  <Menu.Separator />
                  <Menu.Item value="logout" onClick={handleLogout} color="red.500">
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
