"use client";

import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { api } from "../../../lib/api";

const statuses = [
  "SUBMITTED",
  "IN_REVIEW",
  "APPROVED",
  "READY",
  "DELIVERED",
  "REJECTED"
];

export default function StaffRequestsPage() {
  const [status, setStatus] = useState("SUBMITTED");
  const queryClient = useQueryClient();
  const requestsQuery = useQuery({
    queryKey: ["admin-requests", status],
    queryFn: async () => {
      const response = await api.get("/api/admin/requests", { params: { status } });
      return response.data as Array<{ id: number; type: string; status: string; studentId: number }>;
    }
  });

  const transition = useMutation({
    mutationFn: ({ id, nextStatus }: { id: number; nextStatus: string }) =>
      api.post(`/api/admin/requests/${id}/transition`, null, { params: { status: nextStatus } }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["admin-requests", status] });
    }
  });

  return (
    <div className="card space-y-4">
      <div className="flex items-center justify-between">
        <h1 className="text-xl font-semibold text-slate-800">Demandes en traitement</h1>
        <select className="rounded-md border px-3 py-2" value={status} onChange={event => setStatus(event.target.value)}>
          {statuses.map(value => (
            <option key={value}>{value}</option>
          ))}
        </select>
      </div>
      <table className="w-full text-sm">
        <thead className="text-left text-slate-500">
          <tr>
            <th className="py-2">Référence</th>
            <th>Type</th>
            <th>Statut</th>
            <th>Étudiant</th>
            <th></th>
          </tr>
        </thead>
        <tbody>
          {requestsQuery.data?.map(request => (
            <tr key={request.id} className="border-t border-slate-100">
              <td className="py-2">#{request.id}</td>
              <td>{request.type}</td>
              <td>{request.status}</td>
              <td>{request.studentId}</td>
              <td>
                <select
                  className="rounded-md border px-2 py-1"
                  onChange={event => transition.mutate({ id: request.id, nextStatus: event.target.value })}
                >
                  <option>Action...</option>
                  {statuses.map(value => (
                    <option key={value} value={value}>
                      {value}
                    </option>
                  ))}
                </select>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
