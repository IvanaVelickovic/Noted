import { useEffect, useState } from "react";
import api from "../api/client";
import axios from "axios";
import NotesLeftSideBar from "../components/NotesLeftSideBar";
import { useNotesPage } from "../hooks/useNotesPage";

function NotesPage() {
  const [text, setText] = useState("not working yet");

  useEffect(() => {
    const fetchMe = async () => {
      try {
        const res = await api.get("/me");
        if (res.status === 200) {
          setText(`success: ${JSON.stringify(res.data)}`);
        }
      } catch (err) {
        if (axios.isAxiosError(err)) {
          setText(`failed with status: ${err.response?.status}`);
        } else {
          setText("failed: unknown error");
        }
      }
    };

    fetchMe();
  }, []);

  const {
    categories,
    notes,
    note,
    selectedNoteId,
    setSelectedNoteId,
    loading,
    error,
  } = useNotesPage();
  return (
    <div className="flex min-h-screen w-full">
      <NotesLeftSideBar
        categories={categories}
        notes={notes}
        selectedNoteId={selectedNoteId}
        setSelectedNoteId={setSelectedNoteId}
      ></NotesLeftSideBar>
      <div>{note?.title}</div>
    </div>
  );
}

export default NotesPage;
