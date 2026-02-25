import React, { useState, useEffect, useMemo } from 'react'
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
  DialogActions,
  TextField,
  InputAdornment,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  IconButton,
  Typography,
  TableSortLabel
} from '@mui/material'
import SearchIcon from '@mui/icons-material/Search'
import ClearIcon from '@mui/icons-material/Clear'
import { useToast } from '../../../context/ToastContext'
import { UserService } from '../../../services/user.service'
import { User, UserRole } from '../../../types'

const UsersTab: React.FC = () => {
  const navigate = useNavigate()
  const [users, setUsers] = useState<User[]>([])
  const [loading, setLoading] = useState(true)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [userToDelete, setUserToDelete] = useState<User | null>(null)
  const [deleting, setDeleting] = useState(false)
  const { showToast } = useToast()
  
  // Filter state
  const [searchText, setSearchText] = useState('')
  const [roleFilter, setRoleFilter] = useState<string>('')
  
  // Sorting state
  const [sortField, setSortField] = useState<keyof User | ''>('')
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc')

  useEffect(() => {
    loadUsers()
  }, [])

  // Client-side filtering with useMemo
  const filteredUsers = useMemo(() => {
    return users.filter(user => {
      // Text search filter (username, email, surname)
      const matchesSearch = !searchText || 
        (user.username?.toLowerCase().includes(searchText.toLowerCase())) ||
        (user.email?.toLowerCase().includes(searchText.toLowerCase())) ||
        (user.surname?.toLowerCase().includes(searchText.toLowerCase()))
      
      // Role filter
      const matchesRole = !roleFilter || user.role === roleFilter
      
      return matchesSearch && matchesRole
    })
  }, [users, searchText, roleFilter])

  // Handle sort
  const handleSort = (field: keyof User) => {
    if (sortField === field) {
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc')
    } else {
      setSortField(field)
      setSortDirection('asc')
    }
  }

  // Apply sorting to filtered data
  const sortedUsers = useMemo(() => {
    if (!sortField) return filteredUsers
    
    return [...filteredUsers].sort((a, b) => {
      const aValue = a[sortField]
      const bValue = b[sortField]
      
      // Handle undefined/null values
      if (aValue == null && bValue == null) return 0
      if (aValue == null) return sortDirection === 'asc' ? 1 : -1
      if (bValue == null) return sortDirection === 'asc' ? -1 : 1
      
      // Compare values
      if (aValue < bValue) return sortDirection === 'asc' ? -1 : 1
      if (aValue > bValue) return sortDirection === 'asc' ? 1 : -1
      return 0
    })
  }, [filteredUsers, sortField, sortDirection])

  const handleClearFilters = () => {
    setSearchText('')
    setRoleFilter('')
  }

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
    <Box>
      {/* Filter Bar */}
      <Paper sx={{ p: 2, mb: 2 }}>
        <Stack direction="row" spacing={2} alignItems="center" flexWrap="wrap" useFlexGap>
          <TextField
            placeholder="Search by username, email, or surname..."
            size="small"
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon color="action" />
                </InputAdornment>
              ),
            }}
            sx={{ minWidth: 300, flexGrow: 1 }}
          />
          <FormControl size="small" sx={{ minWidth: 120 }}>
            <InputLabel>Role</InputLabel>
            <Select
              value={roleFilter}
              label="Role"
              onChange={(e) => setRoleFilter(e.target.value)}
            >
              <MenuItem value="">All</MenuItem>
              <MenuItem value="USER">USER</MenuItem>
              <MenuItem value="ADMIN">ADMIN</MenuItem>
            </Select>
          </FormControl>
          <IconButton 
            onClick={handleClearFilters} 
            title="Clear filters"
            disabled={!searchText && !roleFilter}
          >
            <ClearIcon />
          </IconButton>
        </Stack>
        <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
          Showing {filteredUsers.length} of {users.length} users
        </Typography>
      </Paper>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>
                <TableSortLabel
                  active={sortField === 'username'}
                  direction={sortField === 'username' ? sortDirection : 'asc'}
                  onClick={() => handleSort('username')}
                >
                  Name
                </TableSortLabel>
              </TableCell>
              <TableCell>
                <TableSortLabel
                  active={sortField === 'email'}
                  direction={sortField === 'email' ? sortDirection : 'asc'}
                  onClick={() => handleSort('email')}
                >
                  Email
                </TableSortLabel>
              </TableCell>
              <TableCell>
                <TableSortLabel
                  active={sortField === 'role'}
                  direction={sortField === 'role' ? sortDirection : 'asc'}
                  onClick={() => handleSort('role')}
                >
                  Role
                </TableSortLabel>
              </TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {sortedUsers.map((user) => (
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
    </TableContainer>

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
    </Box>
  )
}

export default UsersTab
