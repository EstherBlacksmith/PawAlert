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
  Chip,
  Stack,
  Paper
} from '@mui/material'
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
            <TableCell>Title</TableCell>
            <TableCell>Status</TableCell>
            <TableCell>Created</TableCell>
            <TableCell>Actions</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {alerts.map((alert) => (
            <TableRow key={alert.id}>
              <TableCell>{alert.id}</TableCell>
              <TableCell>{alert.title}</TableCell>
              <TableCell>
                <Chip
                  label={alert.status}
                  color={alert.status === 'OPEN' ? 'success' : 'default'}
                  size="small"
                />
              </TableCell>
              <TableCell>{new Date(alert.createdAt).toLocaleDateString()}</TableCell>
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

export default AlertsTab
