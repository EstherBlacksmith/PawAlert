import { Box, Flex, IconButton, useBreakpointValue } from '@chakra-ui/react'
import { Outlet } from 'react-router-dom'
import { useState } from 'react'
import { MdMenu, MdArrowForward } from 'react-icons/md'
import { FaArrowLeft, FaArrowRight } from 'react-icons/fa'
import Sidebar from './Sidebar'
import Header from './Header'

export default function MainLayout() {
  const [isCollapsed, setIsCollapsed] = useState(false)
  const isMobile = useBreakpointValue({ base: true, md: false })
  
  const toggleSidebar = () => setIsCollapsed(!isCollapsed)

  return (
    <Flex 
      minH="100vh"
      bgImage="url('/bg-image.jpg')"
      bgSize="cover"
      bgPosition="center"
      bgRepeat="no-repeat"
    >
      <Sidebar isCollapsed={isCollapsed} onToggle={toggleSidebar} />
      <Flex 
        flex="1" 
        direction="column" 
        ml={isMobile ? 0 : isCollapsed ? '70px' : '250px'}
        transition="margin-left 0.3s ease"
      >
        <Header onToggleSidebar={toggleSidebar} isSidebarCollapsed={isCollapsed} />
        <Box 
          flex="1" 
          p={6} 
          bg="rgba(255, 255, 255, 0.3)"
          _dark={{ bg: 'rgba(17, 24, 39, 0.5)' }}
          maxW="1800px"
          w="100%"
          mx="auto"
        >
          <Outlet />
        </Box>
      </Flex>
    </Flex>
  )
}
