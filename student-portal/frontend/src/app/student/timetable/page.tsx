"use client";

import { useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import dayjs from "dayjs";
import isoWeek from "dayjs/plugin/isoWeek";
import { useSession } from "next-auth/react";
import { api } from "../../../lib/api";

dayjs.extend(isoWeek);

type Timetable = {
  dataJson: string;
};

type Event = {
  title: string;
  start: string;
  end: string;
  day: string;
  type?: string;
  room?: string;
  teacher?: string;
};

type CalendarEvent = Event & {
  day: string;
  startMinutes: number;
  endMinutes: number;
};

const dayOrder = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"];
const dayLabels: Record<string, string> = {
  Monday: "Lundi",
  Tuesday: "Mardi",
  Wednesday: "Mercredi",
  Thursday: "Jeudi",
  Friday: "Vendredi",
  Saturday: "Samedi"
};
const dayIndex: Record<string, number> = {
  Monday: 1,
  Tuesday: 2,
  Wednesday: 3,
  Thursday: 4,
  Friday: 5,
  Saturday: 6
};

export default function TimetablePage() {
  const { status } = useSession();
  const isReady = status === "authenticated";
  const [week, setWeek] = useState(dayjs().startOf("week").add(1, "day"));

  const timetableQuery = useQuery({
    queryKey: ["timetable", week.format("YYYY-MM-DD")],
    queryFn: async () => {
      const response = await api.get(`/api/timetable/my`, {
        params: { week: week.format("YYYY-MM-DD") }
      });
      return response.data as Timetable;
    },
    enabled: isReady
  });

  const events: CalendarEvent[] = useMemo(() => {
    if (!timetableQuery.data) return [];
    const parsed = JSON.parse(timetableQuery.data.dataJson) as { events: Event[] };
    return parsed.events
      .map(event => {
        const canonicalDay = dayOrder.find(d => d.toLowerCase() === event.day.toLowerCase()) ?? event.day;
        const [startHour, startMinute] = event.start.split(":").map(Number);
        const [endHour, endMinute] = event.end.split(":").map(Number);
        return {
          ...event,
          day: canonicalDay,
          startMinutes: startHour * 60 + startMinute,
          endMinutes: endHour * 60 + endMinute
        };
      })
      .sort((a, b) => a.startMinutes - b.startMinutes);
  }, [timetableQuery.data]);

  const daysWithEvents = useMemo(() => {
    const grouped = new Map<string, CalendarEvent[]>();
    for (const day of dayOrder) {
      grouped.set(day, []);
    }
    for (const event of events) {
      if (!grouped.has(event.day)) {
        grouped.set(event.day, []);
      }
      grouped.get(event.day)!.push(event);
    }
    return Array.from(grouped.entries()).filter(([, value]) => value.length > 0);
  }, [events]);

  const startMinutes = events.map(event => event.startMinutes);
  const endMinutes = events.map(event => event.endMinutes);
  const earliestHour = startMinutes.length ? Math.max(7, Math.floor(Math.min(...startMinutes) / 60)) : 8;
  const latestHour = endMinutes.length ? Math.min(21, Math.ceil(Math.max(...endMinutes) / 60)) : 18;
  const hours = Array.from({ length: latestHour - earliestHour + 1 }, (_, index) => earliestHour + index);
  const pxPerMinute = 1;
  const columnHeight = Math.max(1, latestHour - earliestHour) * 60 * pxPerMinute;

  const exportIcs = () => {
    const lines = [
      "BEGIN:VCALENDAR",
      "VERSION:2.0",
      ...events.map(event => {
        const date = week.isoWeekday(dayIndex[event.day] ?? 1).format("YYYYMMDD");
        return [
          "BEGIN:VEVENT",
          `SUMMARY:${event.title}`,
          `DTSTART:${date}T${event.start.replace(":", "")}00`,
          `DTEND:${date}T${event.end.replace(":", "")}00`,
          "END:VEVENT"
        ].join("\n");
      }),
      "END:VCALENDAR"
    ].join("\n");
    const blob = new Blob([lines], { type: "text/calendar" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = `timetable-${week.format("YYYY-[W]WW")}.ics`;
    link.click();
    URL.revokeObjectURL(url);
  };

  if (timetableQuery.isLoading) {
    return (
      <div className="card space-y-4">
        <div className="animate-pulse space-y-2">
          <div className="h-6 w-2/5 rounded bg-slate-200" />
          <div className="h-4 w-1/3 rounded bg-slate-200" />
        </div>
        <div className="h-96 rounded-md border border-slate-200 bg-slate-50" />
      </div>
    );
  }

  if (timetableQuery.isError || !timetableQuery.data) {
    return (
      <div className="card space-y-4">
        <h1 className="text-xl font-semibold text-slate-800">Emploi du temps</h1>
        <p className="text-sm text-amber-600">Impossible de charger votre planning pour le moment.</p>
      </div>
    );
  }

  return (
    <div className="card space-y-4">
      <div className="flex flex-wrap items-center justify-between gap-4">
        <div>
          <h1 className="text-xl font-semibold text-slate-800">Emploi du temps</h1>
          <p className="text-sm text-slate-500">Semaine du {week.format("DD MMMM YYYY")}</p>
        </div>
        <div className="flex gap-2">
          <button className="btn" onClick={() => setWeek(week.subtract(1, "week"))}>
            Semaine precedente
          </button>
          <button className="btn" onClick={() => setWeek(week.add(1, "week"))}>
            Semaine suivante
          </button>
          <button className="btn" onClick={exportIcs}>
            Exporter ICS
          </button>
        </div>
      </div>

      <div className="overflow-x-auto">
        <div
          className="relative min-w-[920px] rounded-lg border border-slate-200 bg-white p-4 shadow-xs"
          role="table"
          aria-label="Emploi du temps hebdomadaire"
        >
          {events.length === 0 ? (
            <div className="flex h-64 items-center justify-center text-sm text-slate-500">
              Aucun cours planifie pour cette semaine.
            </div>
          ) : (
            <div
              className="grid gap-4"
              style={{ gridTemplateColumns: `80px repeat(${daysWithEvents.length}, minmax(0, 1fr))` }}
            >
              <div className="text-xs font-medium uppercase tracking-wide text-slate-400">Heures</div>
              {daysWithEvents.map(([day]) => (
                <div key={day} className="text-center text-xs font-medium uppercase tracking-wide text-slate-400">
                  {dayLabels[day] ?? day}
                  <div className="text-[11px] font-normal capitalize text-slate-500">
                    {week.isoWeekday(dayIndex[day] ?? 1).format("DD MMM")}
                  </div>
                </div>
              ))}

              <div className="relative">
                <div className="flex flex-col gap-8">
                  {hours.map(hour => (
                    <div key={hour} className="text-xs font-semibold text-slate-500">
                      {`${hour.toString().padStart(2, "0")}:00`}
                    </div>
                  ))}
                </div>
              </div>

              {daysWithEvents.map(([day, dayEvents]) => (
                <div key={day} className="relative">
                  <div
                    className="relative rounded-md border border-slate-100 bg-slate-50"
                    style={{ height: columnHeight }}
                  >
                    {hours.map((hour, index) => (
                      <div
                        key={`${day}-${hour}`}
                        className={`absolute left-0 right-0 border-t ${index === 0 ? "border-transparent" : "border-slate-200"}`}
                        style={{ top: index * 60 * pxPerMinute }}
                      />
                    ))}

                    {dayEvents.map(event => {
                      const top = (event.startMinutes - earliestHour * 60) * pxPerMinute;
                      const duration = Math.max(event.endMinutes - event.startMinutes, 30);
                      const height = Math.max(duration * pxPerMinute - 6, 48);
                      const typeClass =
                        event.type && event.type.toLowerCase().includes("lab")
                          ? "border-emerald-200 bg-emerald-50 text-emerald-900"
                          : event.type && event.type.toLowerCase().includes("work")
                            ? "border-indigo-200 bg-indigo-50 text-indigo-900"
                            : "border-sky-200 bg-sky-50 text-sky-900";

                      return (
                        <div
                          key={`${event.day}-${event.title}-${event.start}`}
                          className={`absolute left-2 right-2 rounded-md border px-3 py-2 text-xs shadow-sm transition hover:shadow-md ${typeClass}`}
                          style={{ top, height }}
                        >
                          <div className="text-[11px] font-semibold uppercase tracking-wide">
                            {event.type ?? "Cours"}
                          </div>
                          <div className="mt-1 text-sm font-semibold">{event.title}</div>
                          <div className="text-[11px] font-medium text-slate-600">
                            {event.start} - {event.end}
                          </div>
                          {(event.room || event.teacher) && (
                            <div className="mt-2 space-y-1 text-[11px] font-medium text-slate-500">
                              {event.room && <div>Salle {event.room}</div>}
                              {event.teacher && <div>{event.teacher}</div>}
                            </div>
                          )}
                        </div>
                      );
                    })}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
