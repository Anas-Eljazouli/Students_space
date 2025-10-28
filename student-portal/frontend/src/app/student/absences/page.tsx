"use client";

import { useQuery } from "@tanstack/react-query";
import { api } from "../../../lib/api";
import { useSession } from "next-auth/react";

type Absence = {
  id: number;
  moduleCode: string;
  moduleTitle: string;
  session: string;
  lessonDate: string;
  reason?: string | null;
};

export default function StudentAbsencesPage() {
  const { status } = useSession();
  const isReady = status === "authenticated";
  const absencesQuery = useQuery({
    queryKey: ["absences"],
    queryFn: async () => {
      const response = await api.get("/api/absences/my");
      return response.data as Absence[];
    },
    enabled: isReady
  });

  return (
    <div className="card space-y-4">
      <div>
        <h1 className="text-xl font-semibold text-slate-800">Mes absences</h1>
        <p className="text-sm text-slate-500">Synthèse des absences saisies par vos enseignants.</p>
      </div>
      <table className="w-full text-sm">
        <thead className="text-left text-slate-500">
          <tr>
            <th className="py-2">Date</th>
            <th>Module</th>
            <th>Session</th>
            <th>Remarque</th>
          </tr>
        </thead>
        <tbody>
          {absencesQuery.data?.length ? (
            absencesQuery.data.map(absence => (
              <tr key={absence.id} className="border-t border-slate-100">
                <td className="py-2">{new Date(absence.lessonDate).toLocaleDateString()}</td>
                <td>
                  <div className="font-medium text-slate-800">{absence.moduleTitle}</div>
                  <div className="text-xs text-slate-500">{absence.moduleCode}</div>
                </td>
                <td>{absence.session}</td>
                <td>{absence.reason ?? "—"}</td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan={4} className="py-6 text-center text-slate-400">
                Aucune absence enregistrée.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}
