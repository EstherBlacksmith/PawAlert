import { Box, Heading, Text, Button, VStack } from '@chakra-ui/react'
import { Link } from 'react-router-dom'

export default function NotFound() {
  return (
    <Box
      minH="100vh"
      display="flex"
      alignItems="center"
      justifyContent="center"
      bg="gray.50"
      p={4}
    >
      <VStack gap={6} textAlign="center">
        <Text fontSize="8xl" fontWeight="bold" color="purple.500">
          404
        </Text>
        <Heading size="xl" color="gray.800">
          Page Not Found
        </Heading>
        <Text color="gray.500" fontSize="lg" maxW="400px">
          The page you're looking for doesn't exist or has been moved.
        </Text>
        <Link to="/dashboard">
          <Button
            colorPalette="brand"
            size="lg"
            mt={4}
          >
            Go to Dashboard
          </Button>
        </Link>
      </VStack>
    </Box>
  )
}
