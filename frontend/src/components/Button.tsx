import type { ButtonHTMLAttributes } from "react";

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: "primary" | "secondary" | "ternary";
};

const variantStyles = {
  primary: "bg-button-bg text-white hover:bg-button-hover/90 cursor-pointer",
  secondary:
    "bg-transparent border border-button-bg/40 text-button-bg hover:bg-button-bg/15 cursor-pointer",
  ternary:
    "font-mono text-paragraph-light border-input-border border-2 rounded-lg bg-note-fill px-5 py-1 cursor-pointer",
};

function Button({
  variant = "primary",
  className = "",
  children,
  ...props
}: ButtonProps) {
  return (
    <button
      className={`px-7 py-3 rounded-lg font-mono ${variantStyles[variant]} ${className}`}
      {...props}
    >
      {children}
    </button>
  );
}

export default Button;
