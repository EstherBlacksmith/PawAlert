import React, { useState, useEffect, useCallback } from 'react'
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
  DialogActions,
  TextField,
  InputAdornment,
  Select,
  MenuItem,
  FormControl,
  InputLabel,
  IconButton,
  Typography
} from '@mui/material'
import SearchIcon from '@mui/icons-material/Search'
import ClearIcon from '@mui/icons-material/Clear'
import { useToast } from '../../../context/ToastContext'
import { AlertService } from '../../../services/alert.service'
import { Alert, AlertStatus, AlertEvent } from '../../../types'

const AlertsTab: React.FC = () => {
  const navigate = useNavigate()
  const [alerts, setAlerts] = useState<Alert[]>([])
  const [alertEvents, setAlertEvents] = useState<Map<string, AlertEvent[]>>(new Map())
  const [loading, setLoading] = useState(true)
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false)
  const [alertToDelete, setAlertToDelete] = useState<Alert | null>(null)
  const [deleting, setDeleting] = useState(false)
  const { showToast } = useToast()
  
  // Filter state
  const [searchText, setSearchText] = useState('')
  const [debouncedSearchText, setDebouncedSearchText] = useState('')
  const [statusFilter, setStatusFilter] = useState<string>('')
  
  // Debounce search text (300ms)
  useEffect(() => {
    const timer = setTimeout(() => {
      setDebouncedSearchText(searchText)
    }, 300)
    return () => clearTimeout(timer)
  }, [searchText])

  // Load alerts with server-side filtering
  const loadAlertsWithFilters = useCallback(async () => {
    try {
      setLoading(true)
      const filters: { title?: string; status?: AlertStatus } = {}
      
      if (debouncedSearchText) {
        filters.title = debouncedSearchText
      }
      if (statusFilter) {
        filters.status = statusFilter as AlertStatus
      }
      
      // Use searchAlertsWithFilters if we have filters, otherwise get all
      let data: Alert[]
      if (debouncedSearchText || statusFilter) {
        data = await AlertService.searchAlertsWithFilters(filters)
      } else {
        data = await AlertService.getAllAlerts()
      }
      setAlerts(data)
      
      // Fetch events for each alert to get creation date
      const eventsMap = new Map<string, AlertEvent[]>()
      await Promise.all(
        data.map(async (alert) => {
          try {
            const events = await AlertService.getAlertEvents(alert.id)
            eventsMap.set(alert.id, events)
          } catch {
            // If events fail, just set empty array
            eventsMap.set(alert.id, [])
          }
        })
      )
      setAlertEvents(eventsMap)
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
  }, [debouncedSearchText, statusFilter, showToast])

  useEffect(() => {
    loadAlertsWithFilters()
  }, [loadAlertsWithFilters])

  // Helper function to get creation date from events (earliest event)
  const getCreationDate = (alertId: string): string | null => {
    const events = alertEvents.get(alertId)
    if (!events || events.length === 0) return null
    
    // Sort by changedAt ascending and get the first (earliest) event
    const sortedEvents = [...events].sort((a, b) => 
      new Date(a.changedAt).getTime() - new Date(b.changedAt).getTime()
    )
    return sortedEvents[0].changedAt
  }

  // Helper function to format date safely
  const formatDate = (dateString: string | null | undefined): string => {
    if (!dateString) return '-'
    try {
      const date = new Date(dateString)
      if (isNaN(date.getTime())) return '-'
      return date.toLocaleDateString('es-ES', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric'
      })
    } catch {
      return '-'
    }
  }

  const handleClearFilters = () => {
    setSearchText('')
    setDebouncedSearchText('')
    setStatusFilter('')
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
      await loadAlertsWithFilters()
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
      case 'OPENED':
        return 'error'
      case 'CLOSED':
        return 'default'
      case 'SAFE':
        return 'info'
      case 'SEEN':
        return 'warning'
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
    <Box>
      {/* Filter Bar */}
      <Paper sx={{ p: 2, mb: 2 }}>
        <Stack direction="row" spacing={2} alignItems="center" flexWrap="wrap" useFlexGap>
          <TextField
            placeholder="Search by title..."
            size="small"
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            InputProps={{
              startAdornment: (
                <InputAdornment position="start">
                  <SearchIcon color="action" />
                </InputAdornment>
              ),
            }}
            sx={{ minWidth: 300, flexGrow: 1 }}
          />
          <FormControl size="small" sx={{ minWidth: 120 }}>
            <InputLabel>Status</InputLabel>
            <Select
              value={statusFilter}
              label="Status"
              onChange={(e) => setStatusFilter(e.target.value)}
            >
              <MenuItem value="">All</MenuItem>
              <MenuItem value="OPENED">OPENED</MenuItem>
              <MenuItem value="SEEN">SEEN</MenuItem>
              <MenuItem value="SAFE">SAFE</MenuItem>
              <MenuItem value="CLOSED">CLOSED</MenuItem>
            </Select>
          </FormControl>
          <IconButton 
            onClick={handleClearFilters} 
            title="Clear filters"
            disabled={!searchText && !statusFilter}
          >
            <ClearIcon />
          </IconButton>
        </Stack>
        <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
          Showing {alerts.length} alerts
        </Typography>
      </Paper>

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
              <TableCell>{formatDate(getCreationDate(alert.id))}</TableCell>
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
    </TableContainer>

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
    </Box>
  )
}

export default AlertsTab
