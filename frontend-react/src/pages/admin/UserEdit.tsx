import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import {
  Box,
  Button,
  Card,
  CardBody,
  CardHeader,
  Heading,
  Input,
  VStack,
  HStack,
  FormControl,
  FormLabel,
  Spinner,
  Select,
} from '@chakra-ui/react'
import { useToast } from '../../context/ToastContext'
import { userService } from '../../services/user.service'
import { User } from '../../types'

const UserEdit: React.FC = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
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

  const handleSave = async () => {
    if (!user) return
    try {
      setSaving(true)
      await userService.updateUser(user.id, user)
      toast({
        title: 'Success',
        description: 'User updated successfully',
        status: 'success',
        duration: 5000,
        isClosable: true,
      })
      navigate('/admin/dashboard')
    } catch (error) {
      toast({
        title: 'Error',
        description: 'Failed to update user',
        status: 'error',
        duration: 5000,
        isClosable: true,
      })
    } finally {
      setSaving(false)
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
        <Button onClick={() => navigate('/admin/dashboard')}>Back to Dashboard</Button>
      </Box>
    )
  }

  return (
    <Box p={4}>
      <Card>
        <CardHeader>
          <Heading>Edit User</Heading>
        </CardHeader>
        <CardBody>
          <VStack spacing={4} align="stretch">
            <FormControl>
              <FormLabel>Name</FormLabel>
              <Input
                value={user.name}
                onChange={(e) => setUser({ ...user, name: e.target.value })}
              />
            </FormControl>

            <FormControl>
              <FormLabel>Email</FormLabel>
              <Input
                type="email"
                value={user.email}
                onChange={(e) => setUser({ ...user, email: e.target.value })}
              />
            </FormControl>

            <FormControl>
              <FormLabel>Phone</FormLabel>
              <Input
                value={user.phone || ''}
                onChange={(e) => setUser({ ...user, phone: e.target.value })}
              />
            </FormControl>

            <FormControl>
              <FormLabel>Role</FormLabel>
              <Select
                value={user.role || 'USER'}
                onChange={(e) => setUser({ ...user, role: e.target.value })}
              >
                <option value="USER">User</option>
                <option value="ADMIN">Admin</option>
              </Select>
            </FormControl>

            <HStack spacing={2}>
              <Button
                colorScheme="blue"
                onClick={handleSave}
                isLoading={saving}
              >
                Save
              </Button>
              <Button
                colorScheme="gray"
                onClick={() => navigate('/admin/dashboard')}
              >
                Cancel
              </Button>
            </HStack>
          </VStack>
        </CardBody>
      </Card>
    </Box>
  )
}

export default UserEdit
