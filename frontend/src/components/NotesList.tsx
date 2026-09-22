import { formatDate } from "../utils/formatDate";
import { getCategoryColor } from "../utils/categoryColors";
import type { CategoryDetailed } from "../api/categories";
import type { NoteBasicInfo } from "../api/notes";

type NotesListProps = {
  notes: NoteBasicInfo[];
  categories: CategoryDetailed[];
  selectedNoteId: string;
  onSelectNote: (id: string) => void;
  loading?: string;
  error?: string;
};

const SELECTED_NOTE_STYLE = " border-l-[3px] border-l-button-bg bg-note-fill ";

function NotesList({
  notes,
  categories,
  selectedNoteId,
  onSelectNote,
  loading,
  error,
}: NotesListProps) {
  if (loading) return <div className="text-paragraph">Loading...</div>;
  if (error) return <div className="text-red-500">Error: {error}</div>;

  return (
    <ul className="flex flex-col my-1 overflow-y-auto overflow-x-hidden max-h-112 cursor-pointer">
      {notes.map((note) => {
        const color = getCategoryColor(note.categoryId, categories);
        return (
          <li
            className={`flex flex-col p-3 gap-y-0.5 ${
              note.id === selectedNoteId ? SELECTED_NOTE_STYLE : ""
            }`}
            key={note.id}
            onClick={() => onSelectNote(note.id)}
          >
            <h3 className="text-header text-lg">{note.title}</h3>
            <div className="flex gap-x-5">
              <div
                className="font-mono text-sm rounded-lg px-0.5 py-px"
                style={{ color, backgroundColor: `${color}26` }}
              >
                {
                  categories.find((c) => c.categoryId === note.categoryId)
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
  );
}

export default NotesList;
