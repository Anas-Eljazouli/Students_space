"use client";

import { useQuery } from "@tanstack/react-query";
import { api } from "../../../lib/api";

export default function AdminUsersPage() {
  const usersQuery = useQuery({
    queryKey: ["admin-users"],
    queryFn: async () => {
      const response = await api.get("/api/admin/users");
      return response.data as Array<{ id: number; email: string; fullName: string; role: string }>;
    }
  });

  return (
    <div className="card">
      <h1 className="text-xl font-semibold text-slate-800">Utilisateurs</h1>
      <table className="mt-4 w-full text-sm">
        <thead className="text-left text-slate-500">
          <tr>
            <th className="py-2">ID</th>
            <th>Nom</th>
            <th>Email</th>
            <th>Rôle</th>
          </tr>
        </thead>
        <tbody>
          {usersQuery.data?.map(user => (
            <tr key={user.id} className="border-t border-slate-100">
              <td className="py-2">{user.id}</td>
              <td>{user.fullName}</td>
              <td>{user.email}</td>
              <td>{user.role}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
