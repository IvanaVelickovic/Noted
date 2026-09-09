import { useState } from "react";
import { useNotes } from "../hooks/useNotes";
import { formatDate } from "../utils/formatDate";
import { useLogout } from "../hooks/useLogout";

function NotesLeftSideBar() {
  const { notes, loading, error } = useNotes();
  const [selectedNote, setSelectedNote] = useState(0);
  const selectedNoteStyle = " border-l-[3px] border-l-button-bg bg-note-fill ";
  const logout = useLogout();

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
        <ul className="flex flex-col my-1 overflow-y-auto overflow-x-hidden max-h-122 cursor-pointer">
          {notes.map((note, index) => (
            <li
              className={`flex flex-col p-3 gap-y-0.5 ${selectedNote == index ? selectedNoteStyle : ""}`}
              key={note.id}
              onClick={() => setSelectedNote(index)}
            >
              <h3 className="text-header text-lg">{note.title}</h3>
              <div className="flex gap-x-5">
                <div className="font-mono text-sm text-[#7ECFAB] bg-[#7ECFAB]/15 rounded-lg px-0.5 py-px">
                  {note.categoryId}
                </div>
                <div className="font-mono text-sm text-date-notes">
                  {formatDate(note.lastEdited)}
                </div>
              </div>
            </li>
          ))}
        </ul>

        {/* CATEGORIES */}
        <div className="border-t-2 border-t-input-border flex flex-col gap-y-0.5 p-3 max-h-45 h-full">
          <div className="flex justify-between items-center">
            <h3 className="text-date-notes font-mono text-[0.92rem]">
              CATEGORIES
            </h3>
            <button className="text-paragraph-light text-[1.2rem] mr-1.5 cursor-pointer">
              +
            </button>
          </div>
          <ul className="flex flex-col overflow-y-auto cursor-pointer">
            <li className="flex justify-between items-center text-header text-sm font-mono bg-note-fill rounded px-3 py-1.5">
              <h5>All notes</h5>
              <div className="flex gap-x-0.5">
                <img
                  src="./images/edit_button.png"
                  className="h-[1.56rem]"
                ></img>
                <div className="text-[0.94rem] text-button-bg">x</div>
              </div>
            </li>
            <li className="text-paragraph text-sm font-mono px-3 py-1.5">
              Biology
            </li>
          </ul>
        </div>
      </div>
    </div>
  );
}

export default NotesLeftSideBar;
