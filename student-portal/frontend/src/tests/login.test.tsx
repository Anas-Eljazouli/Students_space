import { render, screen } from "@testing-library/react";
import LoginPage from "../app/login/page";
import { SessionProvider } from "next-auth/react";

describe("LoginPage", () => {
  it("renders form fields", () => {
    render(
      <SessionProvider>
        <LoginPage />
      </SessionProvider>
    );
    expect(screen.getByText(/Connexion/)).toBeInTheDocument();
    expect(screen.getByLabelText(/Email/)).toBeInTheDocument();
    expect(screen.getByLabelText(/Mot de passe/)).toBeInTheDocument();
  });
});
