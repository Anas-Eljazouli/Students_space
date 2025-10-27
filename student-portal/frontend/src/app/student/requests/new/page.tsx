"use client";

import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { api } from "../../../../lib/api";
import { useRouter } from "next/navigation";
import toast from "react-hot-toast";
import { useState } from "react";

const schema = z.object({
  type: z.enum(["CERTIFICAT_SCOLARITE", "ATTESTATION"]),
  reason: z.string().min(3)
});

type FormValues = z.infer<typeof schema>;

export default function NewRequestPage() {
  const router = useRouter();
  const [file, setFile] = useState<File | null>(null);
  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm<FormValues>({ resolver: zodResolver(schema) });

  const onSubmit = async (values: FormValues) => {
    const payload = JSON.stringify({ reason: values.reason });
    const response = await api.post("/api/requests", {
      type: values.type,
      payloadJson: payload
    });
    if (file) {
      const formData = new FormData();
      formData.append("file", file);
      await api.post(`/api/requests/${response.data.id}/files`, formData, {
        headers: { "Content-Type": "multipart/form-data" }
      });
    }
    toast.success("Demande créée");
    router.push("/student/requests");
  };

  return (
    <div className="card max-w-2xl">
      <h1 className="text-xl font-semibold text-slate-800">Nouvelle demande</h1>
      <form className="mt-4 space-y-4" onSubmit={handleSubmit(onSubmit)}>
        <div>
          <label className="text-sm font-medium text-slate-700">Type</label>
          <select className="mt-1 w-full rounded-md border border-slate-200 px-3 py-2" {...register("type")}>
            <option value="CERTIFICAT_SCOLARITE">Certificat de scolarité</option>
            <option value="ATTESTATION">Attestation</option>
          </select>
          {errors.type && <p className="mt-1 text-sm text-red-500">{errors.type.message}</p>}
        </div>
        <div>
          <label className="text-sm font-medium text-slate-700">Motif</label>
          <textarea
            className="mt-1 w-full rounded-md border border-slate-200 px-3 py-2"
            rows={4}
            {...register("reason")}
          />
          {errors.reason && <p className="mt-1 text-sm text-red-500">{errors.reason.message}</p>}
        </div>
        <div>
          <label className="text-sm font-medium text-slate-700">Ajouter un fichier</label>
          <input type="file" className="mt-1 w-full" onChange={event => setFile(event.target.files?.[0] ?? null)} />
        </div>
        <button className="btn" type="submit">
          Soumettre
        </button>
      </form>
    </div>
  );
}
