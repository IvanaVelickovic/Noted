import { useEffect, useMemo, useState } from "react";
import { formatDate } from "../utils/formatDate";
import { useLogout } from "../hooks/useLogout";
import type { CategoryDetailed } from "../api/categories";
import type { NoteBasicInfo } from "../api/notes";
import { getCategoryColor } from "../utils/categoryColors";
import { useCategoryActions } from "../hooks/useCategoryActions";
import { useInlineEdit } from "../hooks/useInlineEdit";

type NotesLeftSideBarProps = {
  categories: CategoryDetailed[];
  fetchCategories: () => Promise<void>;
  notes: NoteBasicInfo[];
  selectedNoteId: string;
  setSelectedNoteId: React.Dispatch<React.SetStateAction<string>>;
  loading?: string;
  error?: string;
};

function NotesLeftSideBar({
  categories,
  fetchCategories,
  notes,
  selectedNoteId,
  setSelectedNoteId,
  loading,
  error,
}: NotesLeftSideBarProps) {
  const [selectedCategoryId, setSelectedCategoryId] = useState<
    string | undefined
  >(undefined);
  const selectedNoteStyle = " border-l-[3px] border-l-button-bg bg-note-fill ";
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

  return (
    <div className="w-[24%] flex flex-col border-r-2 border-r-input-border">
      {/* HEADER */}
      <div className="flex justify-between items-center p-3 border-b-2 border-b-input-border">
        <h1 className="text-button-bg font-display text-[1.35rem]">Noted.</h1>
        <div className="flex gap-x-2 h-7">
          <img
            src="./images/logout_button.png"
            className="cursor-pointer"
            onClick={logout}
            alt="logout"
          ></img>
          <img src="./images/add_button.png" className="cursor-pointer"></img>
        </div>
      </div>

      <div className="flex flex-col justify-between h-full">
        {/* NOTES */}
        {loading && <div className="text-paragraph">Loading...</div>}
        {error && <div className="text-red-500">Error: {error}</div>}
        <ul className="flex flex-col my-1 overflow-y-auto overflow-x-hidden max-h-116 cursor-pointer">
          {notesByCategory.map((note) => {
            const color = getCategoryColor(note.categoryId, categories);
            return (
              <li
                className={`flex flex-col p-3 gap-y-0.5 ${note.id == selectedNoteId ? selectedNoteStyle : ""}`}
                key={note.id}
                onClick={() => setSelectedNoteId(note.id)}
              >
                <h3 className="text-header text-lg">{note.title}</h3>
                <div className="flex gap-x-5">
                  <div
                    className="font-mono text-sm rounded-lg px-0.5 py-px"
                    style={{ color, backgroundColor: `${color}26` }}
                  >
                    {
                      categories.find((c) => c.categoryId == note.categoryId)
                        ?.categoryName
                    }
                  </div>
                  <div className="font-mono text-sm text-date-notes">
                    {formatDate(note.lastEdited)}
                  </div>
                </div>
              </li>
            );
          })}
        </ul>

        {/* CATEGORIES */}
        <div className="border-t-2 border-t-input-border flex flex-col gap-y-0.5 p-3 max-h-41 h-full">
          <div className="flex justify-between items-center">
            <h3 className="text-date-notes font-mono text-[0.92rem]">
              CATEGORIES
            </h3>
            <button
              className="text-paragraph-light text-[1.2rem] mr-1.5 cursor-pointer"
              onClick={createNewCategory}
            >
              +
            </button>
          </div>
          <ul className="flex flex-col overflow-y-auto cursor-pointer">
            <li
              className={`flex justify-between items-center text-header text-sm font-mono rounded px-3 py-1.5 ${selectedCategoryId == undefined ? "bg-note-fill" : ""}`}
              onClick={() => setSelectedCategoryId(undefined)}
            >
              <h5>All notes</h5>
            </li>
            {categories.map((category) => {
              const color = getCategoryColor(category.categoryId, categories);
              const isEditing = editingId === category.categoryId;
              return (
                <li
                  className={`flex items-center justify-between text-paragraph text-sm font-mono px-3 py-0.5 ${selectedCategoryId == category.categoryId ? "bg-note-fill" : ""}`}
                  style={{ color }}
                  key={category.categoryId}
                  onClick={() =>
                    !isEditing && setSelectedCategoryId(category.categoryId)
                  }
                >
                  <div className="flex items-center gap-x-1.5">
                    <span className="text-lg py-0">•</span>
                    {isEditing ? (
                      <input
                        autoFocus
                        value={value}
                        onChange={(e) => setValue(e.target.value)}
                        onBlur={commitEdit}
                        onKeyDown={(e) => {
                          if (e.key === "Enter") commitEdit();
                          if (e.key === "Escape") cancelEditing();
                        }}
                        onClick={(e) => e.stopPropagation()} // don't trigger categorySelection
                        className="bg-transparent border-b border-button-bg outline-none flex-1"
                      />
                    ) : (
                      category.categoryName
                    )}
                  </div>

                  {selectedCategoryId == category.categoryId && !isEditing && (
                    <div className="flex gap-x-0.5">
                      <img
                        src="./images/edit_button.png"
                        className="h-[1.56rem]"
                        onClick={(e) => {
                          e.stopPropagation();
                          startEditing(
                            category.categoryId,
                            category.categoryName,
                          );
                        }}
                      ></img>
                      <div
                        className="text-[0.94rem] text-button-bg"
                        onClick={(e) => {
                          e.stopPropagation();
                          deleteCategory(category.categoryId);
                        }}
                      >
                        x
                      </div>
                    </div>
                  )}
                </li>
              );
            })}
            {editingId === "__new__" && (
              <li className="flex items-center gap-x-1.5 text-paragraph text-sm font-mono px-3 py-0.5">
                <span
                  className="text-lg py-0"
                  style={{
                    color: getCategoryColor("new", [
                      ...categories,
                      { categoryId: "new" } as CategoryDetailed,
                    ]),
                  }}
                >
                  •
                </span>
                <input
                  autoFocus
                  value={value}
                  onChange={(e) => setValue(e.target.value)}
                  onBlur={commitEdit}
                  onKeyDown={(e) => {
                    if (e.key === "Enter") commitEdit();
                    if (e.key === "Escape") {
                      setEditingId(null);
                      setValue("");
                    }
                  }}
                  placeholder="New category"
                  className="bg-transparent border-b border-button-bg outline-none flex-1"
                />
              </li>
            )}
          </ul>
        </div>
      </div>
    </div>
  );
}

export default NotesLeftSideBar;
