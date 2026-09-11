import { useEffect, useMemo, useState } from "react";
import { useLogout } from "../hooks/useLogout";
import { useCategoryActions } from "../hooks/useCategoryActions";
import { useInlineEdit } from "../hooks/useInlineEdit";
import type { CategoryDetailed } from "../api/categories";
import type { NoteBasicInfo } from "../api/notes";
import NotesList from "./NotesList";
import CategoriesList from "./CategoriesList";
import { useNoteActions } from "../hooks/useNoteActions";

type NotesLeftSideBarProps = {
  categories: CategoryDetailed[];
  fetchCategories: () => Promise<void>;
  notes: NoteBasicInfo[];
  fetchNotes: () => Promise<void>;
  selectedNoteId: string;
  setSelectedNoteId: React.Dispatch<React.SetStateAction<string>>;
  loading?: string;
  error?: string;
};

function NotesLeftSideBar({
  categories,
  fetchCategories,
  notes,
  fetchNotes,
  selectedNoteId,
  setSelectedNoteId,
  loading,
  error,
}: NotesLeftSideBarProps) {
  const [selectedCategoryId, setSelectedCategoryId] = useState<
    string | undefined
  >(undefined);

  const logout = useLogout();
  const { createCategory, updateCategory, deleteCategory } =
    useCategoryActions(fetchCategories);
  const {
    editingId,
    setEditingId,
    value,
    setValue,
    startEditing,
    cancelEditing,
  } = useInlineEdit();

  const { createNote } = useNoteActions(fetchNotes);

  const notesByCategory = useMemo(() => {
    if (!selectedCategoryId) return notes;
    return notes.filter((n) => n.categoryId === selectedCategoryId);
  }, [notes, selectedCategoryId]);

  useEffect(() => {
    if (notesByCategory.length > 0) {
      const isCurrentInList = notesByCategory.some(
        (n) => n.id === selectedNoteId,
      );
      if (!isCurrentInList) {
        setSelectedNoteId(notesByCategory[0].id);
      }
    } else {
      setSelectedNoteId("");
    }
  }, [selectedCategoryId]);

  const commitEdit = async () => {
    const trimmed = value.trim();
    if (!trimmed) return cancelEditing();

    try {
      if (editingId === "__new__") {
        await createCategory(trimmed);
      } else if (editingId) {
        await updateCategory(trimmed, editingId);
      }
    } catch (err) {
      console.log(err);
    } finally {
      cancelEditing();
    }
  };

  const createNewCategory = () => {
    setEditingId("__new__");
    setValue("");
  };

  const addNewNote = async () => {
    const newNoteId = await createNote("Untitled note", "Start writing...");
    setSelectedNoteId(newNoteId);
  };

  return (
    <div className="w-[24%] flex flex-col border-r-2 border-r-input-border">
      {/* HEADER */}
      <div className="flex justify-between items-center p-3 border-b-2 border-b-input-border min-h-[8.8%]">
        <h1 className="text-button-bg font-display text-[1.35rem]">Noted.</h1>
        <div className="flex gap-x-2 h-7">
          <img
            src="./images/logout_button.png"
            className="cursor-pointer"
            onClick={logout}
            alt="logout"
          />
          <img
            src="./images/add_button.png"
            className="cursor-pointer"
            onClick={() => addNewNote()}
          />
        </div>
      </div>

      <div className="flex flex-col justify-between h-full">
        <NotesList
          notes={notesByCategory}
          categories={categories}
          selectedNoteId={selectedNoteId}
          onSelectNote={setSelectedNoteId}
          loading={loading}
          error={error}
        />

        <CategoriesList
          categories={categories}
          selectedCategoryId={selectedCategoryId}
          onSelectCategory={setSelectedCategoryId}
          editingId={editingId}
          value={value}
          onValueChange={setValue}
          onCommitEdit={commitEdit}
          onCancelEdit={cancelEditing}
          onStartEditing={startEditing}
          onDeleteCategory={deleteCategory}
          onCreateCategory={createNewCategory}
        />
      </div>
    </div>
  );
}

export default NotesLeftSideBar;
