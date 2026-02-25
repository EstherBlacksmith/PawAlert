import { Box, List, ListItem, ListItemButton, ListItemIcon, ListItemText, IconButton, Divider, Badge } from '@mui/material'
import { useLocation, useNavigate } from 'react-router-dom'
import { FaHome, FaPaw, FaExclamationTriangle, FaMapMarkerAlt, FaShieldAlt, FaArrowLeft, FaArrowRight } from 'react-icons/fa'
import { useAuth } from '../../context/AuthContext'
import ConnectionStatus from '../notifications/ConnectionStatus'

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
  // For URLs with query params, check the pathname only
  const toPathname = to.split('?')[0]
  const isActive = location.pathname === toPathname || location.pathname.startsWith(toPathname + '/')

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
  const { isAdmin } = useAuth()

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
        {/* General Navigation Links */}
        <NavItem to="/dashboard" icon={<FaHome />} isCollapsed={isCollapsed}>
          Dashboard
        </NavItem>
        <NavItem to="/alerts" icon={<FaExclamationTriangle />} isCollapsed={isCollapsed}>
          All Alerts
        </NavItem>
        <NavItem to="/pets/public" icon={<FaPaw />} isCollapsed={isCollapsed}>
          All Pets
        </NavItem>
        <NavItem to="/alerts/nearby" icon={<FaMapMarkerAlt />} isCollapsed={isCollapsed}>
          Nearby Alerts
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
