import { useEffect, useState } from 'react'
import { Box, Typography, Button, Grid, Card, CardContent, CardMedia, CircularProgress, Chip, IconButton, Paper } from '@mui/material'
import { Link, useNavigate } from 'react-router-dom'
import { FaPlus, FaEdit, FaTrash, FaSearch as FaSearchIcon, FaHeart, FaArrowLeft } from 'react-icons/fa'
import { petService } from '../../services/pet.service'
import { alertService } from '../../services/alert.service'
import { Pet, Alert } from '../../types'

export default function PetList() {
  const navigate = useNavigate()
  const [pets, setPets] = useState<Pet[]>([])
  const [activeAlerts, setActiveAlerts] = useState<Record<string, Alert>>({})
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const fetchPetsAndAlerts = async () => {
      try {
        const data = await petService.getPets()
        setPets(data)
        
        // Check active alerts for each pet
        const alertsMap: Record<string, Alert> = {}
        for (const pet of data) {
          const alert = await alertService.getActiveAlertByPetId(pet.petId)
          if (alert) {
            alertsMap[pet.petId] = alert
          }
        }
        setActiveAlerts(alertsMap)
      } catch (error) {
        console.error('Error fetching pets:', error)
      } finally {
        setIsLoading(false)
      }
    }
    fetchPetsAndAlerts()
  }, [])

  const handleDelete = async (petId: string) => {
    if (window.confirm('Are you sure you want to delete this pet?')) {
      try {
        await petService.deletePet(petId)
        setPets(pets.filter(p => p.petId !== petId))
      } catch (error) {
        console.error('Error deleting pet:', error)
      }
    }
  }

  if (isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '300px' }}>
        <CircularProgress color="primary" />
      </Box>
    )
  }

   return (
     <Paper sx={{ bgcolor: 'rgba(255, 255, 255, 0.85)', p: 3, borderRadius: 2, boxShadow: 3 }}>
        <Button 
          variant="text"
          sx={{ mb: 2 }}
          onClick={() => navigate('/')}
          size="small"
          startIcon={<FaArrowLeft />}
        >
          Back
        </Button>

       <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h5" color="text.primary">
            My Pets
          </Typography>
          <Typography color="text.secondary" sx={{ mt: 0.5 }}>
            Manage your registered pets
          </Typography>
        </Box>
        <Button component={Link} to="/pets/create" variant="contained" color="primary" startIcon={<FaPlus />}>
          Add Pet
        </Button>
      </Box>

      {pets.length === 0 ? (
        <Card sx={{ p: 4, textAlign: 'center' }}>
          <Typography color="text.secondary">No pets registered yet.</Typography>
          <Button component={Link} to="/pets/create" sx={{ mt: 2 }} variant="outlined" color="primary">
            Register your first pet
          </Button>
        </Card>
      ) : (
        <Grid container spacing={3}>
          {pets.map((pet) => (
            <Grid item xs={12} sm={6} lg={4} key={pet.petId}>
              <Card sx={{ overflow: 'hidden' }}>
                {pet.petImage ? (
                  <Box sx={{ position: 'relative', height: '200px', overflow: 'hidden' }}>
                    <CardMedia
                      component="img"
                      image={pet.petImage}
                      alt={pet.officialPetName}
                      sx={{ objectFit: 'cover', width: '100%', height: '100%' }}
                    />
                  </Box>
                ) : (
                  <Box 
                    sx={{ 
                      height: '200px', 
                      bgcolor: 'grey.100', 
                      display: 'flex', 
                      alignItems: 'center', 
                      justifyContent: 'center' 
                    }}
                  >
                    <Typography sx={{ fontSize: '3rem' }}>🐾</Typography>
                  </Box>
                )}
                <CardContent>
                  <Typography variant="h6" mb={1}>{pet.officialPetName}</Typography>
                  <Typography variant="body2" color="text.secondary" mb={1}>
                    {pet.species} {pet.breed && `- ${pet.breed}`}
                  </Typography>
                  {pet.gender && (
                    <Chip label={pet.gender} size="small" sx={{ mb: 1 }} />
                  )}
                  {pet.workingPetName && (
                    <Typography variant="body2" color="text.disabled">
                      Also known as: {pet.workingPetName}
                    </Typography>
                  )}
                  <Box sx={{ display: 'flex', mt: 2, gap: 1, flexWrap: 'wrap', alignItems: 'center' }}>
                     <Button
                       size="small"
                       variant="outlined"
                       color="primary"
                       onClick={() => navigate(`/pets/${pet.petId}`)}
                       startIcon={<FaSearchIcon />}
                     >
                       View Pet
                     </Button>
                     {activeAlerts[pet.petId] ? (
                        <Button
                          size="small"
                          variant="contained"
                          color="primary"
                          onClick={() => navigate(`/alerts/${activeAlerts[pet.petId].id}`)}
                          startIcon={<FaSearchIcon />}
                        >
                          View Alert
                        </Button>
                     ) : (
                       <Button
                         size="small"
                         variant="contained"
                         color="primary"
                         onClick={() => navigate(`/alerts/create?petId=${pet.petId}`)}
                         startIcon={<FaHeart />}
                       >
                         Create Alert
                       </Button>
                     )}
                     <IconButton component={Link} to={`/pets/${pet.petId}/edit`} size="small" color="primary">
                       <FaEdit />
                     </IconButton>
                     <IconButton 
                       size="small" 
                       color="error"
                       onClick={() => handleDelete(pet.petId)}
                     >
                       <FaTrash />
                     </IconButton>
                   </Box>
                </CardContent>
              </Card>
            </Grid>
          ))}
        </Grid>
      )}
    </Paper>
  )
}
