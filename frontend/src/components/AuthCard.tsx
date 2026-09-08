import { Link } from "react-router-dom";
import Button from "./Button";
import { useAuthForm } from "../hooks/useAuthForm";

type AuthCardProps = {
  authType: "login" | "register";
};

function AuthCard({ authType }: AuthCardProps) {
  const { formData, handleChange, handleSubmit, error, loading } =
    useAuthForm(authType);

  return (
    <form
      onSubmit={handleSubmit}
      className="bg-white p-5 rounded-lg flex flex-col items-center w-[28%]"
    >
      <Link
        to="/"
        className="text-button-bg font-display text-3xl my-3.5 cursor-pointer"
      >
        Noted.
      </Link>

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
            value={formData.name}
            onChange={handleChange}
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
            value={formData.email}
            onChange={handleChange}
            type="email"
            placeholder="you@example.com"
            required
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
            value={formData.password}
            onChange={handleChange}
            type="password"
            placeholder="••••••••"
            minLength={8}
            maxLength={30}
            required
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
            value={formData.confirmPassword}
            onChange={handleChange}
            type="password"
            placeholder="••••••••"
            minLength={8}
            maxLength={30}
          ></input>
        </div>

        {/* BUTTON AND FOOTER */}
        {error && (
          <p className="text-red-600 text-sm mt-2 mb-0">Error: {error}</p>
        )}
        <Button className="w-full my-4">
          {loading
            ? "Loading..."
            : authType === "login"
              ? "Sign in"
              : "Create account"}
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
    </form>
  );
}

export default AuthCard;
