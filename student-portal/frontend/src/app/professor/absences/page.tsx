"use client";

import { useState } from "react";
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

type AbsenceDraft = {
  date: string;
  reason: string;
};

const MODULE_ORDER = ["NET-FOR", "DATA-VIS"];

export default function ProfessorAbsencesPage() {
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

  const students =
    rosterQuery.data?.map(student => ({
      ...student,
      modules: [...student.modules].sort(
        (a, b) => MODULE_ORDER.indexOf(a.moduleCode) - MODULE_ORDER.indexOf(b.moduleCode)
      )
    })) ?? [];

  const [absenceDrafts, setAbsenceDrafts] = useState<Record<string, AbsenceDraft>>({});

  const createMutation = useMutation({
    mutationFn: async (payload: { studentId: number; moduleCode: string; lessonDate: string; reason?: string }) => {
      await api.post("/api/professor/absences", payload);
      return payload;
    },
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ["professor-students"] });
      toast.success("Absence enregistrée");
      const key = `${variables.studentId}-${variables.moduleCode}`;
      setAbsenceDrafts(drafts => ({ ...drafts, [key]: { date: "", reason: "" } }));
    },
    onError: () => toast.error("Impossible d'enregistrer l'absence")
  });

  const deleteMutation = useMutation({
    mutationFn: async (id: number) => {
      await api.delete(`/api/professor/absences/${id}`);
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["professor-students"] });
      toast.success("Absence supprimée");
    },
    onError: () => toast.error("Impossible de supprimer l'absence")
  });

  const handleDraftChange = (key: string, updates: Partial<AbsenceDraft>) => {
    setAbsenceDrafts(drafts => ({
      ...drafts,
      [key]: {
        date: updates.date ?? drafts[key]?.date ?? "",
        reason: updates.reason ?? drafts[key]?.reason ?? ""
      }
    }));
  };

  const handleCreateAbsence = (studentId: number, moduleCode: string) => {
    const key = `${studentId}-${moduleCode}`;
    const draft = absenceDrafts[key];
    if (!draft?.date) {
      toast.error("Sélectionnez une date d'absence");
      return;
    }
    createMutation.mutate({
      studentId,
      moduleCode,
      lessonDate: draft.date,
      reason: draft.reason || undefined
    });
    setAbsenceDrafts(drafts => ({ ...drafts, [key]: { date: "", reason: "" } }));
  };

  return (
    <div className="space-y-6">
      <div className="card">
        <h1 className="text-xl font-semibold text-slate-800">Suivi des absences</h1>
        <p className="mt-2 text-sm text-slate-500">
          Enregistrez les absences constatées pour Network Forensics et Data Visualization Studio.
        </p>
      </div>

      {students.map(student => (
        <div key={student.studentId} className="card space-y-5">
          <div>
            <h2 className="text-lg font-semibold text-slate-800">{student.studentName}</h2>
            <p className="text-sm text-slate-500">{student.email}</p>
          </div>
          <div className="space-y-4">
            {student.modules.map(module => {
              const key = `${student.studentId}-${module.moduleCode}`;
              const draft = absenceDrafts[key] ?? { date: "", reason: "" };
              return (
                <div key={module.moduleCode} className="rounded-md border border-slate-200 bg-white p-4 shadow-sm">
                  <h3 className="text-sm font-semibold text-slate-700">{module.moduleTitle}</h3>
                  <p className="text-xs text-slate-500">{module.moduleCode}</p>
                  <div className="mt-4 flex flex-col gap-3 md:flex-row">
                    <input
                      type="date"
                      value={draft.date}
                      onChange={event => handleDraftChange(key, { date: event.target.value })}
                      className="rounded-md border px-3 py-2 text-sm md:w-40"
                    />
                    <input
                      type="text"
                      placeholder="Motif (facultatif)"
                      value={draft.reason}
                      onChange={event => handleDraftChange(key, { reason: event.target.value })}
                      className="flex-1 rounded-md border px-3 py-2 text-sm"
                    />
                    <button
                      type="button"
                      className="btn whitespace-nowrap"
                      onClick={() => handleCreateAbsence(student.studentId, module.moduleCode)}
                      disabled={createMutation.isPending}
                    >
                      Ajouter
                    </button>
                  </div>
                  <table className="mt-4 w-full text-sm">
                    <thead className="text-left text-slate-500">
                      <tr>
                        <th className="py-2">Date</th>
                        <th>Motif</th>
                        <th></th>
                      </tr>
                    </thead>
                    <tbody>
                      {module.absences?.length ? (
                        module.absences.map(absence => (
                          <tr key={absence.id} className="border-t border-slate-100">
                            <td className="py-2">{new Date(absence.lessonDate).toLocaleDateString()}</td>
                            <td>{absence.reason ?? "—"}</td>
                            <td className="text-right">
                              <button
                                type="button"
                                className="text-xs font-semibold text-rose-500 hover:text-rose-600"
                                onClick={() => deleteMutation.mutate(absence.id)}
                                disabled={deleteMutation.isPending}
                              >
                                Supprimer
                              </button>
                            </td>
                          </tr>
                        ))
                      ) : (
                        <tr>
                          <td colSpan={3} className="py-3 text-center text-slate-400">
                            Aucune absence enregistrée pour ce module.
                          </td>
                        </tr>
                      )}
                    </tbody>
                  </table>
                </div>
              );
            })}
          </div>
        </div>
      ))}
    </div>
  );
}
