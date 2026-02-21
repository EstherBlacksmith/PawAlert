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
}

const NotificationContext = createContext<NotificationContextType | undefined>(undefined);

export function NotificationProvider({ children }: { children: ReactNode }) {
  const [notifications, setNotifications] = useState<NotificationMessage[]>([]);
  const navigate = useNavigate();

  const handleNotification = useCallback((notification: NotificationMessage) => {
    // Add notification to the list (keep last 20)
    setNotifications((prev) => {
      const updated = [notification, ...prev];
      return updated.slice(0, MAX_NOTIFICATIONS);
    });

    // Show toast notification
    const toastType = notification.type === 'NEW_ALERT' ? 'info' : 
                      notification.type === 'ALERT_STATUS_CHANGE' ? 'warning' : 'info';
    
    toaster.create({
      title: notification.title,
      description: notification.message,
      type: toastType,
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

  return (
    <NotificationContext.Provider
      value={{
        notifications,
        clearNotifications,
        isConnected,
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
