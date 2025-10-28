"use client";

import { useQuery } from "@tanstack/react-query";
import { api } from "../../../lib/api";
import { useSession } from "next-auth/react";
import Link from "next/link";

type PaymentSummary = {
  id: number;
  status: string;
  amountCents: number;
  label: string;
  currency: string;
};

const statusLabels: Record<string, string> = {
  PROCESSING: "En cours",
  SUCCEEDED: "Valide",
  FAILED: "Refuse",
  REFUNDED: "Rembourse"
};

export default function DashboardPage() {
  const { data: session, status } = useSession();
  const isReady = status === "authenticated";
  const gradesQuery = useQuery({
    queryKey: ["grades"],
    queryFn: async () => {
      const response = await api.get("/api/grades/my");
      return response.data as Array<{ moduleTitle: string; grade: number; session: string }>;
    },
    enabled: isReady
  });

  const requestsQuery = useQuery({
    queryKey: ["requests"],
    queryFn: async () => {
      const response = await api.get("/api/requests/my");
      return response.data as Array<{ id: number; type: string; status: string }>;
    },
    enabled: isReady
  });

  const absencesQuery = useQuery({
    queryKey: ["absences"],
    queryFn: async () => {
      const response = await api.get("/api/absences/my");
      return response.data as Array<{ moduleTitle: string; moduleCode: string; lessonDate: string }>;
    },
    enabled: isReady
  });

  const paymentsQuery = useQuery({
    queryKey: ["payments"],
    queryFn: async () => {
      const response = await api.get("/api/payments/my");
      return response.data as PaymentSummary[];
    },
    enabled: isReady
  });

  const grades = gradesQuery.data ?? [];
  const requests = requestsQuery.data ?? [];
  const absences = absencesQuery.data ?? [];
  const payments = paymentsQuery.data ?? [];

  return (
    <div className="space-y-6">
      <section className="grid gap-4 md:grid-cols-4">
        <div className="card">
          <h2 className="text-lg font-semibold">Bonjour {session?.user?.name}</h2>
          <p className="mt-2 text-sm text-slate-500">Voici un apercu de votre activite recente.</p>
        </div>
        <div className="card">
          <h3 className="text-sm font-medium text-slate-500">Demandes en cours</h3>
          <p className="mt-2 text-3xl font-semibold text-brand">
            {requests.filter(r => r.status !== "DELIVERED").length}
          </p>
          <Link className="mt-4 inline-flex text-sm text-brand" href="/student/requests">
            Voir les demandes
          </Link>
        </div>
        <div className="card">
          <h3 className="text-sm font-medium text-slate-500">Absences enregistrees</h3>
          <p className="mt-2 text-3xl font-semibold text-brand">
            {absences.length}
          </p>
          <Link className="mt-4 inline-flex text-sm text-brand" href="/student/absences">
            Voir les absences
          </Link>
        </div>
        <div className="card">
          <h3 className="text-sm font-medium text-slate-500">Paiements recents</h3>
          <ul className="mt-2 space-y-1 text-sm">
            {payments.length > 0 ? (
              payments.slice(0, 3).map(payment => (
                <li key={payment.id} className="flex items-center justify-between gap-2">
                  <span>#{payment.id}</span>
                  <span className="truncate text-slate-600" title={payment.label}>
                    {payment.label}
                  </span>
                  <span>{(payment.amountCents / 100).toFixed(2)} {payment.currency}</span>
                  <span className="font-medium">
                    {statusLabels[payment.status] ?? payment.status}
                  </span>
                </li>
              ))
            ) : (
              <li className="text-slate-400">Aucun paiement</li>
            )}
          </ul>
        </div>
      </section>
      <section className="card">
        <h2 className="text-lg font-semibold text-slate-800">Dernieres notes</h2>
        <table className="mt-4 w-full text-sm">
          <thead className="text-left text-slate-500">
            <tr>
              <th className="py-2">Module</th>
              <th className="py-2">Session</th>
              <th className="py-2">Note</th>
            </tr>
          </thead>
          <tbody>
            {grades.length > 0 ? (
              grades.slice(0, 5).map(grade => (
                <tr key={grade.moduleTitle} className="border-t border-slate-100">
                  <td className="py-2">{grade.moduleTitle}</td>
                  <td>{grade.session}</td>
                  <td className="font-semibold">{grade.grade}</td>
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={3} className="py-4 text-center text-slate-400">
                  Aucune note publiee
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </section>
    </div>
  );
}

