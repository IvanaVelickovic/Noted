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

export const notesApi = {
    getAll : () => 
        api.get<NoteBasicInfo[]>("/notes/get-all").then((res) => res.data),
    get: (id: string) => 
        api.get<Note>(`/notes/get/${id}`).then((res) => res.data),
    
}