import { useCallback } from "react"
import axios from "axios";
import { notesApi } from "../api/notes";

export function useNoteActions(refetchNotes: () => Promise<void>){
    const createNote = useCallback(async (title: string, body: string) => {
        try{
            const noteBasicInfo = await notesApi.create({ title, body });

            console.log("successfully added a new note with id: " + noteBasicInfo.id);
            await refetchNotes();
            return noteBasicInfo.id;
        } catch(err) {
            throw axios.isAxiosError(err)
            ? new Error(err.response?.data.error ?? "Failed to create note")
            : err;
        }
    }, [refetchNotes]);

    const updateNote = useCallback(async (title: string, body: string, noteId: string, categoryId?: string) => {
        try {
            if (categoryId === "") categoryId = undefined;
            await notesApi.update({ title, body, categoryId }, noteId);
            console.log("successfully updated a note");
            await refetchNotes();

        } catch(err){
            throw axios.isAxiosError(err)
            ? new Error(err.response?.data.error ?? "Failed to update note")
            : err;
        }

    }, [refetchNotes])

    const deleteNote = useCallback(async (id: string) => {
        try {
            await notesApi.delete(id);
            await refetchNotes();
        } catch(err){
            throw axios.isAxiosError(err)
            ? new Error(err.response?.data.error ?? "Failed to delete note")
            : err;
        }
    }, [refetchNotes]);

    return {createNote, updateNote, deleteNote};
}