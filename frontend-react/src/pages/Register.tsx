import { useState } from 'react'
import { Box, Stack, Typography, TextField, Button, Alert, AlertTitle, Paper, CircularProgress, Select, MenuItem, FormControl, Grid, IconButton, InputAdornment } from '@mui/material'
import { FiEye, FiEyeOff } from 'react-icons/fi'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'
import { ErrorResponse } from '../types'
import { extractError, showSuccessToast } from '../utils/errorUtils'

// Country prefixes for phone number with ISO country codes for flag images
const COUNTRY_PREFIXES = [
  { code: 'es', prefix: '+34', country: 'Spain' },
  { code: 'gb', prefix: '+44', country: 'UK' },
  { code: 'us', prefix: '+1', country: 'USA' },
  { code: 'fr', prefix: '+33', country: 'France' },
  { code: 'de', prefix: '+49', country: 'Germany' },
  { code: 'it', prefix: '+39', country: 'Italy' },
  { code: 'pt', prefix: '+351', country: 'Portugal' },
  { code: 'be', prefix: '+32', country: 'Belgium' },
  { code: 'nl', prefix: '+31', country: 'Netherlands' },
  { code: 'se', prefix: '+46', country: 'Sweden' },
  { code: 'mx', prefix: '+52', country: 'Mexico' },
  { code: 'ar', prefix: '+54', country: 'Argentina' },
  { code: 'co', prefix: '+57', country: 'Colombia' },
  { code: 'cl', prefix: '+56', country: 'Chile' },
  { code: 'br', prefix: '+55', country: 'Brazil' },
]

// Flag component using flag-icons CDN (Windows 10 doesn't support flag emojis)
const Flag = ({ code }: { code: string }) => (
  <img 
    src={`https://cdn.jsdelivr.net/gh/lipis/flag-icons@7.2.3/flags/4x3/${code.toLowerCase()}.svg`}
    alt={code}
    style={{ width: 20, height: 15, marginRight: 8, verticalAlign: 'middle', objectFit: 'cover' }}
  />
)

