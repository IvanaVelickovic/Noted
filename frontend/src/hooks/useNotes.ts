import { useCallback, useEffect, useState } from "react";
import { notesApi, type NoteBasicInfo } from "../api/notes";
import axios from "axios";
import type { CategoryDetailed } from "../api/categories";

export function useNotes(categories : CategoryDetailed[]){
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [notes, setNotes] = useState<NoteBasicInfo[]>([]);

    const fetchNotes = useCallback(async () => {
        setLoading(true);
        setError("");

        try{
            const data = await notesApi.getAll();
            setNotes(data);
        } catch(err) {
            setError(axios.isAxiosError(err) ? err.response?.data?.error ?? "Failed to load notes" : "Failed to load notes");
        } finally {
            setLoading(false);
        }
    }, [categories]);

    useEffect(() => {
        fetchNotes();
    }, [fetchNotes]);

    return { notes, loading, error, fetchNotes };
}