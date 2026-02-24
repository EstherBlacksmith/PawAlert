import { createContext, useContext, useState, useEffect, useCallback, ReactNode } from 'react';
import { useNavigate } from 'react-router-dom';
import { toaster } from '../toaster';
import { NotificationMessage } from '../types/notification';
import { useSseNotifications } from '../hooks/useSseNotifications';

const MAX_NOTIFICATIONS = 20;

interface NotificationContextType {
  notifications: NotificationMessage[];
  clearNotifications: () => void;
  isConnected: boolean;
  unreadBadgeCount: number;
  clearBadgeForAlert: (alertId: string) => void;
  decrementBadgeCount: () => void;
}

const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

export function NotificationProvider({ children }: { children: ReactNode }) {
  const [notifications, setNotifications] = useState<NotificationMessage[]>([]);
  const [unreadBadgeCount, setUnreadBadgeCount] = useState(0);
  const navigate = useNavigate();

  const handleNotification = useCallback((notification: NotificationMessage) => {
    // Add notification to the list (keep last 20)
    setNotifications((prev) => {
      const updated = [notification, ...prev];
      return updated.slice(0, MAX_NOTIFICATIONS);
    });

    // Increment badge count for NEW_ALERT notifications
    if (notification.type === 'NEW_ALERT') {
      setUnreadBadgeCount((prev) => {
        const newCount = prev + 1;
        console.log('[NotificationContext] NEW_ALERT received - Badge count incremented to:', newCount);
        return newCount;
      });
    }

    // Determine toast type and color based on notification type and alert status
    let toastType: 'info' | 'warning' | 'success' | 'error' = 'info';
    let alertStatus: 'OPEN' | 'CLOSED' | 'SAFE' | 'FOUND' | undefined = undefined;

    if (notification.type === 'ALERT_STATUS_CHANGE') {
      alertStatus = notification.alertStatus as 'OPEN' | 'CLOSED' | 'SAFE' | 'FOUND' | 'SEEN';
      
      // Determine toast type based on new status
      switch (alertStatus) {
        case 'OPEN':
          toastType = 'warning'; // Red/orange - alert is open/active
          break;
        case 'SEEN':
          toastType = 'info'; // Blue - alert has been seen
          break;
        case 'CLOSED':
          toastType = 'info'; // Will be styled as grey
          break;
        case 'SAFE':
          toastType = 'success'; // Green - pet is safe
          break;
        case 'FOUND':
          toastType = 'info'; // Blue - pet was found
          break;
        default:
          toastType = 'info';
      }
    } else if (notification.type === 'NEW_ALERT') {
      toastType = 'warning';
      alertStatus = 'OPEN';
    }
    
    toaster.create({
      title: notification.title,
      description: notification.message,
      type: toastType,
      alertStatus: alertStatus,
      duration: 8000,
      closable: true,
      action: {
        label: 'View Alert',
        onClick: () => {
          navigate(`/alerts/${notification.alertId}`);
        },
      },
    });
  }, [navigate]);

  const { isConnected, connect, disconnect } = useSseNotifications(handleNotification);

  // Connect on mount, disconnect on unmount
  useEffect(() => {
    connect();
    return () => {
      disconnect();
    };
  }, [connect, disconnect]);

  const clearNotifications = useCallback(() => {
    setNotifications([]);
  }, []);

  const clearBadgeForAlert = useCallback((alertId: string) => {
    // Decrement badge count when user views an alert
    console.log('[NotificationContext] clearBadgeForAlert called for alertId:', alertId);
    setUnreadBadgeCount((prev) => {
      const newCount = Math.max(0, prev - 1);
      console.log('[NotificationContext] Badge count updated from', prev, 'to', newCount);
      return newCount;
    });
  }, []);

  const decrementBadgeCount = useCallback(() => {
    // Decrement badge count (ensure it doesn't go below 0)
    setUnreadBadgeCount((prev) => Math.max(0, prev - 1));
  }, []);

  return (
    <NotificationContext.Provider
      value={{
        notifications,
        clearNotifications,
        isConnected,
        unreadBadgeCount,
        clearBadgeForAlert,
        decrementBadgeCount,
      }}
    >
      {children}
    </NotificationContext.Provider>
  );
}

export function useNotifications() {
  const context = useContext(NotificationContext);
  if (context === undefined) {
    throw new Error('useNotifications must be used within a NotificationProvider');
  }
  return context;
}
