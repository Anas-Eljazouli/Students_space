"use client";

import axios from "axios";
import { getSession, signOut } from "next-auth/react";

export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  withCredentials: false
});

api.interceptors.request.use(async config => {
  const session = await getSession();
  if (session && (session as any).accessToken) {
    config.headers = {
      ...config.headers,
      Authorization: `Bearer ${(session as any).accessToken}`
    };
  }
  return config;
});

api.interceptors.response.use(
  response => response,
  async error => {
    if (error.response?.status === 401) {
      await signOut({ callbackUrl: "/login" });
    }
    return Promise.reject(error);
  }
);
