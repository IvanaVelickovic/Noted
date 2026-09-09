import { useEffect, useState } from "react";
import api from "../api/client";
import axios from "axios";
import NotesLeftSideBar from "../components/NotesLeftSideBar";

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
  return (
    <div className="flex min-h-screen w-full">
      <NotesLeftSideBar></NotesLeftSideBar>
      <div>{text}</div>
    </div>
  );
}

export default NotesPage;
