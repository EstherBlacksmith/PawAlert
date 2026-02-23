import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import {
  Box,
  Button,
  Card,
  CardContent,
  CardHeader,
  Typography,
  CircularProgress,
  Stack,
  Chip
} from '@mui/material'
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
        <CircularProgress />
      </Box>
    )
  }

  if (!user) {
    return (
      <Box p={2}>
        <Typography>User not found</Typography>
        <Button onClick={() => navigate('/admin/dashboard')}>Back to Dashboard</Button>
      </Box>
    )
  }

  return (
    <Box p={2}>
      <Card>
        <CardHeader title={user.name} />
        <CardContent>
          <Stack spacing={2}>
            <Box>
              <Typography variant="subtitle2" fontWeight="bold">Email:</Typography>
              <Typography>{user.email}</Typography>
            </Box>
            <Box>
              <Typography variant="subtitle2" fontWeight="bold">Phone:</Typography>
              <Typography>{user.phone || 'N/A'}</Typography>
            </Box>
            <Box>
              <Typography variant="subtitle2" fontWeight="bold">Role:</Typography>
              <Chip label={user.role || 'USER'} color="primary" size="small" />
            </Box>
            <Box>
              <Typography variant="subtitle2" fontWeight="bold">Status:</Typography>
              <Chip label="Active" color="success" size="small" />
            </Box>
            <Stack direction="row" spacing={1}>
              <Button variant="contained" color="primary" onClick={() => navigate(`/admin/users/${id}/edit`)}>
                Edit
              </Button>
              <Button variant="contained" onClick={() => navigate('/admin/dashboard')}>
                Back
              </Button>
            </Stack>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  )
}

export default UserDetail