export default function Register() {
  const [formData, setFormData] = useState({
    username: '',
    surname: '',
    email: '',
    phonePrefix: '+34',
    phoneNumber: '',
    password: '',
    confirmPassword: '',
  })
  const [error, setError] = useState<ErrorResponse | string | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [showPassword, setShowPassword] = useState(false)
  const { register, login } = useAuth()
  const navigate = useNavigate()

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handlePrefixChange = (e: { target: { value: string } }) => {
    setFormData({ ...formData, phonePrefix: e.target.value })
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setError(null)

    if (formData.password !== formData.confirmPassword) {
      setError('Las contraseñas no coinciden')
      return
    }

    // Combine prefix + phone number
    const fullPhoneNumber = formData.phonePrefix + formData.phoneNumber.replace(/\D/g, '')

    setIsLoading(true)

    try {
      // Register the user
      await register({
        username: formData.username,
        email: formData.email,
        password: formData.password,
        surname: formData.surname,
        phoneNumber: fullPhoneNumber,
      })
      
      // Auto-login after registration
      await login({ email: formData.email, password: formData.password })
      showSuccessToast('Account Created', 'Welcome to PawAlert!')
      navigate('/dashboard')
    } catch (err: unknown) {
      // Use centralized error extraction for user-friendly messages
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
        alignItems: 'center',
        justifyContent: 'center',
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
        },
        p: { xs: 1, md: 2, lg: 3 }
      }}
    >
      <Paper
        elevation={8}
        sx={{
          width: '100%',
          maxWidth: { xs: '100%', md: '800px' },
          bgcolor: 'rgba(255, 255, 255, 0.95)',
          backdropFilter: 'blur(10px)',
          borderRadius: 4,
          border: '1px solid rgba(255, 255, 255, 0.2)',
          p: { xs: 2, md: 4 },
          position: 'relative',
          overflow: 'hidden',
          animation: 'slideUp 0.6s ease-out',
          zIndex: 2,
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
        <Stack spacing={2} position="relative">
          {/* Logo */}
          <Box textAlign="center">
            <Box 
              sx={{
                display: 'inline-block',
                mb: 1,
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
                style={{ width: 100, height: 100 }}
              />
            </Box>
            <Typography 
              variant="body2" 
              color="text.secondary" 
              fontWeight={500}
            >
              Welcome to our commewnity 🐾
            </Typography>
          </Box>

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
               <Grid container spacing={3}>
                 {/* Left Column: Username, Surname, Email */}
                 <Grid size={{ xs: 12, sm: 6 }}>
                   <Stack spacing={2}>
                     {/* Username */}
                     <Box>
                       <Typography 
                         variant="caption"
                         fontWeight={600}
                         color="text.secondary"
                         sx={{ display: 'block', mb: 1 }}
                       >
                         Username
                       </Typography>
                       <TextField
                         name="username"
                         value={formData.username}
                         onChange={handleChange}
                         placeholder="Choose a username"
                         required
                         fullWidth
                         size="small"
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

                     {/* Surname */}
                     <Box>
                       <Typography 
                         variant="caption"
                         fontWeight={600}
                         color="text.secondary"
                         sx={{ display: 'block', mb: 1 }}
                       >
                         Surname
                       </Typography>
                       <TextField
                         name="surname"
                         value={formData.surname}
                         onChange={handleChange}
                         placeholder="Your surname"
                         required
                         fullWidth
                         size="small"
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

                     {/* Email */}
                     <Box>
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
                         name="email"
                         value={formData.email}
                         onChange={handleChange}
                         placeholder="your@email.com"
                         required
                         fullWidth
                         size="small"
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
                   </Stack>
                 </Grid>

                 {/* Right Column: Phone, Password, Submit */}
                 <Grid size={{ xs: 12, sm: 6 }}>
                   <Stack spacing={2}>
                     {/* Phone Number */}
                     <Box>
                       <Typography 
                         variant="caption"
                         fontWeight={600}
                         color="text.secondary"
                         sx={{ display: 'block', mb: 1 }}
                       >
                         Phone Number
                       </Typography>
                        <Box sx={{ display: 'flex', gap: 1, width: '100%' }}>
                          <FormControl sx={{ width: '120px', flexShrink: 0 }}>
                            <Select
                              value={formData.phonePrefix}
                              onChange={handlePrefixChange}
                              size="small"
                              renderValue={(value) => {
                                const country = COUNTRY_PREFIXES.find(c => c.prefix === value)
                                return country ? (
                                  <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                    <Flag code={country.code} />
                                    {country.prefix}
                                  </Box>
                                ) : value
                              }}
                              sx={{
                                bgcolor: 'rgba(255, 255, 255, 0.8)',
                                transition: 'all 0.3s ease',
                                '&:hover': {
                                  bgcolor: 'white',
                                },
                                '&.Mui-focused': {
                                  bgcolor: 'white',
                                }
                              }}
                            >
                              {COUNTRY_PREFIXES.map((country) => (
                                <MenuItem value={country.prefix} key={country.code}>
                                  <Box sx={{ display: 'flex', alignItems: 'center' }}>
                                    <Flag code={country.code} />
                                    <Typography>{country.prefix} {country.country}</Typography>
                                  </Box>
                                </MenuItem>
                              ))}
                            </Select>
                          </FormControl>
                         <TextField
                           name="phoneNumber"
                           value={formData.phoneNumber}
                           onChange={handleChange}
                           placeholder="612345678"
                           fullWidth
                           size="small"
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
                     </Box>

                        {/* Password */}
                        <Box>
                          <Typography 
                            variant="caption"
                            fontWeight={600}
                            color="text.secondary"
                            sx={{ display: 'block', mb: 1 }}
                          >
                            Password
                          </Typography>
                          <TextField
                            type={showPassword ? 'text' : 'password'}
                            name="password"
                            value={formData.password}
                            onChange={handleChange}
                            placeholder="Create a password"
                            required
                            fullWidth
                            size="small"
                            slotProps={{
                              input: {
                                endAdornment: (
                                  <InputAdornment position="end">
                                    <IconButton
                                      aria-label="toggle password visibility"
                                      onClick={() => setShowPassword(!showPassword)}
                                      edge="end"
                                      size="small"
                                    >
                                      {showPassword ? <FiEyeOff /> : <FiEye />}
                                    </IconButton>
                                  </InputAdornment>
                                )
                              }
                            }}
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

                        {/* Confirm Password */}
                        <Box>
                          <Typography 
                            variant="caption"
                            fontWeight={600}
                            color="text.secondary"
                            sx={{ display: 'block', mb: 1 }}
                          >
                            Confirmar Contraseña
                          </Typography>
                          <TextField
                            type={showPassword ? 'text' : 'password'}
                            name="confirmPassword"
                            value={formData.confirmPassword}
                            onChange={handleChange}
                            placeholder="Confirm your password"
                            required
                            fullWidth
                            size="small"
                            error={formData.confirmPassword !== '' && formData.password !== formData.confirmPassword}
                            helperText={formData.confirmPassword !== '' && formData.password !== formData.confirmPassword ? 'Las contraseñas no coinciden' : ''}
                            slotProps={{
                              input: {
                                endAdornment: (
                                  <InputAdornment position="end">
                                    <IconButton
                                      aria-label="toggle password visibility"
                                      onClick={() => setShowPassword(!showPassword)}
                                      edge="end"
                                      size="small"
                                    >
                                      {showPassword ? <FiEyeOff /> : <FiEye />}
                                    </IconButton>
                                  </InputAdornment>
                                )
                              }
                            }}
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
                     </Stack>
                   </Grid>
                 </Grid>

                {/* Submit Button - Full width below both columns */}
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
                    mt: 3,
                    transition: 'all 0.3s ease',
                    '&:hover': { 
                      transform: 'translateY(-2px)',
                      boxShadow: '0 12px 24px rgba(251, 111, 4, 0.3)'
                    }
                  }}
                >
                  {isLoading ? <CircularProgress size={24} color="inherit" /> : 'Create Account'}
                </Button>

                {/* Already have an account? */}
                <Typography textAlign="center" variant="body2" color="text.secondary" fontWeight={500} sx={{ mt: 2 }}>
                  Already have an account?{' '}
                  <Link to="/login" style={{ textDecoration: 'none' }}>
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
                      Sign in here
                    </Typography>
                  </Link>
                </Typography>
              </form>
        </Stack>
      </Paper>
    </Box>
  )
}
