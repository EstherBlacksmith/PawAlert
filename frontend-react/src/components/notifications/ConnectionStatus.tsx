import { Stack, Typography, Box } from '@mui/material'
import { useNotifications } from '../../context/NotificationContext'

export default function ConnectionStatus() {
  const { isConnected } = useNotifications()

  return (
    <Stack direction="row" spacing={1} alignItems="center" sx={{ px: 1.5, py: 1 }}>
      <Box
        sx={{
          width: 8,
          height: 8,
          borderRadius: '50%',
          bgcolor: isConnected ? 'success.main' : 'error.main',
        }}
      />
      <Typography variant="body2" color="text.secondary">
        {isConnected ? 'Connected' : 'Disconnected'}
      </Typography>
    </Stack>
  )
}
