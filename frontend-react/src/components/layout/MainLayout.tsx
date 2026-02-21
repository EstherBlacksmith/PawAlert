import { Box, Flex } from '@chakra-ui/react'
import { Outlet } from 'react-router-dom'
import Sidebar from './Sidebar'
import Header from './Header'

export default function MainLayout() {
  return (
    <Flex minH="100vh">
      <Sidebar />
      <Flex flex="1" direction="column" ml={{ base: 0, md: '280px' }}>
        <Header />
        <Box flex="1" p={6} bg="gray.50" _dark={{ bg: 'gray.900' }}>
          <Outlet />
        </Box>
      </Flex>
    </Flex>
  )
}
