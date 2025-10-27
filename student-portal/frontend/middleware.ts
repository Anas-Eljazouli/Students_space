export { default } from "next-auth/middleware";

export const config = {
  matcher: [
    "/student/:path*",
    "/staff/:path*",
    "/admin/:path*"
  ]
};
