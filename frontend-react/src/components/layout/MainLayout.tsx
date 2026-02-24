import { Box, useMediaQuery, useTheme } from '@mui/material'
import { Outlet } from 'react-router-dom'
import { useState } from 'react'
import Sidebar from './Sidebar'
import Header from './Header'
import { useAuth } from '../../context/AuthContext'
import adminBgImage from '../../assets/admin-bg-image.jpg'

export default function MainLayout() {
  const [isCollapsed, setIsCollapsed] = useState(false)
  const theme = useTheme()
  const isMobile = useMediaQuery(theme.breakpoints.down('md'))
  const { isAdmin } = useAuth()

  const toggleSidebar = () => setIsCollapsed(!isCollapsed)

  const sidebarWidth = isCollapsed ? 70 : 250

  // Determine background based on user role
  const getBackgroundStyle = () => {
    if (isAdmin()) {
      return {
        backgroundImage: `url(${adminBgImage})`,
        backgroundSize: 'cover',
        backgroundPosition: 'center',
        backgroundAttachment: 'fixed',
        backgroundRepeat: 'no-repeat',
      }
    } else {
      // Regular user - transparent background to show app background
      return {
        background: 'transparent',
      }
    }
  }

  return (
    <Box
      sx={{
        display: 'flex',
        minHeight: '100vh',
        width: '100%',
        ...getBackgroundStyle(),
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

      {/* Background overlay - covers entire viewport */}
      <Box
        sx={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: isAdmin() ? 'rgba(0, 0, 0, 0.3)' : 'transparent',
          pointerEvents: 'none',
          zIndex: 0,
        }}
      />

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
          position: 'relative',
          zIndex: 1,
        }}
      >
        {/* Page content - transparent background, each page controls its own styling */}
        <Box
          sx={{
            flex: 1,
            p: 3,
            maxWidth: '100%',
            overflowY: 'auto',
            position: 'relative',
            zIndex: 1,
            backgroundColor: 'transparent',
            minHeight: 'calc(100vh - 100px)',
          }}
        >
          <Outlet />
        </Box>
      </Box>
    </Box>
  )
}
