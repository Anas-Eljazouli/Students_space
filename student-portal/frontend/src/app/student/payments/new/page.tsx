"use client";

import { useForm } from "react-hook-form";
import { z } from "zod";
import { zodResolver } from "@hookform/resolvers/zod";
import { api } from "../../../../lib/api";
import { useRouter } from "next/navigation";
import toast from "react-hot-toast";

const schema = z.object({
  amount: z.coerce.number().min(1),
  currency: z.string().default("EUR"),
  purpose: z.string().min(3)
});

type FormValues = z.infer<typeof schema>;

export default function NewPaymentPage() {
  const router = useRouter();
  const {
    register,
    handleSubmit,
    formState: { errors }
  } = useForm<FormValues>({ resolver: zodResolver(schema), defaultValues: { currency: "EUR" } });

  const onSubmit = async (values: FormValues) => {
    const response = await api.post("/api/payments/intent", {
      amountCents: Math.round(values.amount * 100),
      currency: values.currency,
      purpose: values.purpose
    });
    toast.success("Paiement initié");
    router.push(`/student/payments/${response.data.paymentId}`);
  };

  return (
    <div className="card max-w-xl">
      <h1 className="text-xl font-semibold text-slate-800">Nouveau paiement</h1>
      <form className="mt-4 space-y-4" onSubmit={handleSubmit(onSubmit)}>
        <div>
          <label className="text-sm font-medium text-slate-700">Montant (€)</label>
          <input type="number" step="0.01" className="mt-1 w-full rounded-md border px-3 py-2" {...register("amount")} />
          {errors.amount && <p className="mt-1 text-sm text-red-500">{errors.amount.message}</p>}
        </div>
        <div>
          <label className="text-sm font-medium text-slate-700">Libellé</label>
          <input className="mt-1 w-full rounded-md border px-3 py-2" {...register("purpose")} />
          {errors.purpose && <p className="mt-1 text-sm text-red-500">{errors.purpose.message}</p>}
        </div>
        <button className="btn" type="submit">
          Continuer
        </button>
      </form>
    </div>
  );
}
