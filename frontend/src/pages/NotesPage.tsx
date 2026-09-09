import { useEffect, useState } from "react";
import api from "../api/client";
import axios from "axios";

function NotesPage() {
  const [text, setText] = useState("not working yet");

  useEffect(() => {
    const fetchMe = async () => {
      try {
        const res = await api.get("/users/me");
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
  return <div>{text}</div>;
}

export default NotesPage;
