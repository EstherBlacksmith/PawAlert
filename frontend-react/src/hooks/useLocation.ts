import { useState, useCallback } from 'react'

export interface LocationResult {
  latitude: number | null
  longitude: number | null
  source: 'gps' | 'ip' | 'dev-default' | null
  error: string | null
  isLoading: boolean
}

export interface DetectLocationReturn {
  latitude: number | null
  longitude: number | null
  source: 'gps' | 'ip' | 'dev-default' | null
  error: string | null
}

export interface UseLocationReturn extends LocationResult {
  detectLocation: () => Promise<DetectLocationReturn>
  clearLocation: () => void
}

interface IpApiResponse {
  status: string
  lat: number
  lon: number
  message?: string
}

const GPS_TIMEOUT = 10000
const GPS_MAXIMUM_AGE = 0

/**
 * Default location for development mode (Madrid, Spain)
 */
const getDevDefaultLocation = () => {
  return { lat: 40.416775, lon: -3.703790 }
}

export function useLocation(): UseLocationReturn {
  const [latitude, setLatitude] = useState<number | null>(null)
  const [longitude, setLongitude] = useState<number | null>(null)
  const [source, setSource] = useState<'gps' | 'ip' | 'dev-default' | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(false)

  const getGpsLocation = (): Promise<GeolocationPosition> => {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject(new Error('Geolocation is not supported by this browser'))
        return
      }

      navigator.geolocation.getCurrentPosition(
        resolve,
        reject,
        {
          enableHighAccuracy: true,
          timeout: GPS_TIMEOUT,
          maximumAge: GPS_MAXIMUM_AGE
        }
      )
    })
  }

  /**
   * Get location from IP geolocation API
   * Uses ipapi.co which supports HTTPS (free: 1000 requests/month)
   */
  const getIpLocation = async (): Promise<{ lat: number; lon: number }> => {
    try {
      const response = await fetch('https://ipapi.co/json/')
      if (!response.ok) {
        throw new Error(`IP geolocation failed: ${response.status}`)
      }
      const data = await response.json()
      if (data.latitude && data.longitude) {
        return { lat: data.latitude, lon: data.longitude }
      }
      throw new Error('Invalid IP geolocation response')
    } catch (error) {
      console.error('IP geolocation error:', error)
      throw error
    }
  }

  const detectLocation = useCallback(async (): Promise<DetectLocationReturn> => {
    setIsLoading(true)
    setError(null)

    try {
      // Try GPS first
      const position = await getGpsLocation()
      setLatitude(position.coords.latitude)
      setLongitude(position.coords.longitude)
      setSource('gps')
      return { latitude: position.coords.latitude, longitude: position.coords.longitude, source: 'gps', error: null }
    } catch (gpsError: any) {
      console.log('GPS failed, trying IP geolocation:', gpsError)
      
      try {
        // Fallback to IP geolocation
        const ipLocation = await getIpLocation()
        setLatitude(ipLocation.lat)
        setLongitude(ipLocation.lon)
        setSource('ip')
        return { latitude: ipLocation.lat, longitude: ipLocation.lon, source: 'ip', error: null }
      } catch (ipError: any) {
        console.error('IP geolocation also failed:', ipError)
        
        // In development mode, use a default location
        if (import.meta.env.DEV) {
          const devLocation = getDevDefaultLocation()
          setLatitude(devLocation.lat)
          setLongitude(devLocation.lon)
          setSource('dev-default')
          console.log('Using development default location (Madrid)')
          return { latitude: devLocation.lat, longitude: devLocation.lon, source: 'dev-default', error: null }
        }
        
        // Set appropriate error message
        let errorMessage = 'Could not determine location automatically. Please enter coordinates manually.'
        
        if (gpsError.code === 1) {
          errorMessage = 'Location access denied. Please enable location permissions or enter coordinates manually.'
        } else if (gpsError.code === 2) {
          errorMessage = 'Unable to determine location. Please enter coordinates manually.'
        } else if (gpsError.code === 3) {
          errorMessage = 'Location request timed out. Please try again or enter coordinates manually.'
        }
        
        setError(errorMessage)
        setLatitude(null)
        setLongitude(null)
        setSource(null)
        return { latitude: null, longitude: null, source: null, error: errorMessage }
      }
    } finally {
      setIsLoading(false)
    }
  }, [])

  const clearLocation = useCallback(() => {
    setLatitude(null)
    setLongitude(null)
    setSource(null)
    setError(null)
  }, [])

  return {
    latitude,
    longitude,
    source,
    error,
    isLoading,
    detectLocation,
    clearLocation
  }
}

export default useLocation
