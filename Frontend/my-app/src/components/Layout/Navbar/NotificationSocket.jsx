import { useEffect, useRef } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { urlServer } from '../../../api/urlServer.js';

const NotificationSocket = ({ parentId, onMessage }) => {
  const stompClientRef = useRef(null);

  useEffect(() => {
    if (!parentId || typeof onMessage !== 'function') {
      console.warn('[NotificationSocket] ❗ parentId hoặc onMessage không hợp lệ');
      return;
    }

    const token = localStorage.getItem('token');
    if (!token) {
      console.error('[NotificationSocket] ❗ Không tìm thấy token trong localStorage');
      return;
    }

    const socket = new SockJS(`${urlServer}/ws`);
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
        console.log(`[WebSocket] 📡 Subscribed topic: ${topic}`);

        stompClient.subscribe(topic, (message) => {
          if (!message.body) {
            console.warn('[WebSocket] 📭 Nhận message rỗng');
            return;
          }

          console.log('[WebSocket] 📩 Raw message:', message.body);

          try {
            const parsed = JSON.parse(message.body);
            console.log('[WebSocket] ✅ Parsed JSON:', parsed);
            onMessage(parsed);
          } catch (e) {
            console.warn('[WebSocket] ⚠️ Không phải JSON, trả về chuỗi:', message.body);
            onMessage(message.body);
          }
        });
      },

      onStompError: (frame) => {
        console.error('[WebSocket] ❌ STOMP lỗi:', frame.headers['message']);
        console.error('[WebSocket] 🪵 Chi tiết:', frame.body);
      },

      onWebSocketError: (error) => {
        console.error('[WebSocket] ❌ WebSocket lỗi:', error);
      },
    });

    stompClient.activate();
    stompClientRef.current = stompClient;

    return () => {
      console.log('[NotificationSocket] 🔌 Ngắt kết nối WebSocket');
      stompClient.deactivate(); // cleanup khi component bị unmount
    };
  }, []); // chỉ chạy 1 lần duy nhất

  useEffect(() => {
    if (!parentId || !stompClientRef.current?.connected) return;

    const topic = `/topic/parent/${parentId}`;
    console.log(`[WebSocket] 📡 Re-subscribing topic: ${topic}`);

    const subscription = stompClientRef.current.subscribe(topic, (message) => {
      if (!message.body) return;
      try {
        onMessage(JSON.parse(message.body));
      } catch {
        onMessage(message.body);
      }
    });

    return () => {
      console.log(`[WebSocket] 📴 Hủy subscription topic: ${topic}`);
      subscription.unsubscribe();
    };
  }, [parentId]);

  return null;
};

export default NotificationSocket;
