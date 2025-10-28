"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { ReactNode, useEffect, useMemo, useRef, useState } from "react";
import { signOut, useSession } from "next-auth/react";
import clsx from "clsx";
import { Bell } from "lucide-react";
import { useNotifications } from "../../store/useNotifications";
import { ChatbotWidget } from "../chatbot/ChatbotWidget";
import { api } from "../../lib/api";

const navItems = [
  { href: "/student/dashboard", label: "Tableau de bord", roles: ["STUDENT"] },
  { href: "/student/grades", label: "Notes", roles: ["STUDENT"] },
  { href: "/student/absences", label: "Absences", roles: ["STUDENT"] },
  { href: "/student/timetable", label: "Emploi du temps", roles: ["STUDENT"] },
  { href: "/student/requests", label: "E-guichet", roles: ["STUDENT"] },
  { href: "/student/payments", label: "Paiements", roles: ["STUDENT"] },
  { href: "/professor/dashboard", label: "Tableau de bord", roles: ["PROFESSOR"] },
  { href: "/professor/grades", label: "Notes", roles: ["PROFESSOR"] },
  { href: "/professor/absences", label: "Absences", roles: ["PROFESSOR"] },
  { href: "/professor/timetable", label: "Emploi du temps", roles: ["PROFESSOR"] },
  { href: "/admin/payments", label: "Paiements", roles: ["ADMIN"] },
  { href: "/admin/users", label: "Utilisateurs", roles: ["ADMIN"] }
];

export function AppShell({ children }: { children: ReactNode }) {
  const pathname = usePathname();
  const { data: session } = useSession();
  const notifications = useNotifications(state => state.notifications);
  const clearNotifications = useNotifications(state => state.clearNotifications);
  const [showNotifications, setShowNotifications] = useState(false);
  const popoverRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (!showNotifications) {
      return;
    }
    const handleClickOutside = (event: MouseEvent) => {
      if (popoverRef.current && !popoverRef.current.contains(event.target as Node)) {
        setShowNotifications(false);
      }
    };
    document.addEventListener("mousedown", handleClickOutside);
    return () => document.removeEventListener("mousedown", handleClickOutside);
  }, [showNotifications]);

  const formattedNotifications = useMemo(
    () =>
      notifications.map(notification => ({
        ...notification,
        timeLabel: notification.receivedAt.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })
      })),
    [notifications]
  );

  return (
    <div className="flex min-h-screen">
      <aside className="w-72 bg-white shadow-sm">
        <div className="p-6">
          <p className="text-2xl font-semibold text-brand">Portail Étudiant</p>
          <p className="mt-1 text-sm text-slate-500">Bienvenue, {session?.user?.name}</p>
        </div>
        <nav className="mt-6 space-y-1 px-4">
          {navItems
            .filter(item => (session?.user as any)?.role && item.roles.includes((session?.user as any).role))
            .map(item => (
              <Link
                key={item.href}
                href={item.href}
                className={clsx(
                  "block rounded-md px-3 py-2 text-sm font-medium",
                  pathname?.startsWith(item.href)
                    ? "bg-brand text-white"
                    : "text-slate-600 hover:bg-slate-100"
                )}
              >
                {item.label}
              </Link>
            ))}
        </nav>
        <div className="mt-auto px-6 pb-6">
          <button className="btn w-full" onClick={() => signOut({ callbackUrl: "/login" })}>
            Déconnexion
          </button>
        </div>
      </aside>
      <main className="flex-1 space-y-6 bg-slate-50 p-8">
        <header className="flex items-center justify-between">
          <h1 className="text-2xl font-semibold text-slate-800">{pathname}</h1>
          <div className="flex items-center gap-3" ref={popoverRef}>
            <button
              type="button"
              aria-label="Notifications"
              onClick={() => setShowNotifications(value => !value)}
              className="relative rounded-full bg-white p-2 shadow transition hover:bg-slate-100 focus:outline-none focus:ring-2 focus:ring-brand/40"
            >
              <Bell className="h-5 w-5 text-brand" />
              {notifications.length > 0 && (
                <span className="absolute -right-1 -top-1 inline-flex h-5 w-5 items-center justify-center rounded-full bg-brand text-xs text-white">
                  {notifications.length}
                </span>
              )}
            </button>
            {showNotifications && (
              <div className="absolute right-0 top-12 z-20 w-80 overflow-hidden rounded-xl border border-slate-100 bg-white shadow-xl">
                <div className="flex items-center justify-between border-b border-slate-100 px-4 py-2">
                  <p className="text-sm font-semibold text-slate-700">Notifications</p>
                  <button
                    type="button"
                    className="text-xs font-medium text-brand hover:underline disabled:cursor-not-allowed disabled:opacity-70"
                    onClick={async () => {
                      try {
                        await api.delete("/api/notifications/my");
                        clearNotifications();
                        setShowNotifications(false);
                      } catch (error) {
                        // eslint-disable-next-line no-console
                        console.error("Unable to clear notifications", error);
                      }
                    }}
                    disabled={formattedNotifications.length === 0}
                  >
                    Tout effacer
                  </button>
                </div>
                <div className="max-h-80 overflow-y-auto px-4 py-3">
                  {formattedNotifications.length === 0 ? (
                    <p className="text-sm text-slate-500">Aucune notification pour le moment.</p>
                  ) : (
                    <ul className="space-y-3 text-sm text-slate-700">
                      {formattedNotifications.map(notification => {
                        const payload = notification.payload as Record<string, unknown> | string | null;
                        const subject =
                          payload && typeof payload === "object" && "subject" in payload
                            ? String(payload.subject)
                            : undefined;
                        const message =
                          payload && typeof payload === "object" && "message" in payload
                            ? String(payload.message)
                            : typeof payload === "string"
                              ? payload
                              : undefined;
                        const sender =
                          payload && typeof payload === "object" && "sender" in payload
                            ? String(payload.sender)
                            : undefined;
                        return (
                          <li
                            key={notification.id}
                            className="rounded-md border border-slate-100 bg-slate-50 px-3 py-2"
                          >
                            <p className="font-medium text-slate-800">{subject ?? notification.type}</p>
                            {sender && (
                              <p className="text-xs font-medium text-slate-500">
                                De : <span className="font-semibold">{sender}</span>
                              </p>
                            )}
                            {message && (
                              <p className="mt-1 text-slate-600 break-words text-sm leading-relaxed">{message}</p>
                            )}
                            {!message && payload && typeof payload === "object" && (
                              <p className="mt-1 text-slate-600 break-words text-xs">
                                {JSON.stringify(payload, null, 2)}
                              </p>
                            )}
                            <p className="mt-1 text-xs text-slate-400">{notification.timeLabel}</p>
                          </li>
                        );
                      })}
                    </ul>
                  )}
                </div>
              </div>
            )}
          </div>
        </header>
        {children}
      </main>
      <ChatbotWidget />
    </div>
  );
}
