export interface NotificationMessage {
  id: string;
  type: 'ALERT_STATUS_CHANGE' | 'NEW_ALERT' | 'SYSTEM';
  title: string;
  message: string;
  alertId: string;
  alertStatus: string;
  petName: string;
  timestamp: string;
}

export interface NotificationState {
  notifications: NotificationMessage[];
  isConnected: boolean;
}
