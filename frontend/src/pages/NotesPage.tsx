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
    <>
      {loading && <div>Loading...</div>}
      {error && <div className="text-red-500">Error: {error}</div>}
      {!error && !loading && (
        <div className="flex min-h-screen w-full">
          <NotesLeftSideBar
            categories={categories}
            fetchCategories={fetchCategories}
            notes={notes}
            fetchNotes={fetchNotes}
            selectedNoteId={selectedNoteId}
            setSelectedNoteId={setSelectedNoteId}
          ></NotesLeftSideBar>
          <NotesEditor
            note={note}
            category={selectedCategory}
            categories={categories}
            color={selectedColor}
            fetchNotes={fetchNotes}
            fetchCategories={fetchCategories}
          />
        </div>
      )}
    </>
  );
}

export default NotesPage;
