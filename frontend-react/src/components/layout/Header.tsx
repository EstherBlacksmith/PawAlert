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

  const { isAdmin } = useAuth()

  return (
    <Box
      as="header"
      bg={isAdmin() ? 'rgba(255, 255, 255, 0.95)' : 'white'}
      _dark={{ bg: isAdmin() ? 'rgba(31, 41, 55, 0.95)' : 'gray.800', borderColor: 'gray.700' }}
      borderBottom="1px solid"
      borderColor={isAdmin() ? 'gray.300' : 'gray.200'}
      px={6}
      py={3}
      position="sticky"
      top={0}
      zIndex={10}
      boxShadow={isAdmin() ? 'md' : 'none'}
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
              color={isAdmin() ? 'gray.700' : 'gray.600'}
              _hover={{ bg: isAdmin() ? 'gray.100' : 'brand.50', color: isAdmin() ? 'gray.900' : 'brand.500' }}
              transition="all 0.2s"
              onClick={() => navigate('/subscriptions')}
              cursor="pointer"
            >
              <FaBell />
            </IconButton>

           <Menu.Root>
             <Menu.Trigger asChild>
               <Button
                 variant="ghost"
                 borderRadius="full"
                 px={2}
                 color={isAdmin() ? 'gray.700' : 'inherit'}
                 _hover={{ bg: isAdmin() ? 'gray.100' : 'brand.50' }}
               >
                 <Flex align="center" gap={2}>
                   <Avatar.Root size="sm">
                     <Avatar.Fallback name={user?.username || 'User'} />
                   </Avatar.Root>
                   <Text display={{ base: 'none', md: 'block' }} fontSize="sm" color={isAdmin() ? 'gray.700' : 'inherit'}>
                     {user?.username || 'User'}
                   </Text>
                 </Flex>
               </Button>
             </Menu.Trigger>
             <Portal>
               <Menu.Positioner>
                 <Menu.Content bg={isAdmin() ? 'white' : undefined} _dark={{ bg: isAdmin() ? 'gray.800' : undefined }}>
                   <Menu.Item
                     value="profile"
                     onClick={() => navigate('/profile')}
                     _hover={{ bg: isAdmin() ? 'gray.100' : 'brand.50' }}
                     color={isAdmin() ? 'gray.700' : 'inherit'}
                   >
                     <FaUser style={{ marginRight: '8px', color: isAdmin() ? 'gray.600' : 'gray.600' }} />
                     Profile
                   </Menu.Item>
                   <Menu.Separator />
                   <Menu.Item
                     value="logout"
                     onClick={handleLogout}
                     color={isAdmin() ? 'red.600' : 'red.500'}
                     _hover={{ bg: isAdmin() ? 'red.100' : 'red.50' }}
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
