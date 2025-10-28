"use client";

import Link from "next/link";
import { useQuery } from "@tanstack/react-query";
import { api } from "../../../lib/api";
import { useSession } from "next-auth/react";

type PaymentSummary = {
  id: number;
  label: string;
  amountCents: number;
  currency: string;
  paymentMethod: string;
  status: string;
  createdAt: string;
};

const statusLabels: Record<string, string> = {
  PROCESSING: "En cours de traitement",
  SUCCEEDED: "Valide",
  FAILED: "Refuse",
  REFUNDED: "Rembourse"
};

export default function PaymentsPage() {
  const { status } = useSession();
  const isReady = status === "authenticated";
  const paymentsQuery = useQuery({
    queryKey: ["payments"],
    queryFn: async () => {
      const response = await api.get("/api/payments/my");
      return response.data as PaymentSummary[];
    },
    enabled: isReady
  });

  return (
    <div className="card">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-semibold text-slate-800">Paiements</h1>
        <Link href="/student/payments/new" className="btn">
          Nouveau paiement
        </Link>
      </div>
      <table className="mt-4 w-full text-sm">
        <thead className="text-left text-slate-500">
          <tr>
            <th className="py-2">Reference</th>
            <th>Libelle</th>
            <th>Methode</th>
            <th>Montant</th>
            <th>Statut</th>
            <th>Date</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {paymentsQuery.data?.map(payment => (
            <tr key={payment.id} className="border-t border-slate-100">
              <td className="py-2 font-medium">#{payment.id}</td>
              <td className="max-w-xs truncate" title={payment.label}>
                {payment.label}
              </td>
              <td>{payment.paymentMethod}</td>
              <td>
                {(payment.amountCents / 100).toFixed(2)} {payment.currency}
              </td>
              <td className="font-medium">
                {statusLabels[payment.status] ?? payment.status}
              </td>
              <td>{new Date(payment.createdAt).toLocaleDateString()}</td>
              <td>
                <Link href={`/student/payments/${payment.id}`} className="text-brand">
                  Consulter
                </Link>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
