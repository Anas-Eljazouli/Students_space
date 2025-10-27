"use client";

import { ReactNode } from "react";
import { AppShell } from "../../components/layout/AppShell";
import { useNotificationsSocket } from "../../hooks/useNotificationsSocket";

export default function StudentLayout({ children }: { children: ReactNode }) {
  useNotificationsSocket();
  return <AppShell>{children}</AppShell>;
}
