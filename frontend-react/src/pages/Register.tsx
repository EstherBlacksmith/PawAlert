import { useState } from 'react'
import { Box, VStack, Heading, Text, Input, Button, HStack, Alert } from '@chakra-ui/react'
import { Link, useNavigate } from 'react-router-dom'
import { FaPaw } from 'react-icons/fa'
import { useAuth } from '../context/AuthContext'
import { ErrorResponse } from '../types'
import { extractError, showSuccessToast } from '../utils/errorUtils'

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
      p={{ base: 2, md: 4, lg: 6 }}
    >
      <Box
        w="full"
        maxW={{ base: 'full', md: '420px', lg: '420px' }}
        bg="rgba(255, 255, 255, 0.95)"
        backdropFilter="blur(10px)"
        borderRadius="2xl"
        border="1px solid"
        borderColor="rgba(255, 255, 255, 0.2)"
        boxShadow="0 8px 32px rgba(0, 0, 0, 0.1)"
        p={{ base: 3, md: 5, lg: 7 }}
        position="relative"
        overflow="hidden"
        animation="slideUp 0.6s ease-out"
        zIndex={2}
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
        <VStack gap={{ base: 3, md: 4, lg: 5 }} position="relative">
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
              size="lg" 
              color="brand.500"
              fontWeight="700"
              letterSpacing="-0.5px"
              mb={2}
            >
              Create Account
            </Heading>
            <Text 
              fontSize="md" 
              color="gray.600" 
              fontWeight="500"
              mt={1}
            >
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
              <VStack gap={{ base: 2.5, md: 3, lg: 4 }} w="full">
                <Box w="full">
                  <Text 
                    as="label"
                    fontSize="sm" 
                    fontWeight="600" 
                    color="gray.700"
                    display="block"
                    mb={2}
                  >
                    Username
                  </Text>
                  <Input
                    name="username"
                    value={formData.username}
                    onChange={handleChange}
                    placeholder="Choose a username"
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
                </Box>

                <Box w="full">
                  <Text 
                    as="label"
                    fontSize="sm" 
                    fontWeight="600" 
                    color="gray.700"
                    display="block"
                    mb={2}
                  >
                    Surname
                  </Text>
                  <Input
                    name="surname"
                    value={formData.surname}
                    onChange={handleChange}
                    placeholder="Your surname"
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
                </Box>

                <Box w="full">
                  <Text 
                    as="label"
                    fontSize="sm" 
                    fontWeight="600" 
                    color="gray.700"
                    display="block"
                    mb={2}
                  >
                    Email
                  </Text>
                  <Input
                    type="email"
                    name="email"
                    value={formData.email}
                    onChange={handleChange}
                    placeholder="your@email.com"
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
                </Box>

                <Box w="full">
                  <Text 
                    as="label"
                    fontSize="sm" 
                    fontWeight="600" 
                    color="gray.700"
                    display="block"
                    mb={2}
                  >
                    Phone Number
                  </Text>
                 <HStack gap={2} w="full">
                   <Box w="100px">
                     <select
                       value={formData.phonePrefix}
                       onChange={handlePrefixChange}
                       style={{
                         width: '100%',
                         padding: '12px',
                         borderRadius: '6px',
                         border: '1px solid rgba(0, 0, 0, 0.1)',
                         backgroundColor: 'rgba(255, 255, 255, 0.8)',
                         fontSize: '16px',
                         transition: 'all 0.3s ease',
                         fontFamily: 'inherit',
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
                  </HStack>
                </Box>

                <Box w="full">
                  <Text 
                    as="label"
                    fontSize="sm" 
                    fontWeight="600" 
                    color="gray.700"
                    display="block"
                    mb={2}
                  >
                    Password
                  </Text>
                  <Input
                    type="password"
                    name="password"
                    value={formData.password}
                    onChange={handleChange}
                    placeholder="Create a password"
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
                </Box>

                <Box w="full">
                  <Text 
                    as="label"
                    fontSize="sm" 
                    fontWeight="600" 
                    color="gray.700"
                    display="block"
                    mb={2}
                  >
                    Confirm Password
                  </Text>
                  <Input
                    type="password"
                    name="confirmPassword"
                    value={formData.confirmPassword}
                    onChange={handleChange}
                    placeholder="Confirm your password"
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
                </Box>

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
                 Create Account
               </Button>
             </VStack>
           </form>

           {/* Login Link */}
           <Text textAlign="center" fontSize="sm" color="gray.600" fontWeight="500">
             Already have an account?{' '}
             <Link to="/login">
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
                 Sign in here
               </Text>
             </Link>
           </Text>
        </VStack>
      </Box>
    </Box>
  )
}
