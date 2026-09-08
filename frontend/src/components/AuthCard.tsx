import { Link } from "react-router-dom";
import Button from "./Button";

type AuthCardProps = {
  authType: "login" | "register";
};

function AuthCard({ authType }: AuthCardProps) {
  return (
    <div className="bg-white p-5 rounded-lg flex flex-col items-center w-[28%]">
      <h1 className="text-button-bg font-display text-3xl my-3.5">Noted.</h1>

      <div className="flex flex-col items-center gap-y-1 py-1.5">
        <h2 className="text-header text-[1.6rem] font-display">
          {authType == "login" ? "Welcome back" : "Create your account"}
        </h2>
        <p className="text-paragraph-light text-base">
          {authType == "login"
            ? "Sign in to your notes"
            : "Start taking notes that matter"}
        </p>
      </div>

      <div className="w-full flex flex-col items-center justify-center gap-y-2.5 my-4 px-5">
        {/* INPUT NAME */}
        <div className={`w-full ${authType == "login" ? "hidden" : ""}`}>
          <label
            htmlFor="name"
            className="font-mono text-sm text-paragraph-light"
          >
            NAME
          </label>
          <input
            className="border w-full h-10 block px-2 rounded text-header/50 bg-input-bg border-input-border"
            id="name"
            type="text"
            placeholder="Your name"
          ></input>
        </div>

        {/* INPUT EMAIL */}
        <div className="w-full">
          <label
            htmlFor="email"
            className="font-mono text-sm text-paragraph-light"
          >
            EMAIL
          </label>
          <input
            className="border w-full h-10 block px-2 rounded text-header/50 bg-input-bg border-input-border"
            id="email"
            type="email"
            placeholder="you@example.com"
          ></input>
        </div>

        {/* INPUT PASSWORD */}
        <div className="w-full">
          <label
            htmlFor="password"
            className="font-mono text-sm text-paragraph-light"
          >
            PASSWORD
          </label>
          <input
            className="border w-full h-10 block px-2 rounded text-header/50 bg-input-bg border-input-border"
            id="password"
            type="password"
            placeholder="••••••••"
          ></input>
        </div>

        {/* INPUT CONFIRM PASSWORD */}
        <div className={`w-full ${authType == "login" ? "hidden" : ""}`}>
          <label
            htmlFor="confirmPassword"
            className="font-mono text-sm text-paragraph-light"
          >
            CONFIRM PASSWORD
          </label>
          <input
            className="border w-full h-10 block px-2 rounded text-header/50 bg-input-bg border-input-border"
            id="confirmPassword"
            type="password"
            placeholder="••••••••"
          ></input>
        </div>

        {/* BUTTON AND FOOTER */}
        <Button className="w-full my-4">
          {authType == "login" ? "Sign in" : "Create account"}
        </Button>
        <div className="flex gap-x-1 text-[0.9rem]">
          <p className="text-paragraph-light">
            {authType == "login" ? "No account?" : "Already have an account?"}
          </p>

          <Link
            to={authType == "login" ? "/register" : "/login"}
            className="text-button-bg hover:cursor-pointer underline"
          >
            {authType == "login" ? "Create one" : "Sign in"}
          </Link>
        </div>
      </div>
    </div>
  );
}

export default AuthCard;
