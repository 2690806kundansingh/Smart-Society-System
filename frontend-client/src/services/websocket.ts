import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { NotificationItem, User } from '../types';

type NotificationCallback = (notification: NotificationItem) => void;

class WebSocketService {
  private client: Client | null = null;
  private subscribers: NotificationCallback[] = [];
  private isConnected = false;

  public connect(user: User, onNotificationReceived: NotificationCallback) {
    if (this.client && this.isConnected) {
      this.subscribers.push(onNotificationReceived);
      return;
    }

    this.subscribers.push(onNotificationReceived);

    const socketUrl = import.meta.env.VITE_WS_URL || '/ws-notifications';

    this.client = new Client({
      webSocketFactory: () => new SockJS(socketUrl),
      reconnectDelay: 5000,
      heartbeatIncoming: 4000,
      heartbeatOutgoing: 4000,
      debug: (str) => {
        if (import.meta.env.DEV) {
          console.debug('[STOMP]', str);
        }
      },
      onConnect: () => {
        this.isConnected = true;
        console.log('[STOMP] Connected to notification broker');

        // Subscribe based on user roles
        const isStaff = user.roles.includes('ROLE_STAFF');
        const isAdmin = user.roles.includes('ROLE_ADMIN');

        // Resident channel
        this.client?.subscribe(`/queue/resident-${user.id}`, (message) => {
          this.handleIncomingMessage(message.body);
        });

        // Staff channel
        if (isStaff) {
          this.client?.subscribe(`/queue/staff-${user.id}`, (message) => {
            this.handleIncomingMessage(message.body);
          });
        }

        // Admin society broadcast topic
        if (isAdmin) {
          this.client?.subscribe(`/topic/society-${user.societyId}-admin`, (message) => {
            this.handleIncomingMessage(message.body);
          });
        }
      },
      onDisconnect: () => {
        this.isConnected = false;
        console.log('[STOMP] Disconnected from notification broker');
      },
      onStompError: (frame) => {
        console.error('[STOMP] Broker error:', frame.headers['message'], frame.body);
      },
    });

    this.client.activate();
  }

  private handleIncomingMessage(body: string) {
    try {
      const notification: NotificationItem = JSON.parse(body);
      this.subscribers.forEach((callback) => callback(notification));
    } catch (e) {
      console.error('Failed to parse incoming WebSocket message', e);
    }
  }

  public disconnect() {
    if (this.client) {
      this.client.deactivate();
      this.client = null;
      this.isConnected = false;
      this.subscribers = [];
    }
  }
}

export const wsService = new WebSocketService();
