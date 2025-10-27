"use client";

import { useParams } from "next/navigation";
import { useQuery } from "@tanstack/react-query";
import { api } from "../../../../lib/api";
import Link from "next/link";
import { useSession } from "next-auth/react";

export default function RequestDetailsPage() {
  const { status } = useSession();
  const isReady = status === "authenticated";
  const params = useParams<{ id: string }>();
  const requestId = params.id;

  const requestQuery = useQuery({
    queryKey: ["request", requestId],
    queryFn: async () => {
      const response = await api.get(`/api/requests/${requestId}`);
      return response.data as {
        id: number;
        type: string;
        status: string;
        payloadJson: string;
        files: Array<{ id: number; filename: string; url: string }>;
        createdAt: string;
      };
    },
    enabled: isReady && !!requestId
  });

  const data = requestQuery.data;

  return (
    <div className="space-y-4">
      <Link href="/student/requests" className="text-sm text-brand">
        ← Retour aux demandes
      </Link>
      <div className="card space-y-3">
        <h1 className="text-xl font-semibold text-slate-800">Demande #{data?.id}</h1>
        <p className="text-sm text-slate-500">Type : {data?.type}</p>
        <p className="text-sm text-slate-500">Statut actuel : {data?.status}</p>
        <div className="rounded-lg bg-slate-100 p-3 text-sm">
          <pre>{data?.payloadJson}</pre>
        </div>
        <div>
          <h2 className="text-sm font-medium text-slate-700">Pièces jointes</h2>
          <ul className="mt-2 space-y-2 text-sm text-brand">
            {data?.files?.map(file => (
              <li key={file.id}>
                <a href={file.url} target="_blank" rel="noreferrer">
                  {file.filename}
                </a>
              </li>
            )) || <li>Aucune pièce jointe</li>}
          </ul>
        </div>
      </div>
    </div>
  );
}
