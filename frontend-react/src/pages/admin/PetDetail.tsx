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
import { petService } from '../../services/pet.service'
import { Pet } from '../../types'

const AdminPetDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [pet, setPet] = useState<Pet | null>(null)
  const [loading, setLoading] = useState(true)
  const toast = useToast()

  useEffect(() => {
    loadPet()
  }, [id])

  const loadPet = async () => {
    if (!id) return
    try {
      setLoading(true)
      const data = await petService.getPet(id)
      setPet(data)
    } catch (error) {
      toast({
        title: 'Error loading pet',
        description: 'Failed to load pet details',
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

  if (!pet) {
    return (
      <Box p={2}>
        <Typography>Pet not found</Typography>
        <Button onClick={() => navigate('/admin/dashboard')}>Back to Dashboard</Button>
      </Box>
    )
  }

  return (
    <Box p={2}>
      <Card>
        <CardHeader title={pet.name} />
        <CardContent>
          <Stack spacing={2}>
            <Box>
              <Typography variant="subtitle2" fontWeight="bold">Type:</Typography>
              <Typography>{pet.type}</Typography>
            </Box>
            <Box>
              <Typography variant="subtitle2" fontWeight="bold">Breed:</Typography>
              <Typography>{pet.breed || 'N/A'}</Typography>
            </Box>
            <Box>
              <Typography variant="subtitle2" fontWeight="bold">Owner:</Typography>
              <Typography>{pet.owner?.name || 'N/A'}</Typography>
            </Box>
            <Box>
              <Typography variant="subtitle2" fontWeight="bold">Status:</Typography>
              <Chip label="Active" color="success" size="small" />
            </Box>
            <Stack direction="row" spacing={1}>
              <Button variant="contained" color="primary" onClick={() => navigate(`/pets/${id}/edit`)}>
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

export default AdminPetDetail
