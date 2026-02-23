# Plan de Migración: Chakra UI → Material UI (MUI)

## Resumen del Proyecto

- **Total de archivos con Chakra UI**: 35 archivos `.tsx`
- **Archivos de configuración**: `theme.ts`, `main.tsx`
- **Componentes personalizados**: Sistema de iconos, Toast personalizado

## Decisión: ¿Por qué Material UI?

| Criterio | MUI | Shadcn/ui | Ant Design |
|----------|-----|-----------|------------|
| Similitud API con Chakra | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ |
| Velocidad de migración | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ |
| Documentación | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| Sin configuración extra | ✅ | ❌ (Tailwind) | ✅ |

---

## Fase 1: Instalación y Configuración Inicial

### Paso 1.1: Instalar dependencias MUI

```bash
npm uninstall @chakra-ui/react
npm install @mui/material @mui/icons-material @emotion/react @emotion/styled
```

### Paso 1.2: Crear nuevo archivo de tema

**Archivo**: `frontend-react/src/theme.ts`

```typescript
import { createTheme } from '@mui/material/styles'

export const theme = createTheme({
  palette: {
    primary: {
      main: '#2563eb',
    },
    secondary: {
      main: '#64748b',
    },
  },
})
```

### Paso 1.3: Actualizar main.tsx

**Archivo**: `frontend-react/src/main.tsx`

```typescript
import ReactDOM from 'react-dom/client'
import { ThemeProvider, CssBaseline } from '@mui/material'
import { theme } from './theme'
import App from './App'

ReactDOM.createRoot(document.getElementById('root')!).render(
  <ThemeProvider theme={theme}>
    <CssBaseline />
    <App />
  </ThemeProvider>
)
```

---

## Fase 2: Mapeo de Componentes Chakra → MUI

### Componentes de Layout

| Chakra UI | MUI | Notas |
|-----------|-----|-------|
| `Box` | `Box` | Mismo componente |
| `Flex` | `Box display="flex"` | Usar prop `display` |
| `VStack` | `Stack direction="column"` | Importar de `@mui/material` |
| `HStack` | `Stack direction="row"` | Importar de `@mui/material` |
| `SimpleGrid` | `Grid` | Usar sistema de grid de MUI |
| `Card` | `Card` | Mismo nombre |
| `CardHeader` | `CardHeader` | Mismo nombre |
| `CardBody` | `CardContent` | Diferente nombre |

### Componentes de Formulario

| Chakra UI | MUI | Notas |
|-----------|-----|-------|
| `Button` | `Button` | Props diferentes |
| `Input` | `TextField` | MUI usa TextField |
| `Textarea` | `TextField multiline` | Usar prop `multiline` |
| `Select` | `Select` | API diferente |
| `Switch` | `Switch` | Similar |
| `RadioGroup` | `RadioGroup` | Similar |
| `FormControl` | `FormControl` | Similar |
| `FormLabel` | `InputLabel` | Diferente nombre |

### Componentes de Visualización

| Chakra UI | MUI | Notas |
|-----------|-----|-------|
| `Heading` | `Typography variant="h*"` | Usar variantes |
| `Text` | `Typography` | Usar variantes |
| `Badge` | `Chip` o `Badge` | Dos opciones |
| `Spinner` | `CircularProgress` | Diferente componente |
| `Alert` | `Alert` | Similar |
| `Avatar` | `Avatar` | Mismo nombre |
| `Icon` | `SvgIcon` o directo | Usar `@mui/icons-material` |

### Componentes de Diálogo

| Chakra UI | MUI | Notas |
|-----------|-----|-------|
| `Dialog.Root` | `Dialog` | API diferente |
| `Dialog.Content` | `DialogContent` | Mismo nombre |
| `Dialog.Header` | `DialogTitle` | Diferente nombre |
| `Dialog.Body` | `DialogContent` | Mismo contenedor |
| `Dialog.Footer` | `DialogActions` | Diferente nombre |
| `Dialog.Backdrop` | Automático | MUI lo maneja |

---

## Fase 3: Orden de Migración Recomendado

### 3.1 Archivos de configuración (2 archivos)
1. `frontend-react/src/theme.ts`
2. `frontend-react/src/main.tsx`

### 3.2 Componentes base (5 archivos)
1. `frontend-react/src/components/icons/index.tsx`
2. `frontend-react/src/components/ui/Toast.tsx`
3. `frontend-react/src/components/layout/Header.tsx`
4. `frontend-react/src/components/layout/Sidebar.tsx`
5. `frontend-react/src/components/layout/MainLayout.tsx`

### 3.3 Componentes de alertas (5 archivos)
1. `frontend-react/src/components/alerts/AlertEventsList.tsx`
2. `frontend-react/src/components/alerts/ClosureReasonDialog.tsx`
3. `frontend-react/src/components/alerts/StatusLocationDialog.tsx`
4. `frontend-react/src/components/alerts/SubscribeButton.tsx`
5. `frontend-react/src/components/alerts/NearbyAlerts.tsx`
6. `frontend-react/src/components/alerts/NearbyAlertsMap.tsx`

