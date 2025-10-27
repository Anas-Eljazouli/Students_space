import "../styles/globals.css";
import { ReactNode } from "react";
import { Providers } from "../components/providers";

export const metadata = {
  title: "Portail Étudiant",
  description: "Gestion unifiée des services étudiants"
};

export default function RootLayout({ children }: { children: ReactNode }) {
  return (
    <html lang="fr">
      <body className="bg-slate-50 text-slate-900">
        <Providers>{children}</Providers>
      </body>
    </html>
  );
}
