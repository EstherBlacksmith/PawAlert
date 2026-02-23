import { useState, useEffect, useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import {
  Box,
  Typography,
  Stack,
  Card,
  CardContent,
  CircularProgress,
  Grid,
  Tabs,
  Tab,
  Paper,
  LinearProgress,
  Chip
} from '@mui/material'
import { FaUsers, FaPaw } from 'react-icons/fa'
import { GiHealthPotion, GiSword, GiCheck, GiBell } from '../../components/icons'
import { useAuth } from '../../context/AuthContext'
import { userService } from '../../services/user.service'
import { petService } from '../../services/pet.service'
import { alertService } from '../../services/alert.service'
import { User, Pet, Alert } from '../../types'
import UsersTab from './components/UsersTab'
import PetsTab from './components/PetsTab'
import AlertsTab from './components/AlertsTab'

interface Stats {
  totalUsers: number
  totalPets: number
  openedAlerts: number
  seenAlerts: number
  safeAlerts: number
  closedAlerts: number
  totalAlerts: number
  totalSubscriptions: number
}

interface TabPanelProps {
  children?: React.ReactNode
  index: number
  value: number
}

function TabPanel(props: TabPanelProps) {
  const { children, value, index, ...other } = props

  return (
    <div
      role="tabpanel"
      hidden={value !== index}
      id={`tabpanel-${index}`}
      aria-labelledby={`tab-${index}`}
      {...other}
    >
      {value === index && <Box sx={{ pt: 3 }}>{children}</Box>}
    </div>
  )
}

interface StatCardProps {
  icon: React.ElementType
  label: string
  value: number
  color: string
  gradientColors: string
}

function StatCard({ icon: Icon, label, value, color, gradientColors }: StatCardProps) {
  return (
    <Card 
      elevation={3}
      sx={{ 
        p: 3,
        transition: 'all 0.3s',
        '&:hover': { 
          boxShadow: 6, 
          transform: 'translateY(-4px)' 
        },
        overflow: 'hidden',
        position: 'relative'
      }}
    >
      {/* Gradient background overlay */}
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
      <CardContent sx={{ position: 'relative' }}>
        <Stack direction="row" alignItems="center" gap={2}>
          <Box
            sx={{
              p: 1.5,
              borderRadius: '50%',
              background: gradientColors
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

interface StatusBreakdownProps {
  opened: number
  seen: number
  safe: number
  closed: number
  total: number
}

function StatusBreakdown({ opened, seen, safe, closed, total }: StatusBreakdownProps) {
  if (total === 0) return null

  const openedPercent = (opened / total) * 100
  const seenPercent = (seen / total) * 100
  const safePercent = (safe / total) * 100
  const closedPercent = (closed / total) * 100

  return (
    <Card elevation={3} sx={{ p: 3 }}>
      <Typography variant="h6" gutterBottom color="text.primary">
        Alert Status Distribution
      </Typography>
      <Stack spacing={2}>
        {/* OPENED */}
        <Box>
          <Stack direction="row" justifyContent="space-between" alignItems="center" mb={0.5}>
            <Stack direction="row" alignItems="center" gap={1}>
              <Chip 
                label="OPENED" 
                size="small" 
                sx={{ 
                  bgcolor: '#ffebee', 
                  color: '#c62828',
                  fontWeight: 'bold',
                  fontSize: '0.7rem'
                }} 
              />
              <Typography variant="body2" color="text.secondary">
                {opened} alerts
              </Typography>
            </Stack>
            <Typography variant="body2" fontWeight="bold" color="text.primary">
              {openedPercent.toFixed(1)}%
            </Typography>
          </Stack>
          <LinearProgress 
            variant="determinate" 
            value={openedPercent} 
            sx={{ 
              height: 8, 
              borderRadius: 4,
              bgcolor: '#ffebee',
              '& .MuiLinearProgress-bar': {
                bgcolor: '#c62828',
                borderRadius: 4
              }
            }} 
          />
        </Box>

        {/* SEEN */}
        <Box>
          <Stack direction="row" justifyContent="space-between" alignItems="center" mb={0.5}>
            <Stack direction="row" alignItems="center" gap={1}>
              <Chip 
                label="SEEN" 
                size="small" 
                sx={{ 
                  bgcolor: '#fff3e0', 
                  color: '#e65100',
                  fontWeight: 'bold',
                  fontSize: '0.7rem'
                }} 
              />
              <Typography variant="body2" color="text.secondary">
                {seen} alerts
              </Typography>
            </Stack>
            <Typography variant="body2" fontWeight="bold" color="text.primary">
              {seenPercent.toFixed(1)}%
            </Typography>
          </Stack>
          <LinearProgress 
            variant="determinate" 
            value={seenPercent} 
            sx={{ 
              height: 8, 
              borderRadius: 4,
              bgcolor: '#fff3e0',
              '& .MuiLinearProgress-bar': {
                bgcolor: '#e65100',
                borderRadius: 4
              }
            }} 
          />
        </Box>

        {/* SAFE */}
        <Box>
          <Stack direction="row" justifyContent="space-between" alignItems="center" mb={0.5}>
            <Stack direction="row" alignItems="center" gap={1}>
              <Chip 
                label="SAFE" 
                size="small" 
                sx={{ 
                  bgcolor: '#e3f2fd', 
                  color: '#1565c0',
                  fontWeight: 'bold',
                  fontSize: '0.7rem'
                }} 
              />
              <Typography variant="body2" color="text.secondary">
                {safe} alerts
              </Typography>
            </Stack>
            <Typography variant="body2" fontWeight="bold" color="text.primary">
              {safePercent.toFixed(1)}%
            </Typography>
          </Stack>
          <LinearProgress 
            variant="determinate" 
            value={safePercent} 
            sx={{ 
              height: 8, 
              borderRadius: 4,
              bgcolor: '#e3f2fd',
              '& .MuiLinearProgress-bar': {
                bgcolor: '#1565c0',
                borderRadius: 4
              }
            }} 
          />
        </Box>

        {/* CLOSED */}
        <Box>
          <Stack direction="row" justifyContent="space-between" alignItems="center" mb={0.5}>
            <Stack direction="row" alignItems="center" gap={1}>
              <Chip 
                label="CLOSED" 
                size="small" 
                sx={{ 
                  bgcolor: '#e8f5e9', 
                  color: '#2e7d32',
                  fontWeight: 'bold',
                  fontSize: '0.7rem'
                }} 
              />
              <Typography variant="body2" color="text.secondary">
                {closed} alerts
              </Typography>
            </Stack>
            <Typography variant="body2" fontWeight="bold" color="text.primary">
              {closedPercent.toFixed(1)}%
            </Typography>
          </Stack>
          <LinearProgress 
            variant="determinate" 
            value={closedPercent} 
            sx={{ 
              height: 8, 
              borderRadius: 4,
              bgcolor: '#e8f5e9',
              '& .MuiLinearProgress-bar': {
                bgcolor: '#2e7d32',
                borderRadius: 4
              }
            }} 
          />
        </Box>
      </Stack>
    </Card>
  )
}

interface RecentActivityProps {
  alerts: Alert[]
}

function RecentActivity({ alerts }: RecentActivityProps) {
  const recentAlerts = [...alerts]
    .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime())
    .slice(0, 5)

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'OPENED': return '#c62828'
      case 'SEEN': return '#e65100'
      case 'SAFE': return '#1565c0'
      case 'CLOSED': return '#2e7d32'
      default: return '#757575'
    }
  }

  const getTimeAgo = (dateString: string) => {
    const date = new Date(dateString)
    const now = new Date()
    const diffMs = now.getTime() - date.getTime()
    const diffMins = Math.floor(diffMs / 60000)
    const diffHours = Math.floor(diffMins / 60)
    const diffDays = Math.floor(diffHours / 24)

    if (diffDays > 0) return `${diffDays}d ago`
    if (diffHours > 0) return `${diffHours}h ago`
    if (diffMins > 0) return `${diffMins}m ago`
    return 'Just now'
  }

  return (
    <Card elevation={3} sx={{ p: 3 }}>
      <Typography variant="h6" gutterBottom color="text.primary">
        Recent Activity
      </Typography>
      {recentAlerts.length === 0 ? (
        <Typography variant="body2" color="text.secondary">
          No recent alerts
        </Typography>
      ) : (
        <Stack spacing={1.5}>
          {recentAlerts.map((alert) => (
            <Box 
              key={alert.id}
              sx={{ 
                p: 1.5, 
                borderRadius: 1, 
                bgcolor: 'grey.50',
                borderLeft: 3,
                borderColor: getStatusColor(alert.status)
              }}
            >
              <Stack direction="row" justifyContent="space-between" alignItems="flex-start">
                <Box>
                  <Typography variant="body2" fontWeight="medium" color="text.primary">
                    {alert.title}
                  </Typography>
                  <Typography variant="caption" color="text.secondary">
                    by {alert.createdByUsername || 'Unknown'}
                  </Typography>
                </Box>
                <Stack alignItems="flex-end" gap={0.5}>
                  <Chip 
                    label={alert.status} 
                    size="small" 
                    sx={{ 
                      bgcolor: `${getStatusColor(alert.status)}15`, 
                      color: getStatusColor(alert.status),
                      fontWeight: 'bold',
                      fontSize: '0.65rem',
                      height: 20
                    }} 
                  />
                  <Typography variant="caption" color="text.secondary">
                    {getTimeAgo(alert.createdAt)}
                  </Typography>
                </Stack>
              </Stack>
            </Box>
          ))}
        </Stack>
      )}
    </Card>
  )
}

export default function AdminDashboard() {
  const { isAdmin } = useAuth()
  const navigate = useNavigate()
  
  const [users, setUsers] = useState<User[]>([])
  const [pets, setPets] = useState<Pet[]>([])
  const [alerts, setAlerts] = useState<Alert[]>([])
  const [stats, setStats] = useState<Stats>({
    totalUsers: 0,
    totalPets: 0,
    openedAlerts: 0,
    seenAlerts: 0,
    safeAlerts: 0,
    closedAlerts: 0,
    totalAlerts: 0,
    totalSubscriptions: 0
  })
  
  const [isLoading, setIsLoading] = useState(true)
  const [activeTab, setActiveTab] = useState(0)

  const fetchData = useCallback(async () => {
    setIsLoading(true)
    try {
      const [usersData, petsData, alertsData] = await Promise.all([
        userService.getAllUsers().catch(() => [] as User[]),
        petService.getAllPets().catch(() => [] as Pet[]),
        alertService.getAllAlertsForAdmin().catch(() => [] as Alert[])
      ])
      
      setUsers(usersData)
      setPets(petsData)
      setAlerts(alertsData)
      
      const openedAlerts = alertsData.filter(a => a.status === 'OPENED').length
      const seenAlerts = alertsData.filter(a => a.status === 'SEEN').length
      const safeAlerts = alertsData.filter(a => a.status === 'SAFE').length
      const closedAlerts = alertsData.filter(a => a.status === 'CLOSED').length

      // Note: Total subscriptions would require an admin endpoint
      // Currently there's no admin API to get all platform subscriptions
      // This could be implemented in the backend as GET /alerts/subscriptions/admin/all
      const totalSubscriptions = 0
      
      setStats({
        totalUsers: usersData.length,
        totalPets: petsData.length,
        openedAlerts,
        seenAlerts,
        safeAlerts,
        closedAlerts,
        totalAlerts: alertsData.length,
        totalSubscriptions
      })
    } catch (error) {
      console.error('Error fetching admin data:', error)
    } finally {
      setIsLoading(false)
    }
  }, [])

  useEffect(() => {
    // Redirect non-admin users
    if (!isAdmin()) {
      navigate('/dashboard')
      return
    }
    
    fetchData()
  }, [isAdmin, navigate, fetchData])

  const refreshData = () => {
    fetchData()
  }

  if (isLoading) {
    return (
      <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
        <CircularProgress />
      </Box>
    )
  }

  return (
    <Paper sx={{ bgcolor: 'rgba(255, 255, 255, 0.85)', p: 3, borderRadius: 2, boxShadow: 3 }}>
      <Stack spacing={3}>
        {/* Header */}
        <Box>
          <Typography variant="h4" sx={{ fontWeight: 'bold' }}>
            Admin Dashboard
          </Typography>
          <Typography color="text.secondary" sx={{ mt: 0.5 }}>
            Manage users, pets, and alerts across the platform
          </Typography>
        </Box>

        {/* Statistics Cards - 6 cards */}
        <Grid container spacing={2}>
          <Grid item xs={12} sm={6} md={2}>
            <StatCard 
              icon={FaUsers} 
              label="Total Users" 
              value={stats.totalUsers} 
              color="blue"
              gradientColors="linear-gradient(to bottom right, #4091d7, #2e5f9e)"
            />
          </Grid>

          <Grid item xs={12} sm={6} md={2}>
            <StatCard 
              icon={FaPaw} 
              label="Total Pets" 
              value={stats.totalPets} 
              color="purple"
              gradientColors="linear-gradient(to bottom right, #9c27b0, #7b1fa2)"
            />
          </Grid>

          <Grid item xs={12} sm={6} md={2}>
            <StatCard 
              icon={GiHealthPotion} 
              label="Open Alerts" 
              value={stats.openedAlerts} 
              color="red"
              gradientColors="linear-gradient(to bottom right, #b34045, #8b2e32)"
            />
          </Grid>

          <Grid item xs={12} sm={6} md={2}>
            <StatCard 
              icon={GiSword} 
              label="In Progress" 
              value={stats.seenAlerts + stats.safeAlerts} 
              color="orange"
              gradientColors="linear-gradient(to bottom right, #ff9800, #f57c00)"
            />
          </Grid>

          <Grid item xs={12} sm={6} md={2}>
            <StatCard 
              icon={GiCheck} 
              label="Resolved" 
              value={stats.closedAlerts} 
              color="green"
              gradientColors="linear-gradient(to bottom right, #2d884d, #1f5a34)"
            />
          </Grid>

          <Grid item xs={12} sm={6} md={2}>
            <StatCard 
              icon={GiBell} 
              label="Total Subscriptions" 
              value={stats.totalSubscriptions} 
              color="teal"
              gradientColors="linear-gradient(to bottom right, #009688, #00796b)"
            />
          </Grid>
        </Grid>

        {/* Status Breakdown and Recent Activity */}
        <Grid container spacing={2}>
          <Grid item xs={12} md={6}>
            <StatusBreakdown 
              opened={stats.openedAlerts}
              seen={stats.seenAlerts}
              safe={stats.safeAlerts}
              closed={stats.closedAlerts}
              total={stats.totalAlerts}
            />
          </Grid>
          <Grid item xs={12} md={6}>
            <RecentActivity alerts={alerts} />
          </Grid>
        </Grid>

        {/* Tabs */}
        <Card elevation={3} sx={{ background: 'rgba(255, 255, 255, 0.97)' }}>
          <Tabs
            value={activeTab}
            onChange={(e, newValue) => setActiveTab(newValue)}
            sx={{ borderBottom: 1, borderColor: 'divider' }}
          >
            <Tab label={`Alerts (${stats.totalAlerts})`} id="tab-0" aria-controls="tabpanel-0" />
            <Tab label={`Users (${stats.totalUsers})`} id="tab-1" aria-controls="tabpanel-1" />
            <Tab label={`Pets (${stats.totalPets})`} id="tab-2" aria-controls="tabpanel-2" />
          </Tabs>

          <Box sx={{ p: 2 }}>
            <TabPanel value={activeTab} index={0}>
              <AlertsTab />
            </TabPanel>

            <TabPanel value={activeTab} index={1}>
              <UsersTab />
            </TabPanel>

            <TabPanel value={activeTab} index={2}>
              <PetsTab />
            </TabPanel>
          </Box>
        </Card>
      </Stack>
    </Paper>
  )
}
