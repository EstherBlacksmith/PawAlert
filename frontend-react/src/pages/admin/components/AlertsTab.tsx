import React, { useState, useEffect } from 'react'
import {
  Box,
  Button,
  Table,
  Thead,
  Tr,
  Th,
  Td,
  Spinner,
  HStack,
  Badge,
} from '@chakra-ui/react'
import { useToast } from '../../../context/ToastContext'
import { AlertService } from '../../../services/alert.service'
import { Alert } from '../../../types'

const AlertsTab: React.FC = () => {
  const [alerts, setAlerts] = useState<Alert[]>([])
  const [loading, setLoading] = useState(true)
  const toast = useToast()

  useEffect(() => {
    loadAlerts()
  }, [])

  const loadAlerts = async () => {
    try {
      setLoading(true)
      const data = await AlertService.getAllAlerts()
      setAlerts(data)
    } catch (error) {
      toast({
        title: 'Error loading alerts',
        description: 'Failed to load alerts',
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
            <Th>Title</Th>
            <Th>Status</Th>
            <Th>Created</Th>
            <Th>Actions</Th>
          </Tr>
        </Thead>
        <tbody>
          {alerts.map((alert) => (
            <tr key={alert.id}>
              <td>{alert.id}</td>
              <td>{alert.title}</td>
              <td>
                <Badge colorScheme={alert.status === 'OPEN' ? 'green' : 'gray'}>
                  {alert.status}
                </Badge>
              </td>
              <td>{new Date(alert.createdAt).toLocaleDateString()}</td>
              <td>
                <HStack spacing={2}>
                  <Button size="sm" colorScheme="blue">
                    View
                  </Button>
                </HStack>
              </td>
            </tr>
          ))}
        </tbody>
      </Table>
    </Box>
  )
}

export default AlertsTab
