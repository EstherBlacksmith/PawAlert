import { Box, useMediaQuery, useTheme } from '@mui/material'
import { Outlet } from 'react-router-dom'
import { useState } from 'react'
import Sidebar from './Sidebar'
import Header from './Header'
import { useAuth } from '../../context/AuthContext'

export default function MainLayout() {
  const [isCollapsed, setIsCollapsed] = useState(false)
  const theme = useTheme()
  const isMobile = useMediaQuery(theme.breakpoints.down('md'))
  const { isAdmin } = useAuth()

  const toggleSidebar = () => setIsCollapsed(!isCollapsed)

  const sidebarWidth = isCollapsed ? 70 : 250

  return (
    <Box
      sx={{
        display: 'flex',
        minHeight: '100vh',
        bgcolor: 'background.default',
      }}
    >
      {/* Sidebar - Fixed position, extends to top of page */}
      <Box
        sx={{
          position: 'fixed',
          left: 0,
          top: 0,
          bottom: 0,
          width: isMobile ? 0 : sidebarWidth,
          transition: 'width 0.3s ease',
          zIndex: 1200,
          overflow: 'hidden',
        }}
      >
        <Sidebar isCollapsed={isCollapsed} onToggle={toggleSidebar} />
      </Box>

      {/* Header - Fixed position, aligned with content area (not spanning sidebar) */}
      <Box
        sx={{
          position: 'fixed',
          top: 0,
          left: isMobile ? 0 : sidebarWidth + 24,
          right: 0,
          height: '60px',
          zIndex: 1100,
          transition: 'left 0.3s ease',
        }}
      >
        <Header />
      </Box>

      {/* Main content area - below header */}
      <Box
        sx={{
          flex: 1,
          display: 'flex',
          flexDirection: 'column',
          ml: isMobile ? 0 : `${sidebarWidth}px`,
          transition: 'margin-left 0.3s ease',
          height: '100vh',
          overflow: 'hidden',
          pt: '60px',
        }}
      >
        {/* Page content */}
        <Box
          sx={{
            flex: 1,
            p: 3,
            maxWidth: '100%',
            overflowY: 'auto',
          }}
        >
          <Outlet />
        </Box>
      </Box>
    </Box>
  )
}
