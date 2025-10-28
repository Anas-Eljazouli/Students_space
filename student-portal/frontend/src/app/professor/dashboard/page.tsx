"use client";

import { useQuery } from "@tanstack/react-query";
import { api } from "../../../lib/api";
import { useSession } from "next-auth/react";

type ProfessorStudent = {
  studentId: number;
  studentName: string;
  email: string;
  modules: Array<{
    moduleCode: string;
    moduleTitle: string;
    grade: number | null;
    absences: Array<{ id: number; lessonDate: string; reason?: string | null }>;
  }>;
};

const MODULE_LABELS: Record<string, string> = {
  "NET-FOR": "Network Forensics",
  "DATA-VIS": "Data Visualization Studio"
};

export default function ProfessorDashboardPage() {
  const { status } = useSession();
  const isReady = status === "authenticated";
  const rosterQuery = useQuery({
    queryKey: ["professor-students"],
    queryFn: async () => {
      const response = await api.get("/api/professor/students");
      return response.data as ProfessorStudent[];
    },
    enabled: isReady
  });

  const students = rosterQuery.data ?? [];
  const studentCount = students.length;
  const modules = Object.keys(MODULE_LABELS);
  const gradesAssigned = students.reduce((acc, student) => {
    return (
      acc +
      student.modules.filter(module => module.grade !== null && module.grade !== undefined).length
    );
  }, 0);
  const absencesCount = students.reduce(
    (acc, student) =>
      acc +
      student.modules.reduce((sum, module) => sum + (module.absences?.length ?? 0), 0),
    0
  );

  return (
    <div className="space-y-6">
      <section className="grid gap-4 md:grid-cols-3">
        <div className="card">
          <h1 className="text-xl font-semibold text-slate-800">Tableau de bord professeur</h1>
          <p className="mt-2 text-sm text-slate-500">
            Vous suivez {modules.length} modules : Network Forensics et Data Visualization Studio.
          </p>
        </div>
        <div className="card">
          <h3 className="text-sm font-medium text-slate-500">Étudiants suivis</h3>
          <p className="mt-2 text-3xl font-semibold text-brand">{studentCount}</p>
        </div>
        <div className="card">
          <h3 className="text-sm font-medium text-slate-500">Absences enregistrées</h3>
          <p className="mt-2 text-3xl font-semibold text-brand">{absencesCount}</p>
        </div>
        <div className="card">
          <h3 className="text-sm font-medium text-slate-500">Notes mises à jour</h3>
          <p className="mt-2 text-3xl font-semibold text-brand">{gradesAssigned}</p>
        </div>
      </section>

      <section className="card">
        <h2 className="text-lg font-semibold text-slate-800">Modules encadrés</h2>
        <ul className="mt-3 space-y-2 text-sm text-slate-700">
          {modules.map(code => (
            <li key={code} className="flex items-center justify-between rounded-md border border-slate-100 bg-slate-50 px-3 py-2">
              <span className="font-medium text-slate-800">{MODULE_LABELS[code]}</span>
              <span className="text-xs text-slate-500">{code}</span>
            </li>
          ))}
        </ul>
      </section>
    </div>
  );
}
