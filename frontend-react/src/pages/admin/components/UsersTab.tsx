import React, { useState, useEffect } from 'react'
import {
  Box,
  Button,
  Spinner,
  HStack,
  Badge,
  Table,
  Thead,
  Tr,
  Th,
  Td,
} from '@chakra-ui/react'
import { useToast } from '../../../context/ToastContext'
import { UserService } from '../../../services/user.service'
import { User } from '../../../types'

const UsersTab: React.FC = () => {
  const [users, setUsers] = useState<User[]>([])
  const [loading, setLoading] = useState(true)
  const toast = useToast()

  useEffect(() => {
    loadUsers()
  }, [])

  const loadUsers = async () => {
    try {
      setLoading(true)
      const data = await UserService.getAllUsers()
      setUsers(data)
    } catch (error) {
      toast({
        title: 'Error loading users',
        description: 'Failed to load users',
        status: 'error',
        duration: 5000,
        isClosable: true,
      })
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minH="400px">
        <Spinner />
      </Box>
    )
  }

  return (
    <Box overflowX="auto">
      <Table>
        <Thead>
          <Tr>
            <Th>ID</Th>
            <Th>Name</Th>
            <Th>Email</Th>
            <Th>Role</Th>
            <Th>Actions</Th>
          </Tr>
        </Thead>
        <tbody>
          {users.map((user) => (
            <tr key={user.id}>
              <td>{user.id}</td>
              <td>{user.name}</td>
              <td>{user.email}</td>
              <td>
                <Badge colorScheme={user.role === 'ADMIN' ? 'red' : 'blue'}>
                  {user.role}
                </Badge>
              </td>
              <td>
                <HStack spacing={2}>
                  <Button size="sm" colorScheme="blue">
                    Edit
                  </Button>
                  <Button size="sm" colorScheme="red">
                    Delete
                  </Button>
                </HStack>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </Box>
  )
}

export default UsersTab
