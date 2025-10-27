"use client";

import Link from "next/link";
import { useQuery } from "@tanstack/react-query";
import { api } from "../../../lib/api";
import { useSession } from "next-auth/react";

export default function RequestsPage() {
  const { status } = useSession();
  const isReady = status === "authenticated";
  const requestsQuery = useQuery({
    queryKey: ["requests"],
    queryFn: async () => {
      const response = await api.get("/api/requests/my");
      return response.data as Array<{ id: number; type: string; status: string; createdAt: string }>;
    },
    enabled: isReady
  });

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-semibold text-slate-800">Mes demandes</h1>
        <Link href="/student/requests/new" className="btn">
          Nouvelle demande
        </Link>
      </div>
      <div className="card">
        <table className="w-full text-sm">
          <thead className="text-left text-slate-500">
            <tr>
              <th className="py-2">Référence</th>
              <th>Type</th>
              <th>Statut</th>
              <th>Créée le</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            {requestsQuery.data?.map(request => (
              <tr key={request.id} className="border-t border-slate-100">
                <td className="py-2 font-medium">#{request.id}</td>
                <td>{request.type}</td>
                <td>{request.status}</td>
                <td>{new Date(request.createdAt).toLocaleDateString()}</td>
                <td>
                  <Link href={`/student/requests/${request.id}`} className="text-brand">
                    Détails
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
