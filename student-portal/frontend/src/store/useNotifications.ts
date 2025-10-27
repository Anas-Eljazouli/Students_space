"use client";

import { create } from "zustand";

export type Notification = {
  id: string;
  type: string;
  payload: unknown;
  receivedAt: Date;
};

type NotificationState = {
  notifications: Notification[];
  setNotifications: (notifications: Notification[]) => void;
  addNotification: (notification: { type: string; payload: unknown; receivedAt?: Date }) => void;
  clearNotifications: () => void;
};

export const useNotifications = create<NotificationState>(set => ({
  notifications: [],
  setNotifications: notifications =>
    set(() => ({
      notifications: notifications
        .map(notification => ({
          ...notification,
          receivedAt:
            notification.receivedAt instanceof Date
              ? notification.receivedAt
              : new Date(notification.receivedAt)
        }))
        .sort((a, b) => b.receivedAt.getTime() - a.receivedAt.getTime())
        .slice(0, 50)
    })),
  addNotification: ({ type, payload, receivedAt }) =>
    set(state => ({
      notifications: [
        {
          id: crypto.randomUUID(),
          type,
          payload,
          receivedAt: receivedAt ?? new Date()
        },
        ...state.notifications
      ].slice(0, 50)
    })),
  clearNotifications: () =>
    set(() => ({
      notifications: []
    }))
}));
