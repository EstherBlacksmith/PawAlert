import { useState, useEffect } from 'react'
import { Box, Heading, Button, VStack, Field, Input, Textarea, Flex, Spinner, Alert } from '@chakra-ui/react'
import { useNavigate, useParams } from 'react-router-dom'
import { alertService } from '../../services/alert.service'
import { useAuth } from '../../context/AuthContext'
import { ErrorResponse } from '../../types'

export default function AlertEdit() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { user, isAdmin } = useAuth()
  const [isLoading, setIsLoading] = useState(false)
  const [isFetching, setIsFetching] = useState(true)
  const [error, setError] = useState<ErrorResponse | null>(null)
  
  const [formData, setFormData] = useState({
    title: '',
    description: '',
  })

  useEffect(() => {
    const fetchAlert = async () => {
      try {
        if (id) {
          const alert = await alertService.getAlert(id)
          
          // Check if user can edit this alert (closed alerts can only be edited by admins)
          if (alert.status === 'CLOSED' && !isAdmin()) {
            // Non-admin cannot edit closed alerts - redirect to detail page
            navigate(`/alerts/${id}`, { replace: true })
            return
          }
          
          setFormData({
            title: alert.title,
            description: alert.description,
          })
        }
      } catch (error: any) {
        console.error('Error fetching alert:', error)
        if (error.response?.status === 404) {
          navigate('/alerts', { replace: true })
        }
      } finally {
        setIsFetching(false)
      }
    }
    fetchAlert()
  }, [id])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!user) {
      setError({
        status: 401,
        error: 'Unauthorized',
        message: 'You must be logged in to edit an alert.'
      })
      return
    }

    setIsLoading(true)
    setError(null)

    try {
      if (id) {
        await alertService.updateAlert(id, user.userId, {
          title: formData.title.trim(),
          description: formData.description.trim(),
        })
        navigate(`/alerts/${id}`)
      }
    } catch (error: any) {
      console.error('Error updating alert:', error)
      if (error.response?.data) {
        setError(error.response.data)
      } else {
        setError({
          status: error.response?.status || 500,
          error: error.response?.statusText || 'Error',
          message: 'An unexpected error occurred. Please try again.'
        })
      }
    } finally {
      setIsLoading(false)
    }
  }

  if (isFetching) {
    return (
      <Flex justify="center" align="center" minH="300px">
        <Spinner size="xl" color="brand.500" />
      </Flex>
    )
  }

  return (
    <Box w="100%" mx="auto" bg="rgba(255, 255, 255, 0.85)" p={6} borderRadius="lg" boxShadow="lg">
      <Heading size="lg" mb={6} color="gray.800" _dark={{ color: 'white' }}>
        Edit Alert
      </Heading>

      {error && (
        <Alert.Root status="error" mb={6} borderRadius="md">
          <Alert.Indicator />
          <Box flex="1">
            <Alert.Title fontSize="sm" fontWeight="bold">
              {error.error || 'Error'}
            </Alert.Title>
            <Alert.Description fontSize="sm">
              {error.message}
            </Alert.Description>
          </Box>
        </Alert.Root>
      )}

      <Box as="form" onSubmit={handleSubmit}>
        <VStack gap={4}>
          <Field.Root>
            <Field.Label>Title *</Field.Label>
            <Input 
              name="title" 
              value={formData.title} 
              onChange={handleChange} 
              required 
              placeholder="Enter alert title"
            />
          </Field.Root>

          <Field.Root>
            <Field.Label>Description *</Field.Label>
            <Textarea 
              name="description" 
              value={formData.description} 
              onChange={handleChange} 
              required
              placeholder="Enter alert description"
              rows={4}
            />
          </Field.Root>

          <Flex w="full" gap={4} pt={4}>
            <Button 
              type="button" 
              variant="outline" 
              onClick={() => navigate(`/alerts/${id}`)}
              flex={1}
            >
              Cancel
            </Button>
            <Button 
              type="submit" 
              colorPalette="brand" 
              bg="brand.500"
              _hover={{ bg: 'brand.600' }}
              flex={1}
              loading={isLoading}
            >
              Save Changes
            </Button>
          </Flex>
        </VStack>
      </Box>
    </Box>
  )
}
