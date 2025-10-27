"use client";

import Link from "next/link";
import { useQuery } from "@tanstack/react-query";
import { api } from "../../../lib/api";
import { useSession } from "next-auth/react";

export default function PaymentsPage() {
  const { status } = useSession();
  const isReady = status === "authenticated";
  const paymentsQuery = useQuery({
    queryKey: ["payments"],
    queryFn: async () => {
      const response = await api.get("/api/payments/my");
      return response.data as Array<{ id: number; amountCents: number; status: string; createdAt: string }>;
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
            <th className="py-2">Référence</th>
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
              <td>{(payment.amountCents / 100).toFixed(2)} €</td>
              <td>{payment.status}</td>
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
