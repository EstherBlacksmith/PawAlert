import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import {
  Box,
  Button,
  Card,
  CardContent,
  CardHeader,
  Typography,
  TextField,
  Stack,
  CircularProgress,
  FormControl,
  InputLabel,
  Select,
  MenuItem
} from '@mui/material'
import { useToast } from '../../context/ToastContext'
import { userService } from '../../services/user.service'
import { User } from '../../types'

const UserEdit: React.FC = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [user, setUser] = useState<User | null>(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)
  const { showToast } = useToast()

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
      showToast({
        title: 'Error loading user',
        description: 'Failed to load user details',
        type: 'error',
        duration: 5000,
        closable: true,
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
      await userService.adminUpdateUser(user.userId, user)
      showToast({
        title: 'Success',
        description: 'User updated successfully',
        type: 'success',
        duration: 5000,
        closable: true,
      })
      navigate('/admin/dashboard')
    } catch (error) {
      showToast({
        title: 'Error',
        description: 'Failed to update user',
        type: 'error',
        duration: 5000,
        closable: true,
      })
    } finally {
      setSaving(false)
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
        <Button onClick={() => navigate('/admin/dashboard')}>Back to Dashboard</Button>
      </Box>
    )
  }

  return (
    <Box p={2}>
      <Card>
        <CardHeader title="Edit User" />
        <CardContent>
          <Stack spacing={3}>
            <TextField
              label="Username"
              value={user.username || ''}
              onChange={(e) => setUser({ ...user, username: e.target.value })}
              fullWidth
            />

            <TextField
              label="Surname"
              value={user.surname || ''}
              onChange={(e) => setUser({ ...user, surname: e.target.value })}
              fullWidth
            />

            <TextField
              label="Email"
              type="email"
              value={user.email || ''}
              onChange={(e) => setUser({ ...user, email: e.target.value })}
              fullWidth
            />

            <TextField
              label="Phone"
              value={user.phoneNumber || ''}
              onChange={(e) => setUser({ ...user, phoneNumber: e.target.value })}
              fullWidth
            />

            <FormControl fullWidth>
              <InputLabel>Role</InputLabel>
              <Select
                value={user.role || 'USER'}
                label="Role"
                onChange={(e) => setUser({ ...user, role: e.target.value })}
              >
                <MenuItem value="USER">User</MenuItem>
                <MenuItem value="ADMIN">Admin</MenuItem>
              </Select>
            </FormControl>

            <Stack direction="row" spacing={2}>
              <Button
                variant="contained"
                color="primary"
                onClick={handleSave}
                disabled={saving}
              >
                {saving ? 'Saving...' : 'Save'}
              </Button>
              <Button
                variant="contained"
                onClick={() => navigate('/admin/dashboard')}
              >
                Cancel
              </Button>
            </Stack>
          </Stack>
        </CardContent>
      </Card>
    </Box>
  )
}

export default UserEdit
