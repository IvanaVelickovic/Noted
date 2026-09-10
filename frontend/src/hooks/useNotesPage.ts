import { useEffect, useRef, useState } from "react";
import { useCategories } from "./useCategories";
import { useNotes } from "./useNotes";
import { useNote } from "./useNote";

export function useNotesPage(){
    const [selectedNoteId, setSelectedNoteId] = useState<string>("");
    const initialAutoSelectDone = useRef(false);

    const { categories, fetchCategories, loading: loadingCategories, error: errorCategories} = useCategories();
    const { notes, loading: loadingNotes, error: errorNotes} = useNotes(categories);

    
    useEffect(() => {
        if(!initialAutoSelectDone.current && notes.length > 0){
            setSelectedNoteId(notes[0].id);
            initialAutoSelectDone.current = true;
        }
    }, [notes, selectedNoteId])

   const { note, loading: loadingNote, error: errorNote } = useNote(notes, selectedNoteId);

    return {
    categories,
    fetchCategories,
    notes,
    note,
    selectedNoteId,
    setSelectedNoteId,
    loading: loadingCategories || loadingNotes || loadingNote,
    error: errorCategories || errorNotes || errorNote,
  };

}