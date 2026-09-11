import { useEffect, useState } from "react";
import api from "../api/client";
import axios from "axios";
import NotesLeftSideBar from "../components/NotesLeftSideBar";
import { useNotesPage } from "../hooks/useNotesPage";
import NotesEditor from "../components/NotesEditor";
import { getCategoryColor } from "../utils/categoryColors";

function NotesPage() {
  const {
    categories,
    fetchCategories,
    notes,
    fetchNotes,
    note,
    selectedNoteId,
    setSelectedNoteId,
    loading,
    error,
  } = useNotesPage();

  const selectedNote = notes.find((n) => n.id === selectedNoteId);
  const selectedCategory = categories.find(
    (c) => c.categoryId === selectedNote?.categoryId,
  );
  const selectedColor = selectedCategory
    ? getCategoryColor(selectedCategory.categoryId, categories)
    : undefined;

  return (
    <div className="flex min-h-screen w-full">
      <NotesLeftSideBar
        categories={categories}
        fetchCategories={fetchCategories}
        notes={notes}
        selectedNoteId={selectedNoteId}
        setSelectedNoteId={setSelectedNoteId}
      ></NotesLeftSideBar>
      <NotesEditor
        note={note}
        category={selectedCategory}
        color={selectedColor}
        fetchNotes={fetchNotes}
      />
    </div>
  );
}

export default NotesPage;
