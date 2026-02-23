import React, { useState, useEffect } from 'react'
import {
  Box,
  Button,
  Table,
  Thead,
  Tbody,
  Tr,
  Th,
  Td,
  Spinner,
  HStack,
} from '@chakra-ui/react'
import { useToast } from '../../../context/ToastContext'
import { PetService } from '../../../services/pet.service'
import { Pet } from '../../../types'

export const PetsTab: React.FC = () => {
  const [pets, setPets] = useState<Pet[]>([])
  const [loading, setLoading] = useState(true)
  const toast = useToast()

  useEffect(() => {
    loadPets()
  }, [])

  const loadPets = async () => {
    try {
      setLoading(true)
      const data = await PetService.getAllPets()
      setPets(data)
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

  if (loading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minH="400px">
        <Spinner />
      </Box>
    )
  }

  return (
    <Box overflowX="auto">
      <Table>
        <Thead>
          <Tr>
            <Th>ID</Th>
            <Th>Name</Th>
            <Th>Type</Th>
            <Th>Owner</Th>
            <Th>Actions</Th>
          </Tr>
        </Thead>
        <Tbody>
          {pets.map((pet) => (
            <Tr key={pet.id}>
              <Td>{pet.id}</Td>
              <Td>{pet.name}</Td>
              <Td>{pet.type}</Td>
              <Td>{pet.owner?.name || 'N/A'}</Td>
              <Td>
                <HStack spacing={2}>
                  <Button size="sm" colorScheme="blue">
                    View
                  </Button>
                </HStack>
              </Td>
            </Tr>
          ))}
        </Tbody>
      </Table>
    </Box>
  )
}
