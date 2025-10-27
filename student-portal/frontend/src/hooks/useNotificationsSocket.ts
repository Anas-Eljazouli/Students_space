"use client";

import { useEffect } from "react";
import { Client } from "@stomp/stompjs";
import { useNotifications } from "../store/useNotifications";
import { useSession } from "next-auth/react";
import { api } from "../lib/api";

export function useNotificationsSocket() {
  const { data: session } = useSession();
  const addNotification = useNotifications(state => state.addNotification);
  const setNotifications = useNotifications(state => state.setNotifications);

  useEffect(() => {
    if (!session?.user?.id) {
      return;
    }

    let cancelled = false;

    const loadExistingNotifications = async () => {
      try {
        const response = await api.get("/api/notifications/my");
        if (!cancelled) {
          const normalized = response.data.map((item: any) => ({
            id: String(item.id),
            type: item.type as string,
            payload: item.payload,
            receivedAt: new Date(item.createdAt)
          }));
          setNotifications(normalized);
        }
      } catch (error) {
        // eslint-disable-next-line no-console
        console.error("Unable to load notifications", error);
      }
    };

    loadExistingNotifications();

    const client = new Client({
      brokerURL: process.env.NEXT_PUBLIC_WS_URL,
      connectHeaders: {
        Authorization: `Bearer ${(session as any).accessToken}`
      }
    });

    client.onConnect = () => {
      client.subscribe(`/topic/users/${session.user.id}`, message => {
        const body = JSON.parse(message.body);
        addNotification({ type: body.type, payload: body.payload });
      });
    };

    client.activate();
    return () => {
      cancelled = true;
      client.deactivate();
    };
  }, [session, addNotification, setNotifications]);
}
