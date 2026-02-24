import React, { useState, useEffect } from 'react'
import { useNavigate } from 'react-router-dom'
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
  Paper,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogContentText,
  DialogActions
} from '@mui/material'
import { useToast } from '../../../context/ToastContext'
import { AlertService } from '../../../services/alert.service'
import { Alert } from '../../../types'

const AlertsTab: React.FC = () => {
  const navigate = useNavigate()
  const [alerts, setAlerts] = useState<Alert[]>([])
  const [loading, setLoading] = useState(true)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [alertToDelete, setAlertToDelete] = useState<Alert | null>(null)
  const [deleting, setDeleting] = useState(false)
  const { showToast } = useToast()

  useEffect(() => {
    loadAlerts()
  }, [])

  const loadAlerts = async () => {
    try {
      setLoading(true)
      const data = await AlertService.getAllAlerts()
      setAlerts(data)
    } catch (error) {
      showToast({
        title: 'Error loading alerts',
        description: 'Failed to load alerts',
        type: 'error',
        duration: 5000,
      })
    } finally {
      setLoading(false)
    }
  }

  const handleDeleteClick = (alert: Alert) => {
    setAlertToDelete(alert)
    setDeleteDialogOpen(true)
  }

  const handleDeleteConfirm = async () => {
    if (!alertToDelete || !alertToDelete.id) return

    try {
      setDeleting(true)
      await AlertService.deleteAlert(alertToDelete.id)
      showToast({
        title: 'Alert deleted',
        description: `Alert "${alertToDelete.title || alertToDelete.id}" has been deleted successfully`,
        type: 'success',
        duration: 5000,
      })
      setDeleteDialogOpen(false)
      setAlertToDelete(null)
      // Refresh the alert list
      await loadAlerts()
    } catch (error) {
      showToast({
        title: 'Error deleting alert',
        description: 'Failed to delete alert. Please try again.',
        type: 'error',
        duration: 5000,
      })
    } finally {
      setDeleting(false)
    }
  }

  const handleDeleteCancel = () => {
    setDeleteDialogOpen(false)
    setAlertToDelete(null)
  }

  // Helper function to get chip color based on alert status
  const getStatusColor = (status: string): 'success' | 'error' | 'warning' | 'info' | 'default' => {
    switch (status) {
      case 'OPEN':
        return 'error'
      case 'CLOSED':
        return 'default'
      case 'SAFE':
        return 'info'
      case 'FOUND':
        return 'success'
      default:
        return 'default'
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
                  color={getStatusColor(alert.status)}
                  size="small"
                />
              </TableCell>
              <TableCell>{new Date(alert.createdAt).toLocaleDateString()}</TableCell>
              <TableCell>
                <Stack direction="row" spacing={1}>
                  <Button 
                    size="small" 
                    variant="contained" 
                    color="primary"
                    onClick={() => navigate(`/alerts/${alert.id}`)}
                  >
                    View
                  </Button>
                  <Button 
                    size="small" 
                    variant="contained" 
                    color="secondary"
                    onClick={() => navigate(`/alerts/${alert.id}/edit`)}
                  >
                    Edit
                  </Button>
                  <Button 
                    size="small" 
                    variant="contained" 
                    color="error"
                    onClick={() => handleDeleteClick(alert)}
                  >
                    Delete
                  </Button>
                </Stack>
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>

      {/* Delete Confirmation Dialog */}
      <Dialog open={deleteDialogOpen} onClose={handleDeleteCancel}>
        <DialogTitle>Confirm Delete</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Are you sure you want to delete alert "{alertToDelete?.title || alertToDelete?.id}"? 
            This action cannot be undone.
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleDeleteCancel} disabled={deleting}>
            Cancel
          </Button>
          <Button 
            onClick={handleDeleteConfirm} 
            color="error" 
            variant="contained"
            disabled={deleting}
          >
            {deleting ? 'Deleting...' : 'Delete'}
          </Button>
        </DialogActions>
      </Dialog>
    </TableContainer>
  )
}

export default AlertsTab
