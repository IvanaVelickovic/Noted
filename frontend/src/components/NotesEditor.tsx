import { useEffect, useState } from "react";
import type { CategoryDetailed } from "../api/categories";
import type { Note } from "../api/notes";
import Button from "./Button";
import { useNoteActions } from "../hooks/useNoteActions";
import { countWords } from "../utils/wordCounter";
import { formatDateTime } from "../utils/formatDate";
import DeleteConfirmationModal from "./DeleteConfirmationModal";
import SummarizeCard from "./SummarizeCard";
import { useSummarizeNote } from "../hooks/useSummarizeNote";

type NotesEditorProps = {
  note: Note | undefined;
  category: CategoryDetailed | undefined;
  categories: CategoryDetailed[];
  color: string | undefined;
  fetchNotes: () => Promise<void>;
  fetchCategories: () => Promise<void>;
};

function NotesEditor({
  note,
  category,
  categories,
  color,
  fetchNotes,
  fetchCategories,
}: NotesEditorProps) {
  const [title, setTitle] = useState("");
  const [body, setBody] = useState("");

  const isDirty = note && (title !== note.title || body !== note.body);

  const { updateNote, deleteNote } = useNoteActions(
    fetchNotes,
    fetchCategories,
  );
  const [showDetails, setShowDetails] = useState(false);
  const [showDeleteDialog, setShowDeleteDialog] = useState(false);
  const [showSummarize, setShowSummarize] = useState(false);

  const { summarize, job, loading, error } = useSummarizeNote();

  const handleSummarizeClick = () => {
    if (!note) return;
    setShowSummarize(true);
    summarize(note.id);
  };

  useEffect(() => {
    if (note) {
      setTitle(note?.title);
      setBody(note?.body);
    }
  }, [note]);

  const handleDelete = async () => {
    if (!note) return;
    await deleteNote(note.id);
  };

  return (
    <div className="w-[76%] h-screen bg-input-bg flex">
      <div className="w-full">
        {/* TOP MENU */}
        <div className="shrink-0 h-[8.8%] flex justify-between items-center p-3 border-b-2 border-b-input-border">
          <div
            className="font-mono text-[0.9rem] rounded-xl px-1.5 py-0.5"
            style={{ color, backgroundColor: `${color}26` }}
          >
            {category?.categoryName}
          </div>
          <div className="flex gap-x-3">
            <button
              className="font-mono text-paragraph-light px-3 cursor-pointer"
              onClick={() => setShowDetails(!showDetails)}
            >
              ℹ Details
            </button>
            <Button
              disabled={!isDirty}
              variant={isDirty ? "primary" : "ternary"}
              onClick={() => {
                if (!note) return;
                updateNote(title, body, note?.id, category?.categoryId);
              }}
            >
              {isDirty ? "Save" : "Saved"}
            </Button>
            {!showSummarize && (
              <button
                className="font-mono text-paragraph-light border-[#CEC0B5] border rounded-lg px-5 cursor-pointer"
                onClick={handleSummarizeClick}
              >
                ✦ Summarize
              </button>
            )}
          </div>
        </div>

        {/* DETAILS MENU */}
        {showDetails && (
          <div className="shrink-0 p-3 border-b-2 border-b-input-border flex items-center justify-between bg-[#F3ECE5] px-4.5">
            <div className="flex gap-x-9">
              <div>
                <h3 className="font-mono text-date-notes text-sm">CREATED</h3>
                <p className="font-mono text-paragraph text-sm">
                  {formatDateTime(note?.createdAt)}
                </p>
              </div>
              <div>
                <h3 className="font-mono text-date-notes text-sm">
                  LAST EDITED
                </h3>
                <p className="font-mono text-paragraph text-sm">
                  {formatDateTime(note?.lastEdited)}
                </p>
              </div>
              <div>
                <h3 className="font-mono text-date-notes text-sm">WORDS</h3>
                <p className="font-mono text-paragraph text-sm">
                  {countWords(body)}
                </p>
              </div>
              <div>
                <h3 className="font-mono text-date-notes text-sm">CATEGORY</h3>
                <select
                  value={category?.categoryId ?? ""}
                  onChange={(e) => {
                    const newCategoryId = e.target.value || undefined;
                    if (note) updateNote(title, body, note.id, newCategoryId);
                  }}
                  className="font-mono text-sm rounded-lg px-1.5 py-0.5 outline-none cursor-pointer"
                  style={{
                    color,
                    backgroundColor: color ? `${color}26` : undefined,
                  }}
                >
                  <option value="">No category</option>
                  {categories.map((c) => (
                    <option key={c.categoryId} value={c.categoryId}>
                      {c.categoryName}
                    </option>
                  ))}
                </select>
              </div>
            </div>
            <button
              className="font-mono text-delete border-2 border-delete/30 rounded-lg px-5 py-1.5 cursor-pointer hover:bg-delete/10"
              onClick={() => setShowDeleteDialog(true)}
            >
              Delete
            </button>
          </div>
        )}

        {/* EDITOR */}
        <div className="flex-1 min-h-0 flex flex-col p-8 pt-12 overflow-y-auto">
          <textarea
            className="font-display text-header text-3xl outline-none w-full resize-none"
            value={title}
            onChange={(e) => setTitle(e.target.value)}
          ></textarea>
          <textarea
            className="text-[#5A3F45] text-[1.05rem] w-full h-120 outline-none resize-none"
            value={body}
            onChange={(e) => setBody(e.target.value)}
          ></textarea>
        </div>

        <DeleteConfirmationModal
          isOpen={showDeleteDialog}
          onClose={() => setShowDeleteDialog(false)}
          onConfirm={handleDelete}
          noteTitle={note?.title}
        ></DeleteConfirmationModal>
      </div>
      {showSummarize && (
        <SummarizeCard
          setShowSummarize={setShowSummarize}
          job={job}
          error={error}
          loading={loading}
          onRegenerate={handleSummarizeClick}
        ></SummarizeCard>
      )}
    </div>
  );
}

export default NotesEditor;
