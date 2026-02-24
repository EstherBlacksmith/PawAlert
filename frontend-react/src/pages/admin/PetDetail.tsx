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
  Chip,
  Grid,
  Paper,
  Divider
} from '@mui/material'
import { FaArrowLeft, FaEdit, FaTrash } from 'react-icons/fa'
import { useToast } from '../../context/ToastContext'
import { petService } from '../../services/pet.service'
import { Pet } from '../../types'

const AdminPetDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const [pet, setPet] = useState<Pet | null>(null)
  const [loading, setLoading] = useState(true)
  const [deleting, setDeleting] = useState(false)
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

  const handleDelete = async () => {
    if (!pet || !id) return
    
    if (!window.confirm(`Are you sure you want to delete "${pet.officialPetName}"? This action cannot be undone.`)) {
      return
    }

    try {
      setDeleting(true)
      await petService.adminDeletePet(id)
      toast({
        title: 'Pet deleted',
        description: 'The pet has been successfully deleted',
        status: 'success',
        duration: 3000,
        isClosable: true,
      })
      navigate('/admin/dashboard')
    } catch (error) {
      toast({
        title: 'Error deleting pet',
        description: 'Failed to delete pet',
        status: 'error',
        duration: 5000,
        isClosable: true,
      })
    } finally {
      setDeleting(false)
    }
  }

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    )
  }

  if (!pet) {
    return (
      <Box p={3}>
        <Typography>Pet not found</Typography>
        <Button onClick={() => navigate('/admin/dashboard')}>Back to Dashboard</Button>
      </Box>
    )
  }

  return (
    <Paper sx={{ p: 3, maxWidth: 800, mx: 'auto' }}>
      <Button 
        variant="text" 
        sx={{ mb: 2 }} 
        onClick={() => navigate('/admin/dashboard')}
        startIcon={<FaArrowLeft />}
      >
        Back to Dashboard
      </Button>

      <Card>
        <CardHeader 
          title={pet.officialPetName}
          subheader={pet.workingPetName ? `Also known as: ${pet.workingPetName}` : undefined}
        />
        <Divider />
        <CardContent>
          {/* Pet Image */}
          {pet.petImage && (
            <Box sx={{ mb: 3, textAlign: 'center' }}>
              <Box
                component="img"
                src={pet.petImage}
                alt={pet.officialPetName}
                sx={{
                  maxWidth: '100%',
                  maxHeight: 300,
                  borderRadius: 2,
                  boxShadow: 2
                }}
              />
            </Box>
          )}

          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle2" fontWeight="bold" color="text.secondary">
                Species
              </Typography>
              <Typography variant="body1">
                <Chip 
                  label={pet.species} 
                  color="primary" 
                  size="small" 
                  sx={{ textTransform: 'capitalize' }}
                />
              </Typography>
            </Grid>

            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle2" fontWeight="bold" color="text.secondary">
                Breed
              </Typography>
              <Typography variant="body1">{pet.breed || 'N/A'}</Typography>
            </Grid>

            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle2" fontWeight="bold" color="text.secondary">
                Size
              </Typography>
              <Typography variant="body1">
                {pet.size ? (
                  <Chip label={pet.size} size="small" sx={{ textTransform: 'capitalize' }} />
                ) : 'N/A'}
              </Typography>
            </Grid>

            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle2" fontWeight="bold" color="text.secondary">
                Gender
              </Typography>
              <Typography variant="body1">
                {pet.gender ? (
                  <Chip label={pet.gender} size="small" sx={{ textTransform: 'capitalize' }} />
                ) : 'N/A'}
              </Typography>
            </Grid>

            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle2" fontWeight="bold" color="text.secondary">
                Color
              </Typography>
              <Typography variant="body1">{pet.color || 'N/A'}</Typography>
            </Grid>

            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle2" fontWeight="bold" color="text.secondary">
                Chip Number
              </Typography>
              <Typography variant="body1">{pet.chipNumber || 'N/A'}</Typography>
            </Grid>

            <Grid item xs={12}>
              <Typography variant="subtitle2" fontWeight="bold" color="text.secondary">
                Description
              </Typography>
              <Typography variant="body1">{pet.petDescription || 'No description provided'}</Typography>
            </Grid>

            <Grid item xs={12}>
              <Typography variant="subtitle2" fontWeight="bold" color="text.secondary">
                Owner User ID
              </Typography>
              <Typography variant="body2" sx={{ fontFamily: 'monospace', bgcolor: 'grey.100', p: 1, borderRadius: 1 }}>
                {pet.userId || 'N/A'}
              </Typography>
            </Grid>
          </Grid>

          <Divider sx={{ my: 3 }} />

          <Stack direction="row" spacing={2}>
            <Button 
              variant="contained" 
              color="primary" 
              onClick={() => navigate(`/admin/pets/${id}/edit`)}
              startIcon={<FaEdit />}
            >
              Edit
            </Button>
            <Button 
              variant="outlined" 
              color="error" 
              onClick={handleDelete}
              disabled={deleting}
              startIcon={<FaTrash />}
            >
              {deleting ? 'Deleting...' : 'Delete'}
            </Button>
          </Stack>
        </CardContent>
      </Card>
    </Paper>
  )
}

export default AdminPetDetail
