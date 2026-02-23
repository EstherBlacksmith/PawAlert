import { createTheme } from '@mui/material/styles'

// PawAlert Color Palette
// Primary: #FFA500 (Orange) - Energy, warmth, urgency for alerts
// Secondary: #0093AF (Teal Blue) - Trust, calm, professionalism
// Neutral: #BEBFC5 (Silver Gray) - Balance, neutrality

// Extend the palette to include neutral
declare module '@mui/material/styles' {
  interface Palette {
    neutral: Palette['primary'];
  }
  interface PaletteOptions {
    neutral?: PaletteOptions['primary'];
  }
}

export const theme = createTheme({
  palette: {
    primary: {
      main: '#FFA500',
      light: '#FFB733',
      dark: '#CC8400',
      contrastText: '#ffffff',
    },
    secondary: {
      main: '#0093AF',
      light: '#33A8C1',
      dark: '#00758C',
      contrastText: '#ffffff',
    },
    neutral: {
      main: '#BEBFC5',
      light: '#D4D5D9',
      dark: '#9A9BA0',
      contrastText: '#333333',
    },
    success: {
      main: '#22c55e',
    },
    warning: {
      main: '#FFA500',
    },
    error: {
      main: '#ef4444',
    },
    background: {
      default: '#fafafa',
      paper: '#ffffff',
    },
  },
  typography: {
    fontFamily: '"Inter", "Roboto", "Helvetica", "Arial", sans-serif',
    h1: {
      fontSize: '2.5rem',
      fontWeight: 600,
    },
    h2: {
      fontSize: '2rem',
      fontWeight: 600,
    },
    h3: {
      fontSize: '1.75rem',
      fontWeight: 600,
    },
    h4: {
      fontSize: '1.5rem',
      fontWeight: 600,
    },
    h5: {
      fontSize: '1.25rem',
      fontWeight: 600,
    },
    h6: {
      fontSize: '1rem',
      fontWeight: 600,
    },
  },
  components: {
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: 'none',
          borderRadius: 8,
        },
      },
    },
    MuiCard: {
      styleOverrides: {
        root: {
          borderRadius: 12,
          boxShadow: '0 1px 3px 0 rgb(0 0 0 / 0.1), 0 1px 2px -1px rgb(0 0 0 / 0.1)',
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          '& .MuiOutlinedInput-root': {
            borderRadius: 8,
          },
        },
      },
    },
  },
})
