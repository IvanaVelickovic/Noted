import api from "./client";

export type NoteBasicInfo = {
    id: string;
    createdAt: string;
    lastEdited: string;
    title: string;
    categoryId: string;
}

export const notesApi = {
    getAll : () => 
        api.get<NoteBasicInfo[]>("/notes/get-all").then((res) => res.data),
}