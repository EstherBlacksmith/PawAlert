import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Box,
  CircularProgress,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Button,
  Chip,
  Stack,
  Paper,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions
} from '@mui/material'
import { useToast } from '../../../context/ToastContext'
import { UserService } from '../../../services/user.service'
import { User } from '../../../types'

const UsersTab: React.FC = () => {
  const navigate = useNavigate()
  const [users, setUsers] = useState<User[]>([])
  const [loading, setLoading] = useState(true)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [userToDelete, setUserToDelete] = useState<User | null>(null)
  const [deleting, setDeleting] = useState(false)
  const { showToast } = useToast()

  useEffect(() => {
    loadUsers()
  }, [])

  const loadUsers = async () => {
    try {
      setLoading(true)
      const data = await UserService.getAllUsers()
      setUsers(data)
    } catch (error) {
      showToast({
        title: 'Error loading users',
        description: 'Failed to load users',
        type: 'error',
        duration: 5000,
      })
    } finally {
      setLoading(false)
    }
  }

  const handleDeleteClick = (user: User) => {
    setUserToDelete(user)
    setDeleteDialogOpen(true)
  }

  const handleDeleteConfirm = async () => {
    if (!userToDelete) return

    try {
      setDeleting(true)
      await UserService.deleteUser(userToDelete.id)
      showToast({
        title: 'User deleted',
        description: `User ${userToDelete.username || userToDelete.id} has been deleted successfully`,
        type: 'success',
        duration: 5000,
      })
      setDeleteDialogOpen(false)
      setUserToDelete(null)
      // Refresh the user list
      await loadUsers()
    } catch (error) {
      showToast({
        title: 'Error deleting user',
        description: 'Failed to delete user. Please try again.',
        type: 'error',
        duration: 5000,
      })
    } finally {
      setDeleting(false)
    }
  }

  const handleDeleteCancel = () => {
    setDeleteDialogOpen(false)
    setUserToDelete(null)
  }

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    )
  }

  return (
    <TableContainer component={Paper}>
      <Table>
        <TableHead>
          <TableRow>
            <TableCell>ID</TableCell>
            <TableCell>Name</TableCell>
            <TableCell>Email</TableCell>
            <TableCell>Role</TableCell>
            <TableCell>Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {users.map((user) => (
            <TableRow key={user.id}>
              <TableCell>{user.id}</TableCell>
              <TableCell>{user.name}</TableCell>
              <TableCell>{user.email}</TableCell>
              <TableCell>
                <Chip
                  label={user.role}
                  color={user.role === 'ADMIN' ? 'error' : 'primary'}
                  size="small"
                />
              </TableCell>
              <TableCell>
                <Stack direction="row" spacing={1}>
                  <Button 
                    size="small" 
                    variant="contained" 
                    color="primary"
                    onClick={() => navigate(`/admin/users/${user.id}`)}
                  >
                    View
                  </Button>
                  <Button 
                    size="small" 
                    variant="contained" 
                    color="secondary"
                    onClick={() => navigate(`/admin/users/${user.id}/edit`)}
                  >
                    Edit
                  </Button>
                  <Button 
                    size="small" 
                    variant="contained" 
                    color="error"
                    onClick={() => handleDeleteClick(user)}
                  >
                    Delete
                  </Button>
                </Stack>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialogOpen} onClose={handleDeleteCancel}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete user "{userToDelete?.username || userToDelete?.id}"? 
            This action cannot be undone.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDeleteCancel} disabled={deleting}>
            Cancel
          </Button>
          <Button 
            onClick={handleDeleteConfirm} 
            color="error" 
            variant="contained"
            disabled={deleting}
          >
            {deleting ? 'Deleting...' : 'Delete'}
          </Button>
        </DialogActions>
      </Dialog>
    </TableContainer>
  )
}

export default UsersTab
