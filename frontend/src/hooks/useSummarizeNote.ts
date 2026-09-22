import { useCallback, useEffect, useRef, useState } from "react";
import axios from "axios";
import { notesApi } from "../api/notes";
import { jobsApi, type SummaryJob } from "../api/jobs";

const POLL_INTERVAL_MS = 1500;

export function useSummarizeNote() {
  const [job, setJob] = useState<SummaryJob | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const intervalRef = useRef<ReturnType<typeof setInterval> | null>(null);

  const stopPolling = useCallback(() => {
    if (intervalRef.current) {
      clearInterval(intervalRef.current);
      intervalRef.current = null;
    }
  }, []);

  // clean up on unmount so we don't poll after the component is gone
  useEffect(() => {
    return () => stopPolling();
  }, [stopPolling]);

  const pollJob = useCallback(
    (jobId: string) => {
      intervalRef.current = setInterval(async () => {
        try {
          const updatedJob = await jobsApi.get(jobId);
          setJob(updatedJob);

          if (updatedJob.status === "COMPLETED" || updatedJob.status === "FAILED") {
            stopPolling();
            setLoading(false);
            if (updatedJob.status === "FAILED") {
              setError(updatedJob.errorMessage ?? "Summarization failed");
            }
          }
        } catch (err) {
          stopPolling();
          setLoading(false);
          setError(
            axios.isAxiosError(err)
              ? (err.response?.data.error ?? "Failed to check summary status")
              : "Failed to check summary status",
          );
        }
      }, POLL_INTERVAL_MS);
    },
    [stopPolling],
  );

  const summarize = useCallback(
    async (noteId: string) => {
      setLoading(true);
      setError(null);
      setJob(null);
      stopPolling();

      try {
        const { jobId } = await notesApi.summarize(noteId);
        pollJob(jobId);
      } catch (err) {
        setLoading(false);
        setError(
          axios.isAxiosError(err)
            ? (err.response?.data.error ?? "Failed to start summarization")
            : "Failed to start summarization",
        );
      }
    },
    [pollJob, stopPolling],
  );

  const reset = useCallback(() => {
    stopPolling();
    setJob(null);
    setLoading(false);
    setError(null);
  }, [stopPolling]);

  return { summarize, job, loading, error, reset };
}