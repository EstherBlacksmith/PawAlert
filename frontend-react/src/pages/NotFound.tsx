import { Box, Typography, Button, Stack } from '@mui/material'
import { Link } from 'react-router-dom'

export default function NotFound() {
  return (
    <Box
      sx={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        bgcolor: 'grey.50',
        p: 2
      }}
    >
      <Stack spacing={3} textAlign="center" alignItems="center">
        <Typography
          variant="h1"
          sx={{
            fontSize: '8rem',
            fontWeight: 'bold',
            color: 'primary.main'
          }}
        >
          404
        </Typography>
        <Typography variant="h3" color="text.primary">
          Page Not Found
        </Typography>
        <Typography
          color="text.secondary"
          sx={{ fontSize: '1.125rem', maxWidth: '400px' }}
        >
          The page you're looking for doesn't exist or has been moved.
        </Typography>
        <Button
          component={Link}
          to="/dashboard"
          variant="contained"
          color="primary"
          size="large"
          sx={{ mt: 2 }}
        >
          Go to Dashboard
        </Button>
      </Stack>
    </Box>
  )
}
