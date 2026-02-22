import { Box, Flex, IconButton, useBreakpointValue } from '@chakra-ui/react'
import { Outlet } from 'react-router-dom'
import { useState } from 'react'
import { MdMenu, MdArrowForward } from 'react-icons/md'
import { FaArrowLeft, FaArrowRight } from 'react-icons/fa'
import Sidebar from './Sidebar'
import Header from './Header'
import { useAuth } from '../../context/AuthContext'
import bgImage from '../../assets/bg-image.jpg'

export default function MainLayout() {
  const [isCollapsed, setIsCollapsed] = useState(false)
  const isMobile = useBreakpointValue({ base: true, md: false })
  const { isAdmin } = useAuth()
  
  const toggleSidebar = () => setIsCollapsed(!isCollapsed)

  return (
    <Flex
      minH="100vh"
      backgroundImage={isAdmin() ? `url(${bgImage})` : "url('/bg-image.jpg')"}
      backgroundSize="cover"
      backgroundPosition="center"
      backgroundAttachment={isAdmin() ? 'fixed' : 'scroll'}
      backgroundRepeat="no-repeat"
      position="relative"
    >
      <Sidebar isCollapsed={isCollapsed} onToggle={toggleSidebar} />
      <Flex
        flex="1"
        direction="column"
        ml={isMobile ? 0 : isCollapsed ? '70px' : '100px'}
        transition="margin-left 0.3s ease"
        position="relative"
        zIndex={1}
      >
        <Header onToggleSidebar={toggleSidebar} isSidebarCollapsed={isCollapsed} />
        <Box
          flex="1"
          p={6}
          bg={isAdmin() ? 'rgba(255, 255, 255, 0.05)' : 'rgba(255, 255, 255, 0.3)'}
          _dark={{ bg: isAdmin() ? 'rgba(17, 24, 39, 0.2)' : 'rgba(17, 24, 39, 0.5)' }}
          maxW="100%"
          w="100%"
          mx="0"
          backdropFilter={isAdmin() ? 'blur(2px)' : 'none'}
          overflowY="auto"
          position="relative"
          _before={isAdmin() ? {
            content: '""',
            position: 'absolute',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.55)',
            backdropFilter: 'blur(2px)',
            zIndex: 0,
            pointerEvents: 'none',
            borderRadius: 'inherit',
          } : undefined}
        >
          <Box position="relative" zIndex={1}>
            <Outlet />
          </Box>
        </Box>
      </Flex>
    </Flex>
  )
}
