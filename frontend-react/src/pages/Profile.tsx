import { useState, useEffect } from 'react'
import { Box, Heading, Button, VStack, Field, Input, Text, Card, Flex, Avatar, Stack, Switch, Spinner, Alert, SimpleGrid } from '@chakra-ui/react'
import { useNavigate } from 'react-router-dom'
import { FaArrowLeft } from 'react-icons/fa'
import { useAuth } from '../context/AuthContext'
import { userService } from '../services/user.service'
import { ErrorResponse } from '../types'
import { extractError, showSuccessToast, showErrorToast } from '../utils/errorUtils'
import { GiUser, GiSave, GiBell, GiSmartphone, GiMail } from '../components/icons'

export default function Profile() {
  const navigate = useNavigate()
  const { user, setUser } = useAuth()
  const [isLoading, setIsLoading] = useState(false)
  const [isFetching, setIsFetching] = useState(true)
  const [error, setError] = useState<ErrorResponse | null>(null)
  const [userData, setUserData] = useState<{
    username: string
    email: string
    surname: string
    phoneNumber: string
    telegramChatId: string
    emailNotificationsEnabled: boolean
    telegramNotificationsEnabled: boolean
  } | null>(null)
  
  // Fetch fresh user data from backend on mount
  useEffect(() => {
    const fetchUserData = async () => {
      if (!user?.userId) {
        setIsFetching(false)
        return
      }
      
      try {
        const freshUser = await userService.getUser(user.userId)
        const data = {
          username: freshUser.username || '',
          email: freshUser.email || '',
          surname: freshUser.surname || '',
          phoneNumber: freshUser.phoneNumber || '',
          telegramChatId: freshUser.telegramChatId || '',
          emailNotificationsEnabled: freshUser.emailNotificationsEnabled ?? true,
          telegramNotificationsEnabled: freshUser.telegramNotificationsEnabled ?? true,
        }
        setUserData(data)
        // Also update the auth context with fresh data
        setUser(freshUser)
      } catch (error) {
        console.error('Error fetching user data:', error)
         // Fall back to cached data
         setUserData({
           username: user?.username || '',
           email: user?.email || '',
           surname: user?.surname || '',
           phoneNumber: user?.phoneNumber || '',
           telegramChatId: user?.telegramChatId || '',
           emailNotificationsEnabled: user?.emailNotificationsEnabled ?? true,
           telegramNotificationsEnabled: user?.telegramNotificationsEnabled ?? true,
         })
      } finally {
        setIsFetching(false)
      }
    }
    
    fetchUserData()
  }, [user?.userId])
  
   const [formData, setFormData] = useState({
     username: '',
     email: '',
     surname: '',
     phoneNumber: '',
     telegramChatId: '',
     emailNotificationsEnabled: true,
     telegramNotificationsEnabled: true,
   })
  
  // Update form data when user data is fetched
  useEffect(() => {
    if (userData) {
      setFormData({
        ...userData,
      })
    }
  }, [userData])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleSwitchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.checked })
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!user?.userId || !userData) return
    
    setIsLoading(true)
    setError(null)
    try {
      const updatedUser = await userService.updateUser(user.userId, formData, userData)
      setUser(updatedUser)
      setUserData({
        username: updatedUser.username || '',
        email: updatedUser.email || '',
        surname: updatedUser.surname || '',
        phoneNumber: updatedUser.phoneNumber || '',
        telegramChatId: updatedUser.telegramChatId || '',
        emailNotificationsEnabled: updatedUser.emailNotificationsEnabled ?? true,
        telegramNotificationsEnabled: updatedUser.telegramNotificationsEnabled ?? true,
      })
      showSuccessToast('Profile Updated', 'Your changes have been saved successfully.')
    } catch (error) {
      const friendlyError = extractError(error)
      setError(friendlyError)
      showErrorToast(error)
    } finally {
      setIsLoading(false)
    }
  }
  
  if (isFetching) {
    return (
      <Box maxW="600px" mx="auto" textAlign="center" py={10}>
        <Spinner size="xl" />
        <Text mt={4}>Loading profile...</Text>
      </Box>
    )
  }

  return (
    <Box maxW="900px" mx="auto" bg="rgba(255, 255, 255, 0.85)" p={6} borderRadius="lg" boxShadow="lg">
      <Button variant="ghost" mb={4} onClick={() => navigate('/')}>
         <FaArrowLeft style={{ marginRight: '8px' }} />
         Back
       </Button>

      <Heading size="lg" mb={6} color="gray.800" _dark={{ color: 'white' }}>
        Profile Settings
      </Heading>

      {error && (
        <Alert.Root status="error" mb={4}>
          <Alert.Indicator />
          <Alert.Title>{error.error}</Alert.Title>
          <Alert.Description>{error.message}</Alert.Description>
        </Alert.Root>
      )}

      <SimpleGrid columns={{ base: 1, md: 2 }} gap={4} mb={6}>
        <Card.Root>
          <Card.Body>
            <Heading size="md" mb={4}>
              Edit Profile
            </Heading>

            <Box as="form" onSubmit={handleSubmit}>
              <VStack gap={4}>
                <Field.Root>
                  <Field.Label>Username</Field.Label>
                  <Input name="username" value={formData.username} onChange={handleChange} />
                </Field.Root>

                <Field.Root>
                  <Field.Label>Surname (Apellido)</Field.Label>
                  <Input name="surname" value={formData.surname} onChange={handleChange} placeholder="Your surname" />
                </Field.Root>

                <Field.Root>
                  <Field.Label>Email</Field.Label>
                  <Input name="email" type="email" value={formData.email} onChange={handleChange} />
                </Field.Root>

                <Field.Root>
                  <Field.Label>Phone Number</Field.Label>
                  <Input name="phoneNumber" value={formData.phoneNumber} onChange={handleChange} placeholder="+34 600 000 000" />
                  <Field.HelperText>Format: optional + followed by 7-20 digits/symbols (e.g., +34600123456)</Field.HelperText>
                </Field.Root>

                <Field.Root>
                  <Field.Label>Telegram Chat ID</Field.Label>
                  <Input name="telegramChatId" value={formData.telegramChatId} onChange={handleChange} placeholder="Optional" />
                  <Field.HelperText>Get your Chat ID from @userinfobot on Telegram</Field.HelperText>
                </Field.Root>
              </VStack>
            </Box>
          </Card.Body>
        </Card.Root>

        <Card.Root>
          <Card.Body>
            <Heading size="md" mb={4}>
              Notifications
            </Heading>
            <Stack gap={4}>
               <Flex justify="space-between" align="center">
                 <Box>
                   <Text fontWeight="medium">Email Notifications</Text>
                   <Text fontSize="sm" color="gray.700">
                     Receive alerts via email
                   </Text>
                 </Box>
                 <Switch.Root name="emailNotificationsEnabled" checked={formData.emailNotificationsEnabled} onChange={handleSwitchChange} colorPalette="success">
                   <Switch.HiddenInput />
                   <Switch.Control>
                     <Switch.Thumb />
                   </Switch.Control>
                 </Switch.Root>
               </Flex>
               <Flex justify="space-between" align="center">
                 <Box>
                   <Text fontWeight="medium">Telegram Notifications</Text>
                   <Text fontSize="sm" color="gray.700">
                     Receive alerts via Telegram
                   </Text>
                 </Box>
                 <Switch.Root name="telegramNotificationsEnabled" checked={formData.telegramNotificationsEnabled} onChange={handleSwitchChange} colorPalette="success">
                   <Switch.HiddenInput />
                   <Switch.Control>
                     <Switch.Thumb />
                   </Switch.Control>
                 </Switch.Root>
               </Flex>
            </Stack>
          </Card.Body>
        </Card.Root>
      </SimpleGrid>

      <Button 
        type="submit" 
        colorPalette="brand" 
        bg="brand.500"
        _hover={{ bg: 'brand.600' }}
        w="full" 
        loading={isLoading}
        onClick={handleSubmit}
      >
        <GiSave style={{ marginRight: '8px' }} />
        Save Changes
      </Button>
    </Box>
  )
}
