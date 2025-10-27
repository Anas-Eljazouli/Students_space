"use client";

import { useState } from "react";
import { useQuery, useMutation } from "@tanstack/react-query";
import { api } from "../../../lib/api";
import toast from "react-hot-toast";
import { useSession } from "next-auth/react";

interface StudentOption {
  id: number;
  fullName: string;
  email: string;
}

export default function StaffMessagesPage() {
  const { status } = useSession();
  const isReady = status === "authenticated";
  const [studentId, setStudentId] = useState<number | undefined>();
  const [subject, setSubject] = useState("");
  const [message, setMessage] = useState("");

  const studentsQuery = useQuery({
    queryKey: ["staff-students"],
    queryFn: async () => {
      const response = await api.get<StudentOption[]>("/api/admin/notifications/students");
      return response.data;
    },
    enabled: isReady
  });

  const sendMutation = useMutation({
    mutationFn: async () => {
      if (!studentId) {
        throw new Error("Please select a student");
      }
      await api.post("/api/admin/notifications", {
        studentId,
        subject,
        message
      });
    },
    onSuccess: () => {
      toast.success("Message sent");
      setMessage("");
      setSubject("");
      setStudentId(undefined);
    },
    onError: () => toast.error("Unable to send message")
  });

  const isSubmitDisabled = sendMutation.isLoading || !studentId || !subject.trim() || !message.trim();

  return (
    <div className="card max-w-2xl space-y-4">
      <div>
        <h1 className="text-xl font-semibold text-slate-800">Send a message</h1>
        <p className="text-sm text-slate-500">Choose a student and write a message that will appear in their notification center.</p>
      </div>
      <div className="space-y-3">
        <label className="block text-sm font-medium text-slate-700">Student</label>
        <select
          className="w-full rounded-md border border-slate-200 px-3 py-2"
          value={studentId ?? ""}
          onChange={event => setStudentId(event.target.value ? Number(event.target.value) : undefined)}
          disabled={!isReady || studentsQuery.isLoading}
        >
          <option value="">Select a student...</option>
          {studentsQuery.data?.map(student => (
            <option key={student.id} value={student.id}>
              {student.fullName || student.email}
            </option>
          ))}
        </select>
        {studentsQuery.isError && (
          <p className="text-xs text-red-500">Unable to load students list.</p>
        )}
      </div>
      <div className="space-y-3">
        <label className="block text-sm font-medium text-slate-700">Subject</label>
        <input
          className="w-full rounded-md border border-slate-200 px-3 py-2"
          value={subject}
          onChange={event => setSubject(event.target.value)}
          placeholder="Subject..."
        />
      </div>
      <div className="space-y-3">
        <label className="block text-sm font-medium text-slate-700">Message</label>
        <textarea
          className="w-full rounded-md border border-slate-200 px-3 py-2"
          rows={5}
          value={message}
          onChange={event => setMessage(event.target.value)}
          placeholder="Your message for the student..."
        />
      </div>
      <button className="btn" disabled={isSubmitDisabled} onClick={() => sendMutation.mutate()}>
        {sendMutation.isLoading ? "Sending..." : "Send"}
      </button>
    </div>
  );
}
