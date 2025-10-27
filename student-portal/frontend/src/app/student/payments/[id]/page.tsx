"use client";

import { useParams } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { api } from "../../../../lib/api";
import { useSession } from "next-auth/react";

export default function PaymentDetailsPage() {
  const { status } = useSession();
  const isReady = status === "authenticated";
  const params = useParams<{ id: string }>();
  const paymentId = params.id;

  const paymentQuery = useQuery({
    queryKey: ["payment", paymentId],
    queryFn: async () => {
      const response = await api.get(`/api/payments/${paymentId}`);
      return response.data as { id: number; amountCents: number; status: string; providerRef: string };
    },
    enabled: isReady && !!paymentId
  });

  const data = paymentQuery.data;

  return (
    <div className="card max-w-xl space-y-3">
      <h1 className="text-xl font-semibold text-slate-800">Paiement #{data?.id}</h1>
      <p>Montant : {(data?.amountCents ?? 0) / 100} €</p>
      <p>Statut : {data?.status}</p>
      <p className="text-sm text-slate-500">
        Ce paiement est simulé. Utilisez le simulateur pour confirmer et déclencher le webhook.
      </p>
    </div>
  );
}
