"use client";

import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "../../../lib/api";
import { useSession } from "next-auth/react";
import toast from "react-hot-toast";

type AdminPayment = {
  id: number;
  studentName: string;
  label: string;
  amountCents: number;
  currency: string;
  paymentMethod: string;
  status: string;
  justificationUrl?: string | null;
  justificationName?: string | null;
  statusNotes?: string | null;
  createdAt: string;
};

const statusLabels: Record<string, string> = {
  PROCESSING: "En cours de traitement",
  SUCCEEDED: "Valide",
  FAILED: "Refuse",
  REFUNDED: "Rembourse"
};

export default function AdminPaymentsPage() {
  const { status } = useSession();
  const isReady = status === "authenticated";
  const queryClient = useQueryClient();

  const paymentsQuery = useQuery({
    queryKey: ["admin", "payments"],
    queryFn: async () => {
      const response = await api.get("/api/payments");
      return response.data as AdminPayment[];
    },
    enabled: isReady
  });

  const updateMutation = useMutation({
    mutationFn: async (payload: { id: number; status: "SUCCEEDED" | "FAILED"; notes?: string }) => {
      await api.patch(`/api/payments/${payload.id}/status`, {
        status: payload.status,
        notes: payload.notes
      });
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin", "payments"] });
      queryClient.invalidateQueries({ queryKey: ["payments"] });
      toast.success("Statut mis a jour");
    },
    onError: () => toast.error("Impossible de mettre a jour le paiement")
  });

  const handleDecision = async (payment: AdminPayment, decision: "SUCCEEDED" | "FAILED") => {
    let notes: string | undefined = undefined;
    if (decision === "FAILED") {
      notes = window.prompt("Indiquez la raison du refus") ?? undefined;
    }
    updateMutation.mutate({ id: payment.id, status: decision, notes });
  };

  return (
    <div className="card space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-semibold text-slate-800">Revue des paiements</h1>
        <p className="text-sm text-slate-500">
          Validez ou refusez les paiements transmis par les etudiants.
        </p>
      </div>
      <table className="w-full text-sm">
        <thead className="text-left text-slate-500">
          <tr>
            <th className="py-2">Reference</th>
            <th>Etudiant</th>
            <th>Libelle</th>
            <th>Methode</th>
            <th>Montant</th>
            <th>Justificatif</th>
            <th>Statut</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {paymentsQuery.data?.map(payment => (
            <tr key={payment.id} className="border-t border-slate-100">
              <td className="py-2 font-medium">#{payment.id}</td>
              <td>{payment.studentName}</td>
              <td className="max-w-xs truncate" title={payment.label}>
                {payment.label}
              </td>
              <td>{payment.paymentMethod}</td>
              <td>
                {(payment.amountCents / 100).toFixed(2)} {payment.currency}
              </td>
              <td>
                {payment.justificationUrl ? (
                  <a
                    href={`${process.env.NEXT_PUBLIC_API_URL ?? ""}${payment.justificationUrl}`}
                    className="text-brand"
                    target="_blank"
                    rel="noopener noreferrer"
                  >
                    {payment.justificationName ?? "Consulter"}
                  </a>
                ) : (
                  <span className="text-slate-400">Non fourni</span>
                )}
              </td>
              <td>
                <span className="rounded-full bg-slate-100 px-2 py-1 text-xs font-semibold text-slate-700">
                  {statusLabels[payment.status] ?? payment.status}
                </span>
                {payment.statusNotes && (
                  <p className="mt-1 text-xs text-slate-500">{payment.statusNotes}</p>
                )}
              </td>
              <td>
                {payment.status === "PROCESSING" ? (
                  <div className="flex gap-2">
                    <button
                      className="rounded-md bg-emerald-500 px-3 py-1 text-xs font-semibold text-white hover:bg-emerald-600"
                      onClick={() => handleDecision(payment, "SUCCEEDED")}
                      disabled={updateMutation.isPending}
                    >
                      Valider
                    </button>
                    <button
                      className="rounded-md bg-rose-500 px-3 py-1 text-xs font-semibold text-white hover:bg-rose-600"
                      onClick={() => handleDecision(payment, "FAILED")}
                      disabled={updateMutation.isPending}
                    >
                      Refuser
                    </button>
                  </div>
                ) : (
                  <span className="text-xs text-slate-400">Decision prise</span>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      {paymentsQuery.data?.length === 0 && (
        <p className="text-sm text-slate-500">Aucun paiement a verifier pour le moment.</p>
      )}
    </div>
  );
}
