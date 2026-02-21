import { useState, useEffect, useCallback, useRef } from 'react';
import { NotificationMessage } from '../types/notification';

const SSE_URL = '/api/notifications/stream';
const RECONNECT_DELAY = 3000; // 3 seconds

interface UseSseNotificationsReturn {
  isConnected: boolean;
  connect: () => void;
  disconnect: () => void;
}

export function useSseNotifications(
  onNotification: (notification: NotificationMessage) => void
): UseSseNotificationsReturn {
  const [isConnected, setIsConnected] = useState(false);
  const eventSourceRef = useRef<EventSource | null>(null);
  const reconnectTimeoutRef = useRef<NodeJS.Timeout | null>(null);
  const onNotificationRef = useRef(onNotification);

  // Keep callback ref updated
  useEffect(() => {
    onNotificationRef.current = onNotification;
  }, [onNotification]);

  const connect = useCallback(() => {
    // Don't connect if already connected
    if (eventSourceRef.current) {
      console.log('[SSE] connect() called but already connected, skipping');
      return;
    }

    console.log('[SSE] connect() called - creating new EventSource');
    const eventSource = new EventSource(SSE_URL);
    eventSourceRef.current = eventSource;

    eventSource.addEventListener('connected', () => {
      console.log('[SSE] Connected to notification stream');
      setIsConnected(true);
    });

    eventSource.addEventListener('notification', (event) => {
      try {
        const notification: NotificationMessage = JSON.parse(event.data);
        // DEBUG: Detailed notification receipt logging
        console.log('[SSE] Notification event received:', {
          id: notification.id,
          title: notification.title,
          type: notification.type,
          alertId: notification.alertId,
          timestamp: new Date().toISOString(),
          rawEvent: event.data,
        });
        onNotificationRef.current(notification);
      } catch (error) {
        console.error('SSE: Error parsing notification:', error);
      }
    });

    eventSource.onerror = () => {
      console.log('SSE: Connection error or disconnected');
      setIsConnected(false);
      
      // Clean up current connection
      eventSource.close();
      eventSourceRef.current = null;

      // Attempt to reconnect after delay
      if (reconnectTimeoutRef.current) {
        clearTimeout(reconnectTimeoutRef.current);
      }
      reconnectTimeoutRef.current = setTimeout(() => {
        console.log('SSE: Attempting to reconnect...');
        connect();
      }, RECONNECT_DELAY);
    };
  }, []);

  const disconnect = useCallback(() => {
    console.log('[SSE] disconnect() called');
    if (reconnectTimeoutRef.current) {
      clearTimeout(reconnectTimeoutRef.current);
      reconnectTimeoutRef.current = null;
    }

    if (eventSourceRef.current) {
      console.log('[SSE] Closing EventSource connection');
      eventSourceRef.current.close();
      eventSourceRef.current = null;
    }

    setIsConnected(false);
  }, []);

  // Cleanup on unmount
  useEffect(() => {
    return () => {
      disconnect();
    };
  }, [disconnect]);

  return {
    isConnected,
    connect,
    disconnect,
  };
}
