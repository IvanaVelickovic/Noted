import api from "./client";

export type SummaryJobStatus = "PENDING" | "PROCESSING" | "COMPLETED" | "FAILED";

export type SummaryJob = {
    id: string, 
    status: SummaryJobStatus;
    result: string;
    errorMessage: string;
    createdAt: string;
    updatedAt: string;
}

export const jobsApi = {
    get : (id: string) => 
        api.get<SummaryJob>(`/jobs/${id}`).then((res) => res.data)
}