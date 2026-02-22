import api from './api'
import { Pet, CreatePetRequest, UpdatePetRequest, ImageValidationResponse } from '../types'

export const petService = {
  getPets: async (): Promise<Pet[]> => {
    const response = await api.get<Pet[]>('/pets/my-pets')
    return response.data
  },

  getPet: async (petId: string): Promise<Pet> => {
    const response = await api.get<Pet>(`/pets/${petId}`)
    return response.data
  },

  createPet: async (petData: CreatePetRequest): Promise<Pet> => {
    const response = await api.post<Pet>('/pets', petData)
    return response.data
  },

  updatePet: async (petId: string, petData: UpdatePetRequest): Promise<Pet> => {
    console.log('[DEBUG] updatePet called with petId:', petId)
    console.log('[DEBUG] updatePet petData:', petData)
    const token = localStorage.getItem('token')
    console.log('[DEBUG] Token exists:', !!token)
    if (token) {
      console.log('[DEBUG] Token first 20 chars:', token.substring(0, 20))
    }
    const response = await api.patch<Pet>(`/pets/${petId}`, petData)
    return response.data
  },

  deletePet: async (petId: string): Promise<void> => {
    await api.delete(`/pets/${petId}`)
  },

  // Validate image and get AI-detected pet data
  validateImage: async (file: File): Promise<ImageValidationResponse> => {
    const formData = new FormData()
    formData.append('file', file)
    
    const response = await api.post<ImageValidationResponse>('/pets/validate-image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    return response.data
  },

  // Upload image to Cloudinary
  uploadImage: async (file: File): Promise<string> => {
    const formData = new FormData()
    formData.append('file', file)
    formData.append('folder', 'pets')
    
    const response = await api.post<string>('/images/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    return response.data
  },

  // ========== ADMIN METHODS ==========

  // Admin: Get all pets
  getAllPets: async (): Promise<Pet[]> => {
    const response = await api.get<Pet[]>('/pets/admin/all')
    return response.data
  },

  // Admin: Delete pet by ID (bypasses ownership check)
  adminDeletePet: async (petId: string): Promise<void> => {
    await api.delete(`/pets/admin/${petId}`)
  },

  // Get pets by user ID (admin use)
  getPetsByUserId: async (userId: string): Promise<Pet[]> => {
    const response = await api.get<Pet[]>(`/pets/user/${userId}`)
    return response.data
  },
}
