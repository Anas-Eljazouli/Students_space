"use client";

import { useMemo, useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { api } from "../../../lib/api";
import { useSession } from "next-auth/react";

export default function GradesPage() {
  const { status } = useSession();
  const isReady = status === "authenticated";
  const [filter, setFilter] = useState("");
  const gradesQuery = useQuery({
    queryKey: ["grades"],
    queryFn: async () => {
      const response = await api.get("/api/grades/my");
      return response.data as Array<{ id: number; moduleTitle: string; moduleCode: string; grade: number; session: string }>;
    },
    enabled: isReady
  });

  const filtered = useMemo(() => {
    if (!gradesQuery.data) return [];
    return gradesQuery.data.filter(grade => grade.moduleTitle.toLowerCase().includes(filter.toLowerCase()));
  }, [gradesQuery.data, filter]);

  return (
    <div className="card">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-semibold text-slate-800">Mes notes</h1>
        <input
          type="text"
          placeholder="Filtrer par module"
          value={filter}
          onChange={event => setFilter(event.target.value)}
          className="rounded-md border border-slate-200 px-3 py-2"
        />
      </div>
      <table className="mt-4 w-full text-sm">
        <thead className="text-left text-slate-500">
          <tr>
            <th className="py-2">Code</th>
            <th className="py-2">Module</th>
            <th className="py-2">Session</th>
            <th className="py-2">Note</th>
          </tr>
        </thead>
        <tbody>
          {filtered.map(grade => (
            <tr key={grade.id} className="border-t border-slate-100">
              <td className="py-2">{grade.moduleCode}</td>
              <td>{grade.moduleTitle}</td>
              <td>{grade.session}</td>
              <td className="font-semibold">{grade.grade}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