### 3.4 Componentes de mapa (2 archivos)
1. `frontend-react/src/components/map/LocationMap.tsx`
2. `frontend-react/src/components/map/AlertRouteMap.tsx`

### 3.5 Componentes de notificaciones (1 archivo)
1. `frontend-react/src/components/notifications/ConnectionStatus.tsx`

### 3.6 Páginas públicas (4 archivos)
1. `frontend-react/src/pages/Login.tsx`
2. `frontend-react/src/pages/Register.tsx`
3. `frontend-react/src/pages/NotFound.tsx`
4. `frontend-react/src/pages/Dashboard.tsx`

### 3.7 Páginas de mascotas (4 archivos)
1. `frontend-react/src/pages/pets/PetList.tsx`
2. `frontend-react/src/pages/pets/PetCreate.tsx`
3. `frontend-react/src/pages/pets/PetDetail.tsx`
4. `frontend-react/src/pages/pets/PetEdit.tsx`

### 3.8 Páginas de alertas (4 archivos)
1. `frontend-react/src/pages/alerts/AlertList.tsx`
2. `frontend-react/src/pages/alerts/AlertCreate.tsx`
3. `frontend-react/src/pages/alerts/AlertDetail.tsx`
4. `frontend-react/src/pages/alerts/AlertEdit.tsx`

### 3.9 Páginas de administración (7 archivos)
1. `frontend-react/src/pages/admin/AdminDashboard.tsx`
2. `frontend-react/src/pages/admin/PetDetail.tsx`
3. `frontend-react/src/pages/admin/UserDetail.tsx`
4. `frontend-react/src/pages/admin/UserEdit.tsx`
5. `frontend-react/src/pages/admin/components/AlertsTab.tsx`
6. `frontend-react/src/pages/admin/components/PetsTab.tsx`
7. `frontend-react/src/pages/admin/components/UsersTab.tsx`

### 3.10 Otras páginas (2 archivos)
1. `frontend-react/src/pages/Profile.tsx`
2. `frontend-react/src/pages/subscriptions/MySubscriptions.tsx`

---

## Fase 4: Ejemplo de Migración - Dialog

### Antes (Chakra UI v3)

```tsx
import { Dialog, Button, Heading } from '@chakra-ui/react'

function MyDialog({ open, onOpenChange }) {
  return (
    <Dialog.Root open={open} onOpenChange={(details) => onOpenChange(details.open)}>
      <Dialog.Backdrop />
      <Dialog.Positioner>
        <Dialog.Content>
          <Dialog.Header>
            <Heading size="md">Título</Heading>
          </Dialog.Header>
          <Dialog.Body>
            Contenido
          </Dialog.Body>
          <Dialog.Footer>
            <Button onClick={() => onOpenChange(false)}>Cerrar</Button>
          </Dialog.Footer>
        </Dialog.Content>
      </Dialog.Positioner>
    </Dialog.Root>
  )
}
```

### Después (MUI)

```tsx
import { Dialog, DialogTitle, DialogContent, DialogActions, Button, Typography } from '@mui/material'

function MyDialog({ open, onOpenChange }) {
  return (
    <Dialog open={open} onClose={() => onOpenChange(false)}>
      <DialogTitle>
        <Typography variant="h6">Título</Typography>
      </DialogTitle>
      <DialogContent>
        Contenido
      </DialogContent>
      <DialogActions>
        <Button onClick={() => onOpenChange(false)}>Cerrar</Button>
      </DialogActions>
    </Dialog>
  )
}
```

---

## Fase 5: Props a Convertir

### Colores
| Chakra | MUI |
|--------|-----|
| `color="gray.600"` | `color="text.secondary"` |
| `bg="purple.50"` | `sx={{ bgcolor: 'secondary.light' }}` |
| `colorPalette="green"` | `color="success"` |

### Espaciado
| Chakra | MUI |
|--------|-----|
| `p={4}` | `p={2}` (MUI usa unidades diferentes) |
| `m={2}` | `m={1}` |
| `gap={3}` | `spacing={2}` |

### Tamaños
| Chakra | MUI |
|--------|-----|
| `size="sm"` | `size="small"` |
| `size="md"` | `size="medium"` |
| `size="lg"` | `size="large"` |

---

## Fase 6: Verificación Post-Migración

### Checklist
- [ ] `npm run build` sin errores
- [ ] `npm run dev` inicia correctamente
- [ ] Login funciona
- [ ] Dashboard carga
- [ ] CRUD de mascotas funciona
- [ ] CRUD de alertas funciona
- [ ] Diálogos abren y cierran
- [ ] Mapas muestran correctamente
- [ ] Notificaciones funcionan
- [ ] Panel de admin funciona

---

## Notas Importantes

1. **No cambiar lógica de negocio**: Solo cambiar imports y props de componentes UI
2. **Mantener servicios intactos**: Los archivos `*.service.ts` no necesitan cambios
3. **Mantener contextos intactos**: AuthContext, ToastContext, etc. no cambian
4. **Mantener tipos intactos**: Los archivos de tipos no cambian

## Estimación de Archivos a Modificar

| Categoría | Cantidad |
|-----------|----------|
| Configuración | 2 |
| Componentes | 13 |
| Páginas | 20 |
| **Total** | **35** |
