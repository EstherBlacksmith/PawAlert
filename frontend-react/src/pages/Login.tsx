import { useState, useEffect } from 'react'
import { Box, Stack, Typography, TextField, Button, Alert, AlertTitle, Paper, CircularProgress } from '@mui/material'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { ErrorResponse } from '../types'
import NearbyAlertsMap from '../components/alerts/NearbyAlertsMap'
import { extractError } from '../utils/errorUtils'

// Default location fallback (Madrid, Spain)
const DEFAULT_LOCATION = {
  latitude: 40.4168,
  longitude: -3.7038
}

export default function Login() {
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState<ErrorResponse | string | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [location, setLocation] = useState<{ latitude: number; longitude: number } | null>(null)
  const [locationError, setLocationError] = useState<string | null>(null)
  const { login } = useAuth()
  const navigate = useNavigate()

  // Get user's location on component mount with fallback to default location
  useEffect(() => {
    if ('geolocation' in navigator) {
      navigator.geolocation.getCurrentPosition(
        (position) => {
          setLocation({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude
          })
        },
        (err) => {
          console.warn('Geolocation error:', err)
          // Use default location as fallback
          setLocation(DEFAULT_LOCATION)
          setLocationError('Using default location')
        },
        { enableHighAccuracy: false, timeout: 5000, maximumAge: 300000 }
      )
    } else {
      // Geolocation not supported, use default location
      console.warn('Geolocation not supported')
      setLocation(DEFAULT_LOCATION)
      setLocationError('Using default location')
    }
  }, [])

   const handleSubmit = async (e: React.FormEvent) => {
     e.preventDefault()
     setError(null)
     setIsLoading(true)

     try {
       console.log('[DEBUG] Login attempt with email:', email)
       await login({ email, password })
       console.log('[DEBUG] Login successful, navigating to dashboard')
       navigate('/dashboard')
     } catch (err: unknown) {
       // Use centralized error extraction for user-friendly messages
       console.log('[DEBUG] Login error:', err)
       const friendlyError = extractError(err)
       setError(friendlyError)
     } finally {
       setIsLoading(false)
     }
   }

  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        backgroundImage: "url('/src/assets/bg-image.jpg')",
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        backgroundAttachment: { xs: 'scroll', lg: 'fixed' },
        position: 'relative',
        '&::before': {
          content: '""',
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0, 0, 0, 0.4)',
          backdropFilter: 'blur(2px)',
          zIndex: 1
        }
      }}
    >
      {/* Map Section - Left side on desktop, hidden on mobile */}
        <Box
          sx={{
            display: { xs: 'none', lg: 'block' },
            width: '50%',
            height: '100vh',
            position: 'relative',
            bgcolor: 'grey.100',
            overflow: 'hidden',
            zIndex: 2
          }}
        >
         {location ? (
           <Box sx={{ position: 'absolute', top: 0, left: 0, right: 0, bottom: 0 }}>
             <NearbyAlertsMap
               latitude={location.latitude}
               longitude={location.longitude}
               radiusKm={10}
               fullHeight={true}
             />
           </Box>
         ) : (
           <Box 
             sx={{
               position: 'absolute',
               top: 0,
               left: 0,
               right: 0,
               bottom: 0,
               display: 'flex', 
               alignItems: 'center', 
               justifyContent: 'center'
             }}
           >
             <Typography color="text.disabled">Getting location...</Typography>
           </Box>
         )}
       </Box>

       {/* Login Form Section - Right side on desktop, full width on mobile */}
       <Box
         sx={{
           width: { xs: '100%', lg: '50%' },
           display: 'flex',
           alignItems: 'center',
           justifyContent: 'center',
           p: { xs: 2, md: 3, lg: 4 },
           position: 'relative',
           zIndex: 2
         }}
       >
         <Paper
           elevation={8}
           sx={{
             width: '100%',
             maxWidth: { xs: '100%', md: '450px', lg: '480px' },
             bgcolor: 'rgba(255, 255, 255, 0.95)',
             backdropFilter: 'blur(10px)',
             borderRadius: 4,
             border: '1px solid rgba(255, 255, 255, 0.2)',
             p: { xs: 3, md: 4, lg: 5 },
             position: 'relative',
             overflow: 'hidden',
             animation: 'slideUp 0.6s ease-out',
             '@keyframes slideUp': {
               from: { opacity: 0, transform: 'translateY(20px)' },
               to: { opacity: 1, transform: 'translateY(0)' }
             }
           }}
         >
          {/* Subtle gradient overlay */}
          <Box
            sx={{
              position: 'absolute',
              top: 0,
              left: 0,
              right: 0,
              bottom: 0,
              background: 'linear-gradient(to bottom right, rgba(251, 111, 4, 0.05), transparent)',
              pointerEvents: 'none'
            }}
          />
          <Stack spacing={3} position="relative">
           {/* Logo */}
              <Box textAlign="center">
                <Box 
                  sx={{
                    display: 'inline-block',
                    mb: 2,
                    animation: 'pulse 2s infinite',
                    '@keyframes pulse': {
                      '0%, 100%': { opacity: 1 },
                      '50%': { opacity: 0.8 }
                    }
                  }}
                >
                  <img 
                    src="/labrador-head.png" 
                    alt="PawAlert Logo" 
                    style={{ width: 120, height: 120 }}
                  />
                </Box>
                 <Typography
                   variant="body2"
                   color="text.secondary"
                   fontWeight={500}
                   sx={{ mt: 1 }}
                 >
                   Welcome back to our commewnity 🐾
                 </Typography>
              </Box>

              {/* Mobile Map Section - Show below logo on mobile */}
              {location && (
                <Box 
                  sx={{ 
                    display: { xs: 'block', lg: 'none' }, 
                    width: '100%', 
                    borderRadius: 1, 
                    overflow: 'hidden' 
                  }}
                >
                  <NearbyAlertsMap
                    latitude={location.latitude}
                    longitude={location.longitude}
                    radiusKm={10}
                  />
                </Box>
              )}
            {locationError && (
              <Typography variant="caption" color="text.disabled">{locationError}</Typography>
            )}

            {/* Error Message */}
            {error && (
              <Alert severity="error" sx={{ width: '100%' }}>
                {typeof error === 'string' ? (
                  <Typography variant="body2" color="error.dark">
                    {error}
                  </Typography>
                ) : (
                  <>
                    <AlertTitle>{error.error || 'Error'}</AlertTitle>
                    {error.message}
                  </>
                )}
              </Alert>
            )}

              {/* Form */}
              <form onSubmit={handleSubmit} style={{ width: '100%' }}>
                <Stack spacing={3} width="100%">
                  <Box width="100%">
                    <Typography 
                      variant="caption"
                      fontWeight={600}
                      color="text.secondary"
                      sx={{ display: 'block', mb: 1 }}
                    >
                      Email
                    </Typography>
                    <TextField
                      type="email"
                      value={email}
                      onChange={(e: React.ChangeEvent<HTMLInputElement>) => setEmail(e.target.value)}
                      placeholder="Enter your email"
                      required
                      fullWidth
                      size="medium"
                      sx={{
                        '& .MuiOutlinedInput-root': {
                          bgcolor: 'rgba(255, 255, 255, 0.8)',
                          transition: 'all 0.3s ease',
                          '&:hover': {
                            bgcolor: 'white',
                          },
                          '&.Mui-focused': {
                            bgcolor: 'white',
                          }
                        }
                      }}
                    />
                  </Box>

                  <Box width="100%">
                    <Typography 
                      variant="caption"
                      fontWeight={600}
                      color="text.secondary"
                      sx={{ display: 'block', mb: 1 }}
                    >
                      Password
                    </Typography>
                    <TextField
                      type="password"
                      value={password}
                      onChange={(e: React.ChangeEvent<HTMLInputElement>) => setPassword(e.target.value)}
                      placeholder="Enter your password"
                      required
                      fullWidth
                      size="medium"
                      sx={{
                        '& .MuiOutlinedInput-root': {
                          bgcolor: 'rgba(255, 255, 255, 0.8)',
                          transition: 'all 0.3s ease',
                          '&:hover': {
                            bgcolor: 'white',
                          },
                          '&.Mui-focused': {
                            bgcolor: 'white',
                          }
                        }
                      }}
                    />
                  </Box>

                 <Button
                   type="submit"
                   fullWidth
                   variant="contained"
                   color="primary"
                   size="large"
                   disabled={isLoading}
                   sx={{
                     fontWeight: 600,
                     py: 1.5,
                     transition: 'all 0.3s ease',
                     '&:hover': { 
                       transform: 'translateY(-2px)',
                       boxShadow: '0 12px 24px rgba(251, 111, 4, 0.3)'
                     }
                   }}
                 >
                   {isLoading ? <CircularProgress size={24} color="inherit" /> : 'Sign In'}
                 </Button>
               </Stack>
             </form>

             {/* Register Link */}
             <Typography textAlign="center" variant="body2" color="text.secondary" fontWeight={500}>
               Don't have an account?{' '}
               <Link to="/register" style={{ textDecoration: 'none' }}>
                 <Typography 
                   component="span"
                   variant="body2"
                   color="primary"
                   fontWeight={600}
                   sx={{ 
                     '&:hover': { 
                       textDecoration: 'underline'
                     }
                   }}
                 >
                   Register here
                 </Typography>
               </Link>
             </Typography>
          </Stack>
        </Paper>
      </Box>
    </Box>
  )
}
