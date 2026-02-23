import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import {
  Box,
  Button,
  Card,
  CardBody,
  CardHeader,
  Heading,
  Spinner,
  HStack,
  VStack,
  Text,
  Badge,
} from '@chakra-ui/react'
import { useToast } from '../../context/ToastContext'
import { userService } from '../../services/user.service'
import { User } from '../../types'

const UserDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)
  const toast = useToast()

  useEffect(() => {
    loadUser()
  }, [id])

  const loadUser = async () => {
    if (!id) return
    try {
      setLoading(true)
      const data = await userService.getUser(id)
      setUser(data)
    } catch (error) {
      toast({
        title: 'Error loading user',
        description: 'Failed to load user details',
        status: 'error',
        duration: 5000,
        isClosable: true,
      })
      navigate('/admin/dashboard')
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" height="100vh">
        <Spinner size="xl" />
      </Box>
    )
  }

  if (!user) {
    return (
      <Box p={4}>
        <Text>User not found</Text>
        <Button onClick={() => navigate('/admin/dashboard')}>Back to Dashboard</Button>
      </Box>
    )
  }

  return (
    <Box p={4}>
      <Card>
        <CardHeader>
          <Heading>{user.name}</Heading>
        </CardHeader>
        <CardBody>
          <VStack align="start" spacing={4}>
            <Box>
              <Text fontWeight="bold">Email:</Text>
              <Text>{user.email}</Text>
            </Box>
            <Box>
              <Text fontWeight="bold">Phone:</Text>
              <Text>{user.phone || 'N/A'}</Text>
            </Box>
            <Box>
              <Text fontWeight="bold">Role:</Text>
              <Badge colorScheme="blue">{user.role || 'USER'}</Badge>
            </Box>
            <Box>
              <Text fontWeight="bold">Status:</Text>
              <Badge colorScheme="green">Active</Badge>
            </Box>
            <HStack spacing={2}>
              <Button colorScheme="blue" onClick={() => navigate(`/admin/users/${id}/edit`)}>
                Edit
              </Button>
              <Button colorScheme="gray" onClick={() => navigate('/admin/dashboard')}>
                Back
              </Button>
            </HStack>
          </VStack>
        </CardBody>
      </Card>
    </Box>
  )
}

export default UserDetail
