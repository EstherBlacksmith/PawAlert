import { useEffect, useState } from 'react'
import { Box, List, ListItem, ListItemButton, ListItemIcon, ListItemText, IconButton, Divider, Badge } from '@mui/material'
import { useLocation, useNavigate } from 'react-router-dom'
import { FaHome, FaPaw, FaExclamationTriangle, FaUser, FaShieldAlt, FaBell, FaArrowLeft, FaArrowRight } from 'react-icons/fa'
import { useAuth } from '../../context/AuthContext'
import ConnectionStatus from '../notifications/ConnectionStatus'
import { alertService } from '../../services/alert.service'

interface NavItemProps {
  to: string
  icon: React.ReactNode
  children: React.ReactNode
  isCollapsed?: boolean
  badge?: number
}

function NavItem({ to, icon, children, isCollapsed, badge }: NavItemProps) {
  const location = useLocation()
  const navigate = useNavigate()
  const isActive = location.pathname === to || location.pathname.startsWith(to + '/')

  return (
    <ListItem disablePadding sx={{ display: 'block' }}>
      <ListItemButton
        onClick={() => navigate(to)}
        sx={{
          minHeight: 48,
          justifyContent: isCollapsed ? 'center' : 'initial',
          px: 2.5,
          borderRadius: 1,
          mx: 1,
          position: 'relative',
          bgcolor: isActive ? 'primary.50' : 'transparent',
          color: isActive ? 'primary.main' : 'text.secondary',
          '&:hover': {
            bgcolor: 'primary.50',
          },
          '&::before': isActive ? {
            content: '""',
            position: 'absolute',
            left: 0,
            top: '50%',
            transform: 'translateY(-50%)',
            width: 3,
            height: '60%',
            bgcolor: 'primary.main',
            borderRadius: 1,
          } : {},
        }}
      >
        <ListItemIcon
          sx={{
            minWidth: 0,
            mr: isCollapsed ? 0 : 3,
            justifyContent: 'center',
            color: isActive ? 'primary.main' : 'inherit',
          }}
        >
          {badge !== undefined && badge > 0 ? (
            <Badge badgeContent={badge} color="error" max={99}>
              {icon}
            </Badge>
          ) : (
            icon
          )}
        </ListItemIcon>
        {!isCollapsed && (
          <ListItemText
            primary={children}
            primaryTypographyProps={{
              fontWeight: isActive ? 'medium' : 'regular',
            }}
          />
        )}
        {!isCollapsed && badge !== undefined && badge > 0 && (
          <Badge badgeContent={badge} color="error" max={99} />
        )}
      </ListItemButton>
    </ListItem>
  )
}

interface SidebarProps {
  isCollapsed: boolean
  onToggle: () => void
}

export default function Sidebar({ isCollapsed, onToggle }: SidebarProps) {
  const { isAdmin, user } = useAuth()
  const [openedAlertsCount, setOpenedAlertsCount] = useState(0)
  const [subscriptionsCount, setSubscriptionsCount] = useState(0)

  useEffect(() => {
    const fetchCounts = async () => {
      if (!user?.userId) return
      
      try {
        // Fetch user's OPENED alerts count
        const alerts = await alertService.searchAlertsWithFilters({
          status: 'OPENED',
          userId: user.userId
        })
        setOpenedAlertsCount(alerts.length)

        // Fetch user's subscriptions count
        const subscriptions = await alertService.getMySubscriptions()
        setSubscriptionsCount(subscriptions.length)
      } catch (error) {
        console.error('Error fetching sidebar counts:', error)
      }
    }

    fetchCounts()
  }, [user?.userId])

  return (
    <Box
      component="aside"
      sx={{
        width: isCollapsed ? '70px' : '250px',
        bgcolor: 'background.paper',
        borderRight: '1px solid',
        borderColor: 'divider',
        height: '100%',
        pb: 2,
        transition: 'width 0.3s ease',
        overflowX: 'hidden',
        display: 'flex',
        flexDirection: 'column',
      }}
    >
      {/* Toggle Button - with top padding since sidebar extends to top */}
      <Box sx={{ display: 'flex', justifyContent: isCollapsed ? 'center' : 'flex-end', px: 1, pt: 2, pb: 1 }}>
        <IconButton
          aria-label={isCollapsed ? 'Expand sidebar' : 'Collapse sidebar'}
          size="small"
          onClick={onToggle}
          color="primary"
        >
          {isCollapsed ? <FaArrowRight /> : <FaArrowLeft />}
        </IconButton>
      </Box>

      <List sx={{ px: 0, flex: 1 }}>
        <NavItem to="/dashboard" icon={<FaHome />} isCollapsed={isCollapsed}>
          Dashboard
        </NavItem>
        <NavItem to="/pets" icon={<FaPaw />} isCollapsed={isCollapsed}>
          My Pets
        </NavItem>
        <NavItem to="/alerts" icon={<FaExclamationTriangle />} isCollapsed={isCollapsed} badge={openedAlertsCount}>
          My Alerts
        </NavItem>
        <NavItem to="/subscriptions" icon={<FaBell />} isCollapsed={isCollapsed} badge={subscriptionsCount}>
          My Subscriptions
        </NavItem>
        <NavItem to="/profile" icon={<FaUser />} isCollapsed={isCollapsed}>
          Profile
        </NavItem>
        {isAdmin() && (
          <NavItem to="/admin/dashboard" icon={<FaShieldAlt />} isCollapsed={isCollapsed}>
            Admin Panel
          </NavItem>
        )}
      </List>

      {!isCollapsed && (
        <Box sx={{ mt: 'auto', px: 2, pb: 2 }}>
          <Divider sx={{ mb: 2 }} />
          <ConnectionStatus />
        </Box>
      )}
    </Box>
  )
}
