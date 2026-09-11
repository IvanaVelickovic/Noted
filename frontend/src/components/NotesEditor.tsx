import { useEffect, useState } from "react";
import type { CategoryDetailed } from "../api/categories";
import type { Note } from "../api/notes";
import Button from "./Button";
import { useNoteActions } from "../hooks/useNoteActions";

type NotesEditorProps = {
  note: Note | undefined;
  category: CategoryDetailed | undefined;
  color: string | undefined;
  fetchNotes: () => Promise<void>;
};

function NotesEditor({ note, category, color, fetchNotes }: NotesEditorProps) {
  const [title, setTitle] = useState("Untitled note");
  const [body, setBody] = useState("Start writing...");

  const isDirty = note && (title !== note.title || body !== note.body);

  const { updateNote } = useNoteActions(fetchNotes);

  useEffect(() => {
    if (note) {
      setTitle(note?.title);
      setBody(note?.body);
    }
  }, [note]);

  return (
    <div className="w-[76%]">
      {/* TOP MENU */}
      <div className="h-[8.8%] flex justify-between items-center p-3 border-b-2 border-b-input-border">
        <div
          className="font-mono text-base rounded-xl px-1.5 py-0.5"
          style={{ color, backgroundColor: `${color}26` }}
        >
          {category?.categoryName}
        </div>
        <div className="flex gap-x-3">
          <button className="font-mono text-paragraph-light px-3 cursor-pointer">
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
          <button className="font-mono text-paragraph-light border-[#CEC0B5] border rounded-lg px-5 py-1 cursor-pointer">
            ✦ Summarize
          </button>
        </div>
      </div>

      {/* EDITOR */}
      <div className="p-8 pt-12">
        <textarea
          className="font-display text-header text-3xl outline-none w-full resize-none"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        ></textarea>
        <textarea
          className="text-[#5A3F45] text-[1.05rem] w-full h-122 outline-none resize-none"
          value={body}
          onChange={(e) => setBody(e.target.value)}
        ></textarea>
      </div>
    </div>
  );
}

export default NotesEditor;
