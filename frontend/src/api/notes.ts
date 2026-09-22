import api from "./client";

export type NoteBasicInfo = {
    id: string;
    createdAt: string;
    lastEdited: string;
    title: string;
    categoryId: string;
}
export type Note = {
    id: string;
    title : string;
    body : string;
    createdAt: string;
    lastEdited: string;
}
export type CreateNote = {
    title: string;
    body: string;
    categoryId?: string;
}

export const notesApi = {
    getAll : () => 
        api.get<NoteBasicInfo[]>("/notes/get-all").then((res) => res.data),
    get: (id: string) => 
        api.get<Note>(`/notes/get/${id}`).then((res) => res.data),
    create: (payload: CreateNote) =>
        api.post<NoteBasicInfo>("/notes/create", payload).then((res) => res.data),
    update: (payload: CreateNote, id: string) => 
        api.put(`/notes/update/${id}`, payload).then((res) => res.data),
    delete: (id: string) =>
        api.delete(`/notes/delete/${id}`).then((res) => res.data),
    summarize : (id: string) => 
        api.post<{ jobId: string }>(`/notes/${id}/summarize`).then((res) => res.data)
}