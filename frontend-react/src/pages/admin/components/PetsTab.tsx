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
  
  // Filter state
  const [searchText, setSearchText] = useState('')
  const [speciesFilter, setSpeciesFilter] = useState<string>('')
  
  // Sorting state
  type PetSortField = 'officialPetName' | 'species' | 'owner'
  const [sortField, setSortField] = useState<PetSortField | ''>('')
  const [sortDirection, setSortDirection] = useState<'asc' | 'desc'>('asc')

  useEffect(() => {
    loadData()
  }, [])

  // Client-side filtering with useMemo
  const filteredPets = useMemo(() => {
    return pets.filter(pet => {
      // Text search filter (pet name, working name, chip number)
      const matchesSearch = !searchText || 
        (pet.officialPetName?.toLowerCase().includes(searchText.toLowerCase())) ||
        (pet.workingPetName?.toLowerCase().includes(searchText.toLowerCase())) ||
        (pet.chipNumber?.toLowerCase().includes(searchText.toLowerCase()))
      
      // Species filter
      const matchesSpecies = !speciesFilter || pet.species === speciesFilter
      
      return matchesSearch && matchesSpecies
    })
  }, [pets, searchText, speciesFilter])

  // Handle sort
  const handleSort = (field: PetSortField) => {
    if (sortField === field) {
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc')
    } else {
      setSortField(field)
      setSortDirection('asc')
    }
  }

  // Apply sorting to filtered data
  const sortedPets = useMemo(() => {
    if (!sortField) return filteredPets
    
    return [...filteredPets].sort((a, b) => {
      let aValue: string | undefined
      let bValue: string | undefined
      
      // Handle owner field specially since it's computed
      if (sortField === 'owner') {
        aValue = formatOwnerName(a)
        bValue = formatOwnerName(b)
      } else {
        aValue = a[sortField] as string | undefined
        bValue = b[sortField] as string | undefined
      }
      
      // Handle undefined/null values
      if (aValue == null && bValue == null) return 0
      if (aValue == null) return sortDirection === 'asc' ? 1 : -1
      if (bValue == null) return sortDirection === 'asc' ? -1 : 1
      
      // Compare values
      if (aValue < bValue) return sortDirection === 'asc' ? -1 : 1
      if (aValue > bValue) return sortDirection === 'asc' ? 1 : -1
      return 0
    })
  }, [filteredPets, sortField, sortDirection, users])

  const handleClearFilters = () => {
    setSearchText('')
    setSpeciesFilter('')
  }

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
    <Box>
      {/* Filter Bar */}
      <Paper sx={{ p: 2, mb: 2 }}>
        <Stack direction="row" spacing={2} alignItems="center" flexWrap="wrap" useFlexGap>
          <TextField
            placeholder="Search by pet name or chip number..."
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
            <InputLabel>Species</InputLabel>
            <Select
              value={speciesFilter}
              label="Species"
              onChange={(e) => setSpeciesFilter(e.target.value)}
            >
              <MenuItem value="">All</MenuItem>
              <MenuItem value="DOG">DOG</MenuItem>
              <MenuItem value="CAT">CAT</MenuItem>
              <MenuItem value="BIRD">BIRD</MenuItem>
              <MenuItem value="OTHER">OTHER</MenuItem>
            </Select>
          </FormControl>
          <IconButton 
            onClick={handleClearFilters} 
            title="Clear filters"
            disabled={!searchText && !speciesFilter}
          >
            <ClearIcon />
          </IconButton>
        </Stack>
        <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
          Showing {filteredPets.length} of {pets.length} pets
        </Typography>
      </Paper>

      <TableContainer component={Paper}>
        <Table>
          <TableHead>
            <TableRow>
              <TableCell>ID</TableCell>
              <TableCell>
                <TableSortLabel
                  active={sortField === 'officialPetName'}
                  direction={sortField === 'officialPetName' ? sortDirection : 'asc'}
                  onClick={() => handleSort('officialPetName')}
                >
                  Name
                </TableSortLabel>
              </TableCell>
              <TableCell>
                <TableSortLabel
                  active={sortField === 'species'}
                  direction={sortField === 'species' ? sortDirection : 'asc'}
                  onClick={() => handleSort('species')}
                >
                  Type
                </TableSortLabel>
              </TableCell>
              <TableCell>
                <TableSortLabel
                  active={sortField === 'owner'}
                  direction={sortField === 'owner' ? sortDirection : 'asc'}
                  onClick={() => handleSort('owner')}
                >
                  Owner
                </TableSortLabel>
              </TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {sortedPets.map((pet) => (
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
    </TableContainer>

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
    </Box>
  )
}

export default PetsTab
