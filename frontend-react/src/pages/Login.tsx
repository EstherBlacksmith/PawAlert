import { useState, useEffect } from 'react'
import { Box, VStack, Heading, Text, Input, Button, Field, Alert, HStack } from '@chakra-ui/react'
import { Link, useNavigate } from 'react-router-dom'
import { FaPaw } from 'react-icons/fa'
import { useAuth } from '../context/AuthContext'
import { ErrorResponse } from '../types'
import NearbyAlertsMap from '../components/alerts/NearbyAlertsMap'
import { extractError } from '../utils/errorUtils'

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
      bg="gray.50"
    >
      {/* Map Section - Left side on desktop, hidden on mobile */}
      <Box
        display={{ base: 'none', lg: 'block' }}
        w="50%"
        h="100vh"
        position="relative"
        bg="gray.100"
        overflow="hidden"
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
        p={4}
      >
        <Box
          w="full"
          maxW="450px"
          bg="white"
          borderRadius="lg"
          boxShadow="xl"
          p={8}
          position="relative"
          overflow="hidden"
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
                mb={2}
              >
                <FaPaw size={40} color="white" />
              </Box>
              <Heading size="lg" color="brand.500">
                PawAlert
              </Heading>
              <Text color="gray.500" mt={1}>
                Sign in to your account
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
              <VStack gap={4} w="full">
                <Field.Root>
                  <Field.Label>Email</Field.Label>
                  <Input
                    type="email"
                    value={email}
                    onChange={(e: React.ChangeEvent<HTMLInputElement>) => setEmail(e.target.value)}
                    placeholder="Enter your email"
                    required
                  />
                </Field.Root>

                <Field.Root>
                  <Field.Label>Password</Field.Label>
                  <Input
                    type="password"
                    value={password}
                    onChange={(e: React.ChangeEvent<HTMLInputElement>) => setPassword(e.target.value)}
                    placeholder="Enter your password"
                    required
                  />
                </Field.Root>

                <Button
                  type="submit"
                  w="full"
                  colorPalette="brand"
                  size="lg"
                  loading={isLoading}
                  bg="brand.500"
                  _hover={{ bg: 'brand.600' }}
                >
                  Sign In
                </Button>
              </VStack>
            </form>

            {/* Register Link */}
            <Text textAlign="center" fontSize="sm" color="gray.500">
              Don't have an account?{' '}
              <Link to="/register">
                <Text as="span" color="brand.500" fontWeight="medium" _hover={{ color: 'brand.600' }}>
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
