import { useCallback, useEffect, useState } from "react";
import { notesApi, type Note, type NoteBasicInfo } from "../api/notes";
import axios from "axios";

export function useNote(notes : NoteBasicInfo[], selectedId : string){
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");
    const [note, setNote] = useState<Note | undefined>(undefined);

    const fetchNote = useCallback(async () => {
        if(!selectedId){
            setNote(undefined);
            return;
        }
        setLoading(true);
        setError("");

        try{
            const data = await notesApi.get(selectedId);
            console.log("fetched note:", data); 
            setNote(data ?? undefined);
            setError("");
        } catch(err) {
            if (axios.isAxiosError(err) && err.response?.status === 404){
                setNote(undefined);
                setError("");
            } else{
                setError(axios.isAxiosError(err) ? err.response?.data?.error ?? "Failed to load current note" : "Failed to load current note");
            }
            
        } finally {
            setLoading(false);
        }
    }, [notes, selectedId]);

    useEffect(() => {
        fetchNote();
    }, [fetchNote]);

    return { note, loading, error, fetchNote };
}