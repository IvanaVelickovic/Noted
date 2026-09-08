import { useEffect, useState } from "react";
import Button from "./Button";

const TITLE = "Noted.";

function LandingPage() {
  const [displayed, setDisplayed] = useState("");
  const [titleDone, setTitleDone] = useState(false);
  const [showDesc, setShowDesc] = useState(false);
  const [showButtons, setShowButtons] = useState(false);

  useEffect(() => {
    let i = 0;
    const interval = setInterval(() => {
      i++;
      setDisplayed(TITLE.slice(0, i));
      if (i === TITLE.length) {
        clearInterval(interval);
        setTitleDone(true);
        setTimeout(() => setShowDesc(true), 300);
        setTimeout(() => setShowButtons(true), 700);
      }
    }, 110);
    return () => clearInterval(interval);
  }, []);

  return (
    <div className="min-h-screen w-full flex flex-col overflow-hidden">
      <div className="flex-1 w-full text-2xl text-black flex justify-center items-center">
        <div className="flex flex-col items-center p-5 mt-8">
          <h1 className="text-[5.5rem] text-header font-display text-center">
            {displayed}
          </h1>
          <div
            className={`text-center text-[1.1rem] text-paragraph/90 my-7 mb-11 flex flex-col gap-y-1 ${showDesc ? "opacity-100 translate-y-0" : "opacity-0 translate-y-2.5"} 
            transition-[opacity,transform] duration-600 ease-in-out`}
          >
            <p>A quiet place for your thoughts, study notes, and ideas.</p>
            <p>Organize by category. Summarize in seconds. Always private.</p>
          </div>

          <div
            className={`w-full flex gap-x-3 justify-center ${showButtons ? "opacity-100 translate-y-0" : "opacity-0 translate-y-2.5"}
            transition-[opacity,transform] duration-600 ease-in-out`}
          >
            <Button className="text-base transition-all duration-200 ease-in-out hover:-translate-y-0.5">
              Log in
            </Button>
            <Button
              variant="secondary"
              className="text-base transition-all duration-200 ease-in-out hover:-translate-y-0.5"
            >
              Create account
            </Button>
          </div>
        </div>
      </div>

      <footer
        className={`w-full text-center pb-6 text-[0.85rem] text-paragraph/40 ${showButtons ? "opacity-100 translate-y-0" : "opacity-0 translate-y-1.5"}
            transition-[opacity,transform] duration-700 ease-in-out`}
      >
        {" "}
        your notes. always private
      </footer>
    </div>
  );
}

export default LandingPage;
