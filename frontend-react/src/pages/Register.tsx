import { useState } from 'react'
import { Box, VStack, Heading, Text, Input, Button, Field, HStack, Alert } from '@chakra-ui/react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { ErrorResponse } from '../types'
import { extractError, showSuccessToast } from '../utils/errorUtils'

// Country prefixes for phone number
const COUNTRY_PREFIXES = [
  { code: '+34', country: 'Spain' },
  { code: '+44', country: 'UK' },
  { code: '+1', country: 'USA' },
  { code: '+33', country: 'France' },
  { code: '+49', country: 'Germany' },
  { code: '+39', country: 'Italy' },
  { code: '+351', country: 'Portugal' },
  { code: '+32', country: 'Belgium' },
  { code: '+31', country: 'Netherlands' },
  { code: '+46', country: 'Sweden' },
]

export default function Register() {
  const [formData, setFormData] = useState({
    username: '',
    surname: '',
    email: '',
    phonePrefix: '+34',
    phoneNumber: '',
    password: '',
    confirmPassword: '',
  })
  const [error, setError] = useState<ErrorResponse | string | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const { register, login } = useAuth()
  const navigate = useNavigate()

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handlePrefixChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setFormData({ ...formData, phonePrefix: e.target.value })
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)

    if (formData.password !== formData.confirmPassword) {
      setError('Passwords do not match')
      return
    }

    // Combine prefix + phone number
    const fullPhoneNumber = formData.phonePrefix + formData.phoneNumber.replace(/\D/g, '')

    setIsLoading(true)

    try {
      // Register the user
      await register({
        username: formData.username,
        email: formData.email,
        password: formData.password,
        surname: formData.surname,
        phoneNumber: fullPhoneNumber,
      })
      
      // Auto-login after registration
      await login({ email: formData.email, password: formData.password })
      showSuccessToast('Account Created', 'Welcome to PawAlert!')
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
      alignItems="center"
      justifyContent="center"
      bg="gray.50"
      _dark={{ bg: 'gray.900' }}
      p={4}
    >
      <Box
        w="full"
        maxW="400px"
        bg="white"
        _dark={{ bg: 'gray.800' }}
        borderRadius="lg"
        boxShadow="lg"
        p={8}
      >
        <VStack gap={6}>
          {/* Logo */}
          <Box textAlign="center">
            <Text fontSize="4xl" mb={2}>🐾</Text>
            <Heading size="lg" color="purple.600" _dark={{ color: 'purple.400' }}>
              Create Account
            </Heading>
            <Text color="gray.500" mt={1}>
              Join PawAlert today
            </Text>
          </Box>

          {/* Error Message */}
          {error && (
            <Alert.Root status="error" w="full" borderRadius="md">
              <Alert.Indicator />
              <Box flex="1">
                {typeof error === 'string' ? (
                  <Text fontSize="sm" color="red.600" _dark={{ color: 'red.200' }}>
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
                <Field.Label>Username</Field.Label>
                <Input
                  name="username"
                  value={formData.username}
                  onChange={handleChange}
                  placeholder="Choose a username"
                  required
                />
              </Field.Root>

              <Field.Root>
                <Field.Label>Surname</Field.Label>
                <Input
                  name="surname"
                  value={formData.surname}
                  onChange={handleChange}
                  placeholder="Your surname"
                  required
                />
              </Field.Root>

              <Field.Root>
                <Field.Label>Email</Field.Label>
                <Input
                  type="email"
                  name="email"
                  value={formData.email}
                  onChange={handleChange}
                  placeholder="your@email.com"
                  required
                />
              </Field.Root>

              <Field.Root>
                <Field.Label>Phone Number</Field.Label>
                <HStack gap={2}>
                  <Box w="100px">
                    <select
                      value={formData.phonePrefix}
                      onChange={handlePrefixChange}
                      style={{
                        width: '100%',
                        padding: '8px',
                        borderRadius: '6px',
                        border: '1px solid #e2e8f0',
                        backgroundColor: 'white',
                      }}
                    >
                      {COUNTRY_PREFIXES.map((prefix) => (
                        <option key={prefix.code} value={prefix.code}>
                          {prefix.code} ({prefix.country})
                        </option>
                      ))}
                    </select>
                  </Box>
                  <Input
                    name="phoneNumber"
                    value={formData.phoneNumber}
                    onChange={handleChange}
                    placeholder="612345678"
                    flex={1}
                  />
                </HStack>
              </Field.Root>

              <Field.Root>
                <Field.Label>Password</Field.Label>
                <Input
                  type="password"
                  name="password"
                  value={formData.password}
                  onChange={handleChange}
                  placeholder="Create a password"
                  required
                />
              </Field.Root>

              <Field.Root>
                <Field.Label>Confirm Password</Field.Label>
                <Input
                  type="password"
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleChange}
                  placeholder="Confirm your password"
                  required
                />
              </Field.Root>

              <Button
                type="submit"
                w="full"
                colorScheme="purple"
                size="lg"
                loading={isLoading}
              >
                Create Account
              </Button>
            </VStack>
          </form>

          {/* Login Link */}
          <Text textAlign="center" fontSize="sm" color="gray.500">
            Already have an account?{' '}
            <Link to="/login">
              <Text as="span" color="purple.600" _dark={{ color: 'purple.400' }} fontWeight="medium">
                Sign in here
              </Text>
            </Link>
          </Text>
        </VStack>
      </Box>
    </Box>
  )
}
