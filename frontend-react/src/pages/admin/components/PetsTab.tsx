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
  Stack,
  Paper,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions
} from '@mui/material'
import { useToast } from '../../../context/ToastContext'
import { PetService } from '../../../services/pet.service'
import { UserService } from '../../../services/user.service'
import { Pet, User } from '../../../types'

const PetsTab: React.FC = () => {
  const navigate = useNavigate()
  const [pets, setPets] = useState<Pet[]>([])
  const [users, setUsers] = useState<Map<string, User>>(new Map())
  const [loading, setLoading] = useState(true)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [petToDelete, setPetToDelete] = useState<Pet | null>(null)
  const [deleting, setDeleting] = useState(false)
  const { showToast } = useToast()

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    try {
      setLoading(true)
      // Fetch both pets and users in parallel
      const [petsData, usersData] = await Promise.all([
        PetService.getAllPets(),
        UserService.getAllUsers()
      ])
      
      setPets(petsData)
      
      // Create a lookup map of userId -> user
      const userMap = new Map<string, User>()
      usersData.forEach(user => {
        if (user.userId) {
          userMap.set(user.userId, user)
        }
      })
      setUsers(userMap)
    } catch (error) {
      showToast({
        title: 'Error loading pets',
        description: 'Failed to load pets',
        type: 'error',
        duration: 5000,
      })
    } finally {
      setLoading(false)
    }
  }

  // Helper function to format owner name by looking up userId in the users map
  const formatOwnerName = (pet: Pet): string => {
    if (pet.userId) {
      const user = users.get(pet.userId)
      if (user) {
        const parts = []
        if (user.username) parts.push(user.username)
        if (user.surname) parts.push(user.surname)
        if (parts.length > 0) return parts.join(' ')
      }
      // Fallback to truncated UUID if user not found
      return `${pet.userId.substring(0, 8)}...`
    }
    return 'N/A'
  }

  const handleDeleteClick = (pet: Pet) => {
    setPetToDelete(pet)
    setDeleteDialogOpen(true)
  }

  const handleDeleteConfirm = async () => {
    if (!petToDelete || !petToDelete.petId) return

    try {
      setDeleting(true)
      await PetService.deletePet(petToDelete.petId)
      showToast({
        title: 'Pet deleted',
        description: `Pet "${petToDelete.officialPetName || petToDelete.petId}" has been deleted successfully`,
        type: 'success',
        duration: 5000,
      })
      setDeleteDialogOpen(false)
      setPetToDelete(null)
      // Refresh the pet list
      await loadData()
    } catch (error) {
      showToast({
        title: 'Error deleting pet',
        description: 'Failed to delete pet. Please try again.',
        type: 'error',
        duration: 5000,
      })
    } finally {
      setDeleting(false)
    }
  }

  const handleDeleteCancel = () => {
    setDeleteDialogOpen(false)
    setPetToDelete(null)
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
            <TableCell>Type</TableCell>
            <TableCell>Owner</TableCell>
            <TableCell>Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {pets.map((pet) => (
            <TableRow key={pet.petId}>
              <TableCell>{pet.petId}</TableCell>
              <TableCell>{pet.officialPetName}</TableCell>
              <TableCell>{pet.species}</TableCell>
              <TableCell>{formatOwnerName(pet)}</TableCell>
              <TableCell>
                <Stack direction="row" spacing={1}>
                  <Button 
                    size="small" 
                    variant="contained" 
                    color="primary"
                    onClick={() => navigate(`/admin/pets/${pet.petId}`)}
                  >
                    View
                  </Button>
                  <Button 
                    size="small" 
                    variant="contained" 
                    color="secondary"
                    onClick={() => navigate(`/admin/pets/${pet.petId}/edit`)}
                  >
                    Edit
                  </Button>
                  <Button 
                    size="small" 
                    variant="contained" 
                    color="error"
                    onClick={() => handleDeleteClick(pet)}
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
            Are you sure you want to delete pet "{petToDelete?.officialPetName || petToDelete?.petId}"? 
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

export default PetsTab
