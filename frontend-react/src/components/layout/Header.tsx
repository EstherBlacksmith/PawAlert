import { Box, AppBar, Toolbar, Typography, IconButton, Avatar, Menu, MenuItem, Divider, Badge } from '@mui/material'
import { FaBell, FaUser, FaSignOutAlt } from 'react-icons/fa'
import { useAuth } from '../../context/AuthContext'
import { useNotifications } from '../../context/NotificationContext'
import { useNavigate } from 'react-router-dom'
import { useState, useEffect } from 'react'

export default function Header() {
   const { user, logout } = useAuth()
   const { unreadBadgeCount } = useNotifications()
   const navigate = useNavigate()
   const [anchorEl, setAnchorEl] = useState<null | HTMLElement>(null)

   // Log badge count changes
   useEffect(() => {
     console.log('[Header] Badge count updated:', unreadBadgeCount);
   }, [unreadBadgeCount]);

  const handleLogout = () => {
    logout()
    navigate('/login')
    setAnchorEl(null)
  }

  const handleMenuOpen = (event: React.MouseEvent<HTMLElement>) => {
    setAnchorEl(event.currentTarget)
  }

  const handleMenuClose = () => {
    setAnchorEl(null)
  }

  return (
    <AppBar
      position="static"
      elevation={1}
      sx={{
        bgcolor: 'rgba(255, 255, 255, 0.85)',
        borderBottom: '1px solid',
        borderColor: 'grey.200',
      }}
    >
      <Toolbar sx={{ justifyContent: 'space-between' }}>
        {/* Left side - Logo/Brand */}
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
          <Typography
            variant="h6"
            sx={{
              cursor: 'pointer',
              color: 'primary.main',
              display: 'flex',
              alignItems: 'center',
              gap: 1,
              '&:hover': {
                color: 'primary.dark',
                transform: 'scale(1.02)',
              },
              transition: 'all 0.2s',
            }}
            onClick={() => navigate('/dashboard')}
          >
            <img 
              src="/labrador-head.png" 
              alt="PawAlert Logo" 
              style={{ width: 28, height: 28 }}
            />
            PawAlert
          </Typography>
        </Box>

        {/* Right side - User menu */}
        <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
          <IconButton
            aria-label="notifications"
            onClick={() => navigate('/subscriptions')}
            sx={{
              color: 'grey.600',
              '&:hover': {
                bgcolor: 'primary.50',
                color: 'primary.main',
              },
            }}
          >
            <Badge badgeContent={unreadBadgeCount} color="error" max={99}>
              <FaBell />
            </Badge>
          </IconButton>

          <IconButton
            onClick={handleMenuOpen}
            sx={{
              borderRadius: '50%',
              px: 1,
              color: 'grey.700',
              '&:hover': {
                bgcolor: 'primary.50',
              },
            }}
          >
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
              <Avatar sx={{ width: 32, height: 32, bgcolor: 'primary.main', fontSize: '0.875rem' }}>
                {user?.username?.charAt(0).toUpperCase() || 'U'}
              </Avatar>
              <Typography
                variant="body2"
                sx={{ display: { xs: 'none', md: 'block' }, color: 'grey.700' }}
              >
                {user?.username || 'User'}
              </Typography>
            </Box>
          </IconButton>

          <Menu
            anchorEl={anchorEl}
            open={Boolean(anchorEl)}
            onClose={handleMenuClose}
            anchorOrigin={{
              vertical: 'bottom',
              horizontal: 'right',
            }}
            transformOrigin={{
              vertical: 'top',
              horizontal: 'right',
            }}
          >
            <MenuItem
              onClick={() => {
                navigate('/profile')
                handleMenuClose()
              }}
              sx={{ color: 'grey.700' }}
            >
              <FaUser style={{ marginRight: 8, color: 'grey.600' }} />
              Profile
            </MenuItem>
            <Divider />
            <MenuItem
              onClick={handleLogout}
              sx={{ color: 'error.main' }}
            >
              <FaSignOutAlt style={{ marginRight: 8 }} />
              Logout
            </MenuItem>
          </Menu>
        </Box>
      </Toolbar>
    </AppBar>
  )
}
