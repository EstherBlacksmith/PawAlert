import React, { useState, useEffect } from 'react'
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
  Paper
} from '@mui/material'
import { useToast } from '../../../context/ToastContext'
import { PetService } from '../../../services/pet.service'
import { UserService } from '../../../services/user.service'
import { Pet, User } from '../../../types'

const PetsTab: React.FC = () => {
  const [pets, setPets] = useState<Pet[]>([])
  const [users, setUsers] = useState<Map<string, User>>(new Map())
  const [loading, setLoading] = useState(true)
  const toast = useToast()

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
      toast({
        title: 'Error loading pets',
        description: 'Failed to load pets',
        status: 'error',
        duration: 5000,
        isClosable: true,
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
                  <Button size="small" variant="contained" color="primary">
                    View
                  </Button>
                </Stack>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  )
}

export default PetsTab
