import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import {
  Box,
  Button,
  Card,
  CardContent,
  Typography,
  CircularProgress,
  Stack,
  Chip,
  Paper
} from '@mui/material'
import { FaArrowLeft, FaEdit } from 'react-icons/fa'
import { useToast } from '../../context/ToastContext'
import { PetService } from '../../services/pet.service'
import { Pet } from '../../types'

const PetDetail: React.FC = () => {
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
      const data = await PetService.getPetById(id)
      setPet(data)
    } catch (error) {
      toast({
        title: 'Error loading pet',
        description: 'Failed to load pet details',
        status: 'error',
        duration: 5000,
        isClosable: true,
      })
      navigate('/pets')
    } finally {
      setLoading(false)
    }
  }

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '400px' }}>
        <CircularProgress />
      </Box>
    )
  }

  if (!pet) {
    return (
      <Box sx={{ p: 2 }}>
        <Typography>Pet not found</Typography>
      </Box>
    )
  }

  return (
    <Paper sx={{ p: 3, bgcolor: 'rgba(255, 255, 255, 0.85)', borderRadius: 2, boxShadow: 3 }}>
      <Button variant="text" sx={{ mb: 2 }} onClick={() => navigate(-1)} startIcon={<FaArrowLeft />}>
        Back
      </Button>

      <Card elevation={2}>
        <CardContent>
          <Typography variant="h5" mb={2}>{pet.officialPetName}</Typography>
          <Stack spacing={2}>
            <Box>
              <Typography fontWeight="bold">Working Name:</Typography>
              <Typography>{pet.workingPetName || 'N/A'}</Typography>
            </Box>
            <Box>
              <Typography fontWeight="bold">Species:</Typography>
              <Typography>{pet.species}</Typography>
            </Box>
            <Box>
              <Typography fontWeight="bold">Breed:</Typography>
              <Typography>{pet.breed || 'N/A'}</Typography>
            </Box>
            <Box>
              <Typography fontWeight="bold">Size:</Typography>
              <Typography>{pet.size || 'N/A'}</Typography>
            </Box>
            <Box>
              <Typography fontWeight="bold">Color:</Typography>
              <Typography>{pet.color || 'N/A'}</Typography>
            </Box>
            <Box>
              <Typography fontWeight="bold">Gender:</Typography>
              <Typography>{pet.gender || 'N/A'}</Typography>
            </Box>
            <Box>
              <Typography fontWeight="bold">Chip Number:</Typography>
              <Typography>{pet.chipNumber || 'N/A'}</Typography>
            </Box>
            <Box>
              <Typography fontWeight="bold">Description:</Typography>
              <Typography>{pet.petDescription || 'N/A'}</Typography>
            </Box>
            {pet.petImage && (
              <Box>
                <Typography fontWeight="bold" mb={1}>Photo:</Typography>
                <Box
                  component="img"
                  src={pet.petImage}
                  alt={pet.officialPetName}
                  sx={{ maxWidth: '300px', borderRadius: 1 }}
                />
              </Box>
            )}
            <Stack direction="row" spacing={2} sx={{ mt: 2 }}>
              <Button variant="contained" color="primary" onClick={() => navigate(`/pets/${id}/edit`)} startIcon={<FaEdit />}>
                Edit
              </Button>
              <Button variant="outlined" onClick={() => navigate('/pets')}>
                Back
              </Button>
            </Stack>
          </Stack>
        </CardContent>
      </Card>
    </Paper>
  )
}

export default PetDetail
