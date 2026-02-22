import { useState, useEffect } from 'react'
import { Box, VStack, Heading, Text, Input, Button, Field, Alert, HStack } from '@chakra-ui/react'
import { Link, useNavigate } from 'react-router-dom'
import { FaPaw } from 'react-icons/fa'
import { useAuth } from '../context/AuthContext'
import { ErrorResponse } from '../types'
import NearbyAlertsMap from '../components/alerts/NearbyAlertsMap'
import { extractError } from '../utils/errorUtils'

// Define keyframe animations
const slideUpAnimation = `
  @keyframes slideUp {
    from {
      opacity: 0;
      transform: translateY(20px);
    }
    to {
      opacity: 1;
      transform: translateY(0);
    }
  }
  
  @keyframes pulse {
    0%, 100% {
      opacity: 1;
    }
    50% {
      opacity: 0.8;
    }
  }
`

// Inject animations into document
if (typeof document !== 'undefined') {
  const style = document.createElement('style')
  style.textContent = slideUpAnimation
  document.head.appendChild(style)
}

export default function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<ErrorResponse | string | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [location, setLocation] = useState<{ latitude: number; longitude: number } | null>(null)
  const [locationError, setLocationError] = useState<string | null>(null)
  const { login } = useAuth()
  const navigate = useNavigate()

  // Get user's location on component mount
  useEffect(() => {
    if ('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          setLocation({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude
          })
        },
        (err) => {
          console.warn('Geolocation error:', err)
          setLocationError('Location not available')
        },
        { enableHighAccuracy: false, timeout: 5000, maximumAge: 300000 }
      )
    } else {
      setLocationError('Geolocation not supported')
    }
  }, [])

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)
    setIsLoading(true)

    try {
      await login({ email, password })
      navigate('/dashboard')
    } catch (err: unknown) {
      // Use centralized error extraction for user-friendly messages
      const friendlyError = extractError(err)
      setError(friendlyError)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Box
      minH="100vh"
      display="flex"
      bgImage="url('/src/assets/bg-image.jpg')"
      bgSize="cover"
      bgPosition="center"
      bgAttachment={{ base: 'scroll', lg: 'fixed' }}
      position="relative"
      _before={{
        content: '""',
        position: 'absolute',
        top: 0,
        left: 0,
        right: 0,
        bottom: 0,
        bg: 'rgba(0, 0, 0, 0.4)',
        backdropFilter: 'blur(2px)',
        zIndex: 1
      }}
    >
      {/* Map Section - Left side on desktop, hidden on mobile */}
       <Box
         display={{ base: 'none', lg: 'block' }}
         w="50%"
         h="100vh"
         position="relative"
         bg="gray.100"
         overflow="hidden"
         zIndex={2}
       >
        {location ? (
          <Box position="absolute" top={0} left={0} right={0} bottom={0}>
            <NearbyAlertsMap
              latitude={location.latitude}
              longitude={location.longitude}
              radiusKm={10}
              fullHeight={true}
            />
          </Box>
        ) : (
          <Box 
            position="absolute"
            top={0}
            left={0}
            right={0}
            bottom={0}
            display="flex" 
            alignItems="center" 
            justifyContent="center"
          >
            <Text color="gray.400">Getting location...</Text>
          </Box>
        )}
      </Box>

       {/* Login Form Section - Right side on desktop, full width on mobile */}
       <Box
         w={{ base: 'full', lg: '50%' }}
         display="flex"
         alignItems="center"
         justifyContent="center"
         p={{ base: 4, md: 6, lg: 8 }}
         position="relative"
         zIndex={2}
       >
         <Box
           w="full"
           maxW={{ base: 'full', md: '450px', lg: '480px' }}
           bg="rgba(255, 255, 255, 0.95)"
           backdropFilter="blur(10px)"
           borderRadius="2xl"
           border="1px solid"
           borderColor="rgba(255, 255, 255, 0.2)"
           boxShadow="0 8px 32px rgba(0, 0, 0, 0.1)"
           p={{ base: 6, md: 8, lg: 10 }}
           position="relative"
           overflow="hidden"
           animation="slideUp 0.6s ease-out"
         >
          {/* Subtle gradient overlay */}
          <Box
            position="absolute"
            top={0}
            left={0}
            right={0}
            bottom={0}
            bgGradient="linear(to-br, brand.50, transparent)"
            pointerEvents="none"
          />
          <VStack gap={6} position="relative">
           {/* Logo */}
             <Box textAlign="center">
               <Box 
                 display="inline-block" 
                 p={4} 
                 borderRadius="full" 
                 bgGradient="linear(to-br, brand.400, brand.500)"
                 mb={4}
                 boxShadow="0 8px 16px rgba(251, 111, 4, 0.2)"
                 animation="pulse 2s infinite"
               >
                 <FaPaw size={40} color="white" />
               </Box>
               <Heading 
                 size="2xl" 
                 color="brand.500"
                 fontWeight="700"
                 letterSpacing="-0.5px"
                 mb={2}
               >
                 PawAlert
               </Heading>
               <Text
                 fontSize="md"
                 color="gray.600"
                 fontWeight="500"
                 mt={1}
               >
                 Protect your pets 24/7
               </Text>
             </Box>

            {/* Mobile Map Section - Show below logo on mobile */}
            {location && (
              <Box 
                display={{ base: 'block', lg: 'none' }} 
                w="full" 
                borderRadius="md" 
                overflow="hidden"
              >
                <NearbyAlertsMap
                  latitude={location.latitude}
                  longitude={location.longitude}
                  radiusKm={10}
                />
              </Box>
            )}
            {locationError && (
              <Text fontSize="xs" color="gray.400">{locationError}</Text>
            )}

            {/* Error Message */}
            {error && (
              <Alert.Root status="error" w="full" borderRadius="md">
                <Alert.Indicator />
                <Box flex="1">
                  {typeof error === 'string' ? (
                    <Text fontSize="sm" color="red.600">
                      {error}
                    </Text>
                  ) : (
                    <>
                      <Alert.Title fontSize="sm" fontWeight="bold">
                        {error.error || 'Error'}
                      </Alert.Title>
                      <Alert.Description fontSize="sm">
                        {error.message}
                      </Alert.Description>
                    </>
                  )}
                </Box>
              </Alert.Root>
            )}

             {/* Form */}
             <form onSubmit={handleSubmit} style={{ width: '100%' }}>
               <VStack gap={5} w="full">
                 <Field.Root w="full">
                   <Field.Label 
                     fontSize="sm" 
                     fontWeight="600" 
                     color="gray.700"
                     mb={2}
                   >
                     Email
                   </Field.Label>
                   <Input
                     type="email"
                     value={email}
                     onChange={(e: React.ChangeEvent<HTMLInputElement>) => setEmail(e.target.value)}
                     placeholder="Enter your email"
                     required
                     bg="rgba(255, 255, 255, 0.8)"
                     borderColor="rgba(0, 0, 0, 0.1)"
                     fontSize="md"
                     py={3}
                     px={4}
                     transition="all 0.3s ease"
                     _focus={{
                       borderColor: 'brand.500',
                       boxShadow: '0 0 0 3px rgba(251, 111, 4, 0.1)',
                       bg: 'white'
                     }}
                     _hover={{
                       borderColor: 'rgba(0, 0, 0, 0.2)',
                       bg: 'white'
                     }}
                   />
                 </Field.Root>

                 <Field.Root w="full">
                   <Field.Label 
                     fontSize="sm" 
                     fontWeight="600" 
                     color="gray.700"
                     mb={2}
                   >
                     Password
                   </Field.Label>
                   <Input
                     type="password"
                     value={password}
                     onChange={(e: React.ChangeEvent<HTMLInputElement>) => setPassword(e.target.value)}
                     placeholder="Enter your password"
                     required
                     bg="rgba(255, 255, 255, 0.8)"
                     borderColor="rgba(0, 0, 0, 0.1)"
                     fontSize="md"
                     py={3}
                     px={4}
                     transition="all 0.3s ease"
                     _focus={{
                       borderColor: 'brand.500',
                       boxShadow: '0 0 0 3px rgba(251, 111, 4, 0.1)',
                       bg: 'white'
                     }}
                     _hover={{
                       borderColor: 'rgba(0, 0, 0, 0.2)',
                       bg: 'white'
                     }}
                   />
                 </Field.Root>

                 <Button
                   type="submit"
                   w="full"
                   colorPalette="brand"
                   size="lg"
                   loading={isLoading}
                   bg="brand.500"
                   color="white"
                   fontWeight="600"
                   fontSize="md"
                   py={3}
                   transition="all 0.3s ease"
                   _hover={{ 
                     bg: 'brand.600',
                     transform: 'translateY(-2px)',
                     boxShadow: '0 12px 24px rgba(251, 111, 4, 0.3)'
                   }}
                   _active={{
                     transform: 'translateY(0)'
                   }}
                 >
                   Sign In
                 </Button>
               </VStack>
             </form>

             {/* Register Link */}
             <Text textAlign="center" fontSize="sm" color="gray.600" fontWeight="500">
               Don't have an account?{' '}
               <Link to="/register">
                 <Text 
                   as="span" 
                   color="brand.500" 
                   fontWeight="600" 
                   _hover={{ 
                     color: 'brand.600',
                     textDecoration: 'underline'
                   }}
                   transition="all 0.3s ease"
                 >
                   Register here
                 </Text>
               </Link>
             </Text>
          </VStack>
        </Box>
      </Box>
    </Box>
  )
}
