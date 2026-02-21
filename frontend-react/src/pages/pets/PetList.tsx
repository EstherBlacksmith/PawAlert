import { useEffect, useState } from 'react'
import { Box, Heading, Button, SimpleGrid, Card, Text, Flex, Spinner, Badge, IconButton, Image } from '@chakra-ui/react'
import { Link, useNavigate } from 'react-router-dom'
import { FaPlus, FaEdit, FaTrash, FaExclamationTriangle, FaEye } from 'react-icons/fa'
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
      <Flex justify="center" align="center" minH="300px">
        <Spinner size="xl" color="purple.500" />
      </Flex>
    )
  }

  return (
    <Box>
      <Flex justify="space-between" align="center" mb={6}>
        <Box>
          <Heading size="lg" color="gray.800" _dark={{ color: 'white' }}>
            My Pets
          </Heading>
          <Text color="gray.500" mt={1}>
            Manage your registered pets
          </Text>
        </Box>
        <Link to="/pets/create">
          <Button colorScheme="purple">
            <FaPlus style={{ marginRight: '8px' }} />
            Add Pet
          </Button>
        </Link>
      </Flex>

      {pets.length === 0 ? (
        <Card.Root p={8} textAlign="center">
          <Text color="gray.500">No pets registered yet.</Text>
          <Link to="/pets/create">
            <Button mt={4} colorScheme="purple" variant="outline">
              Register your first pet
            </Button>
          </Link>
        </Card.Root>
      ) : (
        <SimpleGrid columns={{ base: 1, md: 2, lg: 3 }} gap={6}>
          {pets.map((pet) => (
            <Card.Root key={pet.petId} overflow="hidden">
              {pet.petImage ? (
                <Box position="relative" h="200px" overflow="hidden">
                  <Image 
                    src={pet.petImage} 
                    alt={pet.officialPetName}
                    objectFit="cover"
                    w="full"
                    h="full"
                  />
                </Box>
              ) : (
                <Box 
                  h="200px" 
                  bg="gray.100" 
                  display="flex" 
                  alignItems="center" 
                  justifyContent="center"
                >
                  <Text color="gray.400" fontSize="3xl">🐾</Text>
                </Box>
              )}
              <Card.Body>
                <Heading size="md" mb={2}>{pet.officialPetName}</Heading>
                <Text fontSize="sm" color="gray.500" mb={2}>
                  {pet.species} {pet.breed && `- ${pet.breed}`}
                </Text>
                {pet.gender && (
                  <Badge mb={2}>{pet.gender}</Badge>
                )}
                {pet.workingPetName && (
                  <Text fontSize="sm" color="gray.400">
                    Also known as: {pet.workingPetName}
                  </Text>
                )}
                <Flex mt={4} gap={2} wrap="wrap" align="center">
                  {activeAlerts[pet.petId] ? (
                    <Button
                      size="sm"
                      colorPalette="blue"
                      onClick={() => navigate(`/alerts/${activeAlerts[pet.petId].id}`)}
                    >
                      <FaEye style={{ marginRight: '4px' }} />
                      View Alert
                    </Button>
                  ) : (
                    <Button
                      size="sm"
                      colorPalette="orange"
                      onClick={() => navigate(`/alerts/create?petId=${pet.petId}`)}
                    >
                      <FaExclamationTriangle style={{ marginRight: '4px' }} />
                      Create Alert
                    </Button>
                  )}
                  <Link to={`/pets/${pet.petId}/edit`}>
                    <IconButton aria-label="Edit" variant="ghost" size="sm">
                      <FaEdit />
                    </IconButton>
                  </Link>
                  <IconButton 
                    aria-label="Delete" 
                    variant="ghost" 
                    size="sm" 
                    colorPalette="red"
                    onClick={() => handleDelete(pet.petId)}
                  >
                    <FaTrash />
                  </IconButton>
                </Flex>
              </Card.Body>
            </Card.Root>
          ))}
        </SimpleGrid>
      )}
    </Box>
  )
}
