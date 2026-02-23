import React, { useState, useEffect } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import {
  Box,
  Button,
  Card,
  CardBody,
  CardHeader,
  Heading,
  Spinner,
  HStack,
  VStack,
  Text,
  Badge,
} from '@chakra-ui/react'
import { useToast } from '../../context/ToastContext'
import { PetService } from '../../services/pet.service'
import { Pet } from '../../types'

export const PetDetail: React.FC = () => {
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
      <Box display="flex" justifyContent="center" alignItems="center" minH="400px">
        <Spinner />
      </Box>
    )
  }

  if (!pet) {
    return (
      <Box p={4}>
        <Text>Pet not found</Text>
      </Box>
    )
  }

  return (
    <Box p={4}>
      <Card>
        <CardHeader>
          <Heading>{pet.name}</Heading>
        </CardHeader>
        <CardBody>
          <VStack align="start" spacing={4}>
            <Box>
              <Text fontWeight="bold">Type:</Text>
              <Text>{pet.type}</Text>
            </Box>
            <Box>
              <Text fontWeight="bold">Breed:</Text>
              <Text>{pet.breed || 'N/A'}</Text>
            </Box>
            <Box>
              <Text fontWeight="bold">Owner:</Text>
              <Text>{pet.owner?.name || 'N/A'}</Text>
            </Box>
            <Box>
              <Text fontWeight="bold">Status:</Text>
              <Badge colorScheme="green">Active</Badge>
            </Box>
            <HStack spacing={2}>
              <Button colorScheme="blue" onClick={() => navigate(`/pets/${id}/edit`)}>
                Edit
              </Button>
              <Button colorScheme="gray" onClick={() => navigate('/pets')}>
                Back
              </Button>
            </HStack>
          </VStack>
        </CardBody>
      </Card>
    </Box>
  )
}
