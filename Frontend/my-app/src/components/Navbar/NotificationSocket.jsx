// NotificationSocket.jsx
import { useEffect } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

const NotificationSocket = ({ parentId, onMessage }) => {
  useEffect(() => {
    if (!parentId || typeof onMessage !== 'function') {
      console.warn('[NotificationSocket] parentId hoặc onMessage không hợp lệ');
      return;
    }

    const token = localStorage.getItem('token');
    const socket = new SockJS('http://localhost:8080/ws');

    const stompClient = createStompClient(socket, token, parentId, onMessage);

    stompClient.activate();

    return () => {
      console.log('[NotificationSocket] Ngắt kết nối WebSocket');
      stompClient.deactivate();
    };
  }, [parentId, onMessage]);

  return null; // không render gì
};

function createStompClient(socket, token, parentId, onMessage) {
  const stompClient = new Client({
    webSocketFactory: () => socket,
    connectHeaders: {
      Authorization: `Bearer ${token}`,
    },
    debug: (str) => console.log('[STOMP]', str),
    reconnectDelay: 5000,

    onConnect: () => {
      console.log('[WebSocket] ✅ Kết nối thành công');

      const topic = `/topic/parent/${parentId}`;
      console.log(`[WebSocket] Đăng ký topic: ${topic}`);

      stompClient.subscribe(topic, (message) => {
        try {
          const parsed = JSON.parse(message.body);
          console.log('📩 Nhận message:', parsed);
          onMessage(parsed);
        } catch (err) {
          console.error('❌ Lỗi parse message:', err);
        }
      });
    },

    onStompError: (frame) => {
      console.error('[WebSocket] ❌ STOMP lỗi:', frame);
    },

    onWebSocketError: (error) => {
      console.error('[WebSocket] ❌ Lỗi kết nối:', error);
    },
  });

  return stompClient;
}

export default NotificationSocket;
