"use client";

import { useParams } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { api } from "../../../../lib/api";
import { useSession } from "next-auth/react";
type PaymentDetails = {
  id: number;
  label: string;
  amountCents: number;
  currency: string;
  status: string;
  paymentMethod: string;
  justificationUrl?: string | null;
  justificationName?: string | null;
  statusNotes?: string | null;
  createdAt: string;
  updatedAt: string;
};

const statusLabels: Record<string, string> = {
  PROCESSING: "En cours de traitement",
  SUCCEEDED: "Valide",
  FAILED: "Refuse",
  REFUNDED: "Rembourse"
};

export default function PaymentDetailsPage() {
  const { status } = useSession();
  const isReady = status === "authenticated";
  const params = useParams<{ id: string }>();
  const paymentId = params.id;

  const paymentQuery = useQuery({
    queryKey: ["payment", paymentId],
    queryFn: async () => {
      const response = await api.get(`/api/payments/${paymentId}`);
      return response.data as PaymentDetails;
    },
    enabled: isReady && !!paymentId
  });

  const data = paymentQuery.data;

  if (!data) {
    return (
      <div className="card max-w-xl space-y-3">
        <h1 className="text-xl font-semibold text-slate-800">Paiement</h1>
        <p className="text-sm text-slate-500">Chargement des informations...</p>
      </div>
    );
  }

  return (
    <div className="card max-w-2xl space-y-4">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-xl font-semibold text-slate-800">Paiement #{data.id}</h1>
          <p className="text-sm text-slate-500">
            Soumis le {new Date(data.createdAt).toLocaleDateString()} - mise a jour le{" "}
            {new Date(data.updatedAt).toLocaleDateString()}
          </p>
        </div>
        <span className="rounded-full bg-slate-100 px-3 py-1 text-sm font-medium text-slate-700">
          {statusLabels[data.status] ?? data.status}
        </span>
      </div>

      <div className="grid gap-4 md:grid-cols-2">
        <div className="rounded-md border border-slate-100 bg-slate-50 p-4">
          <p className="text-xs font-semibold uppercase text-slate-500">Libelle</p>
          <p className="mt-1 text-sm text-slate-800">{data.label}</p>
        </div>
        <div className="rounded-md border border-slate-100 bg-slate-50 p-4">
          <p className="text-xs font-semibold uppercase text-slate-500">Montant</p>
          <p className="mt-1 text-sm text-slate-800">
            {(data.amountCents / 100).toFixed(2)} {data.currency}
          </p>
        </div>
        <div className="rounded-md border border-slate-100 bg-slate-50 p-4">
          <p className="text-xs font-semibold uppercase text-slate-500">Methode</p>
          <p className="mt-1 text-sm text-slate-800">{data.paymentMethod}</p>
        </div>
        <div className="rounded-md border border-slate-100 bg-slate-50 p-4">
          <p className="text-xs font-semibold uppercase text-slate-500">Justificatif</p>
          {data.justificationUrl ? (
            <a
              href={`${process.env.NEXT_PUBLIC_API_URL ?? ""}${data.justificationUrl}`}
              target="_blank"
              rel="noopener noreferrer"
              className="text-sm text-brand"
            >
              {data.justificationName ?? "Consulter le fichier"}
            </a>
          ) : (
            <p className="mt-1 text-sm text-slate-400">Aucun justificatif joint</p>
          )}
        </div>
      </div>

      {data.statusNotes && (
        <div className="rounded-md border border-amber-200 bg-amber-50 p-4 text-sm text-amber-700">
          <p className="font-semibold">Commentaire du service administratif</p>
          <p className="mt-1">{data.statusNotes}</p>
        </div>
      )}
    </div>
  );
}
