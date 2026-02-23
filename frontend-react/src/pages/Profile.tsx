import { useState, useEffect } from 'react'
import { Box, Typography, Button, Stack, TextField, Card, CardContent, Switch, FormControlLabel, CircularProgress, Alert, Grid, Paper } from '@mui/material'
import { useNavigate } from 'react-router-dom'
import { FaArrowLeft } from 'react-icons/fa'
import { useAuth } from '../context/AuthContext'
import { userService } from '../services/user.service'
import { ErrorResponse } from '../types'
import { extractError, showSuccessToast, showErrorToast } from '../utils/errorUtils'
import { GiSave } from '../components/icons'

export default function Profile() {
  const navigate = useNavigate()
  const { user, setUser } = useAuth()
  const [isLoading, setIsLoading] = useState(false)
  const [isFetching, setIsFetching] = useState(true)
  const [error, setError] = useState<ErrorResponse | null>(null)
  const [userData, setUserData] = useState<{
    username: string
    email: string
    surname: string
    phoneNumber: string
    telegramChatId: string
    emailNotificationsEnabled: boolean
    telegramNotificationsEnabled: boolean
  } | null>(null)
  
  // Fetch fresh user data from backend on mount
  useEffect(() => {
    const fetchUserData = async () => {
      if (!user?.userId) {
        setIsFetching(false)
        return
      }
      
      try {
        const freshUser = await userService.getUser(user.userId)
        const data = {
          username: freshUser.username || '',
          email: freshUser.email || '',
          surname: freshUser.surname || '',
          phoneNumber: freshUser.phoneNumber || '',
          telegramChatId: freshUser.telegramChatId || '',
          emailNotificationsEnabled: freshUser.emailNotificationsEnabled ?? true,
          telegramNotificationsEnabled: freshUser.telegramNotificationsEnabled ?? true,
        }
        setUserData(data)
        // Also update the auth context with fresh data
        setUser(freshUser)
      } catch (error) {
        console.error('Error fetching user data:', error)
         // Fall back to cached data
         setUserData({
           username: user?.username || '',
           email: user?.email || '',
           surname: user?.surname || '',
           phoneNumber: user?.phoneNumber || '',
           telegramChatId: user?.telegramChatId || '',
           emailNotificationsEnabled: user?.emailNotificationsEnabled ?? true,
           telegramNotificationsEnabled: user?.telegramNotificationsEnabled ?? true,
         })
      } finally {
        setIsFetching(false)
      }
    }
    
    fetchUserData()
  }, [user?.userId])
  
   const [formData, setFormData] = useState({
     username: '',
     email: '',
     surname: '',
     phoneNumber: '',
     telegramChatId: '',
     emailNotificationsEnabled: true,
     telegramNotificationsEnabled: true,
   })
  
  // Update form data when user data is fetched
  useEffect(() => {
    if (userData) {
      setFormData({
        ...userData,
      })
    }
  }, [userData])

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleSwitchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.checked })
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!user?.userId || !userData) return
    
    setIsLoading(true)
    setError(null)
    try {
      const updatedUser = await userService.updateUser(user.userId, formData, userData)
      setUser(updatedUser)
      setUserData({
        username: updatedUser.username || '',
        email: updatedUser.email || '',
        surname: updatedUser.surname || '',
        phoneNumber: updatedUser.phoneNumber || '',
        telegramChatId: updatedUser.telegramChatId || '',
        emailNotificationsEnabled: updatedUser.emailNotificationsEnabled ?? true,
        telegramNotificationsEnabled: updatedUser.telegramNotificationsEnabled ?? true,
      })
      showSuccessToast('Profile Updated', 'Your changes have been saved successfully.')
    } catch (error) {
      const friendlyError = extractError(error)
      setError(friendlyError)
      showErrorToast(error)
    } finally {
      setIsLoading(false)
    }
  }
  
  if (isFetching) {
    return (
      <Box sx={{ maxWidth: '600px', mx: 'auto', textAlign: 'center', py: 5 }}>
        <CircularProgress />
        <Typography sx={{ mt: 2 }}>Loading profile...</Typography>
      </Box>
    )
  }

  return (
    <Paper sx={{ maxWidth: '900px', mx: 'auto', bgcolor: 'rgba(255, 255, 255, 0.85)', p: 3, borderRadius: 2, boxShadow: 3 }}>
      <Button variant="text" sx={{ mb: 2 }} onClick={() => navigate('/')} startIcon={<FaArrowLeft />}>
         Back
       </Button>

      <Typography variant="h5" mb={3} color="text.primary">
        Profile Settings
      </Typography>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }}>
          <Typography variant="subtitle2" component="div">{error.error}</Typography>
          <Typography variant="body2">{error.message}</Typography>
        </Alert>
      )}

      <Grid container spacing={2} mb={3}>
        <Grid item xs={12} md={6}>
          <Card elevation={2}>
            <CardContent>
              <Typography variant="h6" mb={2}>
                Edit Profile
              </Typography>

              <Box component="form" onSubmit={handleSubmit}>
                <Stack spacing={2}>
                  <Box>
                    <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Username</Typography>
                    <TextField name="username" value={formData.username} onChange={handleChange} fullWidth size="small" />
                  </Box>

                  <Box>
                    <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Surname (Apellido)</Typography>
                    <TextField name="surname" value={formData.surname} onChange={handleChange} placeholder="Your surname" fullWidth size="small" />
                  </Box>

                  <Box>
                    <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Email</Typography>
                    <TextField name="email" type="email" value={formData.email} onChange={handleChange} fullWidth size="small" />
                  </Box>

                  <Box>
                    <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Phone Number</Typography>
                    <TextField name="phoneNumber" value={formData.phoneNumber} onChange={handleChange} placeholder="+34 600 000 000" fullWidth size="small" />
                    <Typography variant="caption" color="text.secondary" sx={{ mt: 0.5 }}>Format: optional + followed by 7-20 digits/symbols (e.g., +34600123456)</Typography>
                  </Box>

                  <Box>
                    <Typography variant="caption" fontWeight="medium" sx={{ display: 'block', mb: 1 }}>Telegram Chat ID</Typography>
                    <TextField name="telegramChatId" value={formData.telegramChatId} onChange={handleChange} placeholder="Optional" fullWidth size="small" />
                    <Typography variant="caption" color="text.secondary" sx={{ mt: 0.5 }}>Get your Chat ID from @userinfobot on Telegram</Typography>
                  </Box>
                </Stack>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={6}>
          <Card elevation={2}>
            <CardContent>
              <Typography variant="h6" mb={2}>
                Notifications
              </Typography>
              <Stack spacing={2}>
               <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                 <Box>
                   <Typography fontWeight="medium">Email Notifications</Typography>
                   <Typography variant="body2" color="text.secondary">
                     Receive alerts via email
                   </Typography>
                 </Box>
                 <FormControlLabel
                   control={
                     <Switch
                       name="emailNotificationsEnabled"
                       checked={formData.emailNotificationsEnabled}
                       onChange={handleSwitchChange}
                       color="success"
                     />
                   }
                   label=""
                 />
               </Box>
               <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                 <Box>
                   <Typography fontWeight="medium">Telegram Notifications</Typography>
                   <Typography variant="body2" color="text.secondary">
                     Receive alerts via Telegram
                   </Typography>
                 </Box>
                 <FormControlLabel
                   control={
                     <Switch
                       name="telegramNotificationsEnabled"
                       checked={formData.telegramNotificationsEnabled}
                       onChange={handleSwitchChange}
                       color="success"
                     />
                   }
                   label=""
                 />
               </Box>
            </Stack>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      <Button 
        type="submit" 
        variant="contained" 
        color="primary"
        fullWidth 
        disabled={isLoading}
        onClick={handleSubmit}
        startIcon={<GiSave />}
      >
        Save Changes
      </Button>
    </Paper>
  )
}
