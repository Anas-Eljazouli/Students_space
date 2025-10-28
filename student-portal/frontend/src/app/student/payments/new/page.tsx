"use client";

import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { api } from "../../../../lib/api";
import { useRouter } from "next/navigation";
import toast from "react-hot-toast";

const fileListSchema = z
  .custom<FileList | undefined>(
    value => value === undefined || value instanceof FileList,
    "Fichier invalide"
  )
  .optional();

const schema = z
  .object({
    amount: z.coerce.number().min(1, "Le montant doit etre superieur a zero"),
    currency: z.string().min(1).default("EUR"),
    label: z.string().min(3, "Libelle trop court"),
    paymentMethod: z.string().min(2, "Indiquez une methode"),
    requestId: z
      .union([z.coerce.number().int().positive(), z.literal(""), z.undefined()])
      .optional(),
    justification: fileListSchema
  })
  .superRefine((data, ctx) => {
    if (data.justification && data.justification.length > 0) {
      const file = data.justification.item(0);
      if (file && file.size > 10 * 1024 * 1024) {
        ctx.addIssue({
          code: z.ZodIssueCode.custom,
          message: "Le fichier ne doit pas depasser 10 Mo",
          path: ["justification"]
        });
      }
    }
  });

type FormValues = z.infer<typeof schema>;

const paymentMethods = ["Carte bancaire", "Virement", "Especes", "Cheque"];

export default function NewPaymentPage() {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    setValue,
    formState: { errors, isSubmitting }
  } = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: { currency: "EUR" }
  });

  const onSubmit = async (values: FormValues) => {
    const formData = new FormData();
    formData.append("amountCents", Math.round(values.amount * 100).toString());
    formData.append("currency", values.currency || "EUR");
    formData.append("label", values.label);
    formData.append("paymentMethod", values.paymentMethod);
    if (values.requestId && values.requestId !== "" && !Number.isNaN(Number(values.requestId))) {
      formData.append("requestId", Number(values.requestId).toString());
    }
    if (values.justification && values.justification.length > 0) {
      const file = values.justification.item(0);
      if (file) {
        formData.append("justification", file);
      }
    }

    const response = await api.post("/api/payments", formData, {
      headers: { "Content-Type": "multipart/form-data" }
    });
    toast.success("Paiement transmis pour validation");
    router.push(`/student/payments/${response.data.id}`);
  };

  return (
    <div className="card max-w-xl space-y-4">
      <h1 className="text-xl font-semibold text-slate-800">Nouveau paiement</h1>
      <p className="text-sm text-slate-500">
        Renseignez les informations ci-dessous. Le service administratif validera votre paiement apres verification du justificatif.
      </p>
      <form className="space-y-4" onSubmit={handleSubmit(onSubmit)}>
        <div>
          <label className="text-sm font-medium text-slate-700">Montant (EUR)</label>
          <input
            type="number"
            step="0.01"
            className="mt-1 w-full rounded-md border px-3 py-2"
            {...register("amount")}
          />
          {errors.amount && <p className="mt-1 text-sm text-red-500">{errors.amount.message}</p>}
        </div>
        <div>
          <label className="text-sm font-medium text-slate-700">Libelle</label>
          <input className="mt-1 w-full rounded-md border px-3 py-2" {...register("label")} />
          {errors.label && <p className="mt-1 text-sm text-red-500">{errors.label.message}</p>}
        </div>
        <div>
          <label className="text-sm font-medium text-slate-700">Methode de paiement</label>
          <select
            className="mt-1 w-full rounded-md border px-3 py-2"
            {...register("paymentMethod")}
            onChange={event => setValue("paymentMethod", event.target.value)}
          >
            <option value="">Choisir...</option>
            {paymentMethods.map(method => (
              <option key={method} value={method}>
                {method}
              </option>
            ))}
          </select>
          {errors.paymentMethod && (
            <p className="mt-1 text-sm text-red-500">{errors.paymentMethod.message}</p>
          )}
        </div>
        <div>
          <label className="text-sm font-medium text-slate-700">Justificatif</label>
          <input
            type="file"
            className="mt-1 w-full rounded-md border px-3 py-2"
            {...register("justification")}
          />
          <p className="mt-1 text-xs text-slate-500">
            Formats acceptes: PDF ou image. Taille maximale 10 Mo.
          </p>
          {errors.justification && (
            <p className="mt-1 text-sm text-red-500">{errors.justification.message as string}</p>
          )}
        </div>
        <div>
          <label className="text-sm font-medium text-slate-700">Demande associee (optionnel)</label>
          <input
            type="number"
            className="mt-1 w-full rounded-md border px-3 py-2"
            placeholder="Identifiant de la demande"
            {...register("requestId")}
          />
          {errors.requestId && (
            <p className="mt-1 text-sm text-red-500">{errors.requestId.message as string}</p>
          )}
        </div>
        <button className="btn" type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Envoi..." : "Soumettre"}
        </button>
      </form>
    </div>
  );
}
