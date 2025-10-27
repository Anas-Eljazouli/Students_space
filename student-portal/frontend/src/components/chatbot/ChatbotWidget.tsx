"use client";

import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { api } from "../../lib/api";

export function ChatbotWidget() {
  const [open, setOpen] = useState(false);
  const [question, setQuestion] = useState("");
  const [query, setQuery] = useState<string | null>(null);
  const faqQuery = useQuery({
    queryKey: ["faq", query],
    queryFn: async () => {
      const response = await api.get("/api/faq/search", { params: { q: query ?? "" } });
      return response.data as Array<{ id: number; question: string; answer: string }>;
    },
    enabled: open
  });

  const ask = () => {
    setQuery(question);
  };

  return (
    <div className="fixed bottom-6 right-6">
      {open && (
        <div className="mb-3 w-80 rounded-xl bg-white p-4 shadow-xl">
          <h2 className="text-lg font-semibold text-slate-800">Assistant 24/7</h2>
          <p className="text-sm text-slate-500">Posez une question fréquente.</p>
          <input
            className="mt-2 w-full rounded-md border px-3 py-2"
            value={question}
            onChange={event => setQuestion(event.target.value)}
            placeholder="Comment obtenir un certificat..."
          />
          <button className="btn mt-3 w-full" onClick={ask}>
            Rechercher
          </button>
          <div className="mt-4 space-y-3 text-sm">
            {faqQuery.data?.map(item => (
              <div key={item.id} className="rounded-md border border-slate-200 p-2">
                <p className="font-semibold">{item.question}</p>
                <p className="text-slate-600">{item.answer}</p>
              </div>
            ))}
          </div>
        </div>
      )}
      <button className="btn" onClick={() => setOpen(value => !value)}>
        {open ? "Fermer" : "Chatbot"}
      </button>
    </div>
  );
}
