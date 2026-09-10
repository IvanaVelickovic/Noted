import { useEffect, useState } from "react";
import { useCategories } from "./useCategories";
import { useNotes } from "./useNotes";
import { useNote } from "./useNote";

export function useNotesPage(){
    const [selectedNoteId, setSelectedNoteId] = useState<string>("");

    const { categories, loading: loadingCategories, error: errorCategories} = useCategories();
    const { notes, loading: loadingNotes, error: errorNotes} = useNotes(categories);

    
    useEffect(() => {
        if(selectedNoteId === "" && notes.length > 0){
            console.log("auto-selecting note:", notes[0].id);
            setSelectedNoteId(notes[0].id);
        }
    }, [notes, selectedNoteId])

   const { note, loading: loadingNote, error: errorNote } = useNote(notes, selectedNoteId);

    return {
    categories,
    notes,
    note,
    selectedNoteId,
    setSelectedNoteId,
    loading: loadingCategories || loadingNotes || loadingNote,
    error: errorCategories || errorNotes || errorNote,
  };

}