import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Box, Grid, Typography, Card, CardContent, Stack, CircularProgress, Paper, Button } from '@mui/material'
import { GiPawPrint, GiHealthPotion, GiCheck, GiSword, GiBell, GiCat } from '../components/icons'
import { useAuth } from '../context/AuthContext'
import { petService } from '../services/pet.service'
import { alertService } from '../services/alert.service'
import { Alert } from '../types'

interface StatCardProps {
  icon: React.ElementType
  label: string
  value: number
  color: string
  gradientColors?: string
  onClick?: () => void
}

function StatCard({ icon: Icon, label, value, color, onClick, gradientColors }: StatCardProps) {
  return (
    <Card 
      elevation={3}
      sx={{ 
        p: 3, 
        cursor: onClick ? "pointer" : "default",
        transition: 'all 0.3s',
        '&:hover': onClick ? { 
          boxShadow: 6, 
          transform: 'translateY(-4px)' 
        } : undefined,
        overflow: 'hidden',
        position: 'relative'
      }}
      onClick={onClick}
    >
      {/* Gradient background overlay */}
      {gradientColors && (
        <Box
          sx={{
            position: 'absolute',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            background: gradientColors,
            opacity: 0.1,
            pointerEvents: 'none'
          }}
        />
      )}
      <CardContent sx={{ position: 'relative' }}>
        <Stack direction="row" alignItems="center" gap={2}>
          <Box
            sx={{
              p: 1.5,
              borderRadius: '50%',
              background: gradientColors || `${color}.light`
            }}
          >
            <Icon size={24} />
          </Box>
          <Box>
            <Typography variant="h3" fontWeight="bold" color="text.primary">
              {value}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {label}
            </Typography>
          </Box>
        </Stack>
      </CardContent>
    </Card>
  )
}

export default function Dashboard() {
  const { user } = useAuth()
  const navigate = useNavigate()
  const [stats, setStats] = useState({
    myPets: 0,
    openAlerts: 0,
    inProgressAlerts: 0,
    resolvedAlerts: 0,
    mySubscriptions: 0,
  })
  const [isLoading, setIsLoading] = useState(true)

  useEffect(() => {
    const fetchStats = async () => {
      try {
        const [pets, alerts, subscriptions] = await Promise.all([
          petService.getPets(),
          alertService.getAlerts(),
          alertService.getMySubscriptions(),
        ])

        const openAlerts = alerts.filter((a: Alert) => a.status === 'OPENED').length
        const inProgressAlerts = alerts.filter((a: Alert) => a.status === 'SEEN' || a.status === 'SAFE').length
        const resolvedAlerts = alerts.filter((a: Alert) => a.status === 'CLOSED').length

        setStats({
          myPets: pets.length,
          openAlerts,
          inProgressAlerts,
          resolvedAlerts,
          mySubscriptions: subscriptions.length,
        })
      } catch (error) {
        console.error('Error fetching stats:', error)
      } finally {
        setIsLoading(false)
      }
    }

    fetchStats()
  }, [])

  if (isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '300px' }}>
        <CircularProgress color="primary" />
      </Box>
    )
  }

  return (
    <Paper sx={{ bgcolor: 'rgba(255, 255, 255, 0.85)', p: 3, borderRadius: 2, boxShadow: 3 }}>
      <Stack spacing={3}>
      {/* Welcome Section */}
      <Box>
        <Typography variant="h5" color="text.primary">
          Welcome back, {user?.username}!
        </Typography>
        <Typography color="text.secondary" sx={{ mt: 0.5 }}>
          Here's your personal overview
        </Typography>
      </Box>

       {/* Stats Grid - 5 cards showing user's personal data */}
       <Grid container spacing={3}>
         <Grid item xs={12} sm={6} md={2.4}>
           <StatCard 
             icon={GiPawPrint} 
             label="My Pets" 
             value={stats.myPets} 
             color="blue" 
             gradientColors="linear-gradient(to bottom right, #4091d7, #2e5f9e)"
             onClick={() => navigate('/pets')} 
           />
         </Grid>
         <Grid item xs={12} sm={6} md={2.4}>
           <StatCard
             icon={GiHealthPotion}
             label="Open Alerts"
             value={stats.openAlerts}
             color="red"
             gradientColors="linear-gradient(to bottom right, #b34045, #8b2e32)"
             onClick={() => navigate('/alerts?status=OPENED')}
           />
         </Grid>
         <Grid item xs={12} sm={6} md={2.4}>
           <StatCard
             icon={GiSword}
             label="In Progress"
             value={stats.inProgressAlerts}
             color="orange"
             gradientColors="linear-gradient(to bottom right, #ff9800, #f57c00)"
             onClick={() => navigate('/alerts?status=SEEN,SAFE')}
           />
         </Grid>
         <Grid item xs={12} sm={6} md={2.4}>
           <StatCard
             icon={GiCheck}
             label="Resolved"
             value={stats.resolvedAlerts}
             color="green"
             gradientColors="linear-gradient(to bottom right, #2d884d, #1f5a34)"
             onClick={() => navigate('/alerts?status=CLOSED')}
           />
         </Grid>
         <Grid item xs={12} sm={6} md={2.4}>
           <StatCard
             icon={GiBell}
             label="My Subscriptions"
             value={stats.mySubscriptions}
             color="purple"
             gradientColors="linear-gradient(to bottom right, #9c27b0, #7b1fa2)"
             onClick={() => navigate('/subscriptions')}
           />
         </Grid>
       </Grid>

      {/* Quick Actions */}
      <Box>
        <Typography variant="h6" mb={2} color="text.primary">
          Quick Actions
        </Typography>
        <Stack direction={{ xs: 'column', sm: 'row' }} spacing={2}>
           <Button
            variant="contained"
            size="large"
            startIcon={<GiCat />}
            onClick={() => navigate('/pets/create')}
            sx={{
              bgcolor: '#2d884d',
              '&:hover': { bgcolor: '#1f5a34' },
              px: 4,
              py: 1.5,
            }}
          >
            Add New Pet
          </Button>
          <Button
            variant="contained"
            size="large"
            startIcon={<GiHealthPotion />}
            onClick={() => navigate('/alerts/create')}
            sx={{
              bgcolor: '#b34045',
              '&:hover': { bgcolor: '#8b2e32' },
              px: 4,
              py: 1.5,
            }}
          >
            Create Alert
          </Button>
        </Stack>
      </Box>
      </Stack>
    </Paper>
  )
}
