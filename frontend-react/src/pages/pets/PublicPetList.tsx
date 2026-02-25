import { useEffect, useState } from 'react'
import { Box, Typography, Grid, Card, CardContent, CardMedia, CircularProgress, Chip, Paper, Button } from '@mui/material'
import { useNavigate } from 'react-router-dom'
import { FaSearch as FaSearchIcon, FaPaw } from 'react-icons/fa'
import { petService } from '../../services/pet.service'
import { Pet } from '../../types'

export default function PublicPetList() {
  const navigate = useNavigate()
  const [pets, setPets] = useState<Pet[]>([])
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const fetchPets = async () => {
      try {
        const data = await petService.getPublicPets()
        setPets(data)
      } catch (error) {
        console.error('Error fetching public pets:', error)
      } finally {
        setIsLoading(false)
      }
    }
    fetchPets()
  }, [])

  if (isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '300px' }}>
        <CircularProgress color="primary" />
      </Box>
    )
  }

  return (
    <Paper sx={{ bgcolor: 'rgba(255, 255, 255, 0.85)', p: 3, borderRadius: 2, boxShadow: 3 }}>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
        <Box>
          <Typography variant="h5" color="text.primary">
            All Pets
          </Typography>
          <Typography color="text.secondary" sx={{ mt: 0.5 }}>
            Browse all registered pets in the community
          </Typography>
        </Box>
      </Box>

      {pets.length === 0 ? (
        <Card sx={{ p: 4, textAlign: 'center' }}>
          <FaPaw size={48} color="text.disabled" />
          <Typography color="text.secondary" sx={{ mt: 2 }}>No pets registered yet.</Typography>
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
