"use client";

import { useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import dayjs from "dayjs";
import isoWeek from "dayjs/plugin/isoWeek";
import { api } from "../../../lib/api";
import { useSession } from "next-auth/react";

dayjs.extend(isoWeek);

type Timetable = {
  dataJson: string;
};

type Event = {
  title: string;
  start: string;
  end: string;
  day: string;
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

  const events = useMemo(() => {
    if (!timetableQuery.data) return [];
    const parsed = JSON.parse(timetableQuery.data.dataJson) as { events: Event[] };
    return parsed.events;
  }, [timetableQuery.data]);

  const exportIcs = () => {
    const lines = [
      "BEGIN:VCALENDAR",
      "VERSION:2.0",
      ...events.map(event => {
        const date = week.isoWeekday(event.day).format("YYYYMMDD");
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

  return (
    <div className="card space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold text-slate-800">Emploi du temps</h1>
          <p className="text-sm text-slate-500">Semaine du {week.format("DD MMMM YYYY")}</p>
        </div>
        <div className="flex gap-2">
          <button className="btn" onClick={() => setWeek(week.subtract(1, "week"))}>
            Semaine précédente
          </button>
          <button className="btn" onClick={() => setWeek(week.add(1, "week"))}>
            Semaine suivante
          </button>
          <button className="btn" onClick={exportIcs}>
            Exporter ICS
          </button>
        </div>
      </div>
      <div className="grid gap-4 md:grid-cols-3">
        {events.map(event => (
          <div key={`${event.day}-${event.title}`} className="rounded-lg border border-slate-200 bg-white p-4">
            <h3 className="text-sm font-semibold text-brand">{event.day}</h3>
            <p className="mt-1 text-lg font-medium text-slate-800">{event.title}</p>
            <p className="text-sm text-slate-500">
              {event.start} - {event.end}
            </p>
          </div>
        ))}
      </div>
    </div>
  );
}
