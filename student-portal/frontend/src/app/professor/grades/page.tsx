"use client";

import { useMemo, useState } from "react";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../../../lib/api";
import { useSession } from "next-auth/react";
import toast from "react-hot-toast";

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

const MODULE_ORDER = ["NET-FOR", "DATA-VIS"];

export default function ProfessorGradesPage() {
  const { status } = useSession();
  const isReady = status === "authenticated";
  const queryClient = useQueryClient();
  const rosterQuery = useQuery({
    queryKey: ["professor-students"],
    queryFn: async () => {
      const response = await api.get("/api/professor/students");
      return response.data as ProfessorStudent[];
    },
    enabled: isReady
  });

  const students = useMemo(() => {
    const list = rosterQuery.data ?? [];
    return list.map(student => ({
      ...student,
      modules: [...student.modules].sort(
        (a, b) => MODULE_ORDER.indexOf(a.moduleCode) - MODULE_ORDER.indexOf(b.moduleCode)
      )
    }));
  }, [rosterQuery.data]);

  const [gradeDrafts, setGradeDrafts] = useState<Record<string, string>>({});

  const gradeMutation = useMutation({
    mutationFn: async (payload: { studentId: number; moduleCode: string; grade: number }) => {
      await api.post("/api/professor/grades", {
        studentId: payload.studentId,
        moduleCode: payload.moduleCode,
        grade: payload.grade
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["professor-students"] });
      toast.success("Note mise à jour");
    },
    onError: () => toast.error("Impossible de mettre à jour la note")
  });

  const handleGradeChange = (studentId: number, moduleCode: string, value: string) => {
    const key = `${studentId}-${moduleCode}`;
    setGradeDrafts(drafts => ({ ...drafts, [key]: value }));
  };

  const handleGradeSubmit = (studentId: number, moduleCode: string) => {
    const key = `${studentId}-${moduleCode}`;
    const value = gradeDrafts[key];
    if (!value || Number.isNaN(Number(value))) {
      toast.error("Veuillez saisir une note valide");
      return;
    }
    gradeMutation.mutate({ studentId, moduleCode, grade: Number(value) });
  };

  return (
    <div className="space-y-6">
      <div className="card">
        <h1 className="text-xl font-semibold text-slate-800">Gestion des notes</h1>
        <p className="mt-2 text-sm text-slate-500">
          Mettez à jour les notes des modules Network Forensics et Data Visualization Studio pour chacun de vos étudiants.
        </p>
      </div>

      {students.map(student => (
        <div key={student.studentId} className="card space-y-4">
          <div className="flex flex-col gap-1 md:flex-row md:items-center md:justify-between">
            <div>
              <h2 className="text-lg font-semibold text-slate-800">{student.studentName}</h2>
              <p className="text-sm text-slate-500">{student.email}</p>
            </div>
          </div>
          <div className="grid gap-4 md:grid-cols-2">
            {student.modules.map(module => {
              const key = `${student.studentId}-${module.moduleCode}`;
              const currentValue =
                gradeDrafts[key] ??
                (module.grade !== null && module.grade !== undefined ? module.grade.toString() : "");
              return (
                <div key={module.moduleCode} className="rounded-md border border-slate-200 bg-white p-4 shadow-sm">
                  <h3 className="text-sm font-semibold text-slate-700">{module.moduleTitle}</h3>
                  <p className="text-xs text-slate-500">{module.moduleCode}</p>
                  <label className="mt-3 block text-xs font-medium uppercase text-slate-500">
                    Note actuelle
                  </label>
                  <div className="mt-1 flex gap-2">
                    <input
                      type="number"
                      step="0.5"
                      min={0}
                      max={20}
                      value={currentValue}
                      onChange={event => handleGradeChange(student.studentId, module.moduleCode, event.target.value)}
                      className="w-full rounded-md border px-3 py-2 text-sm"
                      placeholder="—"
                    />
                    <button
                      type="button"
                      className="btn whitespace-nowrap"
                      onClick={() => handleGradeSubmit(student.studentId, module.moduleCode)}
                      disabled={gradeMutation.isPending}
                    >
                      Enregistrer
                    </button>
                  </div>
                  <p className="mt-2 text-xs text-slate-500">
                    {!module.grade ? "Aucune note publiée." : `Note publiée : ${module.grade.toFixed(1)}/20`}
                  </p>
                </div>
              );
            })}
          </div>
        </div>
      ))}
    </div>
  );
}
