import { Routes, Route, Navigate } from 'react-router-dom'
import { useAuth } from './context/AuthContext'
import Login from './pages/Login'
import Register from './pages/Register'
import Dashboard from './pages/Dashboard'
import MainLayout from './components/layout/MainLayout'
import PetList from './pages/pets/PetList'
import PetCreate from './pages/pets/PetCreate'
import PetEdit from './pages/pets/PetEdit'
import PetDetail from './pages/pets/PetDetail'
import AlertList from './pages/alerts/AlertList'
import AlertCreate from './pages/alerts/AlertCreate'
import AlertDetail from './pages/alerts/AlertDetail'
import AlertEdit from './pages/alerts/AlertEdit'
import Profile from './pages/Profile'
import NotFound from './pages/NotFound'
import AdminDashboard from './pages/admin/AdminDashboard'
import UserDetail from './pages/admin/UserDetail'
import UserEdit from './pages/admin/UserEdit'
import MySubscriptions from './pages/subscriptions/MySubscriptions'

function ProtectedRoute({ children }: { children: React.ReactNode }) {
  const { isAuthenticated, isLoading } = useAuth()

  if (isLoading) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh' }}>
        Loading...
      </div>
    )
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />
  }

  return <>{children}</>
}

function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      
      <Route
        path="/"
        element={
          <ProtectedRoute>
            <MainLayout />
          </ProtectedRoute>
        }
      >
        <Route index element={<Navigate to="/dashboard" replace />} />
        <Route path="dashboard" element={<Dashboard />} />
        <Route path="pets" element={<PetList />} />
        <Route path="pets/create" element={<PetCreate />} />
        <Route path="pets/:id" element={<PetDetail />} />
        <Route path="pets/:id/edit" element={<PetEdit />} />
        <Route path="alerts" element={<AlertList />} />
        <Route path="alerts/create" element={<AlertCreate />} />
        <Route path="alerts/:id" element={<AlertDetail />} />
        <Route path="alerts/:id/edit" element={<AlertEdit />} />
        <Route path="subscriptions" element={<MySubscriptions />} />
        <Route path="profile" element={<Profile />} />
        <Route path="admin/dashboard" element={<AdminDashboard />} />
        <Route path="admin/users/:id" element={<UserDetail />} />
        <Route path="admin/users/:id/edit" element={<UserEdit />} />
      </Route>

      <Route path="*" element={<NotFound />} />
    </Routes>
  )
}

export default App
