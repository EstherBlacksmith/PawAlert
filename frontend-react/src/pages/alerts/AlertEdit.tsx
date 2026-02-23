import { useState, useEffect } from 'react'
import { Box, Typography, Button, Stack, TextField, CircularProgress, Alert as MuiAlert, Paper } from '@mui/material'
import { useNavigate, useParams } from 'react-router-dom'
import { FaArrowLeft } from 'react-icons/fa'
import { alertService } from '../../services/alert.service'
import { useAuth } from '../../context/AuthContext'
import { ErrorResponse } from '../../types'

export default function AlertEdit() {
  const { id } = useParams<{ id: string }>()
  const navigate = useNavigate()
  const { user, isAdmin } = useAuth()
  const [isLoading, setIsLoading] = useState(false)
  const [isFetching, setIsFetching] = useState(true)
  const [error, setError] = useState<ErrorResponse | null>(null)
  
  const [formData, setFormData] = useState({
    title: '',
    description: '',
  })

  useEffect(() => {
    const fetchAlert = async () => {
      try {
        if (id) {
          const alert = await alertService.getAlert(id)
          
          // Check if user can edit this alert (closed alerts can only be edited by admins)
          if (alert.status === 'CLOSED' && !isAdmin()) {
            // Non-admin cannot edit closed alerts - redirect to detail page
            navigate(`/alerts/${id}`, { replace: true })
            return
          }
          
          setFormData({
            title: alert.title,
            description: alert.description,
          })
        }
      } catch (error: any) {
        console.error('Error fetching alert:', error)
        if (error.response?.status === 404) {
          navigate('/alerts', { replace: true })
        }
      } finally {
        setIsFetching(false)
      }
    }
    fetchAlert()
  }, [id])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!user) {
      setError({
        status: 401,
        error: 'Unauthorized',
        message: 'You must be logged in to edit an alert.'
      })
      return
    }

    setIsLoading(true)
    setError(null)

    try {
      if (id) {
        await alertService.updateAlert(id, user.userId, {
          title: formData.title.trim(),
          description: formData.description.trim(),
        })
        navigate(`/alerts/${id}`)
      }
    } catch (error: any) {
      console.error('Error updating alert:', error)
      if (error.response?.data) {
        setError(error.response.data)
      } else {
        setError({
          status: error.response?.status || 500,
          error: error.response?.statusText || 'Error',
          message: 'An unexpected error occurred. Please try again.'
        })
      }
    } finally {
      setIsLoading(false)
    }
  }

  if (isFetching) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '300px' }}>
        <CircularProgress color="primary" />
      </Box>
    )
  }

  return (
    <Paper sx={{ width: '100%', mx: 'auto', bgcolor: 'rgba(255, 255, 255, 0.85)', p: 3, borderRadius: 2, boxShadow: 3 }}>
      <Button variant="text" sx={{ mb: 2 }} onClick={() => navigate(`/alerts/${id}`)} startIcon={<FaArrowLeft />}>
        Back to Alert
      </Button>

      <Typography variant="h5" mb={3} color="text.primary">
        Edit Alert
      </Typography>

      {error && (
        <MuiAlert severity="error" sx={{ mb: 3 }}>
          <Typography variant="subtitle2" component="div">{error.error || 'Error'}</Typography>
          <Typography variant="body2">{error.message}</Typography>
        </MuiAlert>
      )}

       <Box component="form" onSubmit={handleSubmit}>
         <Stack spacing={2}>
           <Box width="100%">
             <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Title *</Typography>
             <TextField 
               name="title" 
               value={formData.title} 
               onChange={handleChange} 
               required 
               placeholder="Enter alert title"
               fullWidth
               size="small"
             />
           </Box>

           <Box width="100%">
             <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Description *</Typography>
             <TextField 
               name="description" 
               value={formData.description} 
               onChange={handleChange} 
               required
               placeholder="Enter alert description"
               multiline
               rows={4}
               fullWidth
               size="small"
             />
           </Box>

          <Stack direction="row" spacing={2} sx={{ pt: 2 }}>
            <Button 
              type="button" 
              variant="outlined" 
              onClick={() => navigate(`/alerts/${id}`)}
              sx={{ flex: 1 }}
            >
              Cancel
            </Button>
            <Button 
              type="submit" 
              variant="contained"
              sx={{ flex: 1, bgcolor: '#4682B4', '&:hover': { bgcolor: '#36648B' } }}
              disabled={isLoading}
            >
              {isLoading ? <CircularProgress size={24} color="inherit" /> : 'Save Changes'}
            </Button>
          </Stack>
        </Stack>
      </Box>
    </Paper>
  )
}
